#!/bin/sh
set -e

JAVA_HOME="/Applications/Apache NetBeans.app/Contents/Home"
export JAVA_HOME
export PATH="${JAVA_HOME}/bin:${PATH}"

APP_NAME="MySnow-2026"
BASE_APP="dist/${APP_NAME}.app"
ARM_APP="dist/${APP_NAME}-arm64.app"

# GraalVM JDK used for jlink runtime creation (must include jmods/)
JLINK_JDK_ARM64="${JLINK_JDK_ARM64:-/Library/Java/JavaVirtualMachines/graalvm-25.jdk/Contents/Home}"

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
