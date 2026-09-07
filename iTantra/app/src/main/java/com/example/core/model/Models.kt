package com.example.core.model

import java.util.UUID

enum class CommunicationMode {
    PUSH_TO_TALK,
    CONTINUOUS
}

enum class Priority {
    NORMAL,
    CRITICAL
}

enum class EmergencyCategory(
    val title: String,
    val iconName: String,
    val defaultText: String
) {
    FIRE("Fire Emergency", "fire", "Fire detected. Immediate evacuation and assistance required."),
    MEDICAL("Medical Alert", "medical", "Medical emergency. Immediate medical assistance required."),
    HELP("Immediate Help", "help", "Urgent help needed at this location."),
    DISTRESS("Distress Signal", "distress", "Distress condition reported. Assistance requested."),
    EVACUATION("Evacuate Area", "evacuate", "Danger detected. Evacuate immediate vicinity immediately."),
    CUSTOM("Emergency Broadcast", "custom", "Emergency alert broadcast.")
}

enum class DeliveryState {
    CREATED,
    ENCODING,
    SENDING,
    RECEIVED,
    DECODING,
    PROCESSING,
    PLAYING,
    DELIVERED,
    FAILED
}

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SENDING,
    DELIVERED,
    ERROR
}

enum class TransportType(val displayName: String) {
    WIFI_DIRECT("Wi-Fi Direct"),
    BLUETOOTH("Bluetooth RFCOMM"),
    LOCAL_SOCKET("Local Wi-Fi Subnet"),
    SIMULATION("Local Loopback Demo")
}

data class DeviceInfo(
    val deviceId: String = UUID.randomUUID().toString().take(8),
    val deviceName: String,
    val supportedLanguages: List<Language> = Language.entries,
    val isConnected: Boolean = false,
    val transportType: TransportType = TransportType.WIFI_DIRECT,
    val signalDbm: Int = -55,
    val ipAddress: String = "192.168.49.1",
    val port: Int = 8888,
    val lastSeen: Long = System.currentTimeMillis()
)

data class VoicePacket(
    val messageId: String = UUID.randomUUID().toString().take(8),
    val senderDeviceId: String,
    val receiverDeviceId: String,
    val sequenceNumber: Long = 1L,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val priority: Priority = Priority.NORMAL,
    val timestamp: Long = System.currentTimeMillis(),
    val textPayload: String,
    val translatedText: String? = null,
    val checksum: Long = 0L,
    val deliveryState: DeliveryState = DeliveryState.CREATED,
    val audioSizeEstimateBytes: Int = (textPayload.length * 3200), // ~16kHz 16-bit PCM estimate
    val packetSizeBytes: Int = textPayload.toByteArray(Charsets.UTF_8).size + 36,
    val latencyMs: Long = 0L
) {
    val bandwidthReductionPercent: Float
        get() {
            val raw = audioSizeEstimateBytes.coerceAtLeast(1)
            val packet = packetSizeBytes.coerceAtLeast(1)
            return ((raw - packet).toFloat() / raw.toFloat() * 100f).coerceIn(0f, 99.9f)
        }
}

data class PerformanceMetrics(
    val sttLatencyMs: Long = 0L,
    val ttsLatencyMs: Long = 0L,
    val transportLatencyMs: Long = 0L,
    val endToEndLatencyMs: Long = 0L,
    val rtf: Float = 0f,
    val ramUsageMb: Long = 0L,
    val cpuPercent: Int = 0,
    val packetSizeBytes: Int = 0,
    val rawAudioBytesEstimated: Int = 0,
    val reductionPercent: Float = 0f,
    val totalDataTransmittedBytes: Long = 0L,
    val internetDataUsedBytes: Long = 0L, // Strictly 0 in iTantra
    val isMeasuredOnDevice: Boolean = true
)

data class ModelDescriptor(
    val language: Language,
    val sttModel: String,
    val ttsModel: String,
    val modelSizeMb: Float,
    val isLoaded: Boolean,
    val isAvailable: Boolean = true,
    val memoryUsageMb: Float = if (isLoaded) (modelSizeMb * 0.45f) else 0f
)

data class AccuracyResult(
    val language: Language,
    val referenceText: String,
    val recognizedText: String,
    val wer: Float,
    val substitutions: Int,
    val deletions: Int,
    val insertions: Int,
    val totalWords: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class DiagnosticItem(
    val id: String,
    val componentName: String,
    val status: DiagnosticStatus,
    val details: String,
    val latencyMs: Long = 0L
)

enum class DiagnosticStatus {
    PASS,
    FAIL,
    NOT_AVAILABLE
}
