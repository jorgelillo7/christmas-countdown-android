#!/usr/bin/env bash
# Installs the release APK on the running emulator and checks the core flow: open a wheel, spin,
# accept the result, history, create a wheel. Run it before every Play upload (R8 only affects
# release builds). Taps go by test tag (testTagsAsResourceId), so any language works.
#
# Usage: apps/decision-wheel/tools/smoke_test.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.decisionwheel
OUT="$ROOT/build/smoke-test/decision-wheel"
APK="$ROOT/apps/decision-wheel/app/build/outputs/apk/release/app-release.apk"

(cd "$ROOT" && ./gradlew -q :apps:decision-wheel:app:assembleRelease)
"$ADB" uninstall "$PKG" >/dev/null 2>&1 || true  # a debug build has another signature
"$ADB" install -r "$APK" | tail -1
"$ADB" shell pm clear "$PKG" >/dev/null                         # fresh presets, empty history
"$ADB" shell cmd locale set-app-locales "$PKG" --locales en-US
"$ADB" logcat -c
"$ADB" shell am start -W -n "$PKG/.MainActivity" >/dev/null
sleep 4

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
shot() { "$ROOT/scripts/screenshot.sh" "$OUT/$1.png" >/dev/null; echo "screen: $1"; }

shot home
tap "Where should we eat?" 5; shot wheel
tap spin 1; shot spinning
sleep 7; shot result
tap accept 3
tap history 3; shot history
"$ADB" shell input keyevent KEYCODE_BACK; sleep 2
tap back 3
tap new_wheel 3
tap name 1; "$ADB" shell input text "Smoke"
tap new_option 1
for option in One Two Three; do "$ADB" shell input text "$option"; "$ADB" shell input keyevent KEYCODE_ENTER; sleep 1; done
"$ADB" shell input keyevent KEYCODE_ESCAPE; sleep 1
tap save 5; shot new-wheel

crashes="$("$ADB" logcat -d -b crash | grep -c FATAL || true)"
pid="$("$ADB" shell pidof "$PKG" || true)"
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
echo "crashes: $crashes"
echo "screenshots: $OUT"
[ -n "$pid" ] && [ "$crashes" = 0 ] && echo "SMOKE TEST PASSED" || { echo "SMOKE TEST FAILED"; exit 1; }
