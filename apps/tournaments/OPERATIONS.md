# Last one standing? — Operations

Run from the **repo root** after `source scripts/env.sh`. Repo-wide commands and scripts:
[docs/operations.md](../../docs/operations.md).

Module paths: app `:apps:tournaments:app`, logic `:apps:tournaments:domain`.
Package: `com.jorgelillo.tournaments` (minSdk 24: navigation-compose needs it).

## Develop

| Task | Command |
|---|---|
| Unit tests | `./gradlew :apps:tournaments:domain:test` |
| Lint | `./gradlew :apps:tournaments:app:lintDebug` |
| Install debug | `./gradlew :apps:tournaments:app:installDebug` |
| Smoke test (release) | `apps/tournaments/tools/smoke_test.sh` |
| Regenerate icons | `.venv/bin/python apps/tournaments/tools/generate_icon.py` |
| Import a shared link on the emulator | `adb shell "am start -a android.intent.action.VIEW -d '<link>' com.jorgelillo.tournaments"` |

UI test tags (`scripts/tap-text.sh`, dialogs included): `new`, `import`, `import_text`,
`import_ok`, `stats`, `about`, `tournament_<id>`, `name`, `game`, `date`, `date_ok`,
`format_0|1`, `bo_0|1|2`, `cut_0..3`, `player`, `add_player`, `start`, `share`, `menu`,
`redraw_menu`, `export_image`, `delete`, `delete_ok`, `tab_matches|bracket|standings`,
`match_<stage>_<round>_<slot>` (stage `elimination` or `swiss`; round-1 slots follow the seed
order 1-8, 4-5, 2-7, 3-6…), `card_<round>_<slot>`, `winner_a|b`, `draw`, `score_<loser games>`,
`comment`, `preview`, `save`, `clear`, `next_round`, `top_cut`, `champion`, `bracket`,
`zoom_in|out|fit`, `standings`, `qr`, `send_link`, `share_image`, `leaderboard`, `back`.

## Share links (App Links)

`https://jorgelillo7.github.io/t/#<code>` carries the tournament: compact JSON → raw deflate →
base64url, prefixed with the format version `1` (`domain/Share.kt`). 64 players with comments
are ~1.2 KB; above `Share.QR_MAX_CHARS` the QR is hidden and only the link is shared.

Opening the link in the app needs `jorgelillo7.github.io/.well-known/assetlinks.json` to list
`com.jorgelillo.tournaments` with the SHA-256 of every signing certificate (debug key, upload key,
Play app signing key), like group-polls. Until then: share to the app, or Import → paste.

## Release history

| versionCode | Version | Notes |
|---|---|---|
| — | 1.0 | Not uploaded yet |
