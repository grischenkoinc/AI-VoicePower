package com.aivoicepower.data.remote

import android.content.Context
import com.aivoicepower.BuildConfig
import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client для роботи з Gemini API
 * Supports: AI Coach, Debate, Sales Pitch, Voice Analysis
 *
 * API Key is securely loaded from local.properties via BuildConfig
 */
@Singleton
class GeminiApiClient @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // API Key loaded from local.properties via BuildConfig
        private val API_KEY = BuildConfig.GEMINI_API_KEY
        private const val MODEL_NAME = "gemini-2.5-flash-lite"
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
     * Аналізує голосовий запис та надає фідбек (текстовий варіант)
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

    /**
     * Аналізує аудіо файл та повертає детальні метрики
     * Використовує Gemini multimodal для прямого аналізу аудіо
     */
    suspend fun analyzeVoiceRecording(
        audioFilePath: String,
        expectedText: String? = null,
        exerciseType: String,
        additionalContext: String? = null
    ): Result<VoiceAnalysisResult> {
        return try {
            val audioFile = File(audioFilePath)
            if (!audioFile.exists()) {
                return Result.failure(Exception("Аудіо файл не знайдено: $audioFilePath"))
            }

            val mimeType = getMimeType(audioFilePath)
            val audioBytes = audioFile.readBytes()

            val prompt = buildVoiceAnalysisPrompt(expectedText, exerciseType, additionalContext)

            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                    blob(mimeType, audioBytes)
                }
            )

            val responseText = response.text
                ?: return Result.failure(Exception("Gemini не повернув відповідь"))

            // Парсимо JSON відповідь
            val analysisResult = parseVoiceAnalysisResponse(responseText)
            Result.success(analysisResult)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getMimeType(filePath: String): String {
        return when {
            filePath.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
            filePath.endsWith(".mp4", ignoreCase = true) -> "audio/mp4"
            filePath.endsWith(".wav", ignoreCase = true) -> "audio/wav"
            filePath.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
            filePath.endsWith(".ogg", ignoreCase = true) -> "audio/ogg"
            else -> "audio/mp4" // Default
        }
    }

    private fun buildVoiceAnalysisPrompt(
        expectedText: String?,
        exerciseType: String,
        additionalContext: String?
    ): String {
        return """
Ти — професійний тренер з мовлення. Проаналізуй цей голосовий запис українською мовою.

Тип вправи: $exerciseType
${if (expectedText != null) "Очікуваний текст: $expectedText" else ""}
${if (additionalContext != null) "Контекст: $additionalContext" else ""}

Оціни за шкалою 0-100:
1. diction (чіткість дикції) - наскільки чітко вимовляються звуки
2. tempo (темп мовлення) - чи комфортний темп, не надто швидко/повільно
3. intonation (інтонація) - виразність, емоційність
4. volume (гучність) - стабільність гучності
5. confidence (впевненість) - наскільки впевнено звучить голос
6. fillerWords (слова-паразити) - 100 = немає паразитів, 0 = багато

Також дай:
- strengths: список 2-3 сильних сторін
- improvements: список 2-3 зон для покращення
- tip: одна конкретна порада
- overallScore: загальна оцінка 0-100

Відповідь ТІЛЬКИ у форматі JSON:
{
  "diction": 75,
  "tempo": 80,
  "intonation": 70,
  "volume": 85,
  "confidence": 72,
  "fillerWords": 90,
  "overallScore": 78,
  "strengths": ["чітка вимова", "хороший темп"],
  "improvements": ["додати емоційності", "менше пауз"],
  "tip": "Спробуй читати з більшим ентузіазмом"
}
        """.trimIndent()
    }

    private fun parseVoiceAnalysisResponse(responseText: String): VoiceAnalysisResult {
        return try {
            val jsonText = extractJson(responseText)
            val gson = Gson()
            val parsed = gson.fromJson(jsonText, VoiceAnalysisJsonResponse::class.java)

            VoiceAnalysisResult(
                diction = parsed.diction ?: 50,
                tempo = parsed.tempo ?: 50,
                intonation = parsed.intonation ?: 50,
                volume = parsed.volume ?: 50,
                confidence = parsed.confidence ?: 50,
                fillerWords = parsed.fillerWords ?: 50,
                overallScore = parsed.overallScore ?: 50,
                strengths = parsed.strengths ?: listOf("Гарний початок!"),
                improvements = parsed.improvements ?: listOf("Продовжуй практикуватись"),
                tip = parsed.tip ?: "Практикуй регулярно"
            )
        } catch (e: Exception) {
            // Fallback if parsing fails
            VoiceAnalysisResult.default()
        }
    }

    private fun extractJson(text: String): String {
        // Шукаємо JSON між ``` або просто весь текст
        val jsonPattern = "```json\\s*([\\s\\S]*?)```".toRegex()
        val match = jsonPattern.find(text)
        if (match != null) {
            return match.groupValues[1].trim()
        }

        // Якщо немає markdown, шукаємо { ... }
        val jsonStart = text.indexOf('{')
        val jsonEnd = text.lastIndexOf('}')
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return text.substring(jsonStart, jsonEnd + 1)
        }

        return text
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

/**
 * Data class для парсингу JSON відповіді від Gemini
 */
private data class VoiceAnalysisJsonResponse(
    @SerializedName("diction") val diction: Int?,
    @SerializedName("tempo") val tempo: Int?,
    @SerializedName("intonation") val intonation: Int?,
    @SerializedName("volume") val volume: Int?,
    @SerializedName("confidence") val confidence: Int?,
    @SerializedName("fillerWords") val fillerWords: Int?,
    @SerializedName("overallScore") val overallScore: Int?,
    @SerializedName("strengths") val strengths: List<String>?,
    @SerializedName("improvements") val improvements: List<String>?,
    @SerializedName("tip") val tip: String?
)
