package com.aivoicepower.domain.model.exercise

enum class ExerciseType {
    // Існуючі
    ARTICULATION,           // Артикуляційна вправа (без запису)
    TONGUE_TWISTER,         // Скоромовка з AI аналізом
    READING,                // Читання тексту
    EMOTION_READING,        // Читання з емоцією
    FREE_SPEECH,            // Вільне мовлення на тему
    RETELLING,              // Переказ
    DIALOGUE,               // Читання діалогу
    PITCH,                  // Презентація/pitch
    QA,                     // Відповіді на питання

    // Нові для курсу "Чітке мовлення"
    TONGUE_TWISTER_BATTLE,  // 3 скоромовки поспіль без помилок
    MINIMAL_PAIRS,          // Схожі слова (бик/бік)
    CONTRAST_SOUNDS,        // Чергування звуків (ша-са-ша-са)
    SLOW_MOTION,            // Скоромовка в уповільненому темпі

    // Нові для курсу "Сила голосу"
    BREATHING               // Дихальна вправа
}

fun ExerciseType.toDisplayString(): String {
    return when (this) {
        ExerciseType.ARTICULATION -> "Артикуляційна вправа"
        ExerciseType.TONGUE_TWISTER -> "Скоромовка"
        ExerciseType.READING -> "Читання"
        ExerciseType.EMOTION_READING -> "Емоційне читання"
        ExerciseType.FREE_SPEECH -> "Вільне мовлення"
        ExerciseType.RETELLING -> "Переказ"
        ExerciseType.DIALOGUE -> "Діалог"
        ExerciseType.PITCH -> "Презентація"
        ExerciseType.QA -> "Питання-відповіді"
        ExerciseType.TONGUE_TWISTER_BATTLE -> "Батл скоромовок"
        ExerciseType.MINIMAL_PAIRS -> "Схожі слова"
        ExerciseType.CONTRAST_SOUNDS -> "Чергування звуків"
        ExerciseType.SLOW_MOTION -> "Повільна скоромовка"
        ExerciseType.BREATHING -> "Дихальна вправа"
    }
}
