package com.aivoicepower.domain.model.user

enum class SkillType {
    DICTION,        // Дикція
    TEMPO,          // Темп мовлення
    INTONATION,     // Інтонація
    VOLUME,         // Гучність
    STRUCTURE,      // Структура мовлення
    CONFIDENCE,     // Впевненість
    FILLER_WORDS,   // Слова-паразити (100 = немає паразитів)

    // Нові для курсу "Сила голосу"
    BREATHING,      // Дихання
    VOICE_QUALITY,  // Якість голосу
    PROJECTION,     // Проекція голосу
    RESONANCE       // Резонанс
}

fun SkillType.toDisplayString(): String {
    return when (this) {
        SkillType.DICTION -> "Дикція"
        SkillType.TEMPO -> "Темп"
        SkillType.INTONATION -> "Інтонація"
        SkillType.VOLUME -> "Гучність"
        SkillType.STRUCTURE -> "Структура"
        SkillType.CONFIDENCE -> "Впевненість"
        SkillType.FILLER_WORDS -> "Без слів-паразитів"
        SkillType.BREATHING -> "Дихання"
        SkillType.VOICE_QUALITY -> "Якість голосу"
        SkillType.PROJECTION -> "Проекція"
        SkillType.RESONANCE -> "Резонанс"
    }
}
