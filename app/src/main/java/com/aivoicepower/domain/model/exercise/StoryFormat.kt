package com.aivoicepower.domain.model.exercise

enum class StoryFormat {
    WITH_PROMPTS,    // З підказками: герой, місце, предмет, твіст
    FROM_IMAGE,      // За картинкою: опис згенерованої сцени
    CONTINUE,        // Продовж історію: початок історії
    RANDOM_WORDS     // 3 випадкові слова для вплетення в історію
}
