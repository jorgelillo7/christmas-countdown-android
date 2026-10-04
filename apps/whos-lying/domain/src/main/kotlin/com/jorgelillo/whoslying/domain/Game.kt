package com.jorgelillo.whoslying.domain

import java.text.Normalizer
import kotlin.random.Random

object Rules {
    const val MIN_PLAYERS = 3
    const val MAX_PLAYERS = 24

    fun drifters(mode: GameMode): Int = if (mode == GameMode.DRIFTER) 1 else 0

    /** Civilians must outnumber impostors at the start: impostors < (players - drifters) / 2. */
    fun maxImpostors(players: Int, mode: GameMode): Int = ((players - drifters(mode) - 1) / 2).coerceAtLeast(0)

    fun minPlayers(mode: GameMode): Int = if (mode == GameMode.DRIFTER) 4 else MIN_PLAYERS

    fun canStart(players: Int, settings: GameSettings): Boolean =
        players in minPlayers(settings.mode)..MAX_PLAYERS && settings.impostors in 1..maxImpostors(players, settings.mode)

    /** Lower case, no accents, no surrounding spaces: "Café " matches "cafe". */
    fun normalize(text: String): String =
        Normalizer.normalize(text.trim().lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")
}

sealed interface Outcome {
    data object CiviliansWin : Outcome

    /** Impostors and/or the drifter survived until at most one civilian was left. */
    data class InfiltratorsWin(val roles: Set<Role>) : Outcome

    data object DrifterGuessed : Outcome

    /** Chaos game where everyone was an impostor: nobody wins, the fun is finding out. */
    data object NoCivilians : Outcome

    /** The group ended the game before anyone won. */
    data object EndedEarly : Outcome
}

/** Result of voting someone out. */
data class Elimination(val player: String, val role: Role, val drifterMustGuess: Boolean)

class Game private constructor(
    val cards: List<Card>,
    val civilianWord: String,
    val category: String,
    val starter: String,
    private val eliminated: MutableSet<String> = mutableSetOf(),
) {
    var outcome: Outcome? = null
        private set
    var pendingGuess: String? = null
        private set

    val alive: List<Card> get() = cards.filter { it.player !in eliminated }
    val isOver: Boolean get() = outcome != null

    fun cardOf(player: String): Card = cards.first { it.player == player }

    fun eliminate(player: String): Elimination {
        check(!isOver && pendingGuess == null) { "Game is not waiting for a vote" }
        val card = cardOf(player)
        eliminated += player
        val mustGuess = card.role == Role.DRIFTER
        if (mustGuess) pendingGuess = player else outcome = evaluate()
        return Elimination(player, card.role, mustGuess)
    }

    /** The eliminated drifter's last chance. Returns true if they guessed the civilians' word. */
    fun guess(text: String): Boolean {
        checkNotNull(pendingGuess) { "No drifter is guessing" }
        pendingGuess = null
        val right = Rules.normalize(text) == Rules.normalize(civilianWord)
        outcome = if (right) Outcome.DrifterGuessed else evaluate()
        return right
    }

    /** Ends the game now and shows who was who. */
    fun reveal() {
        if (outcome != null) return
        pendingGuess = null
        outcome = when {
            cards.none { it.role == Role.CIVILIAN } -> Outcome.NoCivilians
            else -> evaluate() ?: Outcome.EndedEarly
        }
    }

    private fun evaluate(): Outcome? {
        val civilians = alive.count { it.role == Role.CIVILIAN }
        val infiltrators = alive.filter { it.role != Role.CIVILIAN }.map { it.role }.toSet()
        if (cards.none { it.role == Role.CIVILIAN }) return null
        return when {
            infiltrators.isEmpty() -> Outcome.CiviliansWin
            civilians <= 1 -> Outcome.InfiltratorsWin(infiltrators)
            else -> null
        }
    }

    companion object {
        /** Chance that a chaos game ignores the configured impostor count. */
        const val CHAOS_PROBABILITY = 1.0 / 3

        fun deal(
            players: List<String>,
            settings: GameSettings,
            pack: WordPack,
            entry: Entry,
            random: Random,
        ): Game {
            require(Rules.canStart(players.size, settings)) { "Invalid setup" }
            val impostors = impostorCount(players.size, settings, random)
            val drifters = if (impostors == players.size) 0 else Rules.drifters(settings.mode)
            val shuffled = players.indices.shuffled(random)
            val impostorIdx = shuffled.take(impostors).toSet()
            val drifterIdx = shuffled.drop(impostors).take(drifters).toSet()
            val decoy = entry.decoy ?: pack.entries.map { it.word }.filter { it != entry.word }.randomOrNull(random)

            val cards = players.mapIndexed { i, name ->
                val role = when (i) {
                    in impostorIdx -> Role.IMPOSTOR
                    in drifterIdx -> Role.DRIFTER
                    else -> Role.CIVILIAN
                }
                val word = when {
                    role == Role.CIVILIAN -> entry.word
                    role == Role.IMPOSTOR && settings.mode != GameMode.BLIND -> decoy
                    else -> null
                }
                val hint = if (word == null && settings.categoryHint) pack.name else null
                Card(name, role, word, hint)
            }
            val starter = cards.filter { it.role != Role.DRIFTER }.random(random).player
            return Game(cards, entry.word, pack.name, starter)
        }

        private fun impostorCount(players: Int, settings: GameSettings, random: Random): Int {
            if (!settings.chaos || random.nextDouble() >= CHAOS_PROBABILITY) return settings.impostors
            val options = (1..Rules.maxImpostors(players, settings.mode)) + players
            return options.random(random)
        }
    }
}

/** Picks the next secret word, avoiding words used in recent games. */
object WordPicker {
    const val RECENT_MEMORY = 40

    fun pick(packs: List<WordPack>, recent: List<String>, random: Random): Pair<WordPack, Entry> {
        val all = packs.flatMap { pack -> pack.entries.map { pack to it } }
        require(all.isNotEmpty()) { "No words" }
        val fresh = all.filterNot { (_, entry) -> entry.word in recent }
        return (fresh.ifEmpty { all }).random(random)
    }
}
