package com.aivoicepower.data.remote

import android.content.Context
import android.util.Log
import com.aivoicepower.BuildConfig
import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
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
            maxOutputTokens = 2000
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
            val systemPrompt = AiPrompts.buildDebateSystemPrompt(topic, userPosition, roundNumber)
            val userPrompt = AiPrompts.buildDebateUserPrompt(userArgument, conversationHistory)

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
            val systemPrompt = AiPrompts.buildSalesSystemPrompt(product, customerType, interactionStage)
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
            val prompt = AiPrompts.buildDebateEvaluationPrompt(topic, userPosition, rounds)

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
            val systemPrompt = AiPrompts.buildCoachSystemPrompt(userContext)
            val historyPrompt = AiPrompts.buildConversationHistory(conversationHistory)

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
            val prompt = AiPrompts.buildQuickActionsPrompt(userContext)

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
                ?: AiPrompts.DEFAULT_QUICK_ACTIONS

            Result.success(suggestions.ifEmpty { AiPrompts.DEFAULT_QUICK_ACTIONS })
        } catch (e: Exception) {
            // Fallback suggestions
            Result.success(AiPrompts.DEFAULT_QUICK_ACTIONS)
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
            val prompt = AiPrompts.buildTextVoiceAnalysisPrompt(transcription, context)

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
     * Має retry логіку (3 спроби) для обробки 500 Internal Server Error
     */
    suspend fun analyzeVoiceRecording(
        audioFilePath: String,
        expectedText: String? = null,
        exerciseType: String,
        additionalContext: String? = null
    ): Result<VoiceAnalysisResult> {
        Log.d("DiagFlow", "=== GeminiApiClient.analyzeVoiceRecording() CALLED ===")
        Log.d("DiagFlow", "audioFilePath: $audioFilePath")
        Log.d("DiagFlow", "exerciseType: $exerciseType")
        Log.d("Gemini", "=== analyzeVoiceRecording START ===")
        Log.d("Gemini", "Audio file: $audioFilePath")

        val audioFile = File(audioFilePath)
        Log.d("DiagFlow", "File exists check: ${audioFile.exists()}")
        if (!audioFile.exists()) {
            Log.e("DiagFlow", "!!! AUDIO FILE NOT FOUND !!!")
            Log.e("Gemini", "Audio file NOT FOUND: $audioFilePath")
            return Result.failure(Exception("Аудіо файл не знайдено: $audioFilePath"))
        }

        val mimeType = getMimeType(audioFilePath)
        val audioBytes = audioFile.readBytes()
        Log.d("DiagFlow", "Audio loaded: ${audioBytes.size} bytes, mimeType: $mimeType")
        Log.d("Gemini", "Audio file size: ${audioBytes.size} bytes, mimeType: $mimeType")

        val prompt = AiPrompts.buildVoiceAnalysisPrompt(expectedText, exerciseType, additionalContext)

        var lastException: Exception? = null
        val maxAttempts = 3

        for (attempt in 0 until maxAttempts) {
            try {
                Log.d("DiagFlow", ">>> GEMINI API CALL - Attempt ${attempt + 1} of $maxAttempts <<<")
                Log.d("Gemini", "Attempt ${attempt + 1} of $maxAttempts - Sending audio to Gemini API...")

                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                        blob(mimeType, audioBytes)
                    }
                )
                Log.d("Gemini", "Gemini response received!")

                val responseText = response.text
                if (responseText == null) {
                    Log.e("Gemini", "Gemini returned NULL response")
                    lastException = Exception("Gemini не повернув відповідь")
                    continue
                }

                Log.d("Gemini", "Gemini response (first 300 chars): ${responseText.take(300)}")

                // Парсимо JSON відповідь
                val analysisResult = parseVoiceAnalysisResponse(responseText)
                Log.d("Gemini", "Parsed result: overallScore=${analysisResult.overallScore}")
                Log.d("Gemini", "=== analyzeVoiceRecording SUCCESS ===")
                return Result.success(analysisResult)

            } catch (e: Exception) {
                // Детальне логування помилки
                Log.e("DiagFlow", "!!! GEMINI EXCEPTION on attempt ${attempt + 1} !!!")
                Log.e("DiagFlow", "Exception class: ${e.javaClass.name}")
                Log.e("DiagFlow", "Exception message: ${e.message}")
                Log.e("DiagFlow", "Exception cause: ${e.cause}")
                Log.e("Gemini", "Attempt ${attempt + 1} FAILED: ${e.javaClass.simpleName} - ${e.message}")
                lastException = e

                // Перевірка чи це retryable помилка
                // Включає: 500, Internal, Server, 503, unavailable, unexpected
                val isRetryable = e.message?.let { msg ->
                    msg.contains("500") ||
                    msg.contains("Internal", ignoreCase = true) ||
                    msg.contains("Server", ignoreCase = true) ||
                    msg.contains("503") ||
                    msg.contains("unavailable", ignoreCase = true) ||
                    msg.contains("unexpected", ignoreCase = true) ||
                    msg.contains("UNKNOWN", ignoreCase = true) ||
                    msg.contains("error", ignoreCase = true)
                } ?: false

                Log.d("DiagFlow", "isRetryable: $isRetryable, attempt: ${attempt + 1}/${maxAttempts}")

                if (isRetryable && attempt < maxAttempts - 1) {
                    val delayMs = 2000L * (attempt + 1) // 2s, 4s, 6s
                    Log.w("DiagFlow", "Retryable error detected, waiting ${delayMs}ms before retry...")
                    Log.w("Gemini", "Retryable error, waiting ${delayMs}ms before retry...")
                    delay(delayMs)
                } else if (!isRetryable) {
                    // Не retryable помилка - виходимо одразу
                    Log.e("DiagFlow", "NON-retryable error, giving up immediately")
                    Log.e("Gemini", "Non-retryable error, giving up")
                    break
                } else {
                    Log.d("DiagFlow", "Last attempt failed, no more retries")
                }
            }
        }

        // Всі спроби невдалі
        Log.e("Gemini", "=== analyzeVoiceRecording FAILED after $maxAttempts attempts ===")
        Log.e("Gemini", "Last error: ${lastException?.message}")
        return Result.failure(lastException ?: Exception("Unknown error after $maxAttempts attempts"))
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

    private fun parseVoiceAnalysisResponse(responseText: String): VoiceAnalysisResult {
        return try {
            val jsonText = extractJson(responseText)
            val gson = Gson()
            val parsed = gson.fromJson(jsonText, VoiceAnalysisJsonResponse::class.java)

            VoiceAnalysisResult(
                diction = parsed.diction ?: 0,
                tempo = parsed.tempo ?: 0,
                intonation = parsed.intonation ?: 0,
                volume = parsed.volume ?: 0,
                confidence = parsed.confidence ?: 0,
                fillerWords = parsed.fillerWords ?: 0,
                structure = parsed.structure ?: 0,
                persuasiveness = parsed.persuasiveness ?: 0,
                overallScore = parsed.overallScore ?: 0,
                strengths = parsed.strengths ?: emptyList(),
                improvements = parsed.improvements ?: listOf("Не вдалося проаналізувати запис"),
                tip = parsed.tip ?: "Спробуйте записати ще раз",
                coachComment = parsed.coachComment ?: ""
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
    @SerializedName("structure") val structure: Int?,
    @SerializedName("persuasiveness") val persuasiveness: Int?,
    @SerializedName("overallScore") val overallScore: Int?,
    @SerializedName("strengths") val strengths: List<String>?,
    @SerializedName("improvements") val improvements: List<String>?,
    @SerializedName("tip") val tip: String?,
    @SerializedName("coachComment") val coachComment: String?
)
