<div align="center">

# 📱 Lillo Android Apps

**A home for small, polished Android apps — built with Kotlin, Jetpack Compose and shared building blocks.**

![Kotlin](https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![AGP](https://img.shields.io/badge/AGP-8.13-02303A?logo=gradle&logoColor=white)
![Target SDK](https://img.shields.io/badge/targetSdk-36-3DDC84?logo=android&logoColor=white)

</div>

---

## 🧩 Apps

| App | Status | Folder |
|---|---|---|
| 🎄 [Christmas Countdown](apps/christmas-countdown) | [On Google Play](https://play.google.com/store/apps/details?id=com.jorgelillo.christmascountdown) | `apps/christmas-countdown` |

## 🗂️ Repository layout

```
build-logic/                       Gradle convention plugins: shared Android/Kotlin/Compose config
gradle/libs.versions.toml          Single source of truth for every dependency version
core/
└── designsystem/                  :core:designsystem — theme and Compose components shared by all apps
apps/
└── christmas-countdown/
    ├── app/                       :apps:christmas-countdown:app — Android app (UI, widget, resources)
    ├── domain/                    :apps:christmas-countdown:domain — pure Kotlin business logic
    ├── store/                     Play Store assets
    └── tools/                     App-specific scripts (e.g. icon generator)
```

### Principles

- **Each app is a self-contained folder** under `apps/`, with its own `app` and `domain` modules.
- **Domain modules are pure Kotlin** (no `android.*`): fast to test and easy to move to another
  build system later.
- **Shared code lives in `core/`, but only once a second app needs it.** No empty modules "just
  in case".
- **Configuration is written once** in `build-logic` convention plugins and the version catalog.

## 🚀 Building

Requirements: Android Studio 2025.2+ (bundled JDK 21) and Android SDK 36.

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

./gradlew test                                             # unit tests for every module
./gradlew :apps:christmas-countdown:app:installDebug       # run on a device or emulator
./gradlew :apps:christmas-countdown:app:bundleRelease      # Play Store bundle
```

## ➕ Adding a new app (e.g. a roulette)

1. Create `apps/roulette/domain/build.gradle.kts`:
   ```kotlin
   plugins { alias(libs.plugins.jorgelillo.jvm.library) }
   ```
2. Create `apps/roulette/app/build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.jorgelillo.android.application)
       alias(libs.plugins.jorgelillo.android.compose)
   }
   android {
       namespace = "com.jorgelillo.roulette"
       defaultConfig { applicationId = "com.jorgelillo.roulette"; versionCode = 1; versionName = "1.0" }
   }
   dependencies {
       implementation(projects.apps.roulette.domain)
       implementation(projects.core.designsystem)
   }
   ```
3. Register both modules in `settings.gradle.kts`.
4. Wrap the UI in `LilloTheme(colorScheme = …)` with the app's own palette.

SDK levels, Java version, desugaring and Compose setup come from the convention plugins, so a
new app needs no extra Gradle configuration.

## 🔐 Signing

Each app reads its release signing values from a git-ignored
`apps/<app>/keystore.properties`:

```properties
storeFile=/absolute/path/to/upload-key.jks
storePassword=...
keyAlias=...
keyPassword=...
```

## 🧭 Roadmap

- **Now:** one repo, Gradle, more apps added as folders under `apps/`.
- **Later:** possible move into a larger monorepo. The pure Kotlin domain modules are the parts
  designed to migrate first.
