package com.aivoicepower.domain.model.exercise

enum class AnimationType {
    BREATHING_CIRCLE,    // Коло що розширюється/стискається
    BREATHING_SQUARE,    // Квадратне дихання (4-4-4-4)
    TIMER_COUNTDOWN;     // Просто таймер

    fun getDisplayName(): String {
        return when (this) {
            BREATHING_CIRCLE -> "Коло дихання"
            BREATHING_SQUARE -> "Квадратне дихання"
            TIMER_COUNTDOWN -> "Таймер"
        }
    }
}
