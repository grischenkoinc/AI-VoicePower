package com.aivoicepower.data.remote

import android.content.Context
import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import com.aivoicepower.data.chat.MessageRole
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client для роботи з Gemini API
 * Supports: AI Coach, Debate, Sales Pitch, Voice Analysis
 *
 * TODO Phase 8: Move API key to BuildConfig or secure storage
 */
@Singleton
class GeminiApiClient @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // TODO: Move to BuildConfig or secure storage
        // For now, using placeholder - user needs to add their own key
        private const val API_KEY = "YOUR_GEMINI_API_KEY_HERE"
        private const val MODEL_NAME = "gemini-1.5-flash-latest"
    }

    private val generativeModel = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.8f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 800
        }
    )

    /**
     * Генерує відповідь для дебатів
     */
    suspend fun generateDebateResponse(
        topic: String,
        userPosition: String,
        userArgument: String,
        roundNumber: Int,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return try {
            val systemPrompt = buildDebateSystemPrompt(topic, userPosition, roundNumber)
            val userPrompt = buildDebateUserPrompt(userArgument, conversationHistory)

            val response = generativeModel.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )

            val aiResponse = response.text ?: "Я не можу відповісти на цей аргумент."
            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Генерує відповідь AI-клієнта у продажах
     */
    suspend fun generateSalesResponse(
        product: String,
        customerType: String,
        userPitch: String,
        interactionStage: SalesStage
    ): Result<String> {
        return try {
            val systemPrompt = buildSalesSystemPrompt(product, customerType, interactionStage)
            val userPrompt = "Продавець каже: $userPitch"

            val response = generativeModel.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )

            val aiResponse = response.text ?: "Мені потрібно подумати..."
            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Оцінює фінальний результат дебатів
     */
    suspend fun evaluateDebate(
        topic: String,
        userPosition: String,
        rounds: List<Pair<String, String>>
    ): Result<String> {
        return try {
            val prompt = buildDebateEvaluationPrompt(topic, userPosition, rounds)

            val response = generativeModel.generateContent(prompt)

            val evaluation = response.text ?: "Гарна спроба!"
            Result.success(evaluation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Генерує відповідь AI Coach на повідомлення користувача
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
     * Генерує швидкі дії на основі контексту користувача
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
     * Аналізує голосовий запис та надає фідбек
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

    private fun buildDebateSystemPrompt(
        topic: String,
        userPosition: String,
        roundNumber: Int
    ): String {
        val oppositePosition = if (userPosition == "ЗА") "ПРОТИ" else "ЗА"

        return """
Ти — опонент у дебатах на тему: "$topic"
Твоя позиція: $oppositePosition
Раунд: $roundNumber з 5

Твої задачі:
1. Визнай частково правильні моменти в аргументі опонента
2. Наведи сильний контраргумент на позицію $oppositePosition
3. Поставт 1 уточнююче питання або виклик

Правила:
- Відповідай українською мовою
- Будь логічним, але не агресивним
- Використовуй факти та логіку
- Довжина відповіді: 2-4 речення
- Без особистих нападів

Стиль: академічний, але зрозумілий
        """.trimIndent()
    }

    private fun buildDebateUserPrompt(
        userArgument: String,
        history: List<Pair<String, String>>
    ): String {
        val historyText = if (history.isNotEmpty()) {
            "Попередні раунди:\n" + history.joinToString("\n") { (user, ai) ->
                "Опонент: $user\nТи: $ai"
            } + "\n\n"
        } else ""

        return "${historyText}Опонент щойно навів аргумент:\n\"$userArgument\"\n\nТвоя відповідь:"
    }

    private fun buildSalesSystemPrompt(
        product: String,
        customerType: String,
        stage: SalesStage
    ): String {
        return when (stage) {
            SalesStage.INITIAL_PITCH -> """
Ти — потенційний клієнт, якому продають: "$product"
Твій тип: $customerType

Продавець щойно представив товар. Твоя реакція:
1. Висловлюй природну цікавість або скептицизм (залежно від типу)
2. Постав 1-2 конкретні запитання про продукт
3. Висунь типове заперечення для твого типу клієнта

Правила:
- Відповідай українською мовою
- Будь реалістичним клієнтом
- Довжина: 2-3 речення
- Без відразу погоджуватися або відмовлятися
            """.trimIndent()

            SalesStage.HANDLING_OBJECTION -> """
Ти — потенційний клієнт, який слухає відповідь на своє заперечення.
Продукт: "$product"
Твій тип: $customerType

Продавець щойно відповів на твоє заперечення. Тепер прийми рішення:
1. Якщо відповідь переконлива → Згодься купити + поясни чому
2. Якщо відповідь слабка → Ввічливо відмовся + поясни причину

Правила:
- Будь чесним, але не жорстоким
- Оціни якість аргументації продавця
- Довжина: 2-3 речення
- Чітке "так" або "ні" з поясненням
            """.trimIndent()
        }
    }

    private fun buildDebateEvaluationPrompt(
        topic: String,
        userPosition: String,
        rounds: List<Pair<String, String>>
    ): String {
        val transcript = rounds.mapIndexed { index, (user, ai) ->
            "Раунд ${index + 1}:\nОпонент (позиція: $userPosition): $user\nAI: $ai"
        }.joinToString("\n\n")

        return """
Оціни дебати на тему: "$topic"

Транскрипт:
$transcript

Дай короткий фідбек (3-4 речення):
1. Сильні сторони аргументації опонента
2. Що можна покращити
3. Загальна оцінка виступу (1-10)

Формат: короткий текст українською мовою
        """.trimIndent()
    }

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

enum class SalesStage {
    INITIAL_PITCH,
    HANDLING_OBJECTION
}
