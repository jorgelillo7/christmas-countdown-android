package com.jorgelillo.christmascountdown.domain

import kotlin.math.pow

/** A note as a MIDI number (null = rest) and its length in beats. */
data class Note(val midi: Int?, val beats: Double) {
    val frequency: Double? get() = midi?.let { 440.0 * 2.0.pow((it - 69) / 12.0) }
}

data class Melody(val title: String, val bpm: Int, val notes: List<Note>) {
    val totalBeats: Double get() = notes.sumOf { it.beats }

    companion object {
        private val PITCH_CLASSES = mapOf('C' to 0, 'D' to 2, 'E' to 4, 'F' to 5, 'G' to 7, 'A' to 9, 'B' to 11)

        /**
         * Parses a compact score: space-separated `<pitch><octave>:<beats>` tokens, e.g. `F#5:0.5`,
         * with `R:<beats>` for rests and `|` as an optional bar separator.
         */
        fun parse(title: String, bpm: Int, score: String): Melody {
            val notes = score.split(Regex("\\s+")).filter { it.isNotBlank() && it != "|" }.map { token ->
                val (pitch, beats) = token.split(':').let { it[0] to it[1].toDouble() }
                if (pitch == "R") return@map Note(null, beats)
                val pitchClass = PITCH_CLASSES.getValue(pitch[0])
                val sharp = if (pitch[1] == '#') 1 else 0
                val octave = pitch.substring(1 + sharp).toInt()
                Note(12 * (octave + 1) + pitchClass + sharp, beats)
            }
            return Melody(title, bpm, notes)
        }
    }
}

/** Public-domain carols, transcribed one octave up so they sound like a music box. */
object Carols {

    val jingleBells = Melody.parse(
        title = "Jingle Bells",
        bpm = 132,
        score = """
            E5:1 E5:1 E5:2 | E5:1 E5:1 E5:2 | E5:1 G5:1 C5:1.5 D5:0.5 | E5:4
            F5:1 F5:1 F5:1.5 F5:0.5 | F5:1 E5:1 E5:1 E5:0.5 E5:0.5 | E5:1 D5:1 D5:1 E5:1 | D5:2 G5:2
            E5:1 E5:1 E5:2 | E5:1 E5:1 E5:2 | E5:1 G5:1 C5:1.5 D5:0.5 | E5:4
            F5:1 F5:1 F5:1.5 F5:0.5 | F5:1 E5:1 E5:1 E5:0.5 E5:0.5 | G5:1 G5:1 F5:1 D5:1 | C5:4
        """,
    )

    val weWishYouAMerryChristmas = Melody.parse(
        title = "We Wish You a Merry Christmas",
        bpm = 150,
        score = """
            R:2 D5:1 | G5:1 G5:0.5 A5:0.5 G5:0.5 F#5:0.5 | E5:1 E5:1 E5:1
            A5:1 A5:0.5 B5:0.5 A5:0.5 G5:0.5 | F#5:1 D5:1 D5:1
            B5:1 B5:0.5 C6:0.5 B5:0.5 A5:0.5 | G5:1 E5:1 D5:0.5 D5:0.5 | E5:1 A5:1 F#5:1 | G5:3
        """,
    )

    val all: List<Melody> = listOf(jingleBells, weWishYouAMerryChristmas)
}
