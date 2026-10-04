#!/usr/bin/env bash
# Installs the release APK on the running emulator and plays one full game: players, setup, the
# pass-the-phone reveal, countdown, a vote and the result. Run it before every Play upload (R8
# only affects release builds). Taps go by test tag, so any language works.
#
# Usage: apps/whos-lying/tools/smoke_test.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.whoslying
OUT="$ROOT/build/smoke-test/whos-lying"
APK="$ROOT/apps/whos-lying/app/build/outputs/apk/release/app-release.apk"

(cd "$ROOT" && ./gradlew -q :apps:whos-lying:app:assembleRelease)
"$ADB" install -r "$APK" | tail -1
"$ADB" shell pm clear "$PKG" >/dev/null
"$ADB" shell cmd locale set-app-locales "$PKG" --locales en-US
"$ADB" logcat -c
"$ADB" shell am start -W -n "$PKG/.MainActivity" >/dev/null
sleep 4

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
shot() { "$ROOT/scripts/screenshot.sh" "$OUT/$1.png" >/dev/null; echo "screen: $1"; }

shot home
tap play 3
for name in Ana Bea Carlos; do
  tap player_name 1; "$ADB" shell input text "$name"; "$ADB" shell input keyevent KEYCODE_ENTER; sleep 1
done
"$ADB" shell input keyevent KEYCODE_ESCAPE; sleep 1
shot players
tap continue 3
tap mode_classic 1
shot setup
tap start 3
for _ in 1 2 3; do tap tap_to_reveal 1; tap hide 1; done
shot countdown
sleep 5
shot debate
tap go_vote 2
tap vote_Ana 1
shot vote
tap confirm_vote 1
sleep 1; shot expose
sleep 4
tap keep_playing 2 2>/dev/null || true      # only shown when the game goes on
tap reveal_all 3 2>/dev/null || true        # end it if it's still running
sleep 1; shot result

crashes="$("$ADB" logcat -d -b crash | grep -c FATAL || true)"
pid="$("$ADB" shell pidof "$PKG" || true)"
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
echo "crashes: $crashes"
echo "screenshots: $OUT"
[ -n "$pid" ] && [ "$crashes" = 0 ] && echo "SMOKE TEST PASSED" || { echo "SMOKE TEST FAILED"; exit 1; }
