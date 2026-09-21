# MySnow3 Technical Specification

Last updated: 2026-02-06

## Overview
MySnow3 is a NetBeans Platform application for visualizing SNOMED CT terminology. It imports SNOMED CT RF2 data into an embedded Neo4j database and provides:
1. Hierarchical taxonomy browsing (Explorer window).
2. Concept details (FSN, PT/Synonyms, text definitions, modeling relationships, simple map refsets).
3. QuickSearch across multiple SNOMED CT sub-hierarchies.

## Platform & Runtime
1. NetBeans Platform: a build dependency, not a checked-in binary. The build downloads
   it from the official Apache NetBeans distribution on first use — no NetBeans
   installation is required. Release is pinned by `netbeans.version` in
   `nbproject/platform.properties` (currently 31).
2. Java: whichever JDK runs Ant. Set `JAVA_HOME` to choose one; JDK 21 or newer.
   Nothing in the build hardcodes a JDK path.
3. Neo4j: embedded, based on Neo4j Community 5.26.21.

## Repository Structure
1. `ImportSCT_API` — Import API module.
2. `ImportSCT_Impl` — Import implementation (RF2 parsing, DB population).
3. `ImportSCT_UI` — Import UI (select RF2 folder, trigger import).
4. `QuerySCT_API` — Query API module.
5. `QuerySCT_Impl` — Query implementation (graph traversal, search helpers).
6. `QuerySCT_UI` — UI for taxonomy explorer, concept detail, and QuickSearch providers.
7. `Neo4j` — Embedded Neo4j module and runtime jars.
8. `branding` — NetBeans branding assets.
9. `tools` — helper scripts (e.g., build wrappers, DB migration helper).

## Core Data Paths
1. Default Neo4j database location: `~/Library/Application Support/MySnow-2026/neo4j-sct3-store`.
2. Neo4j config: `~/.mysnow3/neo4j.conf`.
3. Config override: set system property `mysnow.neo4j.conf` to a custom file path.
4. Page cache config key: `dbms.pagecache.memory` (MiB or GiB).

## Build & Packaging
1. Standard build: `ant build`.
2. Mac app bundle: `ant build-mac-fixed`.
3. Clean + Mac bundle: `ant build-mac-fixed-clean`.
4. DMG: `hdiutil create -ov -fs HFS+ -volname mysnow3 -srcfolder dist/mysnow3.app dist/mysnow3.dmg`.
5. Wrapper script: `tools/build-mac-fixed.sh` builds the app bundle, jlinks a runtime
   into it, and creates the DMG. It uses `JAVA_HOME`, falling back to
   `/usr/libexec/java_home`; `JLINK_JDK_ARM64` overrides the JDK used for jlink.
6. GraalVM native image (experimental): `tools/build-mac-native.sh`, with `GRAALVM_HOME`
   pointing at a GraalVM installation.

## NetBeans Platform Dependency
1. The platform is fetched by `nbproject/platform.xml`, which `nbproject/build-impl.xml`
   runs before it looks for the build harness. Nothing extra to invoke — any `ant` target
   triggers it if the platform is missing.
2. Source: `https://dlcdn.apache.org/netbeans/netbeans/<version>/netbeans-<version>-bin.zip`,
   falling back to `https://archive.apache.org/dist/...` for superseded releases. Apache
   stopped publishing a platform-only download after release 15, so this full release zip
   is the only official source for a current release.
3. Two ways to get the clusters out of it, selected by `platform.download.mode`:
   - `partial` (default) — `tools/fetch-platform.py` reads the archive's central directory
     over HTTP range requests and pulls down only the byte ranges holding the `platform`
     and `harness` clusters: **about 32 MB instead of ~500 MB**. Each extracted entry is
     CRC-32 checked against the archive's own directory. Needs `python3` (the build prefers
     `/usr/bin/python3`, whose trust store works out of the box).
   - `full` — `ant -Dplatform.download.mode=full` downloads the whole zip and verifies it
     against the SHA-512 Apache publishes, which covers the release as a whole. Use this
     when you want that check; it is also what runs automatically if partial mode cannot.
4. Clusters land in `nbplatform/<version>/`; `full` mode also caches the zip in
   `nbplatform/download/`. Both are git-ignored and sit outside `build/`, so they survive
   `ant clean` and the download happens once per release.
5. To move to another release, change `netbeans.version` in `nbproject/platform.properties`.
   To pre-fetch without building: `ant download-platform`.
6. To build against a local NetBeans installation instead, pass its paths explicitly:
   `ant -Dnbplatform.default.netbeans.dest.dir=<netbeans> -Dnbplatform.default.harness.dir=<netbeans>/harness ...`

## NetBeans Launcher Configuration
1. Launcher config file: `dist/mysnow3.app/Contents/Resources/mysnow3/etc/mysnow3.conf`.
2. Target `fix-jdkhome` points `jdkhome` at the JDK that ran the build (`nbjdk.home`,
   defaulting to the Ant JVM). `tools/build-mac-fixed.sh` then rewrites it to `"jre"`
   in the arm64 bundle, which carries its own jlink runtime.

## Neo4j Integration
1. Embedded DB uses `DatabaseManagementService` (Neo4j 5.x API).
2. `EmbeddedDbManager` encapsulates lifecycle and configuration.
3. `BatchInserterCompat` provides batched writes via transactions.
4. Neo4j runtime jars are embedded in `Neo4j/release/modules/ext`.

## Import Pipeline (RF2 Snapshot)
1. Concepts: `sct2_Concept_Snapshot_INT_*.txt`.
2. Descriptions: `sct2_Description_Snapshot-en_INT_*.txt`.
3. Text Definitions: `sct2_TextDefinition_Snapshot-en_INT_*.txt`.
4. Language refset: `der2_cRefset_LanguageSnapshot-en_INT_*.txt`.
5. Relationships: `sct2_Relationship_Snapshot_INT_*.txt`.
6. Simple Map refset: `der2_sRefset_SimpleMapSnapshot_INT_*.txt`.
7. Concrete Values relationships are detected and skipped.

## Import Behavior
1. Import scans the selected folder recursively to locate required RF2 files.
2. Concept import initializes the database and index on `Concept.sctid`.
3. Description import creates `Has_description` relationships and sets concept FSNs.
4. Language refset import tags descriptions with GB/US flags and relationships.
5. Relationship import creates ISA and non-ISA relationships. Relationship names are derived from attribute FSNs and fall back to `RelType_<id>` if required.
6. Simple map import creates `Has_simple_map` relationships to map nodes.

## Query & UI Behavior
1. Explorer window shows the taxonomy tree rooted at `138875005` (SNOMED CT concept).
2. Concept details panel renders:
   - FSN, SCTID, status, primitive/defined flag.
   - Parent/child/descendant counts.
   - PT/Synonyms, text definitions, simple maps.
   - Modeling relationships (non-ISA).
3. QuickSearch providers target specific sub-hierarchies by root SCTID.
4. Search result actions resolve node by SCTID at click time to ensure transaction safety.

## Diagnostics
1. Concept detail selection shows a relationship summary in the status bar:
   `Relationships: ISA=<n> non-ISA=<n> descriptions=<n>`.

## Key Custom Classes
1. `EmbeddedDbManager` — Neo4j lifecycle and configuration.
2. `Neo4jConfig` — reads config from `~/.mysnow3/neo4j.conf`.
3. `BatchInserterCompat` — batched insert helper.
4. `ExplorerTopComponent` — taxonomy tree.
5. `ConceptDetailTopComponent` — concept details UI.

## Operational Notes
1. RF2 import order matters: Concepts → Descriptions/Language → Relationships.
2. Re-importing relationships is required after importer changes that affect relationship creation.
3. App uses JDK 25; running under older JDKs will prevent module loading.

## Known Build Warnings
1. `dangling-doc-comments` warnings in legacy source files.
2. `unchecked` warnings in some UI collections/comparators.
3. Missing `app_w.exe` warnings on macOS during packaging.
These do not block the build.

## Compatibility Constraints
1. UI layout is preserved (no redesign).
2. Neo4j schema is unchanged.
3. APIs remain compatible with existing modules.
