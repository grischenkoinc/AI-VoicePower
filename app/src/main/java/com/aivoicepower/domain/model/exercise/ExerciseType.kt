package com.aivoicepower.domain.model.exercise

enum class ExerciseType {
    TONGUE_TWISTER,     // Скоромовка
    READING,            // Читання тексту
    EMOTION_READING,    // Читання з емоцією
    FREE_SPEECH,        // Вільне мовлення на тему
    RETELLING,          // Переказ
    DIALOGUE,           // Читання діалогу
    PITCH,              // Презентація/pitch
    QA;                 // Відповіді на питання

    fun getDisplayName(): String {
        return when (this) {
            TONGUE_TWISTER -> "Скоромовка"
            READING -> "Читання"
            EMOTION_READING -> "Емоційне читання"
            FREE_SPEECH -> "Вільне мовлення"
            RETELLING -> "Переказ"
            DIALOGUE -> "Діалог"
            PITCH -> "Презентація"
            QA -> "Питання та відповіді"
        }
    }

    fun requiresAudioAnalysis(): Boolean {
        return true // Всі вправи потребують аналізу (на відміну від розминки)
    }
}
