package com.aivoicepower.data.remote

import com.aivoicepower.domain.model.chat.ConversationContext
import com.aivoicepower.domain.model.chat.Message
import com.aivoicepower.domain.model.chat.MessageRole
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client for Gemini AI API for AI Coach functionality
 */
@Singleton
class GeminiApiClient @Inject constructor(
    private val generativeModel: GenerativeModel
) {

    /**
     * Generates AI Coach response to user message
     */
    suspend fun generateCoachResponse(
        userMessage: String,
        conversationHistory: List<Message>,
        userContext: ConversationContext
    ): Result<String> {
        return try {
            val systemPrompt = buildCoachSystemPrompt(userContext)
            val historyPrompt = buildConversationHistory(conversationHistory)

            val fullPrompt = buildString {
                append(systemPrompt)
                append("\n\n")
                if (historyPrompt.isNotBlank()) {
                    append(historyPrompt)
                    append("\n\n")
                }
                append("Користувач: $userMessage\n\nТвоя відповідь:")
            }

            val response = generativeModel.generateContent(
                content {
                    text(fullPrompt)
                }
            )

            val aiResponse = response.text
                ?: "Вибач, я не можу відповісти зараз. Спробуй переформулювати питання."

            Result.success(aiResponse.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates quick action suggestions based on user context
     */
    suspend fun generateQuickActions(
        userContext: ConversationContext
    ): Result<List<String>> {
        return try {
            val prompt = buildQuickActionsPrompt(userContext)

            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )

            val suggestions = response.text
                ?.lines()
                ?.filter { it.startsWith("-") || it.startsWith("•") || it.matches(Regex("^\\d+\\..*")) }
                ?.map {
                    it.removePrefix("-")
                        .removePrefix("•")
                        .replace(Regex("^\\d+\\.\\s*"), "")
                        .trim()
                }
                ?.filter { it.isNotBlank() }
                ?.take(5)
                ?: getDefaultQuickActions()

            Result.success(suggestions.ifEmpty { getDefaultQuickActions() })
        } catch (e: Exception) {
            // Fallback suggestions
            Result.success(getDefaultQuickActions())
        }
    }

    /**
     * Analyzes voice recording and provides feedback (for improvisation/diagnostic)
     */
    suspend fun analyzeVoice(
        transcription: String,
        context: String
    ): Result<String> {
        return try {
            val prompt = """
Ти — експерт з аналізу мовлення. Проаналізуй наступний текст та дай оцінку.

КОНТЕКСТ ВПРАВИ:
$context

ТРАНСКРИПЦІЯ:
$transcription

Дай коротку (3-5 речень) конструктивну оцінку. Вкажи:
1. Що було добре
2. Що можна покращити
3. Конкретну пораду

Відповідай українською, дружньо та мотивуюче.
            """.trimIndent()

            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )

            val feedback = response.text
                ?: "Дякую за запис! Продовжуй практикуватись."

            Result.success(feedback.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== SYSTEM PROMPTS =====

    private fun buildCoachSystemPrompt(context: ConversationContext): String {
        val contextInfo = context.toPromptContext()

        return """
Ти — AI-тренер з мовлення та публічних виступів в застосунку "AI VoicePower".

${if (contextInfo.isNotBlank()) "ПРОФІЛЬ КОРИСТУВАЧА:\n$contextInfo\n" else ""}

ТВОЇ ЗАДАЧІ:
1. Відповідати на питання про мовлення та виступи
2. Давати персоналізовані поради на основі профілю користувача
3. Допомагати готуватися до конкретних виступів
4. Мотивувати та підтримувати користувача
5. Пропонувати вправи відповідно до слабких місць

ПРАВИЛА:
- Відповідай українською мовою
- Будь дружнім, підтримуючим та конкретним
- Використовуй емодзі помірно для наочності
- Давай практичні поради, а не тільки теорію
- Враховуй рівень користувача (не перевантажуй новачків)
- Якщо не впевнений — кажи чесно
- Довжина відповіді: 2-5 речень (залежно від питання)

СТИЛЬ:
- Як досвідчений тренер, який знає користувача
- Не формальний, але професійний
- Мотивуючий, але реалістичний
- Конкретний та практичний

ЗАБОРОНЕНО:
- Претендувати на медичні діагнози
- Обіцяти нереалістичні результати
- Критикувати користувача
- Давати поради, що можуть нашкодити голосу
        """.trimIndent()
    }

    private fun buildConversationHistory(messages: List<Message>): String {
        if (messages.isEmpty()) return ""

        // Take last 10 messages for context
        val recentMessages = messages.takeLast(10)

        return "КОНТЕКСТ РОЗМОВИ:\n" + recentMessages.mapNotNull { message ->
            when (message.role) {
                MessageRole.USER -> "Користувач: ${message.content}"
                MessageRole.ASSISTANT -> "Ти: ${message.content}"
                MessageRole.SYSTEM -> null // Skip system messages
            }
        }.joinToString("\n")
    }

    private fun buildQuickActionsPrompt(context: ConversationContext): String {
        val contextInfo = context.toPromptContext()

        return """
На основі профілю користувача запропонуй 5 коротких питань/тем, які йому будуть корисні.

${if (contextInfo.isNotBlank()) "Профіль:\n$contextInfo\n" else ""}

Формат: список з 5 пунктів, кожен починається з "-"
Довжина кожного: максимум 8 слів
Стиль: прямі питання або запити на дії

Приклади:
- Дай поради для покращення дикції
- Як позбутися слів-паразитів?
- Підготуй мене до презентації
- Які вправи мені підходять сьогодні?
- Як покращити впевненість у виступах?
        """.trimIndent()
    }

    private fun getDefaultQuickActions(): List<String> = listOf(
        "Дай поради для покращення мовлення",
        "Як підготуватися до виступу?",
        "Які вправи мені підходять?",
        "Як позбутися нервозності?",
        "Підготуй мене до співбесіди"
    )
}
