# 📚 Docs

Guides and runbooks for every app in this repo. Grows as we go.

| Guide | What it covers |
|---|---|
| [Publishing an app on Google Play](publishing-on-google-play.md) | End-to-end checklist: account, signing, Play Console declarations, store listing, release tracks, review |
| [Toolchain](toolchain.md) | Versions in use, how to upgrade them, and the gotchas we hit (AGP 9, R8, JDK) |
| [Adding a new app](adding-a-new-app.md) | Folder layout, Gradle setup, icon, privacy policy and store assets for app #2, #3… |
| [Lessons learned](lessons-learned.md) | Short list of things that went wrong once and how to avoid them |

Per-app docs live next to each app:

| App | Operations (commands, signing, release history) |
|---|---|
| Christmas Countdown | [OPERATIONS.md](../apps/christmas-countdown/OPERATIONS.md) |

Shared scripts are in [`scripts/`](../scripts) and shared branding in [`branding/`](../branding).

Claude Code skills for repeatable operations live in [`.claude/skills/`](../.claude/skills):
`upgrade-android-deps` (toolchain and library upgrades) and `release-android-app` (ship a new version).
