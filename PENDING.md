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
| ⏳ | v1 in Google Play review (sent to production 2026-10-05) · then install it from Play on a real phone | Google's review |
| ⏳ | App card in the pinned "Apps" block of jorgelillo7.github.io (both languages), replacing "coming soon" | The app being live on Google Play (the Play link must work) |
| ⏳ | Group veto turns (each person vetoes one, passing the phone) | Feedback after real use · v1 vetoes are free taps |

## whos-lying

| | What is missing | Waiting on |
|---|---|---|
| 👤 | Play a real game with friends: word pairs, flow, readability | You · `./gradlew :apps:whos-lying:app:installDebug` |
| 👤 | Upload key: `scripts/new-upload-key.sh whos-lying` in your own terminal (store texts and graphics are ready in `apps/whos-lying/store/`) | You |
| 🔨 | Privacy page `privacy/whos-lying/` on jorgelillo7.github.io, then Play Console + internal testing | Your OK to publish the page |
| ⏳ | Open-source license (GPL-3.0 was the candidate) | Your decision · not licensed for now |
| 🔨 | Privacy page must mention the report form (Google Forms, opened in the browser, only the word + what the player writes) | Before publishing `privacy/whos-lying/` |
| ⏳ | Sharing packs between phones (QR or text) | Feedback after real use |

## repo

| | What is missing | Waiting on |
|---|---|---|
| ⏳ | Card tournament manager (Room) | After the decision wheel ships |
| ⏳ | Move into `lillorepo` (Bazel) | Gradle builds or the Play publishing loop slowing you down · domain modules are pure Kotlin to make it easy |
