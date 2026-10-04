#!/usr/bin/env bash
# Creates a local virtualenv (.venv, git-ignored) with the packages the asset generators need.
# Usage: scripts/setup-python.sh   then run tools with .venv/bin/python <script>
set -euo pipefail
cd "$(dirname "$0")/.."
python3 -m venv .venv
.venv/bin/pip3 install --quiet --upgrade pip
.venv/bin/pip3 install --quiet -r scripts/requirements.txt
echo "Ready: use .venv/bin/python <script>"
