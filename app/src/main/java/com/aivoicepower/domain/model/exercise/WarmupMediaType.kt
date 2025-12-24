package com.aivoicepower.domain.model.exercise

enum class WarmupMediaType {
    VIDEO,          // Для артикуляції
    ANIMATION,      // Для дихання
    AUDIO;          // Для голосу

    fun getDisplayName(): String {
        return when (this) {
            VIDEO -> "Відео"
            ANIMATION -> "Анімація"
            AUDIO -> "Аудіо"
        }
    }
}
