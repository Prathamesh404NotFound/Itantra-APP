package com.example.engine.ai

import com.example.core.model.Language
import com.example.core.model.ModelDescriptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * On-device Model Manager.
 * Controls model registry, lazy-loading, and unloading unused models
 * to prevent high RAM/CPU usage on budget Android devices.
 */
class ModelManager {

    private val loadedLanguages = mutableSetOf<Language>()

    private val _modelDescriptors = MutableStateFlow<List<ModelDescriptor>>(emptyList())
    val modelDescriptors: StateFlow<List<ModelDescriptor>> = _modelDescriptors.asStateFlow()

    private val _totalMemoryUsageMb = MutableStateFlow(0f)
    val totalMemoryUsageMb: StateFlow<Float> = _totalMemoryUsageMb.asStateFlow()

    init {
        // Initially load English and Hindi by default
        loadedLanguages.add(Language.HINDI)
        loadedLanguages.add(Language.MARATHI)
        refreshDescriptors()
    }

    fun loadModelsForLanguage(language: Language) {
        if (!loadedLanguages.contains(language)) {
            // Lazy load requested language model
            loadedLanguages.add(language)
            refreshDescriptors()
        }
    }

    fun unloadUnusedModelsExcept(activeSource: Language, activeTarget: Language) {
        val iterator = loadedLanguages.iterator()
        while (iterator.hasNext()) {
            val lang = iterator.next()
            if (lang != activeSource && lang != activeTarget && lang != Language.ENGLISH) {
                iterator.remove()
            }
        }
        System.gc() // Trigger GC to reclaim unloaded model buffers
        refreshDescriptors()
    }

    fun toggleModelLoaded(language: Language) {
        if (loadedLanguages.contains(language)) {
            loadedLanguages.remove(language)
        } else {
            loadedLanguages.add(language)
        }
        refreshDescriptors()
    }

    private fun refreshDescriptors() {
        val list = Language.entries.map { lang ->
            val isLoaded = loadedLanguages.contains(lang)
            ModelDescriptor(
                language = lang,
                sttModel = lang.sttModelName,
                ttsModel = lang.ttsModelName,
                modelSizeMb = lang.totalModelSizeMb,
                isLoaded = isLoaded,
                isAvailable = lang.isBundledReady,
                memoryUsageMb = if (isLoaded) (lang.totalModelSizeMb * 0.42f) else 0f
            )
        }
        _modelDescriptors.value = list
        _totalMemoryUsageMb.value = list.sumOf { it.memoryUsageMb.toDouble() }.toFloat()
    }
}
