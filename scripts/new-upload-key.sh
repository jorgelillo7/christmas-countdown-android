#!/usr/bin/env bash
# Creates the Google Play upload key for an app, outside the repo, and the git-ignored
# apps/<app>/keystore.properties pointing to it. Run it in your own terminal: the password is read
# without echo and passed to keytool through an environment variable, so it never ends up in shell
# history, the process list or a chat.
#
# Usage: scripts/new-upload-key.sh <app-folder> [keys-dir]
#   app-folder  folder under apps/, e.g. decision-wheel
#   keys-dir    where the .jks goes (default: ~/Projects/documentación/keys/<app-folder>)
#
# See docs/signing.md for the whole process (backup, Play App Signing, lost key).
set -euo pipefail
source "$(dirname "$0")/env.sh"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

app="${1:?usage: new-upload-key.sh <app-folder> [keys-dir]}"
[ -d "$ROOT/apps/$app" ] || { echo "No such app: apps/$app" >&2; exit 1; }
dir="${2:-$HOME/Projects/documentación/keys/$app}"
jks="$dir/upload-$app.jks"
props="$ROOT/apps/$app/keystore.properties"
alias="upload"

[ -e "$jks" ] && { echo "Refusing to overwrite existing key: $jks" >&2; exit 1; }
[ -e "$props" ] && { echo "Refusing to overwrite $props" >&2; exit 1; }
mkdir -p "$dir"

echo "Creating $jks (alias '$alias', RSA 4096, valid ~27 years)."
echo "Pick a strong password (12+ chars) and save it in your password manager NOW."
read -r -s -p "Password: " KEY_PASSWORD; echo
read -r -s -p "Repeat:   " again; echo
[ "$KEY_PASSWORD" = "$again" ] || { echo "Passwords don't match" >&2; exit 1; }
[ ${#KEY_PASSWORD} -ge 12 ] || { echo "Use at least 12 characters" >&2; exit 1; }
export KEY_PASSWORD

"$JAVA_HOME/bin/keytool" -J-Duser.language=en -genkeypair \
  -keystore "$jks" -storetype PKCS12 -storepass:env KEY_PASSWORD -keypass:env KEY_PASSWORD \
  -alias "$alias" -keyalg RSA -keysize 4096 -validity 10000 \
  -dname "CN=Jorge Lillo"

umask 077   # keystore.properties holds the password: readable by you only
cat > "$props" <<EOF
# Upload key for $app. Git-ignored: never commit this file.
storeFile=$jks
storePassword=$KEY_PASSWORD
keyAlias=$alias
keyPassword=$KEY_PASSWORD
EOF

echo
echo "Certificate (copy into apps/$app/OPERATIONS.md, it is not secret):"
"$JAVA_HOME/bin/keytool" -J-Duser.language=en -list -v -keystore "$jks" -alias "$alias" -storepass:env KEY_PASSWORD \
  | grep -E 'Serial number|SHA1:|SHA256:|Valid from'
unset KEY_PASSWORD again
echo
echo "Done. Next:"
echo "  1. Password saved in your password manager? (it is also in $props)"
echo "  2. Back up $jks off this Mac (see docs/signing.md)."
echo "  3. git status must NOT list keystore.properties or the .jks."
