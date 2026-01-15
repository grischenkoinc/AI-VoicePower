package com.aivoicepower.domain.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiAnalyzer @Inject constructor() {

    suspend fun analyzeText(prompt: String): String {
        // TODO: Integrate with Gemini API
        // For now, return mock response
        return """
        {
            "overallScore": 75,
            "readingScore": 80,
            "dictionScore": 70,
            "emotionalScore": 75,
            "freeSpeechScore": 78,
            "readingFeedback": {
                "strengths": ["Чітке вимовляння слів", "Гарний темп мовлення"],
                "improvements": ["Інтонаційна виразність", "Паузи між реченнями"]
            },
            "dictionFeedback": {
                "strengths": ["Чітка артикуляція", "Правильна вимова звуків"],
                "improvements": ["Складні звукосполучення", "Темп скоромовки"]
            },
            "emotionalFeedback": {
                "strengths": ["Виразність голосу", "Емоційне забарвлення"],
                "improvements": ["Більший діапазон емоцій", "Природність передачі почуттів"]
            },
            "freeSpeechFeedback": {
                "strengths": ["Впевненість у мовленні", "Логічна структура"],
                "improvements": ["Уникання слів-паразитів", "Більша спонтанність"]
            },
            "recommendations": [
                "Почніть з курсу 'Чітке мовлення' для покращення артикуляції",
                "Практикуйте скоромовки щодня протягом 10-15 хвилин",
                "Працюйте над інтонацією через читання вголос з різними емоціями"
            ],
            "strengths": [
                "Гарна дикція та чіткість вимови",
                "Впевнений темп мовлення",
                "Логічна побудова думок"
            ],
            "areasToImprove": [
                "Емоційна виразність та інтонація",
                "Природність у передачі емоцій",
                "Використання пауз для акцентування"
            ]
        }
        """.trimIndent()
    }
}
