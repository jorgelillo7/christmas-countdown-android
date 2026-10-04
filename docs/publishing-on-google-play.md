# Publishing an app on Google Play

Checklist distilled from relaunching Christmas Countdown in 2026. Follow it top to bottom for a new
app; for an update, jump to [6. Release](#6-release).

## 0. Developer account (once)

- [ ] Personal account, **identity verified** (Play Console → Account details).
- [ ] Log in at least once a year and publish something: inactive accounts get closed and their
      apps removed. Recovering it needs a support ticket.
- [ ] Developer profile (Play Console → Developer page): icon 512×512, header 4096×2304,
      promotional text ≤ 140 chars. Assets: [`branding/developer-profile/`](../branding/developer-profile).
- [ ] **Android developer verification** (2026+): each package name must be registered to the
      verified identity. Play Console → Android developer verification → Register package name.
      For an app using Play App Signing, register the **app signing key** fingerprint (Integrity →
      App signing), not the upload key. Proving ownership may need an APK signed by that key with
      a token file in `assets/adi-registration.properties`.

## 1. Package name and signing (once per app)

- [ ] Choose `applicationId` (`com.jorgelillo.<app>`). **It can never change.**
- [ ] Create the **upload key** and `apps/<app>/keystore.properties`: run
      `scripts/new-upload-key.sh <app>` in your own terminal and follow [signing.md](signing.md)
      (password manager + off-Mac backup of the `.jks`).
- [ ] Use **Play App Signing** (default): Google keeps the app signing key; you only hold the upload key.
- [ ] Lost upload key? See [signing.md](signing.md#lost-key-or-forgotten-password).

## 2. Build the release

```bash
source scripts/env.sh
./gradlew test :apps:<app>:app:lintDebug :apps:<app>:app:bundleRelease
apps/<app>/tools/smoke_test.sh            # release build on the emulator (R8 only runs here)
scripts/verify-bundle.sh apps/<app>/app/build/outputs/bundle/release/app-release.aab
```

- [ ] `versionCode` higher than anything uploaded before (Play Console → App bundle explorer).
- [ ] Signing certificate matches the upload key (`verify-bundle.sh` prints it).
- [ ] targetSdk meets the current requirement (API 36 since 31 Aug 2026):
      https://developer.android.com/google/play/requirements/target-sdk

## 3. Privacy policy

- [ ] Add `privacy/<app>/index.html` to the site repo `jorgelillo7/jorgelillo7.github.io`
      (copy the Christmas Countdown one). URL: `https://jorgelillo7.github.io/privacy/<app>/`.
- [ ] Also link it from inside the app (About screen).
- [ ] Never host it in the app's own repo: renaming the repo would break the URL.

## 4. App content declarations (Policy → App content)

| Declaration | Answer for a no-data, no-ads app |
|---|---|
| Privacy policy | The URL above |
| Ads | No ads |
| App access | All functionality available without special access |
| Content rating | Questionnaire: utility/other, "No" to everything |
| Target audience | 13+ (avoids the Families policy unless the app is for children) |
| Data safety | Does not collect or share data |
| Advertising ID | No |
| Government apps | No |
| Financial features | My app doesn't provide financial features |
| Health apps | My app doesn't have health features |

Check the merged release manifest for permissions before answering Data safety
(`verify-bundle.sh` lists them). Libraries add some (WorkManager adds WAKE_LOCK, etc.).

## 5. Store listing (Grow users → Store presence → Main store listing)

Everything is tracked in `apps/<app>/store/metadata/android/<locale>/` (fastlane supply layout):

| Field | File | Limit |
|---|---|---|
| App name | `title.txt` | 30 |
| Short description | `short_description.txt` | 80 |
| Full description | `full_description.txt` | 4000 |
| Release notes | `changelogs/<versionCode>.txt` | 500 |
| Icon | `images/icon.png` | 512×512 |
| Feature graphic | `images/featureGraphic.png` | 1024×500 |
| Phone screenshots | `images/phoneScreenshots/01..05.png` | 2–8, ≥1080 px for promotion |

- [ ] Default language first, then *Manage translations → Add your own translations*.
      Until a language has its own screenshots it shows the default language's (greyed out, not deletable).
- [ ] Copy long texts from the `.txt` files (`open -e file.txt`), not from a terminal, to keep line breaks.
- [ ] **Don't publish a listing describing features the live version doesn't have** (misrepresentation).
      Ship the listing together with the release that adds them.

## 6. Release

1. **Internal testing first** (Test and release → Testing → Internal testing): upload the `.aab`,
   add yourself as tester, install from the opt-in link on a real phone.
2. Release notes, one block per language:
   ```
   <es-ES>
   …
   </es-ES>
   <en-US>
   …
   </en-US>
   ```
   If it says a language note is "too long" with a short text, clear the box (Cmd+A, delete) and
   type the tags again: leftover hidden text is the usual cause.
3. **Production**: promote the internal release or create a new one with the same bundle.
4. Publishing overview → optionally turn on **Managed publishing** (you press "Publish" after approval)
   → **Send changes for review**. Store listing + release go out together.

Expected review warnings that are fine to accept:
- *Devices no longer supported*: after raising `minSdk`.
- *Native code without debug symbols*: the `.so` files come from AndroidX, not our code.

## 7. After release

- [ ] Install from Play on a real phone and check the app starts.
- [ ] Watch Android vitals (crashes/ANRs) for a few days.
- [ ] Add anything new learned to `docs/lessons-learned.md` and a row in `apps/<app>/release-notes.md`.
