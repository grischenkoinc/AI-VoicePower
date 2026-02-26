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

        // --- Крок 1: Транскрипція (завжди — для тексту порівнюємо, для імпровізації рахуємо слова) ---
        var transcription: String? = null
        var completionPercent: Int? = null
        var orderPercent: Int? = null
        var wordCount: Int? = null

        Log.d("DiagFlow", ">>> Step 1: Transcribing audio...")
        val transcribeResult = geminiApiClient.transcribeAudio(audioFilePath)
        if (transcribeResult.isSuccess) {
            transcription = transcribeResult.getOrNull().orEmpty()
            wordCount = normalizeToWords(transcription).size
            Log.d("DiagFlow", "Transcription: '${transcription.take(100)}', wordCount: $wordCount")

            // Для вправ з очікуваним текстом — порівнюємо
            if (!expectedText.isNullOrBlank()) {
                completionPercent = calculateCompletionPercent(transcription, expectedText)
                orderPercent = calculateOrderPercent(transcription, expectedText)
                Log.d("DiagFlow", "Completion: $completionPercent%, order: $orderPercent%")
            }
        } else {
            Log.w("DiagFlow", "Transcription failed: ${transcribeResult.exceptionOrNull()?.message}, proceeding without it")
        }

        // --- Крок 2: Збагачуємо контекст транскрипцією ---
        val enrichedContext = if (transcription != null) {
            buildString {
                append(context ?: "")
                append("\n\nТРАНСКРИПЦІЯ АУДІО (зроблена окремим кроком, це ФАКТ — довіряй цьому): ")
                append(transcription.ifBlank { "(порожній запис — тиша)" })

                if (transcription.isBlank()) {
                    append("\nУВАГА: Запис ПОРОЖНІЙ — тиша або нерозбірливе. Постав overallScore = 0!")
                } else if (completionPercent != null) {
                    // Вправи з очікуваним текстом
                    append("\nВІДСОТОК ВИКОНАННЯ ТЕКСТУ: $completionPercent%")
                    if (orderPercent != null && orderPercent < 80) {
                        append("\nПОРЯДОК СЛІВ: $orderPercent% (слова ПЕРЕПЛУТАНІ місцями! Знижуй оцінку дикції та загальну)")
                    }
                    if (completionPercent < 15) {
                        append("\nУВАГА: Людина сказала ЗОВСІМ ІНШІ СЛОВА — НЕ ті, що в завданні! Виконання тексту = $completionPercent%. Це означає що завдання НЕ ВИКОНАНО. Оцінка має бути ДУЖЕ НИЗЬКОЮ (overallScore максимум 10), незалежно від якості вимови.")
                    } else if (completionPercent < 100) {
                        append("\nУВАГА: текст виконано НЕ ПОВНІСТЮ ($completionPercent%).")
                        append("\nЛюдина сказала ТІЛЬКИ те, що є в транскрипції вище.")
                        append("\nЗАБОРОНЕНО: згадувати, коментувати чи оцінювати БУДЬ-ЯКІ слова яких НЕМАЄ в транскрипції!")
                        append("\nstrengths/improvements — ТІЛЬКИ про слова з транскрипції, НЕ вигадуй інших!")
                    }
                } else {
                    // Імпровізація (без очікуваного тексту) — повідомляємо кількість слів
                    append("\nКількість слів: ${wordCount ?: 0}")
                    if (wordCount != null && wordCount < 15) {
                        append("\nУВАГА: Людина сказала ДУЖЕ МАЛО слів ($wordCount). Для цієї вправи це НЕДОСТАТНЬО — оцінки мають бути НИЗЬКИМИ!")
                    }
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

        // 4b: Cap за порядком слів (переплутані слова = штраф)
        if (orderPercent != null && orderPercent < 80) {
            val orderCap = when {
                orderPercent < 30 -> 35
                orderPercent < 50 -> 45
                orderPercent < 80 -> 55
                else -> 100
            }
            if (finalResult.overallScore > orderCap) {
                Log.d("DiagFlow", "Capping by word order: order=$orderPercent%, orderCap=$orderCap, overallScore=${finalResult.overallScore}")
                finalResult = finalResult.copy(
                    diction = minOf(finalResult.diction, orderCap + 10),
                    overallScore = minOf(finalResult.overallScore, orderCap),
                    improvements = listOf(
                        "Слова переплутані місцями — порядок слів: $orderPercent%"
                    ) + finalResult.improvements
                )
            }
        }

        // 4c: Cap за кількістю слів (для імпровізації — без очікуваного тексту)
        if (wordCount != null && expectedText.isNullOrBlank()) {
            finalResult = capScoreByWordCount(finalResult, wordCount)
        }

        // 4d: Мінімальний поріг — якщо < 5, вважаємо порожнім записом
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

    /** Нормалізує текст: lowercase, тільки літери, розбиває на слова */
    private fun normalizeToWords(text: String): List<String> =
        text.lowercase()
            .replace(Regex("[^а-яіїєґa-z'\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 1 }

    /**
     * Нечітке порівняння двох слів. Толерантне до:
     * - різних закінчень (тупогуб/тупогубе)
     * - помилок транскрипції (тупогуб/тупогуп)
     * - скорочень (тупогуб/тупог)
     */
    private fun fuzzyMatch(spoken: String, expected: String): Boolean {
        if (spoken == expected) return true

        // Префікс: перші 3 літери збігаються (для слів >= 3 символи)
        if (spoken.length >= 3 && expected.length >= 3 &&
            (spoken.startsWith(expected.take(3)) || expected.startsWith(spoken.take(3)))) {
            return true
        }

        // Одне слово містить інше (для коротких слів: "біб" в "бібу")
        if (spoken.length >= 2 && expected.length >= 2 &&
            (spoken.contains(expected) || expected.contains(spoken))) {
            return true
        }

        // Відстань Леvenштейна <= 1 для слів однакової довжини (біб/біп, бик/бій)
        if (spoken.length == expected.length && spoken.length >= 2) {
            val diff = spoken.zip(expected).count { (a, b) -> a != b }
            if (diff <= 1) return true
        }

        return false
    }

    /**
     * Розраховує відсоток виконання тексту порівнюючи транскрипцію з очікуваним текстом.
     */
    private fun calculateCompletionPercent(transcription: String, expectedText: String): Int {
        val expectedWords = normalizeToWords(expectedText)
        val spokenWords = normalizeToWords(transcription)

        if (expectedWords.isEmpty()) return 100
        if (spokenWords.isEmpty()) return 0

        val matchedCount = expectedWords.count { expected ->
            spokenWords.any { spoken -> fuzzyMatch(spoken, expected) }
        }

        return ((matchedCount.toFloat() / expectedWords.size) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Перевіряє чи слова вимовлені у правильному порядку.
     * 100% = всі слова в правильному порядку, 0% = повністю переплутані.
     */
    private fun calculateOrderPercent(transcription: String, expectedText: String): Int {
        val expectedWords = normalizeToWords(expectedText)
        val spokenWords = normalizeToWords(transcription)

        if (expectedWords.size < 2 || spokenWords.size < 2) return 100

        // Скільки очікуваних слів можна знайти послідовно (в порядку) в spoken
        var spokenIdx = 0
        var inOrderCount = 0
        for (expectedWord in expectedWords) {
            for (i in spokenIdx until spokenWords.size) {
                if (fuzzyMatch(spokenWords[i], expectedWord)) {
                    inOrderCount++
                    spokenIdx = i + 1
                    break
                }
            }
        }

        // Загальна кількість слів що взагалі були сказані
        val totalMatched = expectedWords.count { expected ->
            spokenWords.any { fuzzyMatch(it, expected) }
        }

        if (totalMatched < 2) return 100
        return ((inOrderCount.toFloat() / totalMatched) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Обмежує оцінки за кількістю слів — для імпровізації/вільного мовлення.
     * Якщо людина сказала лише кілька слів, оцінка не може бути високою.
     */
    private fun capScoreByWordCount(result: VoiceAnalysisResult, wordCount: Int): VoiceAnalysisResult {
        val maxScore = when {
            wordCount < 3 -> 5
            wordCount < 8 -> 15
            wordCount < 15 -> 25
            wordCount < 30 -> 40
            wordCount < 50 -> 55
            else -> 100 // достатньо слів
        }

        if (maxScore >= 100 || result.overallScore <= maxScore) return result

        Log.d("DiagFlow", "Capping by word count: words=$wordCount, maxScore=$maxScore, original=${result.overallScore}")

        val ratio = maxScore.toFloat() / maxOf(result.overallScore, 1)
        return result.copy(
            diction = (result.diction * ratio).toInt().coerceIn(0, maxScore),
            tempo = (result.tempo * ratio).toInt().coerceIn(0, maxScore),
            intonation = (result.intonation * ratio).toInt().coerceIn(0, maxScore),
            volume = (result.volume * ratio).toInt().coerceIn(0, maxScore),
            confidence = (result.confidence * ratio).toInt().coerceIn(0, maxScore),
            fillerWords = (result.fillerWords * ratio).toInt().coerceIn(0, maxScore),
            structure = (result.structure * ratio).toInt().coerceIn(0, maxScore),
            persuasiveness = (result.persuasiveness * ratio).toInt().coerceIn(0, maxScore),
            overallScore = maxScore,
            improvements = listOf(
                "Сказано лише $wordCount слів — для цієї вправи потрібно більше"
            ) + result.improvements
        )
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

        // Обрізаємо і окремі метрики, щоб юзер бачив узгоджені оцінки
        // (не було diction: 58, overallScore: 30 — це плутає)
        val metricCap = when {
            completionPercent < 15 -> 20
            completionPercent < 25 -> 25
            completionPercent < 50 -> 40
            completionPercent < 75 -> 55
            completionPercent < 90 -> 70
            else -> 100 // 90-99%: окремі метрики не обрізаємо
        }

        if (result.overallScore <= maxScore && metricCap >= 100) return result

        Log.d("DiagFlow", "Capping scores: completion=$completionPercent%, maxScore=$maxScore, metricCap=$metricCap, original overallScore=${result.overallScore}")

        val message = if (completionPercent < 15) {
            "Сказано не той текст — завдання не виконано (збіг з очікуваним текстом: $completionPercent%)"
        } else {
            "Текст прочитано не повністю — виконано приблизно $completionPercent%"
        }

        return result.copy(
            diction = if (metricCap < 100) minOf(result.diction, metricCap) else result.diction,
            tempo = if (metricCap < 100) minOf(result.tempo, metricCap) else result.tempo,
            intonation = if (metricCap < 100) minOf(result.intonation, metricCap) else result.intonation,
            volume = if (metricCap < 100) minOf(result.volume, metricCap) else result.volume,
            confidence = if (metricCap < 100) minOf(result.confidence, metricCap) else result.confidence,
            fillerWords = if (metricCap < 100) minOf(result.fillerWords, metricCap) else result.fillerWords,
            structure = if (metricCap < 100) minOf(result.structure, metricCap) else result.structure,
            persuasiveness = if (metricCap < 100) minOf(result.persuasiveness, metricCap) else result.persuasiveness,
            overallScore = minOf(result.overallScore, maxScore),
            improvements = listOf(message) + result.improvements
        )
    }
}
