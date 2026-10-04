package com.jorgelillo.decisionwheel.domain

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/** Short wooden "tick" for every segment the pointer passes, as 16-bit mono PCM. */
object ClickSynth {

    fun render(sampleRate: Int = 44_100, durationMillis: Int = 18): ShortArray {
        val length = sampleRate * durationMillis / 1000
        val noise = Random(7)
        return ShortArray(length) { i ->
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 320)
            val body = sin(2 * PI * 1900 * t) * 0.6 + sin(2 * PI * 3400 * t) * 0.25
            val grain = (noise.nextDouble() * 2 - 1) * 0.25
            ((body + grain) * envelope * 0.55 * Short.MAX_VALUE).toInt().toShort()
        }
    }

    /** Wraps PCM in a minimal WAV container (what SoundPool loads). */
    fun wav(pcm: ShortArray, sampleRate: Int = 44_100): ByteArray {
        val dataSize = pcm.size * 2
        val out = java.nio.ByteBuffer.allocate(44 + dataSize).order(java.nio.ByteOrder.LITTLE_ENDIAN)
        out.put("RIFF".toByteArray()).putInt(36 + dataSize).put("WAVE".toByteArray())
        out.put("fmt ".toByteArray()).putInt(16).putShort(1).putShort(1)
            .putInt(sampleRate).putInt(sampleRate * 2).putShort(2).putShort(16)
        out.put("data".toByteArray()).putInt(dataSize)
        pcm.forEach { out.putShort(it) }
        return out.array()
    }
}
