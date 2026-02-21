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
     * Аналізує аудіо запис та повертає детальні метрики.
     *
     * Двоетапний підхід для вправ з очікуваним текстом:
     * 1. Транскрибуємо аудіо окремим викликом → отримуємо факти
     * 2. Рахуємо відсоток виконання тексту програмно
     * 3. Передаємо транскрипцію + відсоток в промпт аналізу
     * 4. Пост-обробка: cap overallScore за відсотком виконання
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

        // --- Крок 1: Транскрипція (тільки якщо є очікуваний текст) ---
        var transcription: String? = null
        var completionPercent: Int? = null

        if (!expectedText.isNullOrBlank()) {
            Log.d("DiagFlow", ">>> Step 1: Transcribing audio for text comparison...")
            val transcribeResult = geminiApiClient.transcribeAudio(audioFilePath)
            if (transcribeResult.isSuccess) {
                transcription = transcribeResult.getOrNull().orEmpty()
                completionPercent = calculateCompletionPercent(transcription, expectedText)
                Log.d("DiagFlow", "Transcription: '${transcription.take(100)}', completion: $completionPercent%")
            } else {
                // Транскрипція не вдалася — продовжуємо без неї (fallback до старої поведінки)
                Log.w("DiagFlow", "Transcription failed: ${transcribeResult.exceptionOrNull()?.message}, proceeding without it")
            }
        }

        // --- Крок 2: Збагачуємо контекст транскрипцією ---
        val enrichedContext = if (transcription != null && completionPercent != null) {
            buildString {
                append(context ?: "")
                append("\n\nТРАНСКРИПЦІЯ АУДІО (зроблена окремим кроком, це ФАКТ — довіряй цьому): ")
                append(transcription.ifBlank { "(порожній запис — тиша)" })
                append("\nВІДСОТОК ВИКОНАННЯ ТЕКСТУ: $completionPercent%")
                if (transcription.isBlank()) {
                    append("\nУВАГА: Запис ПОРОЖНІЙ — тиша або нерозбірливе. Постав overallScore = 0!")
                } else if (completionPercent < 15) {
                    append("\nУВАГА: Людина сказала ЗОВСІМ ІНШІ СЛОВА — НЕ ті, що в завданні! Виконання тексту = $completionPercent%. Це означає що завдання НЕ ВИКОНАНО. Оцінка має бути ДУЖЕ НИЗЬКОЮ (overallScore максимум 10), незалежно від якості вимови.")
                } else if (completionPercent < 100) {
                    append("\nУВАГА: текст виконано НЕ ПОВНІСТЮ. Оцінюй ТІЛЬКИ вимовлені слова. НЕ КОМЕНТУЙ невимовлені частини!")
                }
            }
        } else {
            context
        }

        // --- Крок 3: Аналіз аудіо через Gemini ---
        Log.d("DiagFlow", ">>> Step 2: Calling geminiApiClient.analyzeVoiceRecording()...")
        val result = geminiApiClient.analyzeVoiceRecording(
            audioFilePath = audioFilePath,
            expectedText = expectedText,
            exerciseType = exerciseType,
            additionalContext = enrichedContext
        )

        Log.d("DiagFlow", "<<< geminiApiClient returned: isSuccess=${result.isSuccess}")
        if (result.isFailure) {
            Log.e("DiagFlow", "geminiApiClient FAILED: ${result.exceptionOrNull()?.message}")
            return@withContext result
        }

        // --- Крок 4: Пост-обробка результату ---
        var finalResult = result.getOrNull() ?: return@withContext result

        // 4a: Cap overallScore за відсотком виконання тексту
        if (completionPercent != null) {
            finalResult = capScoreByCompletion(finalResult, completionPercent)
        }

        // 4b: Мінімальний поріг — якщо < 5, вважаємо порожнім записом
        if (finalResult.overallScore < 5) {
            Log.d("DiagFlow", "overallScore=${finalResult.overallScore} < 5, treating as empty recording")
            return@withContext Result.success(VoiceAnalysisResult.default())
        }

        // --- Крок 5: Оновлення навичок ---
        if (finalResult.overallScore > 0) {
            try {
                skillUpdateService.updateFromAnalysis(finalResult, exerciseType)
                Log.d("DiagFlow", "Skill levels updated for exerciseType=$exerciseType")
            } catch (e: Exception) {
                Log.e("DiagFlow", "Failed to update skill levels: ${e.message}")
            }
        }

        Result.success(finalResult)
    }

    /**
     * Розраховує відсоток виконання тексту порівнюючи транскрипцію з очікуваним текстом.
     * Використовує нечітке порівняння слів (contains) для толерантності до форм слова.
     */
    private fun calculateCompletionPercent(transcription: String, expectedText: String): Int {
        val expectedWords = expectedText.lowercase()
            .replace(Regex("[^а-яіїєґa-z'\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 1 } // ігноруємо однолітерні частки
        val spokenWords = transcription.lowercase()
            .replace(Regex("[^а-яіїєґa-z'\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 1 }

        if (expectedWords.isEmpty()) return 100
        if (spokenWords.isEmpty()) return 0

        val matchedCount = expectedWords.count { expected ->
            spokenWords.any { spoken ->
                spoken == expected ||
                (spoken.length >= 4 && expected.length >= 4 &&
                    (spoken.startsWith(expected.take(4)) || expected.startsWith(spoken.take(4))))
            }
        }

        return ((matchedCount.toFloat() / expectedWords.size) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Обмежує overallScore на основі відсотка виконання тексту.
     * Гарантує що неповне виконання не отримає завищену оцінку.
     */
    private fun capScoreByCompletion(result: VoiceAnalysisResult, completionPercent: Int): VoiceAnalysisResult {
        val maxScore = when {
            completionPercent < 25 -> 15
            completionPercent < 50 -> 30
            completionPercent < 75 -> 45
            completionPercent < 90 -> 65
            completionPercent < 100 -> 80
            else -> 100
        }

        if (result.overallScore <= maxScore) return result

        Log.d("DiagFlow", "Capping scores: completion=$completionPercent%, maxScore=$maxScore, original overallScore=${result.overallScore}")

        // Для зовсім невідповідного тексту (< 15%) — обрізаємо і окремі метрики
        // Щоб не було "diction: 90, overallScore: 10" за рандомні слова
        val cappedResult = if (completionPercent < 15) {
            val metricCap = 20
            result.copy(
                diction = minOf(result.diction, metricCap),
                tempo = minOf(result.tempo, metricCap),
                intonation = minOf(result.intonation, metricCap),
                volume = minOf(result.volume, metricCap),
                confidence = minOf(result.confidence, metricCap),
                fillerWords = minOf(result.fillerWords, metricCap),
                structure = minOf(result.structure, metricCap),
                persuasiveness = minOf(result.persuasiveness, metricCap),
                overallScore = maxScore,
                improvements = listOf(
                    "Сказано не той текст — завдання не виконано (збіг з очікуваним текстом: $completionPercent%)"
                ) + result.improvements
            )
        } else {
            result.copy(
                overallScore = maxScore,
                improvements = listOf(
                    "Текст прочитано не повністю — виконано приблизно $completionPercent%"
                ) + result.improvements
            )
        }

        return cappedResult
    }
}
