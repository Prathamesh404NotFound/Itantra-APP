package com.example.core.protocol

import kotlin.math.sqrt

sealed class VadState {
    object Silence : VadState()
    object SpeechStarted : VadState()
    object SpeechContinuing : VadState()
    object Pause : VadState()
    object SpeechStopped : VadState()
}

/**
 * High-efficiency, low-CPU on-device Voice Activity Detector.
 * Processes 16-bit PCM audio frames (16kHz mono).
 * Computes Root Mean Square (RMS) energy and Zero-Crossing Rate (ZCR).
 * Avoids holding CPU wake locks during silence.
 */
class VoiceActivityDetector(
    private val energyThresholdRms: Float = 650.0f,
    private val speechHangoverFrames: Int = 8, // ~250ms hangover
    private val pauseThresholdFrames: Int = 14  // ~450ms pause
) {
    private var isSpeechActive = false
    private var silenceFrameCount = 0
    private var speechFrameCount = 0

    fun processFrame(pcmShorts: ShortArray, length: Int = pcmShorts.size): VadState {
        if (length <= 0) return VadState.Silence

        // 1. Calculate RMS Energy
        var sumSquares = 0.0
        var zeroCrossings = 0
        var prevSign = pcmShorts[0] >= 0

        for (i in 0 until length) {
            val sample = pcmShorts[i].toDouble()
            sumSquares += sample * sample

            val currentSign = pcmShorts[i] >= 0
            if (currentSign != prevSign) {
                zeroCrossings++
                prevSign = currentSign
            }
        }

        val rms = sqrt(sumSquares / length).toFloat()

        val isCurrentFrameSpeech = rms > energyThresholdRms

        return if (isCurrentFrameSpeech) {
            silenceFrameCount = 0
            speechFrameCount++

            if (!isSpeechActive && speechFrameCount >= 2) {
                isSpeechActive = true
                VadState.SpeechStarted
            } else if (isSpeechActive) {
                VadState.SpeechContinuing
            } else {
                VadState.Silence
            }
        } else {
            speechFrameCount = 0
            if (isSpeechActive) {
                silenceFrameCount++
                if (silenceFrameCount in speechHangoverFrames until pauseThresholdFrames) {
                    VadState.Pause
                } else if (silenceFrameCount >= pauseThresholdFrames) {
                    isSpeechActive = false
                    VadState.SpeechStopped
                } else {
                    VadState.SpeechContinuing
                }
            } else {
                VadState.Silence
            }
        }
    }

    fun reset() {
        isSpeechActive = false
        silenceFrameCount = 0
        speechFrameCount = 0
    }
}
