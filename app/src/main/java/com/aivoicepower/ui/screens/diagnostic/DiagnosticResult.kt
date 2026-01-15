package com.aivoicepower.ui.screens.diagnostic

data class DiagnosticResult(
    val overallScore: Int,
    val readingScore: Int,
    val dictionScore: Int,
    val emotionalScore: Int,
    val freeSpeechScore: Int,
    val readingFeedback: TaskFeedback,
    val dictionFeedback: TaskFeedback,
    val emotionalFeedback: TaskFeedback,
    val freeSpeechFeedback: TaskFeedback,
    val recommendations: List<String>,
    val strengths: List<String>,
    val areasToImprove: List<String>
) {
    companion object {
        fun mock() = DiagnosticResult(
            overallScore = 75,
            readingScore = 80,
            dictionScore = 70,
            emotionalScore = 75,
            freeSpeechScore = 78,
            readingFeedback = TaskFeedback(
                strengths = listOf("Чіткість", "Темп"),
                improvements = listOf("Інтонація")
            ),
            dictionFeedback = TaskFeedback(
                strengths = listOf("Артикуляція"),
                improvements = listOf("Складні звуки")
            ),
            emotionalFeedback = TaskFeedback(
                strengths = listOf("Виразність"),
                improvements = listOf("Діапазон емоцій")
            ),
            freeSpeechFeedback = TaskFeedback(
                strengths = listOf("Впевненість"),
                improvements = listOf("Структура")
            ),
            recommendations = listOf(
                "Почніть з курсу 'Чітке мовлення'",
                "Практикуйте скоромовки щодня"
            ),
            strengths = listOf("Гарна дикція", "Впевнений темп"),
            areasToImprove = listOf("Емоційна виразність", "Інтонація")
        )
    }
}

data class TaskFeedback(
    val strengths: List<String>,
    val improvements: List<String>
)
