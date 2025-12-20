package com.aivoicepower.domain.model.exercise

enum class WarmupCategory {
    ARTICULATION,   // Артикуляційна гімнастика
    BREATHING,      // Дихальні вправи
    VOICE;          // Розминка голосу

    fun getDisplayName(): String {
        return when (this) {
            ARTICULATION -> "Артикуляція"
            BREATHING -> "Дихання"
            VOICE -> "Голос"
        }
    }

    fun getDescription(): String {
        return when (this) {
            ARTICULATION -> "Розминка м'язів обличчя та язика"
            BREATHING -> "Дихальні вправи для контролю голосу"
            VOICE -> "Розігрів голосових зв'язок"
        }
    }
}
