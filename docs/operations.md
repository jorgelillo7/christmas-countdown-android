# Operations — repo-wide runbook

Commands shared by every app. Per-app commands (module paths, release steps, signing) live in
`apps/<app>/OPERATIONS.md`; release history in `apps/<app>/release-notes.md`.

## Prerequisites

- Android Studio 2026.2+ with Android SDK 37 (see [setup/toolchain.md](setup/toolchain.md)).
- Every terminal session: `source scripts/env.sh` (Android Studio's JDK, `ADB`, `ANDROID_HOME`).
- Python tools (icons, store graphics): `scripts/setup-python.sh` once, then `.venv/bin/python <script>`.

## Build and test

```bash
./gradlew test                                   # every unit test
./gradlew :apps:<app>:domain:test --tests '*SomeTest*'
./gradlew :apps:<app>:app:lintDebug
./gradlew :apps:<app>:app:installDebug
./gradlew :apps:<app>:app:bundleRelease          # signed if apps/<app>/keystore.properties exists
```

## Shared scripts (`scripts/`)

| Script | Use |
|---|---|
| `env.sh` | `source` it: JDK, adb and SDK paths |
| `emulator.sh start\|stop` | Boot (default `Pixel_9_API_36`) / shut down the emulator |
| `emulator.sh date MMDDhhmmYYYY\|auto` | Freeze the emulator clock (e.g. to test December) / restore it |
| `emulator.sh demo on\|off` | Clean status bar for screenshots |
| `screenshot.sh <out.png>` | Screenshot, dismissing "isn't responding" dialogs first |
| `tap-text.sh "<label or tag>"` | Tap an element by its on-screen text or Compose test tag (any language, any layout) |
| `verify-bundle.sh <aab>` | Signature, versionCode, permissions and native libs of a bundle |
| `setup-python.sh` | Create `.venv` with Pillow/numpy |

## Release (any app)

1. Bump `versionCode`/`versionName`, write `store/metadata/android/<locale>/changelogs/<versionCode>.txt`,
   add a row to `apps/<app>/release-notes.md`.
2. `./gradlew test :apps:<app>:app:lintDebug :apps:<app>:app:bundleRelease`
3. `scripts/emulator.sh start && apps/<app>/tools/smoke_test.sh && scripts/emulator.sh stop`
   (release build: R8 issues only show up here).
4. `scripts/verify-bundle.sh apps/<app>/app/build/outputs/bundle/release/app-release.aab`
5. Play Console: [publishing-on-google-play.md](publishing-on-google-play.md#6-release).

The `release-android-app` skill walks through all of it.

## Machine limits

8 GB RAM: emulator + Android Studio + Gradle don't fit together. Close Chrome before emulator
work, stop the emulator during long builds, run emulator scripts in the foreground.
