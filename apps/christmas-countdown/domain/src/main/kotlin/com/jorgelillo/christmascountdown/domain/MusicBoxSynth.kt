package com.jorgelillo.christmascountdown.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

/**
 * Renders a [Melody] to 16-bit mono PCM with a simple music-box timbre.
 * The buffer loops seamlessly: ringing notes that run past the end wrap around to the start.
 */
object MusicBoxSynth {

    const val DEFAULT_SAMPLE_RATE = 22_050
    private const val RING_SECONDS = 1.2
    private const val ATTACK_SECONDS = 0.004

    fun render(melody: Melody, sampleRate: Int = DEFAULT_SAMPLE_RATE): ShortArray {
        val secondsPerBeat = 60.0 / melody.bpm
        val length = (melody.totalBeats * secondsPerBeat * sampleRate).toInt()
        val mix = FloatArray(length)
        val ringSamples = (RING_SECONDS * sampleRate).toInt()
        val attackSamples = (ATTACK_SECONDS * sampleRate).toInt()

        var beat = 0.0
        for (note in melody.notes) {
            val frequency = note.frequency
            if (frequency != null) {
                val start = (beat * secondsPerBeat * sampleRate).toInt()
                for (i in 0 until ringSamples) {
                    val t = i.toDouble() / sampleRate
                    val phase = 2 * PI * frequency * t
                    val attack = (i.toDouble() / attackSamples).coerceAtMost(1.0)
                    val sample = attack * exp(-t * 3.2) *
                        (sin(phase) + 0.35 * exp(-t * 6) * sin(2 * phase) + 0.12 * exp(-t * 10) * sin(3 * phase))
                    mix[(start + i) % length] += sample.toFloat()
                }
            }
            beat += note.beats
        }

        val peak = mix.maxOf { abs(it) }.takeIf { it > 0f } ?: 1f
        val gain = 0.8f * Short.MAX_VALUE / peak
        return ShortArray(length) { (mix[it] * gain).toInt().toShort() }
    }
}
