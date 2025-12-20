package com.aivoicepower.domain.model.exercise

enum class Emotion {
    NEUTRAL,
    JOY,
    SADNESS,
    ANGER,
    SURPRISE,
    FEAR;

    fun getDisplayName(): String {
        return when (this) {
            NEUTRAL -> "Нейтрально"
            JOY -> "Радість"
            SADNESS -> "Сум"
            ANGER -> "Гнів"
            SURPRISE -> "Здивування"
            FEAR -> "Страх"
        }
    }

    fun getEmoji(): String {
        return when (this) {
            NEUTRAL -> "😐"
            JOY -> "😊"
            SADNESS -> "😢"
            ANGER -> "😠"
            SURPRISE -> "😲"
            FEAR -> "😨"
        }
    }
}
