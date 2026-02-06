#!/bin/sh
set -e

APP_NAME="MySnow-2026"
BASE_APP="dist/${APP_NAME}.app"
NATIVE_DIR="dist/${APP_NAME}-native"
NATIVE_BIN="${NATIVE_DIR}/${APP_NAME}"
NATIVE_APP="dist/${APP_NAME}-native.app"

if [ ! -x "$NATIVE_BIN" ]; then
  echo "ERROR: Native binary not found: ${NATIVE_BIN}"
  exit 1
fi
if [ ! -d "$BASE_APP" ]; then
  echo "ERROR: Base app not found: ${BASE_APP}"
  exit 1
fi

rm -rf "$NATIVE_APP"
cp -R "$BASE_APP" "$NATIVE_APP"

# Replace launcher with native binary
rm -f "$NATIVE_APP/Contents/MacOS/${APP_NAME}"
cp "$NATIVE_BIN" "$NATIVE_APP/Contents/MacOS/${APP_NAME}"
chmod +x "$NATIVE_APP/Contents/MacOS/${APP_NAME}"

# Create DMG
hdiutil create -ov -fs HFS+ -volname "${APP_NAME}-native" -srcfolder "$NATIVE_APP" "dist/${APP_NAME}-native.dmg"
