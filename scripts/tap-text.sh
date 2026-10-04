#!/usr/bin/env bash
# Taps the on-screen element whose text or content description matches exactly (any app, any
# language), instead of fixed coordinates that break when the layout or locale changes.
# Usage: scripts/tap-text.sh "<text>" [wait-seconds-after, default 4]
set -euo pipefail
source "$(dirname "$0")/env.sh"
text="${1:?usage: tap-text.sh <text> [wait]}"
"$ADB" shell uiautomator dump /sdcard/ui.xml >/dev/null
bounds="$("$ADB" shell cat /sdcard/ui.xml | python3 -c '
import html, re, sys
xml, target = sys.stdin.read(), sys.argv[1]
for node in re.findall(r"<node [^>]*>", xml):
    attrs = {k: html.unescape(v) for k, v in re.findall(r"(\w[\w-]*)=\"([^\"]*)\"", node)}
    if target in (attrs.get("text"), attrs.get("content-desc")):
        print(attrs["bounds"]); break
' "$text")"
[ -n "$bounds" ] || { echo "tap-text: \"$text\" not found on screen" >&2; exit 1; }
read -r x1 y1 x2 y2 <<<"$(echo "$bounds" | tr -c '0-9' ' ')"
"$ADB" shell input tap $(( (x1 + x2) / 2 )) $(( (y1 + y2) / 2 ))
sleep "${2:-4}"
