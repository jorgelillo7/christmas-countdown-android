# docs/ — index

One entry per document. Runbooks first, then setup, then reference.

## Operations

| Doc | What's in it |
|---|---|
| [`operations.md`](operations.md) | Repo-wide runbook: prerequisites, build/test, shared scripts, release steps. Per-app commands live in `apps/<app>/OPERATIONS.md` |
| [`publishing-on-google-play.md`](publishing-on-google-play.md) | End-to-end Play checklist: account, signing, App content declarations, store listing, release tracks, review |
| [`adding-a-new-app.md`](adding-a-new-app.md) | Folder layout, Gradle setup, icon, privacy policy and store assets for a new app |
| [`signing.md`](signing.md) | Upload keys: create one per app (`scripts/new-upload-key.sh`), where it lives, backup, lost key |

## Setup

| Doc | What's in it |
|---|---|
| [`setup/toolchain.md`](setup/toolchain.md) | Versions in use, how to upgrade them, and the gotchas we hit (AGP 9, R8, JDK) |

## Reference

| Doc | What's in it |
|---|---|
| [`lessons-learned.md`](lessons-learned.md) | Things that went wrong once and how to avoid them |

## Per app

| App | Operations | Release notes |
|---|---|---|
| Christmas Countdown | [`OPERATIONS.md`](../apps/christmas-countdown/OPERATIONS.md) | [`release-notes.md`](../apps/christmas-countdown/release-notes.md) |
| What next? (decision wheel) | [`OPERATIONS.md`](../apps/decision-wheel/OPERATIONS.md) | [`release-notes.md`](../apps/decision-wheel/release-notes.md) |
| Who's lying? (impostor game) | [`OPERATIONS.md`](../apps/whos-lying/OPERATIONS.md) | [`release-notes.md`](../apps/whos-lying/release-notes.md) |

Shared scripts: [`scripts/`](../scripts). Branding: [`branding/`](../branding).
Claude Code skills: [`.claude/skills/`](../.claude/skills) — `new-android-app`, `upgrade-android-deps`, `release-android-app`.
