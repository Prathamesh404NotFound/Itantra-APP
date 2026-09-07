package com.example.engine.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.example.core.protocol.VadState
import com.example.core.protocol.VoiceActivityDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Native AudioRecord management engine.
 * Captures 16kHz 16-bit mono PCM.
 * Feeds frames directly into VoiceActivityDetector.
 * Computes live amplitude meter for UI feedback.
 */
class AudioRecorderManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val vad = VoiceActivityDetector()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _vadState = MutableStateFlow<VadState>(VadState.Silence)
    val vadState: StateFlow<VadState> = _vadState.asStateFlow()

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startCapture(
        onVadStateChanged: (VadState) -> Unit = {},
        onPcmFrame: (ShortArray, Int) -> Unit = { _, _ -> }
    ) {
        if (!hasPermission()) {
            return
        }

        stopCapture()
        vad.reset()

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = minBufferSize.coerceAtLeast(1024)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return
            }

            audioRecord?.startRecording()
            _isRecording.value = true

            recordingJob = scope.launch(Dispatchers.IO) {
                val pcmBuffer = ShortArray(512) // 32ms chunk at 16kHz
                while (isActive && _isRecording.value) {
                    val readSamples = audioRecord?.read(pcmBuffer, 0, pcmBuffer.size) ?: 0
                    if (readSamples > 0) {
                        // Amplitude calculation
                        var maxAmp = 0
                        for (i in 0 until readSamples) {
                            val absVal = abs(pcmBuffer[i].toInt())
                            if (absVal > maxAmp) maxAmp = absVal
                        }
                        _amplitude.value = (maxAmp / 32768.0f).coerceIn(0f, 1f)

                        // VAD evaluation
                        val state = vad.processFrame(pcmBuffer, readSamples)
                        _vadState.value = state
                        onVadStateChanged(state)
                        onPcmFrame(pcmBuffer, readSamples)
                    }
                }
            }
        } catch (e: Exception) {
            _isRecording.value = false
        }
    }

    fun stopCapture() {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
            audioRecord?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioRecord = null
        _amplitude.value = 0f
        _vadState.value = VadState.Silence
    }
}
