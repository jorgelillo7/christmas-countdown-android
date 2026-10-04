# Pending work

The index of what is still open. **One line per item.** If it needs more than that, the
reasoning lives where it belongs (the app's docs, a PR, `git log`) and this file links to it.

Lines are pruned as items ship. What has **shipped** lives in `apps/<app>/release-notes.md`.

**👤 needs you** · **⏳ waiting on a trigger or on data** · **🔨 ready to pick up** ·
**🚧 blocked on you, and parked by your call**

---

## christmas-countdown

| | What is missing | Waiting on |
|---|---|---|
| ⏳ | Check v4 on a real phone after Play approval: the 3 new carols (transcribed from memory), share text in both modes, widget | Google's review of v4 |
| 👤 | Review the 24 advent entries (EN + ES): content, tone, and facts flagged as shaky (day 8 snowflakes "all six arms", day 20 tinsel ~1610, day 22 NORAD founded 1958, CONAD in 1955) | You · entries in `app/src/main/res/values{,-es}/strings.xml` (`advent_entries`) |
| 👤 | Spanish carols (Los peces en el río, Campana sobre campana, Fum fum fum, Marimorena) | Reliable public-domain scores from you · *El burrito sabanero* is copyrighted (1972), not allowed |
| 🔨 | More advent entries (48–72, EN + ES) rotating by year | Before December of a later year |
| ⏳ | Raise `targetSdk` to 37 (Android 17 behaviour changes, test on an API 37 emulator) | Google Play announcing API 37 as the requirement (usually each August) |

## decision-wheel

| | What is missing | Waiting on |
|---|---|---|
| 👤 | Try it on a real phone: feel of the spin, tick sound, haptics, presets | You · `./gradlew :apps:decision-wheel:app:installDebug` |
| 👤 | Upload key + `keystore.properties`, create the app in Play Console, register the package name | You · `scripts/new-upload-key.sh decision-wheel` · [signing guide](docs/signing.md) |
| 🔨 | Store screenshots and feature graphic (`tools/capture_store_screenshots.sh` + generator, from Christmas Countdown) | The design being final |
| 🔨 | Privacy page `privacy/decision-wheel/` and app card on jorgelillo7.github.io | Your OK on the restyled privacy page preview |
| ⏳ | Group veto turns (each person vetoes one, passing the phone) | Feedback after real use · v1 vetoes are free taps |

## repo

| | What is missing | Waiting on |
|---|---|---|
| ⏳ | Card tournament manager (Room) | After the decision wheel ships |
| ⏳ | Move into `lillorepo` (Bazel) | Gradle builds or the Play publishing loop slowing you down · domain modules are pure Kotlin to make it easy |
