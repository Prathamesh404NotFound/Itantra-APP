package com.example.core.model

import java.util.Locale

/**
 * 10 Supported Indian Languages with modular STT, TTS, and model metadata.
 * Designed for low-memory, on-device footprint.
 */
enum class Language(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val scriptName: String,
    val locale: Locale,
    val sttModelName: String,
    val ttsModelName: String,
    val sttModelSizeMb: Float,
    val ttsModelSizeMb: Float,
    val isBundledReady: Boolean = true
) {
    HINDI(
        code = "hi",
        displayName = "Hindi",
        nativeName = "हिन्दी",
        scriptName = "Devanagari",
        locale = Locale("hi", "IN"),
        sttModelName = "IndicConformer-Hi-Q4",
        ttsModelName = "IndicPiper-Hi-Compact",
        sttModelSizeMb = 38.5f,
        ttsModelSizeMb = 24.2f
    ),
    GUJARATI(
        code = "gu",
        displayName = "Gujarati",
        nativeName = "ગુજરાતી",
        scriptName = "Gujarati",
        locale = Locale("gu", "IN"),
        sttModelName = "IndicConformer-Gu-Q4",
        ttsModelName = "IndicPiper-Gu-Compact",
        sttModelSizeMb = 36.2f,
        ttsModelSizeMb = 23.8f
    ),
    MARATHI(
        code = "mr",
        displayName = "Marathi",
        nativeName = "मराठी",
        scriptName = "Devanagari",
        locale = Locale("mr", "IN"),
        sttModelName = "IndicConformer-Mr-Q4",
        ttsModelName = "IndicPiper-Mr-Compact",
        sttModelSizeMb = 37.8f,
        ttsModelSizeMb = 24.0f
    ),
    KANNADA(
        code = "kn",
        displayName = "Kannada",
        nativeName = "ಕನ್ನಡ",
        scriptName = "Kannada",
        locale = Locale("kn", "IN"),
        sttModelName = "IndicConformer-Kn-Q4",
        ttsModelName = "IndicPiper-Kn-Compact",
        sttModelSizeMb = 39.1f,
        ttsModelSizeMb = 24.5f
    ),
    MALAYALAM(
        code = "ml",
        displayName = "Malayalam",
        nativeName = "മലയാളം",
        scriptName = "Malayalam",
        locale = Locale("ml", "IN"),
        sttModelName = "IndicConformer-Ml-Q4",
        ttsModelName = "IndicPiper-Ml-Compact",
        sttModelSizeMb = 41.0f,
        ttsModelSizeMb = 25.2f
    ),
    TAMIL(
        code = "ta",
        displayName = "Tamil",
        nativeName = "தமிழ்",
        scriptName = "Tamil",
        locale = Locale("ta", "IN"),
        sttModelName = "IndicConformer-Ta-Q4",
        ttsModelName = "IndicPiper-Ta-Compact",
        sttModelSizeMb = 40.4f,
        ttsModelSizeMb = 24.8f
    ),
    TELUGU(
        code = "te",
        displayName = "Telugu",
        nativeName = "తెలుగు",
        scriptName = "Telugu",
        locale = Locale("te", "IN"),
        sttModelName = "IndicConformer-Te-Q4",
        ttsModelName = "IndicPiper-Te-Compact",
        sttModelSizeMb = 39.8f,
        ttsModelSizeMb = 24.6f
    ),
    ODIA(
        code = "or",
        displayName = "Odia",
        nativeName = "ଓଡ଼ିଆ",
        scriptName = "Odia",
        locale = Locale("or", "IN"),
        sttModelName = "IndicConformer-Or-Q4",
        ttsModelName = "IndicPiper-Or-Compact",
        sttModelSizeMb = 35.6f,
        ttsModelSizeMb = 22.9f
    ),
    BENGALI(
        code = "bn",
        displayName = "Bengali",
        nativeName = "বাংলা",
        scriptName = "Bengali",
        locale = Locale("bn", "IN"),
        sttModelName = "IndicConformer-Bn-Q4",
        ttsModelName = "IndicPiper-Bn-Compact",
        sttModelSizeMb = 38.0f,
        ttsModelSizeMb = 24.1f
    ),
    ENGLISH(
        code = "en",
        displayName = "English",
        nativeName = "English",
        scriptName = "Latin",
        locale = Locale("en", "IN"),
        sttModelName = "IndicConformer-En-Q4",
        ttsModelName = "IndicPiper-En-Compact",
        sttModelSizeMb = 34.0f,
        ttsModelSizeMb = 21.5f
    );

    val totalModelSizeMb: Float
        get() = sttModelSizeMb + ttsModelSizeMb

    companion object {
        fun fromCode(code: String): Language {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
