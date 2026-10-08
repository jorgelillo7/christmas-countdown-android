#!/usr/bin/env bash
# Installs the release APK on the running emulator and checks the core flow: open a wheel, spin,
# accept the result, history, create a wheel. Run it before every Play upload (R8 only affects
# release builds). Taps go by test tag (testTagsAsResourceId), so any language works.
#
# Usage: apps/decision-wheel/tools/smoke_test.sh
set -euo pipefail
source "$(dirname "$0")/../../../scripts/smoke_lib.sh"
smoke_start decision-wheel com.jorgelillo.decisionwheel

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

smoke_finish
