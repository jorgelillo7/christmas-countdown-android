#!/usr/bin/env bash
# Taps the on-screen element whose text, content description or test tag matches exactly (any
# app, any language), instead of fixed coordinates that break when the layout or locale changes.
# Compose test tags show up as resource ids when the app sets testTagsAsResourceId = true.
# Usage: scripts/tap-text.sh "<text or tag>" [wait-seconds-after, default 4]
set -euo pipefail
source "$(dirname "$0")/env.sh"
text="${1:?usage: tap-text.sh <text> [wait]}"
# The UI dump fails while the app is starting or animating: drop stale dumps and retry.
find_bounds() {
  "$ADB" shell rm -f /sdcard/ui.xml
  "$ADB" shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1 || return 0
  "$ADB" shell cat /sdcard/ui.xml 2>/dev/null | python3 -c '
import html, re, sys
xml, target = sys.stdin.read(), sys.argv[1]
for node in re.findall(r"<node [^>]*>", xml):
    attrs = {k: html.unescape(v) for k, v in re.findall(r"(\w[\w-]*)=\"([^\"]*)\"", node)}
    rid = attrs.get("resource-id", "")
    if target in (attrs.get("text"), attrs.get("content-desc"), rid, rid.split("/")[-1]):
        print(attrs["bounds"]); break
' "$text"
}
bounds=""
for _ in 1 2 3 4 5; do
  bounds="$(find_bounds)"
  [ -n "$bounds" ] && break
  sleep 2
done
[ -n "$bounds" ] || { echo "tap-text: \"$text\" not found on screen" >&2; exit 1; }
read -r x1 y1 x2 y2 <<<"$(echo "$bounds" | tr -c '0-9' ' ')"
"$ADB" shell input tap $(( (x1 + x2) / 2 )) $(( (y1 + y2) / 2 ))
sleep "${2:-4}"
