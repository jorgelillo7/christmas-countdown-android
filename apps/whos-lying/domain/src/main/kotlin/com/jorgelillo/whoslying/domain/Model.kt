package com.jorgelillo.whoslying.domain

import kotlinx.serialization.Serializable

enum class Role { CIVILIAN, IMPOSTOR, DRIFTER }

enum class GameMode {
    /** The impostor gets a similar but different word and doesn't know they are the impostor. */
    CLASSIC,

    /** The impostor gets no word (optionally the category as a hint) and knows it. */
    BLIND,

    /** Like CLASSIC, plus one "drifter" with no word who wins by guessing the civilians' word. */
    DRIFTER,
}

/** One secret: the civilians' word and, for CLASSIC/DRIFTER, the impostor's similar word. */
@Serializable
data class Entry(val word: String, val decoy: String? = null)

@Serializable
data class WordPack(
    val id: String,
    val name: String,
    val emoji: String,
    val entries: List<Entry>,
    val custom: Boolean = false,
)

@Serializable
data class GameSettings(
    val mode: GameMode = GameMode.CLASSIC,
    val impostors: Int = 1,
    /** Show the pack name to impostors (BLIND) and the drifter. */
    val categoryHint: Boolean = true,
    /** Some games get a random number of impostors, sometimes everyone. */
    val chaos: Boolean = false,
    /** Empty means "all packs". */
    val packIds: Set<String> = emptySet(),
)

/** What one player sees when it's their turn to look at the phone. */
data class Card(
    val player: String,
    val role: Role,
    /** Word shown to the player; null when they get none (BLIND impostor, drifter). */
    val word: String?,
    /** Category hint for players without the civilians' word, when enabled. */
    val hint: String?,
) {
    /** CLASSIC/DRIFTER impostors see a word and are not told they are impostors. */
    val knowsRole: Boolean get() = role != Role.CIVILIAN && word == null
}
