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

## Report-a-word form

"Report this word" (result screen) opens a Google Form **in the browser**, prefilled with the word,
the impostor's word, the pack, the language, the reasons ticked in the app and the comment. The
player only taps *Enviar*. The app itself never goes online.

| What | Where |
|---|---|
| Public form (what players see) | https://docs.google.com/forms/d/e/1FAIpQLSf36XFjNcuWZLrEj4Msc01SdGrY8a-2GlxUy_DqQ-quov1dAQ/viewform |
| Form, responses sheet, Apps Script project | Jorge's Google Drive: "¿Quién miente? · Reportar palabra", "¿Quién miente? · Reportes", Apps Script project "Proyecto sin título" (https://script.google.com → *My projects*) |
| Script source | `tools/create_report_form.gs` (the copy in Apps Script is the same code) |
| Form URL and field ids used by the app | `app/src/main/kotlin/com/jorgelillo/whoslying/ui/Report.kt` (`WordReport`) |

**Changing texts** (title, description, thank-you message, question titles): edit the form directly
in Google Forms. Field ids don't change, so the app keeps working.

**Adding or replacing fields, or recreating the form** (the script always creates a *new* form):

1. Edit `tools/create_report_form.gs` here, paste it into the Apps Script project (script.google.com
   → My projects), save and **Run** `createReportForm`.
2. From the execution log, copy the `Prefilled:` link. It holds the new form URL and one
   `entry.<id>=<PLACEHOLDER>` per field (placeholders are just markers; players never see them).
3. Update `FORM_URL` and the `entry.*` constants in `WordReport`, ship a new version.
4. Keep the old form open until most players have updated (old versions still point to it), then
   close it in Forms (*Responses → Accepting responses: off*).

Opening the `Prefilled:` link yourself shows the placeholders (PALABRA, PARECIDA…); that's
expected. The app replaces them with the real values.

