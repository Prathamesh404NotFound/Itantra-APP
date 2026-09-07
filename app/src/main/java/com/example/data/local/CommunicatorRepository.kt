package com.example.data.local

import com.example.core.model.AccuracyResult
import com.example.core.model.VoicePacket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CommunicatorRepository(
    private val database: AppDatabase
) {
    val allMessages: Flow<List<VoicePacket>> = database.messageDao().getAllMessages()
        .map { entities -> entities.map { it.toVoicePacket() } }

    val emergencyMessages: Flow<List<VoicePacket>> = database.messageDao().getEmergencyMessages()
        .map { entities -> entities.map { it.toVoicePacket() } }

    fun searchMessages(query: String): Flow<List<VoicePacket>> {
        return database.messageDao().searchMessages(query)
            .map { entities -> entities.map { it.toVoicePacket() } }
    }

    suspend fun saveMessage(packet: VoicePacket) = withContext(Dispatchers.IO) {
        database.messageDao().insertMessage(MessageEntity.fromVoicePacket(packet))
    }

    suspend fun updateDeliveryState(messageId: String, state: String) = withContext(Dispatchers.IO) {
        database.messageDao().updateDeliveryState(messageId, state)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        database.messageDao().clearAllMessages()
    }

    val allTtsEvaluations: Flow<List<TtsEvaluationEntity>> = database.ttsEvaluationDao().getAllEvaluations()

    suspend fun saveTtsEvaluation(languageCode: String, sampleText: String, rating: Int) = withContext(Dispatchers.IO) {
        database.ttsEvaluationDao().insertEvaluation(
            TtsEvaluationEntity(
                languageCode = languageCode,
                sampleText = sampleText,
                rating = rating
            )
        )
    }

    val allAccuracyTests: Flow<List<AccuracyTestEntity>> = database.accuracyTestDao().getAllAccuracyTests()

    suspend fun saveAccuracyResult(result: AccuracyResult) = withContext(Dispatchers.IO) {
        database.accuracyTestDao().insertAccuracyTest(
            AccuracyTestEntity(
                languageCode = result.language.code,
                referenceText = result.referenceText,
                recognizedText = result.recognizedText,
                wer = result.wer
            )
        )
    }
}
