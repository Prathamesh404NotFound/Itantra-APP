package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE priorityName = 'CRITICAL' ORDER BY timestamp DESC")
    fun getEmergencyMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE originalText LIKE '%' || :query || '%' OR (translatedText IS NOT NULL AND translatedText LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET deliveryStateName = :state WHERE messageId = :messageId")
    suspend fun updateDeliveryState(messageId: String, state: String)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
}

@Dao
interface TtsEvaluationDao {
    @Query("SELECT * FROM tts_evaluations ORDER BY timestamp DESC")
    fun getAllEvaluations(): Flow<List<TtsEvaluationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: TtsEvaluationEntity)
}

@Dao
interface AccuracyTestDao {
    @Query("SELECT * FROM accuracy_tests ORDER BY timestamp DESC")
    fun getAllAccuracyTests(): Flow<List<AccuracyTestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccuracyTest(test: AccuracyTestEntity)
}
