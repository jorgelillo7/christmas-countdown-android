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

# Wheel to open and veto candidates, per locale (macOS bash 3.2: no associative arrays).
# The winner's chip gets a "⏱" suffix, so it is skipped automatically (its exact label no longer
# matches); the first two candidates that do match are vetoed.
labels() {
  case "$1" in
    en-US) WHEEL="Where should we eat?"; VETOES=("Indian" "Chinese" "Thai" "Tacos") ;;
    es-ES) WHEEL="¿Dónde comemos?"; VETOES=("Ginos" "Lizarran" "TGB" "Goiko") ;;
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
  vetoed=0
  for option in "${VETOES[@]}"; do
    [ "$vetoed" -lt 2 ] || break
    if tap "$option" 2 2>/dev/null; then vetoed=$((vetoed + 1)); fi
  done
  sleep 1
  shot "$locale" 03-repeats-vetoes
  tap back 3
done
"$ADB" shell cmd locale set-app-locales "$PKG" --locales ""
"$ROOT/scripts/emulator.sh" demo off
echo "Raw screenshots in $RAW. Check them before generating the store graphics."
