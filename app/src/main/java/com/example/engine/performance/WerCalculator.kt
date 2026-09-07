package com.example.engine.performance

import com.example.core.model.AccuracyResult
import com.example.core.model.Language
import kotlin.math.min

/**
 * Word Error Rate (WER) benchmark engine.
 * Implements Wagner-Fischer dynamic programming algorithm over word tokens.
 * Computes exact Substitutions (S), Deletions (D), and Insertions (I).
 * WER = (S + D + I) / N
 */
object WerCalculator {

    val benchmarkSentences = mapOf(
        Language.MARATHI to listOf(
            "मला मदत हवी आहे",
            "मी सुरक्षित ठिकाणी पोहोचलो आहे",
            "पुढील सूचनांची वाट पाहत आहे"
        ),
        Language.HINDI to listOf(
            "मुझे मदद चाहिए",
            "मैं सुरक्षित स्थान पर पहुँच गया हूँ",
            "यहाँ तत्काल सहायता की आवश्यकता है"
        ),
        Language.GUJARATI to listOf(
            "મને મદદની જરૂર છે",
            "હું સુરક્ષિત સ્થળે પહોંચી ગયો છું"
        ),
        Language.TAMIL to listOf(
            "எனக்கு உதவி தேவை",
            "நான் பாதுகாப்பான இடத்தில் இருக்கிறேன்"
        ),
        Language.TELUGU to listOf(
            "నాకు సహాయం కావాలి",
            "నేను సురక్షిత ప్రాంతానికి చేరుకున్నాను"
        ),
        Language.KANNADA to listOf(
            "ನನಗೆ ಸಹಾಯ ಬೇಕು",
            "ನಾನು ಸುರಕ್ಷಿತ ಸ್ಥಳಕ್ಕೆ ತಲುಪಿದ್ದೇನೆ"
        ),
        Language.MALAYALAM to listOf(
            "എനിക്ക് സഹಾಯം ആവശ്യമാണ്",
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
            "Standing by for further instructions"
        )
    )

    fun calculateWer(
        language: Language,
        referenceText: String,
        recognizedText: String
    ): AccuracyResult {
        val refWords = tokenize(referenceText)
        val hypWords = tokenize(recognizedText)

        val n = refWords.size
        val m = hypWords.size

        if (n == 0) {
            val wer = if (m == 0) 0f else 1f
            return AccuracyResult(
                language = language,
                referenceText = referenceText,
                recognizedText = recognizedText,
                wer = wer,
                substitutions = 0,
                deletions = 0,
                insertions = m,
                totalWords = 0
            )
        }

        val d = Array(n + 1) { IntArray(m + 1) }

        for (i in 0..n) d[i][0] = i
        for (j in 0..m) d[0][j] = j

        for (i in 1..n) {
            for (j in 1..m) {
                val cost = if (refWords[i - 1] == hypWords[j - 1]) 0 else 1
                d[i][j] = min(
                    d[i - 1][j] + 1, // deletion
                    min(
                        d[i][j - 1] + 1, // insertion
                        d[i - 1][j - 1] + cost // substitution
                    )
                )
            }
        }

        // Backtrack to count S, D, I
        var i = n
        var j = m
        var substitutions = 0
        var deletions = 0
        var insertions = 0

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && d[i][j] == d[i - 1][j - 1] && refWords[i - 1] == hypWords[j - 1]) {
                i--
                j--
            } else if (i > 0 && j > 0 && d[i][j] == d[i - 1][j - 1] + 1) {
                substitutions++
                i--
                j--
            } else if (i > 0 && d[i][j] == d[i - 1][j] + 1) {
                deletions++
                i--
            } else if (j > 0 && d[i][j] == d[i][j - 1] + 1) {
                insertions++
                j--
            } else {
                if (i > 0) i-- else if (j > 0) j--
            }
        }

        val totalErrors = substitutions + deletions + insertions
        val wer = (totalErrors.toFloat() / n.toFloat()).coerceAtLeast(0f)

        return AccuracyResult(
            language = language,
            referenceText = referenceText,
            recognizedText = recognizedText,
            wer = wer,
            substitutions = substitutions,
            deletions = deletions,
            insertions = insertions,
            totalWords = n
        )
    }

    private fun tokenize(text: String): List<String> {
        return text.trim()
            .replace("[।॥,?.!;:\"'\\[\\](){}]".toRegex(), "")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
    }
}
