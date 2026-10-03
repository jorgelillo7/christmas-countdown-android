<div align="center">

# 🎄 Christmas Countdown

**Days, hours, minutes and seconds until Christmas — ticking live on your phone.**

![Android](https://img.shields.io/badge/Android-6.0%2B-3DDC84?logo=android&logoColor=white)
![Target SDK](https://img.shields.io/badge/targetSdk-36%20(Android%2016)-blue)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![AGP](https://img.shields.io/badge/AGP-8.13-02303A?logo=gradle&logoColor=white)
![Google Play](https://img.shields.io/badge/Google%20Play-available-414141?logo=googleplay&logoColor=white)

</div>

---

## ✨ Features

- ⏱️ **Live countdown** to the next 25th of December, updated every second
- 🔁 **Never goes stale** — on Christmas Day it wishes you *Merry Christmas*,
  and from the 26th it starts counting towards next year
- 🌍 **English & Spanish** (*¡Quedan solo… para Navidad!*)
- 📱 **Edge-to-edge** full-screen design, ready for Android 16
- 🪶 **Tiny**: no internet permission, no data collected, ~1 MB bundle

## 🚀 Getting started

**Requirements:** Android Studio 2025.2+ (bundled JDK 21) and Android SDK 36.

```bash
git clone <this repo>
cd ChristmasCountdown
```

Open the folder in Android Studio, let Gradle sync, pick an emulator and press **Run ▶**.

From the command line (uses Android Studio's JDK):

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

./gradlew testDebugUnitTest   # run the unit tests
./gradlew installDebug        # install on a running emulator/device
./gradlew bundleRelease       # build the Play Store bundle (.aab)
```

## 🗂️ Project structure

```
app/src/main/java/com/jorgelillo/christmascountdown/
├── MainActivity.java        # UI: renders the countdown once per second
└── ChristmasCountdown.java  # Pure date logic (no Android types), unit tested
app/src/main/res/
├── layout/activity_main.xml
├── values/        # English strings, colours, styles
└── values-es/     # Spanish strings
```

## 🔐 Release signing

Release builds are signed using a git-ignored `keystore.properties` in the
project root:

```properties
storeFile=/absolute/path/to/upload-key.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Never commit it, nor the keystore.

## 📦 Publishing

See **[PLAY_STORE_RELAUNCH.md](PLAY_STORE_RELAUNCH.md)** for the step-by-step
checklist to publish a new version on Google Play.

## 🙏 Credits

Originally based on the
[android-coffee.com Christmas countdown tutorial](https://android-coffee.com/tutorial-how-many-days-left-until-christmas-app-in-android-studio-1-4/),
modernised in 2026.

<div align="center">

Made with ❤️ and ☕ by **Jorge Lillo** · 🎅 *Ho ho ho!*

</div>
