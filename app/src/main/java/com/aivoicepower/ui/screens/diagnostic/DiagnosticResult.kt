package com.aivoicepower.ui.screens.diagnostic

data class DiagnosticResult(
    val overallScore: Int,

    // 6 core metrics
    val dictionScore: Int,        // Чіткість дикції
    val tempoScore: Int,           // Темп мовлення
    val intonationScore: Int,      // Інтонація та виразність
    val structureScore: Int,       // Структура думок
    val confidenceScore: Int,      // Впевненість
    val fillerWordsScore: Int,     // Слова-паразити (інвертовано: менше = краще)

    // Detailed feedback
    val strengths: List<String>,
    val areasToImprove: List<String>,
    val recommendations: List<String>
) {
    companion object {
        fun mock() = DiagnosticResult(
            overallScore = 72,
            dictionScore = 75,
            tempoScore = 70,
            intonationScore = 68,
            structureScore = 65,
            confidenceScore = 78,
            fillerWordsScore = 85, // Високий = мало слів-паразитів
            strengths = listOf(
                "Впевнений голос",
                "Гарна артикуляція",
                "Стабільний темп"
            ),
            areasToImprove = listOf(
                "Інтонаційна виразність",
                "Структура викладу думок",
                "Зменшити слова-паразити"
            ),
            recommendations = listOf(
                "Почніть з курсу 'Чітке мовлення'",
                "Практикуйте скоромовки щодня",
                "Записуйте себе і аналізуйте"
            )
        )
    }
}
