#!/bin/sh
set -e

# native-image needs a GraalVM JDK. Set GRAALVM_HOME to pick one; otherwise fall
# back to JAVA_HOME (or the macOS default JDK) and check it can actually do it.
if [ -z "$GRAALVM_HOME" ]; then
  if [ -n "$JAVA_HOME" ]; then
    GRAALVM_HOME="$JAVA_HOME"
  elif [ -x /usr/libexec/java_home ]; then
    GRAALVM_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
  fi
fi
if [ -z "$GRAALVM_HOME" ] || [ ! -x "${GRAALVM_HOME}/bin/native-image" ]; then
  echo "ERROR: native-image not found. Set GRAALVM_HOME to a GraalVM installation." >&2
  exit 1
fi
export GRAALVM_HOME
export JAVA_HOME="${JAVA_HOME:-$GRAALVM_HOME}"
export PATH="${GRAALVM_HOME}/bin:${PATH}"

APP_NAME="MySnow-2026"
APP_BUNDLE="dist/${APP_NAME}.app"
APP_RES="${APP_BUNDLE}/Contents/Resources/${APP_NAME}"
NATIVE_OUT="dist/${APP_NAME}-native"

# Build the standard app bundle first (provides classpath and resources)
ant build-mac-fixed "$@"

# Build classpath from all jars in the app resources
CLASSPATH="$(find "${APP_RES}" -type f -name '*.jar' -print | tr '\n' ':' | sed 's/:$//')"
if [ -z "$CLASSPATH" ]; then
  echo "ERROR: No jars found under ${APP_RES}."
  exit 1
fi

mkdir -p "$NATIVE_OUT"

# Best-effort native-image build for NetBeans Platform (experimental)
"${GRAALVM_HOME}/bin/native-image" \
  --no-fallback \
  -H:+UnlockExperimentalVMOptions \
  --initialize-at-run-time=org.apache.logging \
  --initialize-at-run-time=io.netty \
  --initialize-at-run-time=org.neo4j.logging \
  -H:ResourceConfigurationFiles=META-INF/native-image/mysnow/resource-config.json \
  -H:Name="${APP_NAME}" \
  -cp "${CLASSPATH}" \
  org.netbeans.Main

mv "${APP_NAME}" "$NATIVE_OUT/"
