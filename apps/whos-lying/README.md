<div align="center">

<img src="store/play-icon-512.png" width="112" alt="Who's lying? icon" />

# 🕵️ Who's lying? · ¿Quién miente?

**The impostor word game for groups, on one phone. Free forever: every pack, no ads, no subscriptions.**

</div>

## ✨ Features

- 🎭 **Classic:** the impostor gets a *similar* word and doesn't know they're the impostor
- 🙈 **Blind:** the impostor gets no word (optionally the category) and must bluff
- 🤷 **The Drifter:** classic plus a player with no word who wins by guessing it when voted out
- 🌀 **Chaos:** some games get a random number of impostors… sometimes everyone
- 📦 6 built-in packs × 20 word pairs, in Spanish and English, written for this app
- 📝 **Your own packs:** one word per line, `word / similar word` (the similar word is optional)
- 🔁 Recently used words are avoided; the group and setup are remembered
- 🚫 No ads, no accounts, no network, no data collected

## 🏗️ Architecture

| Module | What lives there |
|---|---|
| `domain` | Pure Kotlin: roles and dealing (`Game.deal`), votes, drifter guess (accent/case-insensitive), outcomes, chaos, word picker, built-in packs. Unit tested. |
| `app` | Compose UI, one `GameViewModel`, DataStore JSON (players, settings, custom packs, recent words), navigation-compose. |

In CLASSIC/DRIFTER modes impostors see exactly the same screen as civilians (just a different word),
so nothing gives them away. Only players without a word are told their role.

## 📦 Commands

See [OPERATIONS.md](OPERATIONS.md). Version history: [release-notes.md](release-notes.md).
