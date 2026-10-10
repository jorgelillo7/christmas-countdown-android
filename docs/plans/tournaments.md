# Tournaments app: ¿Solo quedará uno? · Last one standing?

Casual tournaments among friends: Magic, Pokémon, Lorcana, ping-pong, FIFA, padel, chess… Create
one in seconds, enter results at the table, keep the history. No account, no server, no database.

Package `com.jorgelillo.tournaments`, folder `apps/tournaments`. Play title "¿Solo quedará uno?
Torneos" / "Last one standing? Tournaments", following the family's "question + what it is" names.

## Decisions

| Topic | Decision |
|---|---|
| Formats | Single elimination and Swiss (optional top cut of 2/4/8 into elimination). Both from v1. |
| Draw | Random, with a "re-draw" button until the first result is in. |
| Bracket | Two-sided: first half of round 1 on the left, second half on the right, final in the centre. |
| Byes | Bracket size = next power of two; standard seed order, so byes spread and never bye-vs-bye. |
| Matches | Best of 1, 3 or 5 for the whole tournament. Result = games won + optional comment ("20 vidas a 0"). |
| Swiss | Rounds = ceil(log2 n) by default (editable). Pair by ranking avoiding rematches (backtracking); one bye per player, to the lowest ranked. 3/1/0 points, bye = win. |
| Tie-breakers | Points, OMW, GW, OGW, each floored at 33 % — same as Magic / Pokémon / Lorcana organised play (Melee.gg). |
| Storage | One JSON document in DataStore, like the other apps. `allowBackup=true` so history survives a phone change. |
| Moving to another phone | The whole tournament inside a link: compact JSON → deflate → base64url, in the URL fragment of `https://jorgelillo7.github.io/t/#…` (GitHub never sees it). 64 players with comments ≈ 1.2 KB. Shown as a QR code and shared as text. |
| Importing | Three ways, none needing the App Link to be verified: "Import" screen (paste link or code), Android share-to-app (`ACTION_SEND` text/plain from WhatsApp), and the App Link once assetlinks is published. Same id → newest revision wins. |
| Permissions | None. No INTERNET (nothing to talk to), no CAMERA (the system camera reads the QR and opens the link). Data safety: "no data collected". |
| Export | Bracket / standings as a PNG (drawn with `android.graphics.Canvas`, shared via FileProvider like the impostor drawing) and CSV of all matches. |
| Stats | Across all saved tournaments, by player name (trimmed, case-insensitive): titles, finals, match win %, a simple bar chart. Filter by game. |

## What competitors taught us

Challonge, Start.gg, Toornament, Score7, Brackets.app, Bracket HQ, Melee.gg:

1. All of them need internet and almost all an account: offline and account-free is the gap.
2. Desktop-first brackets on a phone mean pinch-zoom and lost context. Ours scrolls both ways and
   the two-sided layout halves the height; a "Matches" list is the default tab while playing.
3. Result entry is the hot path: big tap targets, one sheet, the sentence preview
   ("Pepe ganó 2 a 1 a Luis"), and only valid scores (no 1-0 in a Bo3 knock-out).
4. Score7's QR for spectators is its most praised feature: we do it without a server.
5. Bracket HQ shows simplicity wins for casual use: no seeding screens, no settings walls.
6. Melee shows players a card with their record and opponents: our player sheet does the same
   with history across tournaments.

## Screens

1. **Home** — tournaments (in progress first, then by date), "New tournament", "Import", Stats.
2. **New** — name, game (free text with chips: Magic, Pokémon, Lorcana, One Piece, Yu-Gi-Oh!,
   FIFA, Ping-pong…), date (today, editable), format, Bo1/3/5, Swiss rounds and top cut,
   players (add one by one or paste a list; blanks and duplicates rejected). Names from
   earlier tournaments are offered as chips.
3. **Tournament** — tabs: Matches (pending first) · Bracket (two-sided) or Standings (Swiss) ·
   Share. Re-draw while nothing is played. Champion banner with confetti when the final ends.
4. **Result sheet** — winner by tapping a player, score chips, draw toggle (Swiss), comment.
5. **Stats** — leaderboard and chart.

Victory line: when a match is decided, the winner's name goes bold with the accent colour and
their connector to the next round is drawn thick in the accent; once there is a champion, their
whole path to the final is highlighted.

## Known gaps (fine for v1)

- In Swiss a 1-0 on time cannot be a match win (only reaching the needed games or a draw).
- No double elimination or round robin yet: easy to add on the same model later.
- No live sync: the link is a snapshot. Whoever keeps the results shares again.

## Pending outside the code

- `/t/` fallback page on jorgelillo7.github.io ("open in the app / get it on Play") and the
  assetlinks entry: local until the user approves.
- Play Console app, upload key, store listing, privacy page `privacy/tournaments/`.
