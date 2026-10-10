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
| 👤 | New name "¿Ya es Navidad? Cuenta atrás" / "Is it Christmas yet? Countdown": paste the titles and feature graphics from `store/metadata` into the listing with the next release (the launcher name changes with that build) | Next release · the website already uses it |
| ⏳ | Check v4 on a real phone after Play approval: the 3 new carols (transcribed from memory), share text in both modes, widget | Google's review of v4 |
| 👤 | Review the 24 advent entries (EN + ES): content, tone, and facts flagged as shaky (day 8 snowflakes "all six arms", day 20 tinsel ~1610, day 22 NORAD founded 1958, CONAD in 1955) | You · entries in `app/src/main/res/values{,-es}/strings.xml` (`advent_entries`) |
| 👤 | Spanish carols (Los peces en el río, Campana sobre campana, Fum fum fum, Marimorena) | Reliable public-domain scores from you · *El burrito sabanero* is copyrighted (1972), not allowed |
| 🔨 | More advent entries (48–72, EN + ES) rotating by year | Before December of a later year |
| ⏳ | Raise `targetSdk` to 37 (Android 17 behaviour changes, test on an API 37 emulator) | Google Play announcing API 37 as the requirement (usually each August) |

## decision-wheel

| | What is missing | Waiting on |
|---|---|---|
| ⏳ | 1.1: favourites (presets and custom) listed first, launcher shortcuts for up to 3 favourite/recent wheels, optional "Restore example wheels" | Your go-ahead |

## whos-lying

| | What is missing | Waiting on |
|---|---|---|
| 👤 | Internal test with 4–5 friends from Google Play (upload the v3 bundle first): word pairs, flow, readability | You · Play Console internal testing |
| ⏳ | A tester got "Item not found" on the opt-in link: likely the first review still pending or the wrong Google account in Play | Their next try |
| ⏳ | Open-source license (GPL-3.0 was the candidate) | Your decision · not licensed for now |
| ⏳ | Sharing packs between phones (QR or text) | Feedback after real use |
| 👤 | New icon (a line-up, the liar in a fedora): upload `store/play-icon-512.png` and the feature graphics to the listing now; the launcher icon ships with versionCode 4 | You · Play Console → Main store listing |
| ⏳ | Play Console suggests "Integrate Play Games Services" (shown because the app is in the Games category). Optional: sign-in, achievements, leaderboards. Little value for a one-phone party game with no account; decide whether to dismiss it or add a few achievements later | Your decision |

## group-polls (new app)

| | What is missing | Waiting on |
|---|---|---|
| 👤 | Create the app in Play Console (`com.jorgelillo.grouppolls`, App · Social) and its upload key (`scripts/new-upload-key.sh group-polls`) | You · answers in `apps/group-polls/OPERATIONS.md` → "Play Console answers" |
| 👤 | Create the Firebase project and give the OK | You · lillorepo `packages/group_polls/OPERATIONS.md` → "Create the project" |
| 🔨 | Then: `firebase.properties`, App Check (Play Integrity + debug token), Play signing SHA-256 in `assetlinks.json`, publish `/q/` and `assetlinks.json` on the website (privacy page already live), Terraform on, internal test | The two steps above · plan in [docs/plans/group-polls.md](docs/plans/group-polls.md) |

## tournaments (new app)

| | What is missing | Waiting on |
|---|---|---|
| 👤 | Create the app in Play Console (`com.jorgelillo.tournaments`, App · Tools or Board games) and its upload key (`scripts/new-upload-key.sh tournaments`) | You |
| 🔨 | Then: Play signing SHA-256 in `assetlinks.json`, the `/t/` fallback page on the website (privacy page already live), store screenshots and listing texts, internal test | The step above · plan in [docs/plans/tournaments.md](docs/plans/tournaments.md) |
| ⏳ | Double elimination and round robin | Feedback after real use |

## repo

| | What is missing | Waiting on |
|---|---|---|
| ⏳ | Move into `lillorepo` (Bazel) | Gradle builds or the Play publishing loop slowing you down · domain modules are pure Kotlin to make it easy |
