package com.example.core.protocol

/**
 * Lightweight, on-device sentence boundary detector for Indian scripts and English.
 * Evaluates terminal punctuations, danda (।), double danda (॥), pause intervals,
 * and grammar boundary tokens to group streaming words into complete sentences
 * before packet creation and transmission.
 */
class SentenceBoundaryDetector(
    private val pauseThresholdMs: Long = 500L
) {
    private val terminalPunctuation = setOf('.', '?', '!', '।', '॥', '\n')

    fun isSentenceComplete(
        accumulatedText: String,
        lastWordTimestamp: Long = 0L,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean {
        val trimmed = accumulatedText.trim()
        if (trimmed.isEmpty()) return false

        // Check if last character is terminal punctuation
        val lastChar = trimmed.last()
        if (lastChar in terminalPunctuation) {
            return true
        }

        // Pause duration check: If speaker stopped speaking for > pauseThresholdMs
        // and at least 2 words exist, finalize sentence
        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size >= 2 && (currentTimeMs - lastWordTimestamp) >= pauseThresholdMs) {
            return true
        }

        return false
    }

    fun formatSentence(rawText: String, defaultDanda: Boolean = false): String {
        val clean = rawText.trim().replace("\\s+".toRegex(), " ")
        if (clean.isEmpty()) return clean
        val lastChar = clean.last()
        return if (lastChar in terminalPunctuation) {
            clean
        } else {
            if (defaultDanda) "$clean ।" else "$clean."
        }
    }
}
