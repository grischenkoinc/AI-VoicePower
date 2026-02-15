package com.aivoicepower.domain.model

/**
 * Result of AI voice analysis from Gemini
 * Contains detailed metrics and feedback for voice recordings
 */
data class VoiceAnalysisResult(
    val diction: Int,           // Чіткість дикції (0-100)
    val tempo: Int,             // Темп мовлення (0-100)
    val intonation: Int,        // Інтонація/виразність (0-100)
    val volume: Int,            // Стабільність гучності (0-100)
    val confidence: Int,        // Впевненість голосу (0-100)
    val fillerWords: Int,       // Відсутність слів-паразитів (0-100, 100 = немає паразитів)
    val structure: Int = 50,    // Структура мовлення (0-100) - для спонтанного/переконливого
    val persuasiveness: Int = 50, // Переконливість (0-100) - тільки для persuasive
    val overallScore: Int,      // Загальна оцінка (0-100)
    val strengths: List<String>,     // Сильні сторони
    val improvements: List<String>,  // Зони для покращення
    val tip: String,            // Конкретна порада
    val coachComment: String = "" // Мотиваційний коментар тренера
) {
    companion object {
        /**
         * Default fallback result when analysis fails
         */
        fun default() = VoiceAnalysisResult(
            diction = 0,
            tempo = 0,
            intonation = 0,
            volume = 0,
            confidence = 0,
            fillerWords = 0,
            structure = 0,
            persuasiveness = 0,
            overallScore = 0,
            strengths = emptyList(),
            improvements = listOf("Не вдалося проаналізувати запис"),
            tip = "Спробуйте записати ще раз, переконайтесь що мікрофон працює",
            coachComment = ""
        )
    }
}
