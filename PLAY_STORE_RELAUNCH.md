# Christmas Countdown — Relaunch Guide

Step-by-step checklist to bring the app back to life and publish it again on
Google Play. Work through it in order; tick boxes as you go.

- Package name (never change it): `com.jorgelillo.christmascountdown`
- Last bundle in the repo: `app/release/app-release.aab` (versionCode 1, Aug 2021)

---

## Phase 1 — Modernise the project ✅ (done in code)

What changed and why:

| Area | Before (2021) | Now | Why |
|---|---|---|---|
| Android Gradle Plugin | 7.0.1 | 8.13.2 | Old AGP can't build for current SDKs or run on a current JDK |
| Gradle | 7.0.2 | 8.13 | Required by AGP 8.13 |
| Java | 1.8 | 17 | Required by AGP 8 |
| compileSdk / targetSdk | 31 / 31 | 36 / 36 | **Play requires API 36 for updates since 31 Aug 2026** |
| minSdk | 21 | 23 | Required by current AndroidX libraries |
| Repositories | jcenter | google + mavenCentral | jcenter is shut down |
| Countdown date | hard-coded `2021-12-25` | next 25 Dec, computed | The app showed "Merry Christmas" forever |
| Layout | stretched background, hard-coded English labels | cropped background, translated labels | Looks right on modern tall screens, labels in Spanish too |
| Edge-to-edge | n/a | `EdgeToEdge` + window insets | Enforced for apps targeting API 35+ |
| Release build | not signed, no shrinking | signed via `keystore.properties`, R8 enabled | Smaller bundle, reproducible signing |

Countdown logic lives in `ChristmasCountdown.java` and is covered by unit tests
(`./gradlew testDebugUnitTest`).

Command-line builds need Android Studio's JDK:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew testDebugUnitTest assembleDebug
```

---

## Phase 2 — Run it on the emulator

- [ ] **Free disk space.** The disk is ~98 % full; the emulator refuses to start
  with less than ~7.4 GB free. Candidates:
  - `~/.android/avd/Pixel_6.avd` (3.1 GB) and `~/.android/avd/Pixel_3a_API_31_arm64-v8a.avd`
    (2.6 GB): both AVDs are dead, their system images are no longer installed.
    Delete them from Android Studio → Device Manager.
  - `~/Library/Caches` (11 GB): mostly safe to clear app caches.
- [ ] Open the project in Android Studio and let Gradle sync finish.
- [ ] In Device Manager start **Pixel_9_API_36** (Android 16, already created).
- [ ] Press Run ▶. Check that:
  - the four boxes tick every second,
  - nothing is hidden under the status bar or the navigation bar,
  - with the emulator language set to Spanish, texts appear in Spanish.
- [ ] Optional: set the emulator date to 25 Dec (Settings → System → Date & time,
  turn off automatic time) and check the "Merry Christmas" message, then 26 Dec
  to see it count towards next year.

---

## Phase 3 — Signing key

Bundles uploaded to Play are signed with your **upload key**; Google re-signs
them with the app signing key it keeps (Play App Signing).

Found locally: `~/Projects/documentación/christmas countdown/`
(`christmas.jks`, alias `key0`, created 29 Aug 2021, plus `pass.txt` and
`private_key.pepk`).

The 2021 bundle was signed with a certificate with these fingerprints:

```
SHA1:   33:90:93:3B:B2:7C:97:B5:93:4C:24:2B:2C:4C:DD:FE:11:82:8F:3F
SHA256: 12:EC:1D:3D:8B:56:8D:C9:C5:BE:AF:56:B8:ED:F4:3C:74:64:7A:BE:4A:8E:B0:8B:6A:A2:FE:DB:E3:48:EB:43
```

- [ ] Confirm the keystore password (the content of `pass.txt` was not accepted
  as-is).
- [ ] Verify the keystore is the right one — its SHA1 must match the one above:

  ```bash
  keytool -list -v -keystore "$HOME/Projects/documentación/christmas countdown/christmas.jks"
  ```

- [ ] Create `keystore.properties` in the project root (it is git-ignored, never
  commit it):

  ```properties
  storeFile=/Users/jorge/Projects/documentación/christmas countdown/christmas.jks
  storePassword=...
  keyAlias=key0
  keyPassword=...
  ```

- [ ] **If the key is lost or the password is wrong:** Play Console → the app →
  Test and release → App integrity → App signing → *Request upload key reset*.
  Generate a new key in Android Studio (Build → Generate Signed App Bundle →
  Create new), export its certificate as `.pem`, and submit it. The reset takes
  a couple of days to become active.

---

## Phase 4 — Build the release bundle

- [ ] In Play Console → App bundle explorer, check the **highest versionCode
  ever uploaded**. `app/build.gradle` uses `versionCode = 2`; raise it if
  anything higher was uploaded.
- [ ] Build:

  ```bash
  ./gradlew bundleRelease
  ```

  Output: `app/build/outputs/bundle/release/app-release.aab`.
- [ ] Check it is signed with the upload key:
  `keytool -printcert -jarfile app/build/outputs/bundle/release/app-release.aab`

---

## Phase 5 — Google Play Console

Check each of these in the Console; requirements change and the Console shows
what is actually blocking the app.

- [ ] **App status.** Find out whether the app is *removed*, *unpublished* or
  *suspended* (Dashboard and Inbox). Removed for account inactivity → a new
  release usually restores it once everything below is green.
- [ ] **Developer account.** Complete any pending steps: identity / developer
  verification, contact details, and payments profile.
- [ ] **App content** (Policy → App content), all must be completed:
  - [ ] Privacy policy URL (needed even if no data is collected — a simple page
    is enough).
  - [ ] Data safety form (the app collects no data and has no internet
    permission).
  - [ ] Target audience and content (a Christmas app may attract children: be
    careful, choosing under-13 audiences triggers the Families policy).
  - [ ] Content rating questionnaire.
  - [ ] Ads declaration (no ads).
  - [ ] Government apps / financial features / health declarations if asked.
- [ ] **Store listing.** Refresh screenshots (the UI changed), short and full
  description, in English and Spanish.
- [ ] **Release.** Production → Create new release → upload the `.aab` →
  release notes → review → roll out. For a first test, use *Internal testing*.
- [ ] Check the Publishing overview page and send changes for review.

---

## Notes

- The old `app/release/app-release.aab` is kept only for reference (its
  certificate fingerprint). It cannot be re-uploaded.
- If the Console offers an extension for the target API deadline (until 1 Nov
  2026), it's not needed: the app already targets API 36.
