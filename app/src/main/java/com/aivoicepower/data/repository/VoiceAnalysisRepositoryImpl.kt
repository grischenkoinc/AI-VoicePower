package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.domain.model.VoiceAnalysis
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.VoiceMetrics
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

// Data class для парсингу JSON від Gemini
private data class GeminiAnalysisResponse(
    @SerializedName("overallScore") val overallScore: Float?,
    @SerializedName("clarity") val clarity: Float?,
    @SerializedName("pace") val pace: Float?,
    @SerializedName("volume") val volume: Float?,
    @SerializedName("pronunciation") val pronunciation: Float?,
    @SerializedName("pauseQuality") val pauseQuality: Float?,
    @SerializedName("fillerWordsCount") val fillerWordsCount: Int?,
    @SerializedName("feedback") val feedback: String?,
    @SerializedName("recommendations") val recommendations: List<String>?,
    @SerializedName("strengths") val strengths: List<String>?
)

class VoiceAnalysisRepositoryImpl @Inject constructor(
    private val geminiModel: GenerativeModel,
    private val geminiApiClient: GeminiApiClient
) : VoiceAnalysisRepository {

    private val gson = Gson()

    override suspend fun analyzeVoice(
        audioFile: File,
        context: String
    ): Result<VoiceAnalysis> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Ти - професійний тренер з постановки голосу та ораторського мистецтва.

                Контекст вправи: $context

                ВАЖЛИВО: Прослухай аудіозапис. Якщо в записі немає мовлення або тільки тиша - вкажи це у фідбеку і постав низькі оцінки.

                Якщо є мовлення, проаналізуй його за критеріями:
                1. Чіткість вимови (0-100)
                2. Темп мовлення (слів за хвилину, оптимально 120-150)
                3. Гучність голосу (0-100)
                4. Якість вимови (0-100)
                5. Якість пауз (0-100)
                6. Кількість слів-паразитів

                ОБОВ'ЯЗКОВО надай відповідь ТІЛЬКИ у форматі JSON, без додаткового тексту:
                {
                  "overallScore": 75,
                  "clarity": 80,
                  "pace": 140,
                  "volume": 70,
                  "pronunciation": 85,
                  "pauseQuality": 60,
                  "fillerWordsCount": 3,
                  "feedback": "Детальний фідбек українською мовою що саме почув",
                  "recommendations": ["Рекомендація 1", "Рекомендація 2", "Рекомендація 3"],
                  "strengths": ["Сильна сторона 1", "Сильна сторона 2"]
                }
            """.trimIndent()

            val response = geminiModel.generateContent(
                content {
                    text(prompt)
                    blob("audio/mp4", audioFile.readBytes())
                }
            )

            val responseText = response.text ?: throw Exception("Gemini не повернув відповідь")

            // Витягуємо JSON з відповіді (Gemini може обернути його в markdown)
            val jsonText = extractJson(responseText)

            // Парсимо JSON
            val geminiResponse = try {
                gson.fromJson(jsonText, GeminiAnalysisResponse::class.java)
            } catch (e: Exception) {
                // Якщо не вдалось спарсити - повертаємо сирий фідбек
                return@withContext Result.success(
                    VoiceAnalysis(
                        transcription = "",
                        overallScore = 50f,
                        metrics = VoiceMetrics(
                            clarity = 50f,
                            pace = 120f,
                            volume = 50f,
                            pronunciation = 50f,
                            pauseQuality = 50f,
                            fillerWordsCount = 0
                        ),
                        feedback = responseText,
                        recommendations = listOf("Спробуйте ще раз"),
                        strengths = emptyList()
                    )
                )
            }

            // Конвертуємо у VoiceAnalysis
            val analysis = VoiceAnalysis(
                transcription = "", // Gemini не повертає транскрипцію у цьому форматі
                overallScore = geminiResponse.overallScore ?: 50f,
                metrics = VoiceMetrics(
                    clarity = geminiResponse.clarity ?: 50f,
                    pace = geminiResponse.pace ?: 120f,
                    volume = geminiResponse.volume ?: 50f,
                    pronunciation = geminiResponse.pronunciation ?: 50f,
                    pauseQuality = geminiResponse.pauseQuality ?: 50f,
                    fillerWordsCount = geminiResponse.fillerWordsCount ?: 0
                ),
                feedback = geminiResponse.feedback ?: "Аналіз не доступний",
                recommendations = geminiResponse.recommendations ?: listOf("Спробуйте ще раз"),
                strengths = geminiResponse.strengths ?: emptyList()
            )

            Result.success(analysis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Витягує JSON з тексту (видаляє markdown обгортки якщо є)
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

    /**
     * Аналізує аудіо запис та повертає детальні метрики
     * Делегує до GeminiApiClient
     */
    override suspend fun analyzeRecording(
        audioFilePath: String,
        expectedText: String?,
        exerciseType: String,
        context: String?
    ): Result<VoiceAnalysisResult> = withContext(Dispatchers.IO) {
        Log.d("DiagFlow", "=== VoiceAnalysisRepository.analyzeRecording() CALLED ===")
        Log.d("DiagFlow", "audioFilePath: $audioFilePath")
        Log.d("DiagFlow", "exerciseType: $exerciseType")
        Log.d("DiagFlow", "File exists: ${File(audioFilePath).exists()}")
        Log.d("DiagFlow", ">>> Calling geminiApiClient.analyzeVoiceRecording()...")

        val result = geminiApiClient.analyzeVoiceRecording(
            audioFilePath = audioFilePath,
            expectedText = expectedText,
            exerciseType = exerciseType,
            additionalContext = context
        )

        Log.d("DiagFlow", "<<< geminiApiClient returned: isSuccess=${result.isSuccess}")
        if (result.isFailure) {
            Log.e("DiagFlow", "geminiApiClient FAILED: ${result.exceptionOrNull()?.message}")
        }

        result
    }
}
