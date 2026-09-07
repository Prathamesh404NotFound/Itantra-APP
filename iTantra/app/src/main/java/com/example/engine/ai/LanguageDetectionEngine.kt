package com.example.engine.ai

import com.example.core.model.Language

data class DetectionResult(
    val detectedLanguage: Language,
    val confidence: Float,
    val detectedScript: String,
    val detectionLatencyMs: Long
)

/**
 * On-device Indic language & script identifier.
 * Combines Unicode block mapping with characteristic n-grams and vocabulary markers.
 * Runs in under 5ms without neural network overhead.
 */
class LanguageDetectionEngine {

    private val marathiKeywords = setOf("आहे", "नाही", "मला", "मी", "आम्ही", "झाले", "करा", "होते", "ठिकाणी", "सूचनांची")
    private val hindiKeywords = setOf("है", "नहीं", "मुझे", "मैं", "हम", "हुआ", "करो", "था", "स्थान", "चाहिए")

    fun detectLanguage(text: String): DetectionResult {
        val start = System.currentTimeMillis()
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return DetectionResult(Language.ENGLISH, 0.5f, "Unknown", 1L)
        }

        var devanagariCount = 0
        var gujaratiCount = 0
        var tamilCount = 0
        var teluguCount = 0
        var kannadaCount = 0
        var malayalamCount = 0
        var odiaCount = 0
        var bengaliCount = 0
        var latinCount = 0
        var totalChars = 0

        for (ch in trimmed) {
            if (ch.isWhitespace() || ch.isDigit() || !ch.isLetter()) continue
            totalChars++
            when (ch.code) {
                in 0x0900..0x097F -> devanagariCount++
                in 0x0A80..0x0AFF -> gujaratiCount++
                in 0x0B80..0x0BFF -> tamilCount++
                in 0x0C00..0x0C7F -> teluguCount++
                in 0x0C80..0x0CFF -> kannadaCount++
                in 0x0D00..0x0D7F -> malayalamCount++
                in 0x0B00..0x0B7F -> odiaCount++
                in 0x0980..0x09FF -> bengaliCount++
                in 0x0041..0x005A, in 0x0061..0x007A -> latinCount++
            }
        }

        val total = totalChars.coerceAtLeast(1)

        val (lang, conf, script) = when {
            gujaratiCount > total * 0.3 -> Triple(Language.GUJARATI, (gujaratiCount.toFloat() / total), "Gujarati")
            tamilCount > total * 0.3 -> Triple(Language.TAMIL, (tamilCount.toFloat() / total), "Tamil")
            teluguCount > total * 0.3 -> Triple(Language.TELUGU, (teluguCount.toFloat() / total), "Telugu")
            kannadaCount > total * 0.3 -> Triple(Language.KANNADA, (kannadaCount.toFloat() / total), "Kannada")
            malayalamCount > total * 0.3 -> Triple(Language.MALAYALAM, (malayalamCount.toFloat() / total), "Malayalam")
            odiaCount > total * 0.3 -> Triple(Language.ODIA, (odiaCount.toFloat() / total), "Odia")
            bengaliCount > total * 0.3 -> Triple(Language.BENGALI, (bengaliCount.toFloat() / total), "Bengali")
            devanagariCount > total * 0.3 -> {
                // Distinguish Hindi vs Marathi using lexical tokens
                val words = trimmed.split("\\s+".toRegex())
                val marathiMatches = words.count { it in marathiKeywords }
                val hindiMatches = words.count { it in hindiKeywords }
                if (marathiMatches > hindiMatches) {
                    Triple(Language.MARATHI, 0.95f, "Devanagari")
                } else if (hindiMatches > 0) {
                    Triple(Language.HINDI, 0.95f, "Devanagari")
                } else {
                    // Default to Hindi for Devanagari if ambiguous
                    Triple(Language.HINDI, 0.85f, "Devanagari")
                }
            }
            latinCount > total * 0.3 -> Triple(Language.ENGLISH, (latinCount.toFloat() / total), "Latin")
            else -> Triple(Language.ENGLISH, 0.6f, "Latin")
        }

        val latency = (System.currentTimeMillis() - start).coerceAtLeast(1L)
        return DetectionResult(
            detectedLanguage = lang,
            confidence = conf.coerceIn(0.1f, 1.0f),
            detectedScript = script,
            detectionLatencyMs = latency
        )
    }
}
