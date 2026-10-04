package com.jorgelillo.whoslying.domain

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameTest {

    private val players = listOf("Ana", "Bea", "Carlos", "Dani", "Eva")
    private val pack = WordPack("p", "Lugares", "🗺️", listOf(Entry("Playa", "Piscina"), Entry("Cine", "Teatro")))

    private fun deal(settings: GameSettings, seed: Int = 1, who: List<String> = players) =
        Game.deal(who, settings, pack, pack.entries.first(), Random(seed))

    @Test
    fun classicImpostorGetsTheDecoyAndDoesNotKnow() {
        val game = deal(GameSettings(GameMode.CLASSIC, impostors = 1))
        val impostor = game.cards.single { it.role == Role.IMPOSTOR }
        assertEquals("Piscina", impostor.word)
        assertFalse(impostor.knowsRole)
        assertTrue(game.cards.filter { it.role == Role.CIVILIAN }.all { it.word == "Playa" })
    }

    @Test
    fun blindImpostorGetsNoWordButTheHint() {
        val game = deal(GameSettings(GameMode.BLIND, impostors = 2))
        val impostors = game.cards.filter { it.role == Role.IMPOSTOR }
        assertEquals(2, impostors.size)
        assertTrue(impostors.all { it.word == null && it.hint == "Lugares" && it.knowsRole })
        assertNull(deal(GameSettings(GameMode.BLIND, categoryHint = false)).cards.first { it.role == Role.IMPOSTOR }.hint)
    }

    @Test
    fun drifterModeAddsOneDrifterWhoNeverStarts() {
        repeat(30) { seed ->
            val game = deal(GameSettings(GameMode.DRIFTER), seed)
            assertEquals(1, game.cards.count { it.role == Role.DRIFTER })
            assertNotEquals(Role.DRIFTER, game.cardOf(game.starter).role)
        }
    }

    @Test
    fun civiliansWinWhenEveryInfiltratorIsOut() {
        val game = deal(GameSettings(GameMode.CLASSIC))
        game.eliminate(game.cards.single { it.role == Role.IMPOSTOR }.player)
        assertEquals(Outcome.CiviliansWin, game.outcome)
    }

    @Test
    fun impostorsWinWhenOneCivilianIsLeft() {
        val game = deal(GameSettings(GameMode.CLASSIC), who = listOf("Ana", "Bea", "Carlos"))
        game.eliminate(game.cards.first { it.role == Role.CIVILIAN }.player)
        assertEquals(Outcome.InfiltratorsWin(setOf(Role.IMPOSTOR)), game.outcome)
    }

    @Test
    fun eliminatedDrifterGuessesWithAccentsAndCaseIgnored() {
        val game = Game.deal(players, GameSettings(GameMode.DRIFTER), pack, Entry("Café", "Té"), Random(3))
        val drifter = game.cards.single { it.role == Role.DRIFTER }.player
        assertTrue(game.eliminate(drifter).drifterMustGuess)
        assertTrue(game.guess("  cafe "))
        assertEquals(Outcome.DrifterGuessed, game.outcome)
    }

    @Test
    fun wrongDrifterGuessKeepsTheGameGoing() {
        val game = deal(GameSettings(GameMode.DRIFTER))
        game.eliminate(game.cards.single { it.role == Role.DRIFTER }.player)
        assertFalse(game.guess("Montaña"))
        assertNull(game.outcome)
        assertEquals(4, game.alive.size)
    }

    @Test
    fun impostorLimitsKeepCiviliansAhead() {
        assertEquals(1, Rules.maxImpostors(3, GameMode.CLASSIC))
        assertEquals(2, Rules.maxImpostors(5, GameMode.CLASSIC))
        assertEquals(1, Rules.maxImpostors(4, GameMode.DRIFTER))
        assertFalse(Rules.canStart(3, GameSettings(GameMode.DRIFTER)))
        assertFalse(Rules.canStart(5, GameSettings(impostors = 3)))
    }

    @Test
    fun chaosSometimesMakesEveryoneAnImpostor() {
        val counts = (0 until 400).map { seed ->
            deal(GameSettings(GameMode.BLIND, chaos = true), seed).cards.count { it.role == Role.IMPOSTOR }
        }.toSet()
        assertTrue(1 in counts && players.size in counts, "counts seen: $counts")
        val allImpostors = (0 until 400).map { deal(GameSettings(GameMode.BLIND, chaos = true), it) }
            .first { game -> game.cards.all { it.role == Role.IMPOSTOR } }
        allImpostors.eliminate(players.first())
        assertNull(allImpostors.outcome)
        allImpostors.reveal()
        assertIs<Outcome.NoCivilians>(allImpostors.outcome)
    }

    @Test
    fun endingEarlyIsNotANoCiviliansGame() {
        val game = deal(GameSettings(GameMode.DRIFTER))
        game.eliminate(game.cards.single { it.role == Role.IMPOSTOR }.player)
        assertNull(game.outcome)
        game.reveal()
        assertEquals(Outcome.EndedEarly, game.outcome)
    }

    @Test
    fun pickerAvoidsRecentWords() {
        val (_, entry) = WordPicker.pick(listOf(pack), recent = listOf("Playa"), random = Random(1))
        assertEquals("Cine", entry.word)
    }

    @Test
    fun builtInPacksAreWellFormed() {
        for (language in listOf("es", "en")) {
            val packs = WordPacks.builtIn(language)
            assertEquals(6, packs.size)
            for (p in packs) {
                assertEquals(20, p.entries.size, p.name)
                assertEquals(p.entries.size, p.entries.map { Rules.normalize(it.word) }.toSet().size, "duplicates in ${p.name}")
                p.entries.forEach { assertNotEquals(Rules.normalize(it.word), Rules.normalize(it.decoy!!)) }
            }
        }
    }
}
