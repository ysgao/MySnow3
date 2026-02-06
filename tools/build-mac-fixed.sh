#!/bin/sh
set -e

JAVA_HOME="/Applications/Apache NetBeans.app/Contents/Home"
export JAVA_HOME
export PATH="${JAVA_HOME}/bin:${PATH}"

exec ant build-mac-fixed "$@"
