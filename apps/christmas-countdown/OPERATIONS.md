# Christmas Countdown — Operations

Every command we use for this app. Run them from the **repo root** after:

```bash
source scripts/env.sh          # Android Studio's JDK, adb, SDK paths
```

Module paths: app `:apps:christmas-countdown:app`, logic `:apps:christmas-countdown:domain`.

## Develop

| Task | Command |
|---|---|
| Unit tests (domain logic) | `./gradlew :apps:christmas-countdown:domain:test` |
| All tests in the repo | `./gradlew test` |
| Lint | `./gradlew :apps:christmas-countdown:app:lintDebug` → `apps/christmas-countdown/app/build/reports/lint-results-debug.html` |
| Debug APK | `./gradlew :apps:christmas-countdown:app:assembleDebug` |
| Install debug on emulator/phone | `./gradlew :apps:christmas-countdown:app:installDebug` |
| Launch the app | `$ADB shell am start -n com.jorgelillo.christmascountdown/.MainActivity` |
| Clear app data (advent progress, settings) | `$ADB shell pm clear com.jorgelillo.christmascountdown` |
| Crash log | `$ADB logcat -d -b crash` |

## Emulator

| Task | Command |
|---|---|
| Start (Pixel 9, API 36) | `scripts/emulator.sh start` |
| Stop | `scripts/emulator.sh stop` |
| Pretend it's 5 December (test advent) | `scripts/emulator.sh date 120512002026` |
| Back to real time | `scripts/emulator.sh date auto` |
| Clean status bar for screenshots | `scripts/emulator.sh demo on` / `off` |
| Screenshot | `scripts/screenshot.sh build/shot.png` |
| Tap a button by its label | `scripts/tap-text.sh "Advent"` |
| Run app in Spanish only | `$ADB shell cmd locale set-app-locales com.jorgelillo.christmascountdown --locales es-ES` |

## Release

```bash
# 1. Bump versionCode / versionName in apps/christmas-countdown/app/build.gradle.kts
# 2. Release notes: store/metadata/android/{en-US,es-ES}/changelogs/<versionCode>.txt
# 3. Build, test and verify
./gradlew test :apps:christmas-countdown:app:lintDebug :apps:christmas-countdown:app:bundleRelease
apps/christmas-countdown/tools/smoke_test.sh        # needs a running emulator
scripts/verify-bundle.sh apps/christmas-countdown/app/build/outputs/bundle/release/app-release.aab
# 4. Reveal the bundle in Finder to drag it into Play Console
open -R apps/christmas-countdown/app/build/outputs/bundle/release/app-release.aab
```

Signing needs `apps/christmas-countdown/keystore.properties` (git-ignored). The upload key is
`christmas.jks`, alias `key0`, serial `2a72affc` (kept outside the repo).

Then follow [docs/publishing-on-google-play.md](../../docs/publishing-on-google-play.md#6-release).

## Store assets

```bash
scripts/setup-python.sh                                     # once
.venv/bin/python apps/christmas-countdown/tools/generate_icon.py          # launcher + store icon
apps/christmas-countdown/tools/capture_store_screenshots.sh               # raw captures (emulator running)
.venv/bin/python apps/christmas-countdown/tools/generate_store_assets.py  # framed screenshots + feature graphic
```

## Release history

| versionCode | Version | Date | Notes |
|---|---|---|---|
| 1 | 1.0 | Sep 2021 | Original Java app |
| 2 | 2.0 | Oct 2026 | Relaunch: API 36, countdown no longer stuck in 2021 |
| 3 | 3.0 | Oct 2026 | Kotlin + Compose rewrite: advent, widget, carols, sleeps, new icon |
| 4 | 4.0 | — | 3 more carols in shuffled order, share text matches the mode, toolchain 2026 (AGP 9.4, Gradle 9.8, Kotlin 2.4) |
