#!/usr/bin/env bash
# Captures the raw Play Store screenshots (store/raw/<locale>/) for every locale.
# Needs a running emulator with the app installed. Taps go by on-screen text (scripts/tap-text.sh).
# Then run: .venv/bin/python apps/christmas-countdown/tools/generate_store_assets.py
#
# Usage: apps/christmas-countdown/tools/capture_store_screenshots.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.christmascountdown
RAW="$ROOT/apps/christmas-countdown/store/raw"

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
back() { "$ADB" shell input keyevent KEYCODE_BACK; sleep 2; }
shot() { "$ROOT/scripts/screenshot.sh" "$RAW/$1/$2.png"; }

# On-screen labels per locale (taps go by text, so layout differences between languages don't matter).
label() {
  case "$1:$2" in
    en-US:time) echo "Time" ;;        es-ES:time) echo "Tiempo" ;;
    en-US:sleeps) echo "Sleeps" ;;    es-ES:sleeps) echo "Noches" ;;
    en-US:advent) echo "Advent" ;;    es-ES:advent) echo "Adviento" ;;
    en-US:home) echo "Countdown" ;;   es-ES:home) echo "Cuenta atrás" ;;
    en-US:about) echo "About" ;;      es-ES:about) echo "Información" ;;
  esac
}

"$ROOT/scripts/emulator.sh" date 121210002026   # 12 December: advent calendar half open
"$ROOT/scripts/emulator.sh" demo on
"$ADB" shell pm clear "$PKG" >/dev/null

first=1
for locale in en-US es-ES; do
  "$ADB" shell cmd locale set-app-locales "$PKG" --locales "$locale"
  "$ADB" shell am force-stop "$PKG"
  "$ADB" shell am start -n "$PKG/.MainActivity" >/dev/null
  sleep 30

  tap "$(label $locale time)";   shot "$locale" 01-countdown
  tap "$(label $locale sleeps)"; shot "$locale" 02-sleeps
  tap "$(label $locale advent)" 6
  if [ "$first" = 1 ]; then
    # Open the unlocked doors visible on screen (the layout changes every year) so the grid shows
    # some gifts; door 2 stays closed for the "door" screenshot and door 12 (today) too.
    for door in 1 3 4 5 6 7 8 9 10 11; do
      if tap "$door" 4 2>/dev/null; then back; fi
    done
    first=0
  fi
  shot "$locale" 03-advent
  if [ "$locale" = en-US ]; then tap "2" 5; else tap "🎁" 5; fi   # door 2: closed in en-US, opened afterwards
  shot "$locale" 04-door; back
  tap "$(label $locale about)" 6; shot "$locale" 05-about; back
  tap "$(label $locale home)"
done

"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
"$ROOT/scripts/emulator.sh" demo off
"$ROOT/scripts/emulator.sh" date auto
echo "Raw screenshots in $RAW. Check them before generating the store graphics."
