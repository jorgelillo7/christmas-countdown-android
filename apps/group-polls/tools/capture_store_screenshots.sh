#!/usr/bin/env bash
# Captures the raw Play Store screenshots (store/raw/<locale>/) for every locale, on the
# in-memory backend (sample polls; build without firebase.properties).
# Needs a running emulator. Taps go by test tag (scripts/tap-text.sh).
# Then run: .venv/bin/python apps/group-polls/tools/generate_store_assets.py
#
# Usage: apps/group-polls/tools/capture_store_screenshots.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.grouppolls
RAW="$ROOT/apps/group-polls/store/raw"
PRIVATE_LINK="https://jorgelillo7.github.io/q/?c=DemPrivateX2" # FakePollRepository.SAMPLE_PRIVATE

(cd "$ROOT" && ./gradlew -q :apps:group-polls:app:installDebug)

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
shot() { "$ROOT/scripts/screenshot.sh" "$RAW/$1/$2.png"; }
first_poll() {
  "$ADB" shell uiautomator dump /sdcard/ui.xml >/dev/null
  "$ADB" shell cat /sdcard/ui.xml | grep -o 'resource-id="poll_[A-Za-z0-9]*"' | head -1 | sed 's/resource-id="//;s/"//'
}

"$ROOT/scripts/emulator.sh" demo on
for locale in ${LOCALES:-en-US es-ES}; do
  case "$locale" in
    en-US) NAME=Alex; Q="Netflix%sor%sa%sboard%sgame?"; R=Netflix; B="Board%sgame" ;;
    es-ES) NAME=Pablo; Q="Peli%so%sjuego%sde%smesa"; R=Peli; B="Juego%sde%smesa" ;;
  esac
  "$ADB" shell pm clear "$PKG" >/dev/null
  "$ADB" shell cmd locale set-app-locales "$PKG" --locales "$locale"
  "$ADB" shell am start -W -n "$PKG/.MainActivity" >/dev/null
  sleep 3
  tap name_input 1; "$ADB" shell input text "$NAME"; tap start 3
  shot "$locale" 01-feed

  tap "$(first_poll)" 2
  shot "$locale" 02-vote
  tap vote_red 1
  shot "$locale" 03-predict
  tap predict_red 3
  shot "$locale" 04-result
  tap back 2

  "$ADB" shell am start -W -a android.intent.action.VIEW -d "$PRIVATE_LINK" "$PKG" >/dev/null
  sleep 2
  tap vote_blue 1; tap predict_red 3
  shot "$locale" 05-private
  tap back 2

  tap create 2
  tap question_input 1; "$ADB" shell input text "$Q"
  tap red_input 1; "$ADB" shell input text "$R"
  tap blue_input 1; "$ADB" shell input text "$B"
  "$ADB" shell input keyevent KEYCODE_ESCAPE; sleep 1
  shot "$locale" 06-create
done
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
"$ROOT/scripts/emulator.sh" demo off
echo "Raw screenshots in $RAW. Check them before generating the store graphics."
