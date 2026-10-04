#!/usr/bin/env bash
# Captures the raw Play Store screenshots (store/raw/<locale>/) for every locale.
# Needs a running emulator with the app installed. Taps go by text or test tag (scripts/tap-text.sh).
# Then run: .venv/bin/python apps/decision-wheel/tools/generate_store_assets.py
#
# Usage: apps/decision-wheel/tools/capture_store_screenshots.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
source "$ROOT/scripts/env.sh"
PKG=com.jorgelillo.decisionwheel
RAW="$ROOT/apps/decision-wheel/store/raw"

tap() { "$ROOT/scripts/tap-text.sh" "$@"; }
shot() { "$ROOT/scripts/screenshot.sh" "$RAW/$1/$2.png"; }

# Wheel to open and two options to veto, per locale (macOS bash 3.2: no associative arrays).
labels() {
  case "$1" in
    en-US) WHEEL="Where should we eat?"; VETO1="Indian"; VETO2="Chinese" ;;
    es-ES) WHEEL="¿Dónde comemos?"; VETO1="Ginos"; VETO2="Lizarran" ;;
  esac
}

"$ROOT/scripts/emulator.sh" demo on
for locale in en-US es-ES; do
  labels "$locale"
  "$ADB" shell pm clear "$PKG" >/dev/null                       # fresh presets, empty history
  "$ADB" shell cmd locale set-app-locales "$PKG" --locales "$locale"
  "$ADB" shell am start -W -n "$PKG/.MainActivity" >/dev/null
  sleep 5

  shot "$locale" 04-home
  tap "$WHEEL" 5
  shot "$locale" 01-wheel
  tap spin 7                                                   # spin lasts ~5 s
  shot "$locale" 02-result
  tap accept 3                                                 # winner now has a smaller slice
  tap "$VETO1" 2
  tap "$VETO2" 3
  shot "$locale" 03-repeats-vetoes
  tap back 3
done
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
"$ROOT/scripts/emulator.sh" demo off
echo "Raw screenshots in $RAW. Check them before generating the store graphics."
