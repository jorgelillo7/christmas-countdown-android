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
        // A loop that ends mid-bar sounds off; every score must fill whole bars of its meter.
        for (carol in Carols.all) {
            assertEquals(0.0, carol.totalBeats % carol.beatsPerBar, carol.title)
        }
    }

    @Test
    fun carolsStayInAMusicBoxRange() {
        for (carol in Carols.all) {
            val pitches = carol.notes.mapNotNull { it.midi }
            assertTrue(pitches.min() >= 72 && pitches.max() <= 89, "${carol.title}: ${pitches.min()}..${pitches.max()}")
        }
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

    @Test
    fun nonLoopingRenderLetsTheLastNoteRingOut() {
        val melody = Carols.silentNight
        val looped = MusicBoxSynth.render(melody, sampleRate = 8_000)
        val single = MusicBoxSynth.render(melody, sampleRate = 8_000, loop = false)
        assertTrue(single.size > looped.size)
    }
}
