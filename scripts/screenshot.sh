#!/usr/bin/env bash
# Saves a screenshot of the connected device, dismissing "isn't responding" system dialogs first
# (the emulator shows them often while it is busy).
# Usage: scripts/screenshot.sh <output.png>
set -euo pipefail
source "$(dirname "$0")/env.sh"
out="${1:?usage: screenshot.sh <output.png>}"
for _ in 1 2 3; do
  "$ADB" shell dumpsys window | grep -q 'Application Not Responding' || break
  "$ADB" shell input tap 320 1381   # "Wait" button on a 1080x2424 screen
  sleep 4
done
sleep 1
mkdir -p "$(dirname "$out")"
"$ADB" exec-out screencap -p > "$out"
echo "Saved $out"
