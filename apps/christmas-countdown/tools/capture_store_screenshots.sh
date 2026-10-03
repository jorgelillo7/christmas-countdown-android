#!/usr/bin/env bash
# Captures the raw Play Store screenshots (store/raw/<locale>/) for every locale.
# Needs a running emulator with the app installed (1080x2424 Pixel 9; tap coordinates assume it).
# Then run: .venv/bin/python apps/christmas-countdown/tools/generate_store_assets.py
#
# Usage: apps/christmas-countdown/tools/capture_store_screenshots.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.christmascountdown
RAW="$ROOT/apps/christmas-countdown/store/raw"

tap() { "$ADB" shell input tap "$1" "$2"; sleep "${3:-4}"; }
back() { "$ADB" shell input keyevent KEYCODE_BACK; sleep 2; }
shot() { "$ROOT/scripts/screenshot.sh" "$RAW/$1/$2.png"; }

"$ROOT/scripts/emulator.sh" date 121210002026   # 12 December: advent calendar half open
"$ROOT/scripts/emulator.sh" demo on
"$ADB" shell pm clear "$PKG" >/dev/null

first=1
for locale in en-US es-ES; do
  "$ADB" shell cmd locale set-app-locales "$PKG" --locales "$locale"
  "$ADB" shell am force-stop "$PKG"
  "$ADB" shell am start -n "$PKG/.MainActivity" >/dev/null
  sleep 30

  tap 256 1713; shot "$locale" 01-countdown     # Time mode
  tap 746 1713; shot "$locale" 02-sleeps        # Sleeps mode
  tap 813 2262 6                                # Advent tab
  if [ "$first" = 1 ]; then
    # Open ten unlocked doors (positions of the 2026 layout) so the grid shows some gifts.
    for xy in "159 1647" "920 758" "159 1353" "412 1944" "412 758" "920 1647" "668 1353" "412 1055" "668 1647" "920 1055"; do
      tap $xy; back
    done
    first=0
  fi
  shot "$locale" 03-advent
  tap 920 758 5; shot "$locale" 04-door; back   # An opened door's dialog
  tap 1004 226 6; shot "$locale" 05-about; back # About sheet
  tap 280 2262                                  # Back to the Countdown tab
done

"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
"$ROOT/scripts/emulator.sh" demo off
"$ROOT/scripts/emulator.sh" date auto
echo "Raw screenshots in $RAW. Door positions change every year: check 03/04 before using them."
