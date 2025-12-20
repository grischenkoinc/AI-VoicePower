package com.aivoicepower.domain.model.user

enum class UserGoal {
    CLEAR_SPEECH,      // Говорити чіткіше
    PUBLIC_SPEAKING,   // Виступати впевнено
    BETTER_VOICE,      // Покращити голос
    PERSUASION,        // Переконувати
    INTERVIEW_PREP,    // Підготовка до співбесіди
    GENERAL;           // Загальний розвиток

    fun getDisplayName(): String {
        return when (this) {
            CLEAR_SPEECH -> "Говорити чіткіше"
            PUBLIC_SPEAKING -> "Виступати впевнено"
            BETTER_VOICE -> "Покращити голос"
            PERSUASION -> "Переконувати"
            INTERVIEW_PREP -> "Підготовка до співбесіди"
            GENERAL -> "Загальний розвиток"
        }
    }
}
