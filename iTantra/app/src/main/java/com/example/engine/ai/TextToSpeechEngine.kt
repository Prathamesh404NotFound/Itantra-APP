package com.example.engine.ai

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.core.model.Language
import com.example.core.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface TextToSpeechEngine {
    val isSpeaking: StateFlow<Boolean>
    val currentUtteranceId: StateFlow<String?>

    fun speak(
        text: String,
        language: Language,
        priority: Priority = Priority.NORMAL,
        speechRate: Float = 1.0f,
        pitch: Float = 1.0f,
        onDone: (Long) -> Unit = {},
        onError: (String) -> Unit = {}
    )

    fun stop()
    fun pause()
    fun release()
}

class AndroidOfflineTTSEngine(
    private val context: Context
) : TextToSpeechEngine {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    override val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    private var lastDoneCallback: ((Long) -> Unit)? = null
    private var lastErrorCallback: ((String) -> Unit)? = null
    private var speechStartMs: Long = 0L

    init {
        initializeTts()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        speechStartMs = System.currentTimeMillis()
                        _isSpeaking.value = true
                        _currentUtteranceId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        val duration = System.currentTimeMillis() - speechStartMs
                        lastDoneCallback?.invoke(duration.coerceAtLeast(50L))
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        lastErrorCallback?.invoke("TTS synthesis error for utterance: $utteranceId")
                    }
                })
            } else {
                isInitialized = false
            }
        }
    }

    override fun speak(
        text: String,
        language: Language,
        priority: Priority,
        speechRate: Float,
        pitch: Float,
        onDone: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        lastDoneCallback = onDone
        lastErrorCallback = onError

        if (!isInitialized || tts == null) {
            // Re-init if needed or simulate callback
            initializeTts()
            onDone(120L)
            return
        }

        try {
            val ttsEngine = tts ?: return
            ttsEngine.setSpeechRate(speechRate)
            ttsEngine.setPitch(pitch)

            val result = ttsEngine.setLanguage(language.locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English locale if Indic voice pack not downloaded
                ttsEngine.setLanguage(Language.ENGLISH.locale)
            }

            // Emergency priority flushes queue, normal adds to queue
            val queueMode = if (priority == Priority.CRITICAL) {
                TextToSpeech.QUEUE_FLUSH
            } else {
                TextToSpeech.QUEUE_ADD
            }

            val utteranceId = "utantra_${UUID.randomUUID().toString().take(6)}"

            val params = Bundle().apply {
                if (priority == Priority.CRITICAL) {
                    putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_ALARM)
                    putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                } else {
                    putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
                }
            }

            ttsEngine.speak(text, queueMode, params, utteranceId)
        } catch (e: Exception) {
            _isSpeaking.value = false
            onError("TTS playback exception: ${e.message}")
        }
    }

    override fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    override fun pause() {
        stop()
    }

    override fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
        tts = null
        isInitialized = false
        _isSpeaking.value = false
    }
}
