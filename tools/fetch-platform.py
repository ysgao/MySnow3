#!/usr/bin/env python3
"""Fetch only the platform and harness clusters from an Apache NetBeans release zip.

Apache stopped publishing a platform-only distribution after release 15, so the
platform now ships only inside netbeans-<version>-bin.zip (~500 MB). The clusters this
suite actually needs are ~32 MB of that, so rather than pull the whole IDE we read the
archive's central directory over HTTP range requests and download just the byte ranges
holding those entries.

Integrity: every extracted entry is CRC-32 checked against the archive's own central
directory (zipfile does that on read). The SHA-512 Apache publishes covers the whole
zip and cannot be checked without downloading all of it - build with
-Dplatform.download.mode=full when you want that check.

Usage: fetch-platform.py <zip-url> <dest-dir> [prefix ...]
"""

import io
import os
import stat
import sys
import time
import urllib.request
import zipfile

# Wanted ranges closer together than this are fetched as a single request.
GAP_TOLERANCE = 1 << 20
TAIL_BYTES = 1 << 16
# Mirrors tend to drop very long range reads, so ask in modest pieces.
CHUNK_BYTES = 8 << 20
TIMEOUT = 120
RETRIES = 4


class RangeFile(io.RawIOBase):
    """Seekable read-only file over an HTTP resource, backed by a cache of spans."""

    def __init__(self, url):
        self.url = url
        self.pos = 0
        self.spans = []  # list of (start, bytes)
        with urllib.request.urlopen(
            urllib.request.Request(url, method="HEAD"), timeout=TIMEOUT
        ) as r:
            self.size = int(r.headers["Content-Length"])
            if r.headers.get("Accept-Ranges") != "bytes":
                raise RuntimeError(f"server does not support range requests: {url}")

    def seekable(self):
        return True

    def readable(self):
        return True

    def tell(self):
        return self.pos

    def seek(self, offset, whence=io.SEEK_SET):
        if whence == io.SEEK_SET:
            self.pos = offset
        elif whence == io.SEEK_CUR:
            self.pos += offset
        else:
            self.pos = self.size + offset
        return self.pos

    def _http_range(self, start, end):
        """Fetch an inclusive byte range, retrying short or dropped reads."""
        end = min(end, self.size - 1)
        want = end - start + 1
        problem = None
        for attempt in range(RETRIES):
            try:
                req = urllib.request.Request(
                    self.url, headers={"Range": f"bytes={start}-{end}"}
                )
                with urllib.request.urlopen(req, timeout=TIMEOUT) as r:
                    if r.status != 206:
                        raise RuntimeError(f"expected 206 for a range request, got {r.status}")
                    buf = bytearray()
                    while len(buf) < want:
                        piece = r.read(min(1 << 20, want - len(buf)))
                        if not piece:
                            break
                        buf.extend(piece)
                if len(buf) == want:
                    return bytes(buf)
                problem = f"short read: {len(buf)} of {want} bytes"
            except Exception as exc:  # network flake; worth another go
                problem = exc
            time.sleep(1 + attempt)
        raise RuntimeError(f"range {start}-{end} failed after {RETRIES} tries: {problem}")

    def prefetch(self, start, end):
        """Cache an inclusive byte range as one span, fetched in digestible pieces."""
        buf = bytearray()
        at = start
        while at <= end:
            stop = min(at + CHUNK_BYTES - 1, end)
            buf.extend(self._http_range(at, stop))
            at = stop + 1
        self.spans.append((start, bytes(buf)))
        self.spans.sort(key=lambda s: s[0])

    def _cached(self, start, length):
        for s_start, s_data in self.spans:
            if s_start <= start and start + length <= s_start + len(s_data):
                off = start - s_start
                return s_data[off:off + length]
        return None

    def readinto(self, buf):
        data = self.read(len(buf))
        buf[:len(data)] = data
        return len(data)

    def read(self, size=-1):
        if size < 0:
            size = self.size - self.pos
        size = min(size, self.size - self.pos)
        if size <= 0:
            return b""
        data = self._cached(self.pos, size)
        if data is None:
            data = self._http_range(self.pos, self.pos + size - 1)
        self.pos += len(data)
        return data


def merge(spans):
    """Merge (start, end) pairs that are adjacent or nearly so."""
    merged = []
    for start, end in sorted(spans):
        if merged and start - merged[-1][1] <= GAP_TOLERANCE:
            merged[-1][1] = max(merged[-1][1], end)
        else:
            merged.append([start, end])
    return merged


def main(argv):
    if len(argv) < 3:
        sys.exit(__doc__)
    url, dest = argv[1], argv[2]
    prefixes = tuple(argv[3:]) or ("netbeans/platform/", "netbeans/harness/")

    rf = RangeFile(url)
    # The central directory sits at the end of the archive.
    rf.prefetch(max(0, rf.size - TAIL_BYTES), rf.size - 1)
    try:
        zf = zipfile.ZipFile(rf)
    except zipfile.BadZipFile:
        # Directory larger than the tail we grabbed; take a bigger bite.
        rf.prefetch(max(0, rf.size - 32 * TAIL_BYTES), rf.size - 1)
        zf = zipfile.ZipFile(rf)

    entries = sorted(zf.infolist(), key=lambda i: i.header_offset)
    wanted = [i for i in entries if i.filename.startswith(prefixes)]
    if not wanted:
        sys.exit(f"no entries matching {prefixes} in {url}")

    # An entry's bytes run from its local header up to the next entry's header.
    end_of = {}
    for n, info in enumerate(entries):
        end_of[info.header_offset] = (
            entries[n + 1].header_offset if n + 1 < len(entries) else rf.size
        )

    spans = merge([(i.header_offset, end_of[i.header_offset]) for i in wanted])
    total = sum(e - s for s, e in spans)
    print(f"Fetching {len(wanted)} entries in {len(spans)} range request(s): "
          f"{total / 1048576:.1f} MB of {rf.size / 1048576:.1f} MB")
    for start, end in spans:
        rf.prefetch(start, end - 1)

    written = 0
    for info in wanted:
        rel = info.filename.split("/", 1)[1]  # strip the leading "netbeans/"
        if not rel:
            continue
        target = os.path.join(dest, rel)
        if info.is_dir():
            os.makedirs(target, exist_ok=True)
            continue
        os.makedirs(os.path.dirname(target), exist_ok=True)
        with zf.open(info) as src, open(target, "wb") as out:
            out.write(src.read())  # zipfile checks CRC-32 as it reads
        if (info.external_attr >> 16) & stat.S_IXUSR:
            os.chmod(target, os.stat(target).st_mode | 0o111)
        written += 1

    print(f"Extracted {written} files to {dest}")


if __name__ == "__main__":
    main(sys.argv)
