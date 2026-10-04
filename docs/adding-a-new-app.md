# Adding a new app

Example: a roulette app, `com.jorgelillo.roulette`.

## 1. Modules

```
apps/roulette/
├── app/          Android app (Compose UI, resources)
├── domain/       pure Kotlin logic (no android.*), unit tested
├── store/        Play listing texts and images (fastlane layout)
├── tools/        app-specific scripts (icon, screenshots, smoke test)
├── operations.md    commands to run, test and release
├── release-notes.md version history
└── README.md
```

`apps/roulette/domain/build.gradle.kts`
```kotlin
plugins { alias(libs.plugins.jorgelillo.jvm.library) }
dependencies { testImplementation(libs.kotlin.test) }
```

`apps/roulette/app/build.gradle.kts` (copy signing and build types from Christmas Countdown)
```kotlin
plugins {
    alias(libs.plugins.jorgelillo.android.application)
    alias(libs.plugins.jorgelillo.android.compose)
}
android {
    namespace = "com.jorgelillo.roulette"
    defaultConfig {
        applicationId = "com.jorgelillo.roulette"
        versionCode = 1
        versionName = "1.0"
    }
}
dependencies {
    implementation(projects.apps.roulette.domain)
    implementation(projects.core.designsystem)
}
```

Register both in `settings.gradle.kts`:
```kotlin
include(":apps:roulette:app")
include(":apps:roulette:domain")
```

SDK levels, Java target, desugaring and Compose setup come from `build-logic`; nothing else to configure.

## 2. Theme

Wrap the UI in `LilloTheme(colorScheme = RouletteColorScheme) { … }` from `:core:designsystem`.
If a component is useful to a second app, move it into `core/designsystem` then, not before.

## 3. Icon and store assets

- Copy `apps/christmas-countdown/tools/generate_icon.py`, change the geometry and palette.
- Copy `tools/generate_store_assets.py` and `tools/capture_store_screenshots.sh`; update headlines
  and tap coordinates.
- `scripts/setup-python.sh` creates the Python environment once.

## 4. Privacy policy

Add `privacy/roulette/index.html` to `jorgelillo7/jorgelillo7.github.io` and list the app on its
home page. Link it from the app's About screen.

## 5. Publish

Follow [publishing-on-google-play.md](publishing-on-google-play.md), including registering the
package name in Android developer verification.
