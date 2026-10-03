package com.jorgelillo.christmascountdown.domain

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MelodyTest {

    @Test
    fun parsesPitchesSharpsAndRests() {
        val melody = Melody.parse("test", 120, "A4:1 | F#5:0.5 R:2 C4:1")
        assertEquals(listOf(69, 78, null, 60), melody.notes.map { it.midi })
        assertEquals(4.5, melody.totalBeats)
        assertEquals(440.0, melody.notes[0].frequency!!, 1e-9)
        assertNull(melody.notes[2].frequency)
    }

    @Test
    fun carolsFillWholeBars() {
        assertEquals(0.0, Carols.jingleBells.totalBeats % 4)
        assertEquals(0.0, Carols.weWishYouAMerryChristmas.totalBeats % 3)
    }

    @Test
    fun synthRendersAudibleUnclippedLoop() {
        val melody = Carols.jingleBells
        val pcm = MusicBoxSynth.render(melody, sampleRate = 8_000)
        val expected = (melody.totalBeats * 60.0 / melody.bpm * 8_000).toInt()
        assertEquals(expected, pcm.size)
        val peak = pcm.maxOf { abs(it.toInt()) }
        assertTrue(peak in 20_000..Short.MAX_VALUE.toInt(), "peak was $peak")
    }
}
