package com.aivoicepower.domain.model.exercise

enum class ExerciseType {
    TONGUE_TWISTER,     // Скоромовка з AI аналізом
    READING,            // Читання тексту
    EMOTION_READING,    // Читання з емоцією
    FREE_SPEECH,        // Вільне мовлення на тему
    RETELLING,          // Переказ
    DIALOGUE,           // Читання діалогу
    PITCH,              // Презентація/pitch
    QA,                 // Відповіді на питання
    ARTICULATION        // Артикуляційна вправа (без запису)
}

fun ExerciseType.toDisplayString(): String {
    return when (this) {
        ExerciseType.TONGUE_TWISTER -> "Скоромовка"
        ExerciseType.READING -> "Читання"
        ExerciseType.EMOTION_READING -> "Емоційне читання"
        ExerciseType.FREE_SPEECH -> "Вільне мовлення"
        ExerciseType.RETELLING -> "Переказ"
        ExerciseType.DIALOGUE -> "Діалог"
        ExerciseType.PITCH -> "Презентація"
        ExerciseType.QA -> "Питання-відповіді"
        ExerciseType.ARTICULATION -> "Артикуляційна вправа"
    }
}
