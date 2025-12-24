package com.aivoicepower.data.remote

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client для роботи з Gemini API
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
}

enum class SalesStage {
    INITIAL_PITCH,
    HANDLING_OBJECTION
}
