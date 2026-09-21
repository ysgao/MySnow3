#!/bin/sh
set -e

# JDK for the build. Set JAVA_HOME to pick one; otherwise ask macOS for its default.
# The NetBeans Platform itself is downloaded by the build (see nbproject/platform.xml).
if [ -z "$JAVA_HOME" ] && [ -x /usr/libexec/java_home ]; then
  JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
fi
if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "ERROR: No JDK found. Set JAVA_HOME to a JDK 21 or newer installation." >&2
  exit 1
fi
export JAVA_HOME
export PATH="${JAVA_HOME}/bin:${PATH}"

APP_NAME="MySnow-2026"
BASE_APP="dist/${APP_NAME}.app"
ARM_APP="dist/${APP_NAME}-arm64.app"

# JDK used for the jlink runtime bundled into the app (must include jmods/).
JLINK_JDK_ARM64="${JLINK_JDK_ARM64:-$JAVA_HOME}"

ant build-mac-fixed "$@"

rm -rf "$ARM_APP"
cp -R "$BASE_APP" "$ARM_APP"

# validate jlink JDKs
if [ ! -d "$JLINK_JDK_ARM64/jmods" ]; then
  echo "ERROR: JLINK_JDK_ARM64 must point to a full JDK (with jmods). Current: $JLINK_JDK_ARM64"
  exit 1
fi

# build minimal runtimes (java.se) for each arch
rm -rf "$ARM_APP/Contents/Resources/${APP_NAME}/jre"
arch -arm64 "$JLINK_JDK_ARM64/bin/jlink" \
  --add-modules java.se,jdk.unsupported \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=zip-6 \
  --output "$ARM_APP/Contents/Resources/${APP_NAME}/jre"

# point launcher config to bundled runtime
sed -i '' 's@^jdkhome=.*@jdkhome="jre"@' "$ARM_APP/Contents/Resources/${APP_NAME}/etc/${APP_NAME}.conf"

# create DMG file (arm64 only)
hdiutil create -ov -fs HFS+ -volname "${APP_NAME}-arm64" -srcfolder "$ARM_APP" "dist/${APP_NAME}-arm64.dmg"
