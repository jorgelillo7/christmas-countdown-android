# What next? (decision wheel) — Operations

Run from the **repo root** after `source scripts/env.sh`. Repo-wide commands and scripts:
[docs/operations.md](../../docs/operations.md).

Module paths: app `:apps:decision-wheel:app`, logic `:apps:decision-wheel:domain`.
Package: `com.jorgelillo.decisionwheel` (minSdk 24: navigation-compose needs it).

## Develop

| Task | Command |
|---|---|
| Unit tests | `./gradlew :apps:decision-wheel:domain:test` |
| Lint | `./gradlew :apps:decision-wheel:app:lintDebug` |
| Install debug | `./gradlew :apps:decision-wheel:app:installDebug` |
| Launch | `$ADB shell am start -n com.jorgelillo.decisionwheel/.MainActivity` |
| Reset to presets (clears wheels and history) | `$ADB shell pm clear com.jorgelillo.decisionwheel` |
| Run in Spanish | `$ADB shell cmd locale set-app-locales com.jorgelillo.decisionwheel --locales es-ES` |
| Smoke test (release build, emulator running) | `apps/decision-wheel/tools/smoke_test.sh` |
| Regenerate icons | `.venv/bin/python apps/decision-wheel/tools/generate_icon.py` |

UI test tags (usable with `scripts/tap-text.sh`): `new_wheel`, `more`, `spin`, `accept`, `again`,
`history`, `edit`, `back`, `name`, `new_option`, `save`.

## Release

Not published yet. Before the first release:

- Create an **upload key** for this app (Android Studio → Generate Signed Bundle → Create new),
  back it up off the Mac, and create `apps/decision-wheel/keystore.properties` (git-ignored).
  Until then release builds are signed with the debug key so they can be smoke tested; Play
  rejects them.
- Store listing texts and images in `store/metadata/android/<locale>/`.
- Then follow the `release-android-app` skill / [docs/publishing-on-google-play.md](../../docs/publishing-on-google-play.md).
