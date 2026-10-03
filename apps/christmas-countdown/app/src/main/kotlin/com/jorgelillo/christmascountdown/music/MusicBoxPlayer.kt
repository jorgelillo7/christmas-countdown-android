package com.jorgelillo.christmascountdown.music

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.jorgelillo.christmascountdown.domain.Carols
import com.jorgelillo.christmascountdown.domain.MusicBoxSynth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Plays the carols in a seamless loop. The audio is synthesised on the device the first time it
 * is needed (no audio files in the APK) and streamed in small chunks.
 */
class MusicBoxPlayer(private val scope: CoroutineScope) {

    private val sampleRate = MusicBoxSynth.DEFAULT_SAMPLE_RATE
    private val chunkSize = sampleRate / 10
    private var pcm: ShortArray? = null
    private var job: Job? = null

    fun play() {
        if (job?.isActive == true) return
        job = scope.launch(Dispatchers.Default) {
            val data = pcm ?: render().also { pcm = it }
            val track = createTrack()
            try {
                track.play()
                var position = 0
                while (isActive) {
                    val count = minOf(chunkSize, data.size - position)
                    track.write(data, position, count)
                    position = (position + count) % data.size
                }
            } finally {
                track.pause()
                track.flush()
                track.release()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private fun render(): ShortArray {
        val gap = ShortArray(sampleRate / 2)
        return Carols.all
            .flatMap { listOf(MusicBoxSynth.render(it, sampleRate), gap) }
            .fold(ShortArray(0)) { acc, part -> acc + part }
    }

    private fun createTrack(): AudioTrack {
        val minBuffer = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(maxOf(minBuffer, chunkSize * 2 * 2))
            .build()
            .apply { setVolume(0.6f) }
    }
}
