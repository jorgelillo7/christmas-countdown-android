package com.jorgelillo.whoslying.domain

/**
 * Points for a finished game, added up across games: every player on the winning side scores,
 * eliminated or not. Impostors and the drifter score more because they are outnumbered.
 */
object Scoring {
    const val CIVILIAN_WIN = 5
    const val INFILTRATOR_WIN = 15

    fun points(game: Game): Map<String, Int> = when (val outcome = game.outcome) {
        Outcome.CiviliansWin -> game.cards.filter { it.role == Role.CIVILIAN }.associate { it.player to CIVILIAN_WIN }
        is Outcome.InfiltratorsWin -> game.cards.filter { it.role in outcome.roles }.associate { it.player to INFILTRATOR_WIN }
        Outcome.DrifterGuessed -> game.cards.filter { it.role == Role.DRIFTER }.associate { it.player to INFILTRATOR_WIN }
        Outcome.NoCivilians, Outcome.EndedEarly, null -> emptyMap()
    }
}
