# Plan: "¿Qué votáis?" group polls app

Status: **planned, not started.** Starts after the Who's lying? internal test is released.

## Why

A friend wants to know what the group voted. One person asks a question, shares a link on WhatsApp,
and the people they invite answer with their names visible. Android only for v1 (iPhone users left
out on purpose, fine for now).

## v1 scope

1. First launch: type your name (no accounts, no passwords).
2. Create a question: text plus type, either **two options** (Wii "Everybody Votes" style, red vs
   blue) or **free text**.
3. "Invite" opens the share sheet with `https://jorgelillo7.github.io/q/?c=<code>`.
4. A friend taps the link: the app opens on that question (Android App Links), or Google Play if
   it isn't installed. They enter their name once and answer.
5. Results show who answered what (not anonymous inside a group).
6. Home: questions you created and questions you were invited to, with answer counts.
7. Play requirements for a networked app with user content: "Delete my data", "Report question",
   short terms of use.

Out of v1: public daily questions (Obviously-style, written by Jorge), Google sign-in, web page to
answer from iPhone, predicting the majority.

## Architecture

| Piece | Where | Notes |
|---|---|---|
| Android app | `lillo-android-apps/apps/group-polls/` (`domain` + `app`), package `com.jorgelillo.grouppolls` | Same conventions as the other apps |
| Firestore + anonymous auth | Its **own new GCP project** (third one, next to `biwenger-tools` and `be-water-app`; nothing shared with them) | Firebase Spark/free tier at this scale; anonymous auth gives one answer per device |
| GCP project infra | `lillorepo/infra` Terraform, third project next to `biwenger-tools` and `be-water-app` | Project created by hand then adopted: APIs, `(default)` Firestore database, anonymous auth config, €1 budget alert |
| Firestore security rules + indexes | Next to the app in `lillo-android-apps` | Change with the code; tested with the Firebase emulator; deployed with the Firebase CLI |
| Invite links | `jorgelillo7.github.io`: `/.well-known/assetlinks.json` + `/q/` fallback page | Query string because GitHub Pages can't serve made-up paths |
| Privacy policy | `jorgelillo7.github.io/privacy/group-polls/` | First app that stores data: name + answers |

Data model (draft): `questions/{code}` (text, type, options, creator uid + name, createdAt,
closed) and `questions/{code}/answers/{uid}` (name, option or text, answeredAt). The document id
being the uid makes "one answer per device" a rule, not app logic. The random code is the access
key: rules only allow reading a question by its exact id (no listing).

## Steps

1. **Design doc review**: screens, data model, security rules, link flow. Decide the name.
2. **GCP project**: create by hand, adopt in `lillorepo/infra` (APIs, Firestore, anonymous auth,
   budget). Firebase project linked to it; Android app registered (`google-services.json`).
3. **Runbook MD** (`apps/group-polls/OPERATIONS.md` + a section in lillorepo `infra/README.md`):
   every command and console link: create/adopt project, `terraform plan/apply`, Firebase CLI
   login, deploy rules and indexes, run the emulator and rules tests, App Links verification,
   costs and how to check them, how to delete a user's data.
4. **Security rules first**, with emulator tests (create, answer once, can't read without code,
   can't edit others' answers, delete own data).
5. **Domain + app**: name, create, invite, answer, results, home; deep links.
6. **Links**: `assetlinks.json` (with the upload key and Play App Signing fingerprints) and `/q/`
   page on the site.
7. **Play**: privacy policy, Data safety (name, user id, user content), UGC report + delete data,
   store listing, internal test with the friend group.

## Open questions

- ~~App name~~ **Chosen: "¿Qué votáis? Sondeos" / "What do you vote? Group polls"** (same
  question-to-the-group family as "¿Qué hacemos?" and "¿Quién miente?"). Package
  **`com.jorgelillo.grouppolls`** (permanent once published), folder **`apps/group-polls`**. Check Google Play for clashes before publishing. Not "Canal
  Opiniones": that is the official Spanish name of Nintendo's Wii "Everybody Votes Channel"
  (trademark + Play impersonation policy).
- Look and feel: an homage to the Wii channel is fine in mechanics (vote, predict the majority,
  red vs blue) and in a light, rounded, early-2010s style of our own. No Nintendo assets, Miis,
  channel-menu layout, fonts or sounds.
- Question lifetime: close manually, or auto-close / auto-delete after N days (cheaper, more
  private).
- Can the creator delete a question for everyone? (Yes, probably.)
