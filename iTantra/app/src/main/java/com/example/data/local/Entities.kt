package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.DeliveryState
import com.example.core.model.Language
import com.example.core.model.Priority
import com.example.core.model.VoicePacket

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val messageId: String,
    val senderDeviceId: String,
    val receiverDeviceId: String,
    val sequenceNumber: Long,
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val priorityName: String,
    val timestamp: Long,
    val originalText: String,
    val translatedText: String?,
    val packetSizeBytes: Int,
    val audioSizeEstimateBytes: Int,
    val latencyMs: Long,
    val deliveryStateName: String
) {
    fun toVoicePacket(): VoicePacket {
        return VoicePacket(
            messageId = messageId,
            senderDeviceId = senderDeviceId,
            receiverDeviceId = receiverDeviceId,
            sequenceNumber = sequenceNumber,
            sourceLanguage = Language.fromCode(sourceLanguageCode),
            targetLanguage = Language.fromCode(targetLanguageCode),
            priority = try { Priority.valueOf(priorityName) } catch (e: Exception) { Priority.NORMAL },
            timestamp = timestamp,
            textPayload = originalText,
            translatedText = translatedText,
            deliveryState = try { DeliveryState.valueOf(deliveryStateName) } catch (e: Exception) { DeliveryState.DELIVERED },
            audioSizeEstimateBytes = audioSizeEstimateBytes,
            packetSizeBytes = packetSizeBytes,
            latencyMs = latencyMs
        )
    }

    companion object {
        fun fromVoicePacket(packet: VoicePacket): MessageEntity {
            return MessageEntity(
                messageId = packet.messageId,
                senderDeviceId = packet.senderDeviceId,
                receiverDeviceId = packet.receiverDeviceId,
                sequenceNumber = packet.sequenceNumber,
                sourceLanguageCode = packet.sourceLanguage.code,
                targetLanguageCode = packet.targetLanguage.code,
                priorityName = packet.priority.name,
                timestamp = packet.timestamp,
                originalText = packet.textPayload,
                translatedText = packet.translatedText,
                packetSizeBytes = packet.packetSizeBytes,
                audioSizeEstimateBytes = packet.audioSizeEstimateBytes,
                latencyMs = packet.latencyMs,
                deliveryStateName = packet.deliveryState.name
            )
        }
    }
}

@Entity(tableName = "tts_evaluations")
data class TtsEvaluationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val languageCode: String,
    val sampleText: String,
    val rating: Int, // 1 to 5
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "accuracy_tests")
data class AccuracyTestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val languageCode: String,
    val referenceText: String,
    val recognizedText: String,
    val wer: Float,
    val timestamp: Long = System.currentTimeMillis()
)
