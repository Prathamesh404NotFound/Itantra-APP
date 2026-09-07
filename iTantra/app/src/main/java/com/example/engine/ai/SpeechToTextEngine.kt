package com.example.engine.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.core.model.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface SpeechToTextEngine {
    val isStreaming: StateFlow<Boolean>
    val partialTranscript: StateFlow<String>
    val finalTranscript: StateFlow<String>
    val rmsLevel: StateFlow<Float>

    fun startListening(
        language: Language,
        onPartialResult: (String) -> Unit,
        onFinalResult: (String, Long) -> Unit,
        onError: (String) -> Unit
    )

    fun stopListening()
    fun release()
}

/**
 * Android SpeechRecognizer implementation with offline preference flag.
 * Runs on Main Looper to guarantee safe execution across all Android devices.
 * Extracts live RMS audio meter for real-time voice feedback.
 */
class AndroidOfflineSTTEngine(
    private val context: Context
) : SpeechToTextEngine {

    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isStreaming = MutableStateFlow(false)
    override val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _partialTranscript = MutableStateFlow("")
    override val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

    private val _finalTranscript = MutableStateFlow("")
    override val finalTranscript: StateFlow<String> = _finalTranscript.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    override val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private var speechStartTime = 0L

    override fun startListening(
        language: Language,
        onPartialResult: (String) -> Unit,
        onFinalResult: (String, Long) -> Unit,
        onError: (String) -> Unit
    ) {
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                onError("On-device speech recognizer is unavailable on this device.")
                return@post
            }

            release()

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            speechStartTime = System.currentTimeMillis()
                            _isStreaming.value = true
                        }

                        override fun onBeginningOfSpeech() {
                            _isStreaming.value = true
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            // Normalize dB (-2 to 10 typical range for speech)
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1f)
                            _rmsLevel.value = normalized
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _isStreaming.value = false
                            _rmsLevel.value = 0f
                        }

                        override fun onError(error: Int) {
                            _isStreaming.value = false
                            _rmsLevel.value = 0f
                            val msg = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                                SpeechRecognizer.ERROR_NETWORK,
                                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Offline speech model required"
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized, please speak clearly"
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer busy"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                                else -> "Speech recognition error ($error)"
                            }
                            onError(msg)
                        }

                        override fun onResults(results: Bundle?) {
                            _isStreaming.value = false
                            _rmsLevel.value = 0f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val transcript = matches?.firstOrNull() ?: ""
                            val latency = System.currentTimeMillis() - speechStartTime
                            _finalTranscript.value = transcript
                            onFinalResult(transcript, latency.coerceAtLeast(30L))
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull() ?: ""
                            if (text.isNotBlank()) {
                                _partialTranscript.value = text
                                onPartialResult(text)
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.locale.toLanguageTag())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    // Prefer on-device offline recognition
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }

                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _isStreaming.value = false
                _rmsLevel.value = 0f
                onError("Failed to start voice recognizer: ${e.message}")
            }
        }
    }

    override fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                // Ignore
            }
            _isStreaming.value = false
            _rmsLevel.value = 0f
        }
    }

    override fun release() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                // Ignore
            }
            speechRecognizer = null
            _isStreaming.value = false
            _rmsLevel.value = 0f
        }
    }
}

/**
 * High-performance streaming simulator for on-device testing & environments
 * without proprietary speech service APKs installed.
 * Supports realistic Indic word streaming, pause intervals, and latency metrics.
 */
class StreamingOfflineSTTEngine(
    private val scope: CoroutineScope
) : SpeechToTextEngine {

    private val _isStreaming = MutableStateFlow(false)
    override val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _partialTranscript = MutableStateFlow("")
    override val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

    private val _finalTranscript = MutableStateFlow("")
    override val finalTranscript: StateFlow<String> = _finalTranscript.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    override val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private var streamingJob: Job? = null
    private var startTime = 0L

    private val sampleSentencesByLanguage = mapOf(
        Language.MARATHI to listOf(
            "मला मदत हवी आहे",
            "मी सुरक्षित ठिकाणी पोहोचलो आहे",
            "पुढील सूचनांची वाट पाहत आहे",
            "आम्हाला पाण्याची आवश्यकता आहे"
        ),
        Language.HINDI to listOf(
            "मुझे मदद चाहिए",
            "मैं सुरक्षित स्थान पर पहुँच गया हूँ",
            "हम आगे की सूचना का इंतजार कर रहे हैं",
            "यहाँ तत्काल सहायता की आवश्यकता है"
        ),
        Language.GUJARATI to listOf(
            "મને મદદની જરૂર છે",
            "હું સુરક્ષિત સ્થળે પહોંચી ગયો છું",
            "અહીં તાત્કાલિક સહાયતાની જરૂર છે"
        ),
        Language.TAMIL to listOf(
            "எனக்கு உதவி தேவை",
            "நான் பாதுகாப்பான இடத்தில் இருக்கிறேன்",
            "உடனடி உதவி தேவைப்படுகிறது"
        ),
        Language.TELUGU to listOf(
            "నాకు సహాయం కావాలి",
            "నేను సురక్షిత ప్రదేశంలో ఉన్నాను",
            "తక్షణ సహాయం అవసరం"
        ),
        Language.KANNADA to listOf(
            "ನನಗೆ ಸಹಾಯ ಬೇಕು",
            "ನಾನು ಸುರಕ್ಷಿತ ಸ್ಥಳಕ್ಕೆ ತಲುಪಿದ್ದೇನೆ",
            "ತುರ್ತು ಸಹಾಯದ ಅಗತ್ಯವಿದೆ"
        ),
        Language.MALAYALAM to listOf(
            "എനിക്ക് സഹായം ആവശ്യമാണ്",
            "ഞാൻ സുരക്ഷിതമായ സ്ഥലത്താണ്"
        ),
        Language.BENGALI to listOf(
            "আমার সাহায্য প্রয়োজন",
            "আমি নিরাপদ স্থানে পৌঁছেছি"
        ),
        Language.ODIA to listOf(
            "ମୋତେ ସାହାଯ୍ୟ ଦରକାର",
            "ମୁଁ ସୁରକ୍ଷିତ ସ୍ଥାନରେ ପହଞ୍ଚିଛି"
        ),
        Language.ENGLISH to listOf(
            "I need immediate assistance at this location",
            "Arrived safely at local checkpoint",
            "Battery low, standing by for next update",
            "Clear line, ready to proceed"
        )
    )

    override fun startListening(
        language: Language,
        onPartialResult: (String) -> Unit,
        onFinalResult: (String, Long) -> Unit,
        onError: (String) -> Unit
    ) {
        stopListening()
        startTime = System.currentTimeMillis()
        _isStreaming.value = true
        _partialTranscript.value = ""

        val candidates = sampleSentencesByLanguage[language] ?: sampleSentencesByLanguage[Language.ENGLISH]!!
        val sentence = candidates.random()
        val words = sentence.split(" ")

        streamingJob = scope.launch(Dispatchers.Default) {
            val builder = StringBuilder()
            for (word in words) {
                delay(220) // Streaming token latency
                if (!_isStreaming.value) break
                if (builder.isNotEmpty()) builder.append(" ")
                builder.append(word)
                _partialTranscript.value = builder.toString()
                onPartialResult(builder.toString())
            }

            delay(150)
            if (_isStreaming.value) {
                _isStreaming.value = false
                val finalized = builder.toString()
                _finalTranscript.value = finalized
                val latency = System.currentTimeMillis() - startTime
                onFinalResult(finalized, latency)
            }
        }
    }

    override fun stopListening() {
        streamingJob?.cancel()
        streamingJob = null
        _isStreaming.value = false
    }

    override fun release() {
        stopListening()
    }
}
