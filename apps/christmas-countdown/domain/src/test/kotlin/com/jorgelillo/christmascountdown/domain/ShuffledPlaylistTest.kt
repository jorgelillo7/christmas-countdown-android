package com.jorgelillo.christmascountdown.domain

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ShuffledPlaylistTest {

    private val items = listOf("a", "b", "c", "d", "e")

    @Test
    fun everyItemPlaysOncePerRound() {
        val playlist = ShuffledPlaylist(items, Random(1))
        repeat(20) {
            assertEquals(items.toSet(), List(items.size) { playlist.next() }.toSet())
        }
    }

    @Test
    fun neverRepeatsTheSameItemBackToBack() {
        val playlist = ShuffledPlaylist(items, Random(2))
        val played = List(1_000) { playlist.next() }
        played.zipWithNext().forEach { (a, b) -> assertNotEquals(a, b) }
    }

    @Test
    fun singleItemJustRepeats() {
        val playlist = ShuffledPlaylist(listOf("only"))
        assertEquals(listOf("only", "only", "only"), List(3) { playlist.next() })
    }
}
