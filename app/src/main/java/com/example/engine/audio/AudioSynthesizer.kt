package com.example.engine.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

/**
 * Low-latency PCM audio tone synthesizer using native AudioTrack.
 * Generates emergency sirens and tactile walkie-talkie roger/chirp beeps
 * completely offline without external audio assets.
 */
object AudioSynthesizer {

    private const val SAMPLE_RATE = 22050

    suspend fun playChirp(isStart: Boolean) = withContext(Dispatchers.Default) {
        val freq1 = if (isStart) 600.0 else 1200.0
        val freq2 = if (isStart) 1200.0 else 600.0
        val durationMs = 70
        playDualTone(freq1, freq2, durationMs, 0.4f)
    }

    suspend fun playEmergencyAlert() = withContext(Dispatchers.Default) {
        // High-priority dual-frequency alert warble
        repeat(3) {
            playDualTone(950.0, 1450.0, 140, 1.0f)
            playDualTone(1450.0, 950.0, 140, 1.0f)
        }
    }

    private fun playDualTone(freq1: Double, freq2: Double, durationMs: Int, volume: Float) {
        try {
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val pcm = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                // Linear chirp between freq1 and freq2
                val currentFreq = freq1 + (freq2 - freq1) * (i.toDouble() / numSamples)
                val sample = sin(2.0 * Math.PI * currentFreq * t) * (Short.MAX_VALUE * volume)
                pcm[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(numSamples * 2)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcm, 0, pcm.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            // Ignore if audio hardware unavailable in test environment
        }
    }
}
