#!/usr/bin/env bash
# Installs the release APK on the running emulator and walks the main paths: name, public feed,
# vote + prediction, results, a new private poll (share sheet), its result with names, closing it
# and settings. Run it before every Play upload (R8 only affects release builds). Taps go by test
# tag, so any language works. Without firebase.properties it runs on the in-memory backend.
#
# Usage: apps/group-polls/tools/smoke_test.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.grouppolls
OUT="$ROOT/build/smoke-test/group-polls"
APK="$ROOT/apps/group-polls/app/build/outputs/apk/release/app-release.apk"

(cd "$ROOT" && ./gradlew -q :apps:group-polls:app:assembleRelease)
"$ADB" install -r "$APK" | tail -1
"$ADB" shell pm clear "$PKG" >/dev/null
"$ADB" shell cmd locale set-app-locales "$PKG" --locales en-US
"$ADB" logcat -c
"$ADB" shell am start -W -n "$PKG/.MainActivity" >/dev/null
sleep 4

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
shot() { "$ROOT/scripts/screenshot.sh" "$OUT/$1.png" >/dev/null; echo "screen: $1"; }
type_in() { tap "$1" 1; "$ADB" shell input text "$2"; }

shot welcome
type_in name_input "Alex"
tap start 3
shot feed
# Open the first poll of the feed by its card (test tags start with poll_).
first="$("$ADB" shell uiautomator dump /sdcard/ui.xml >/dev/null && "$ADB" shell cat /sdcard/ui.xml | grep -o 'resource-id="poll_[A-Za-z0-9]*"' | head -1 | sed 's/resource-id="//;s/"//')"
tap "$first" 2
shot vote
tap vote_red 1
tap predict_blue 2
shot result
tap back 2

tap create 2
type_in question_input "Friday%sdinner"
type_in red_input "Pizza"
type_in blue_input "Sushi"
"$ADB" shell input keyevent KEYCODE_ESCAPE; sleep 1
tap submit 3
shot share
"$ADB" shell input keyevent KEYCODE_BACK; sleep 1
tap vote_blue 1
tap predict_blue 2
shot private_result
tap close_poll 1
tap confirm_close 2
tap back 2
tap tab_mine 2
shot mine
tap settings 2
shot settings

crashes="$("$ADB" logcat -d -b crash | grep -c FATAL || true)"
pid="$("$ADB" shell pidof "$PKG" || true)"
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
echo "crashes: $crashes"
echo "screenshots: $OUT"
[ -n "$pid" ] && [ "$crashes" = 0 ] && echo "SMOKE TEST PASSED" || { echo "SMOKE TEST FAILED"; exit 1; }
