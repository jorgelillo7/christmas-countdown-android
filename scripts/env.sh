#!/usr/bin/env bash
# Shared environment for the scripts and for running ./gradlew from a terminal.
# Usage: source scripts/env.sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export ADB="$ANDROID_HOME/platform-tools/adb"
export EMULATOR="$ANDROID_HOME/emulator/emulator"
