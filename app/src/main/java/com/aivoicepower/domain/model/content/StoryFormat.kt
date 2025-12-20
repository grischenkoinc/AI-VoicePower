package com.aivoicepower.domain.model.content

enum class StoryFormat {
    WITH_PROMPTS,       // Історія з підказками
    FROM_IMAGE,         // За картинкою
    CONTINUE,           // Продовж історію
    RANDOM_WORDS;       // Включи 3 випадкові слова

    fun getDisplayName(): String {
        return when (this) {
            WITH_PROMPTS -> "З підказками"
            FROM_IMAGE -> "За картинкою"
            CONTINUE -> "Продовж історію"
            RANDOM_WORDS -> "Випадкові слова"
        }
    }
}
