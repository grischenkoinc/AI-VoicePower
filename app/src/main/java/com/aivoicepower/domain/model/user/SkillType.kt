package com.aivoicepower.domain.model.user

enum class SkillType {
    DICTION,        // Дикція
    TEMPO,          // Темп мовлення
    INTONATION,     // Інтонація
    VOLUME,         // Гучність
    STRUCTURE,      // Структура мовлення
    CONFIDENCE,     // Впевненість
    FILLER_WORDS;   // Слова-паразити (100 = немає паразитів)

    fun getDisplayName(): String {
        return when (this) {
            DICTION -> "Дикція"
            TEMPO -> "Темп"
            INTONATION -> "Інтонація"
            VOLUME -> "Гучність"
            STRUCTURE -> "Структура"
            CONFIDENCE -> "Впевненість"
            FILLER_WORDS -> "Чистота мовлення"
        }
    }

    fun getDescription(): String {
        return when (this) {
            DICTION -> "Чіткість вимови звуків та слів"
            TEMPO -> "Швидкість мовлення"
            INTONATION -> "Виразність та емоційність"
            VOLUME -> "Контроль гучності голосу"
            STRUCTURE -> "Логічність викладу думок"
            CONFIDENCE -> "Впевненість у мовленні"
            FILLER_WORDS -> "Відсутність слів-паразитів"
        }
    }
}
