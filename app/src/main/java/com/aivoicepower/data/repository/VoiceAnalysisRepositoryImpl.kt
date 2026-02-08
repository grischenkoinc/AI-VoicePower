package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.remote.AiPrompts
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.domain.model.VoiceAnalysis
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.VoiceMetrics
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.domain.service.SkillUpdateService
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
    private val geminiApiClient: GeminiApiClient,
    private val skillUpdateService: SkillUpdateService
) : VoiceAnalysisRepository {

    private val gson = Gson()

    override suspend fun analyzeVoice(
        audioFile: File,
        context: String
    ): Result<VoiceAnalysis> = withContext(Dispatchers.IO) {
        try {
            // Перевірка файлу
            if (!audioFile.exists()) {
                Log.e("VoiceAnalysis", "Audio file not found: ${audioFile.absolutePath}")
                return@withContext Result.failure(Exception("Аудіо файл не знайдено"))
            }

            if (audioFile.length() < 1000) {
                Log.e("VoiceAnalysis", "Audio file too small: ${audioFile.length()} bytes")
                return@withContext Result.failure(Exception("Аудіо файл порожній або пошкоджений"))
            }

            Log.d("VoiceAnalysis", "Analyzing file: ${audioFile.absolutePath}, size: ${audioFile.length()} bytes")

            val prompt = if (context.startsWith("СКОРОМОВКА")) {
                AiPrompts.buildTongueTwisterRepoPrompt(context)
            } else {
                AiPrompts.buildStandardRepoPrompt(context)
            }

            // Визначаємо MIME type на основі розширення файлу
            val mimeType = when {
                audioFile.name.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
                audioFile.name.endsWith(".mp4", ignoreCase = true) -> "audio/mp4"
                audioFile.name.endsWith(".wav", ignoreCase = true) -> "audio/wav"
                audioFile.name.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
                audioFile.name.endsWith(".ogg", ignoreCase = true) -> "audio/ogg"
                audioFile.name.endsWith(".3gp", ignoreCase = true) -> "audio/3gpp"
                else -> "audio/mp4" // Default
            }
            Log.d("VoiceAnalysis", "Using MIME type: $mimeType for file: ${audioFile.name}")

            val response = geminiModel.generateContent(
                content {
                    text(prompt)
                    blob(mimeType, audioFile.readBytes())
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
                        overallScore = 0f,
                        metrics = VoiceMetrics(
                            clarity = 0f,
                            pace = 0f,
                            volume = 0f,
                            pronunciation = 0f,
                            pauseQuality = 0f,
                            fillerWordsCount = 0
                        ),
                        feedback = responseText,
                        recommendations = listOf("Не вдалося проаналізувати запис"),
                        strengths = emptyList()
                    )
                )
            }

            // Конвертуємо у VoiceAnalysis
            val analysis = VoiceAnalysis(
                transcription = "", // Gemini не повертає транскрипцію у цьому форматі
                overallScore = geminiResponse.overallScore ?: 0f,
                metrics = VoiceMetrics(
                    clarity = geminiResponse.clarity ?: 0f,
                    pace = geminiResponse.pace ?: 0f,
                    volume = geminiResponse.volume ?: 0f,
                    pronunciation = geminiResponse.pronunciation ?: 0f,
                    pauseQuality = geminiResponse.pauseQuality ?: 0f,
                    fillerWordsCount = geminiResponse.fillerWordsCount ?: 0
                ),
                feedback = geminiResponse.feedback ?: "Аналіз не доступний",
                recommendations = geminiResponse.recommendations ?: listOf("Не вдалося проаналізувати запис"),
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

        // Update skill levels from AI analysis
        // Skip skill update for empty/failed recordings (overallScore <= 0)
        result.getOrNull()?.let { analysisResult ->
            if (analysisResult.overallScore > 0) {
                try {
                    skillUpdateService.updateFromAnalysis(analysisResult, exerciseType)
                    Log.d("DiagFlow", "Skill levels updated for exerciseType=$exerciseType")
                } catch (e: Exception) {
                    Log.e("DiagFlow", "Failed to update skill levels: ${e.message}")
                }
            } else {
                Log.d("DiagFlow", "Skipping skill update: overallScore=${analysisResult.overallScore} (empty/failed recording)")
            }
        }

        result
    }
}
