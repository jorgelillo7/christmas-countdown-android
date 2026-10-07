#!/usr/bin/env bash
# Installs the release APK on the running emulator and checks it starts, shows every screen,
# plays music and does not crash. Run it before every Play upload: R8 only affects release builds.
#
# Usage: apps/christmas-countdown/tools/smoke_test.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.christmascountdown
OUT="$ROOT/build/smoke-test"
APK="$ROOT/apps/christmas-countdown/app/build/outputs/apk/release/app-release.apk"

(cd "$ROOT" && ./gradlew -q :apps:christmas-countdown:app:assembleRelease)
"$ADB" uninstall "$PKG" >/dev/null 2>&1 || true  # a debug build has another signature
"$ADB" install -r "$APK" | tail -1
"$ADB" shell cmd locale set-app-locales "$PKG" --locales en-US   # labels below are English
"$ADB" logcat -c
"$ADB" shell am force-stop "$PKG"
"$ADB" shell am start -n "$PKG/.MainActivity" >/dev/null
sleep 12

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
shot() { "$ROOT/scripts/screenshot.sh" "$OUT/$1.png" >/dev/null; echo "screen: $1"; }
tap "Time"; shot countdown
tap "Sleeps"; shot sleeps
tap "Advent" 5; shot advent
tap "About" 5; shot about
"$ADB" shell input keyevent KEYCODE_BACK; sleep 2
tap "Countdown"
tap "Play carols" 6                                    # music on
pid="$("$ADB" shell pidof "$PKG" || true)"
audio="$("$ADB" shell dumpsys audio | grep "/$pid " | grep -c 'AudioTrack.*state:started' || true)"
tap "Stop carols" 2                                    # music off
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
crashes="$("$ADB" logcat -d -b crash | grep -c FATAL || true)"

echo "music playing: $([ "$audio" -gt 0 ] && echo yes || echo NO)"
echo "crashes: $crashes"
echo "screenshots: $OUT"
[ -n "$pid" ] && [ "$crashes" = 0 ] && [ "$audio" -gt 0 ] && echo "SMOKE TEST PASSED" || { echo "SMOKE TEST FAILED"; exit 1; }
