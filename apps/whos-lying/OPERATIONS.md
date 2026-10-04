# Who's lying? — Operations

Run from the **repo root** after `source scripts/env.sh`. Repo-wide commands and scripts:
[docs/operations.md](../../docs/operations.md).

Module paths: app `:apps:whos-lying:app`, logic `:apps:whos-lying:domain`.
Package: `com.jorgelillo.whoslying` (minSdk 24: navigation-compose needs it).

## Develop

| Task | Command |
|---|---|
| Unit tests | `./gradlew :apps:whos-lying:domain:test` |
| Lint | `./gradlew :apps:whos-lying:app:lintDebug` |
| Install debug | `./gradlew :apps:whos-lying:app:installDebug` |
| Reset (players, packs, settings) | `$ADB shell pm clear com.jorgelillo.whoslying` |
| Run in Spanish | `$ADB shell cmd locale set-app-locales com.jorgelillo.whoslying --locales es-ES` |
| Smoke test (release build, emulator running) | `apps/whos-lying/tools/smoke_test.sh` |
| Regenerate icons | `.venv/bin/python apps/whos-lying/tools/generate_icon.py` |

UI test tags (usable with `scripts/tap-text.sh`, dialogs included): `play`, `how_to`, `modes`,
`packs`, `about`, `modes_info`, `modes_sheet`, `timer_plus|minus`, `timer`, `unplayed`,
`choose_packs`, `pack_<id>`, `select_all_packs`, `packs_done`, `picker_new_pack`, `settings_sheet`, `language`,
`language_system|es|en`, `share`, `rate`, `privacy`, `player_name`, `add_player`, `continue`, `mode_classic|blind|drifter`, `impostors_plus|minus`,
`hint`, `chaos`, `start`, `tap_to_reveal` (the curtain: swipe it up, e.g. `adb shell input swipe 540 1700 540 700 800`), `secret_word`, `hide`, `go_vote`, `timer`, `debate_out`, `vote_<player>`,
`confirm_vote`, `back_to_debate`, `exposing`, `eliminated`, `keep_playing`, `result_title`,
`rounds_plus|minus`, `rounds`, `timer_value`, `time_up`, `rounds_left`, `see_winner`, `podium_title`, `new_match`,
`drawing`, `canvas`, `ink_0..6`, `undo`, `clear`, `share_drawing`, `scores`, `scores_sheet`, `reset_scores`,
`guess_text`, `guess`, `reveal_all`, `play_again`, `home`, `new_pack`, `pack_name`, `pack_words`,
`save_pack`, `back`.

## Release

Not published yet. Before the first release: upload key (`scripts/new-upload-key.sh whos-lying`),
privacy page `privacy/whos-lying/` on jorgelillo7.github.io, store screenshots. Then the
`release-android-app` skill.
