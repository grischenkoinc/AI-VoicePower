package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.local.database.dao.AnalysisResultDao
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.entity.AnalysisResultEntity
import com.aivoicepower.data.remote.AiPrompts
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.domain.model.VoiceAnalysis
import com.aivoicepower.domain.model.VoiceAnalysisResult
import com.aivoicepower.domain.model.VoiceMetrics
import com.aivoicepower.domain.repository.CloudSyncRepository
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.domain.service.SkillUpdateService
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.io.File
import java.util.UUID
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
    private val skillUpdateService: SkillUpdateService,
    private val analysisResultDao: AnalysisResultDao,
    private val diagnosticResultDao: DiagnosticResultDao,
    private val cloudSyncRepository: CloudSyncRepository
) : VoiceAnalysisRepository {

    private val gson = Gson()
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
        context: String?,
        exerciseId: String?,
        recordingDurationMs: Long
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

                // Якщо completion низький — друга спроба транскрипції (захист від нестабільності моделі)
                if (completionPercent!! < 85) {
                    Log.d("DiagFlow", "Low completion ($completionPercent%) — retrying transcription...")
                    val transcribeResult2 = geminiApiClient.transcribeAudio(audioFilePath)
                    if (transcribeResult2.isSuccess) {
                        val transcription2 = transcribeResult2.getOrNull().orEmpty()
                        val completion2 = calculateCompletionPercent(transcription2, expectedText)
                        Log.d("DiagFlow", "Transcription2: '${transcription2.take(100)}', completion2=$completion2%")
                        if (completion2 > completionPercent!!) {
                            Log.d("DiagFlow", "Using transcription2 ($completion2% > $completionPercent%)")
                            transcription = transcription2
                            wordCount = normalizeToWords(transcription).size
                            completionPercent = completion2
                            orderPercent = calculateOrderPercent(transcription, expectedText)
                        }
                    }
                }
            }
        } else {
            Log.w("DiagFlow", "Transcription failed: ${transcribeResult.exceptionOrNull()?.message}, proceeding without it")
        }

        // --- Крок 2: Підтягуємо історію попередніх спроб + рівень юзера ---
        val normalizedType = normalizeExerciseType(exerciseType)

        var previousAttempts: List<AnalysisResultEntity> = emptyList()
        try {
            if (exerciseId != null) {
                previousAttempts = analysisResultDao.getByExerciseId(exerciseId, limit = 3)
            }
            if (previousAttempts.size < 3) {
                val remaining = 3 - previousAttempts.size
                val byType = analysisResultDao.getByExerciseType(normalizedType, limit = remaining + 3)
                    .filter { it.id !in previousAttempts.map { prev -> prev.id } }
                    .take(remaining)
                previousAttempts = previousAttempts + byType
            }
        } catch (e: Exception) {
            Log.w("DiagFlow", "Failed to load previous attempts: ${e.message}")
        }

        var userLevelContext: String? = null
        try {
            val latestDiagnostic = diagnosticResultDao.getAllResultsOnce().firstOrNull()
            if (latestDiagnostic != null) {
                val avgScore = (latestDiagnostic.diction + latestDiagnostic.tempo +
                    latestDiagnostic.intonation + latestDiagnostic.volume +
                    latestDiagnostic.structure + latestDiagnostic.confidence +
                    latestDiagnostic.fillerWords) / 7
                val level = when {
                    avgScore >= 70 -> "просунутий"
                    avgScore >= 45 -> "середній"
                    else -> "початківець"
                }
                userLevelContext = "РІВЕНЬ КОРИСТУВАЧА: $level (діагностика: $avgScore/100)"
            }
        } catch (e: Exception) {
            Log.w("DiagFlow", "Failed to load user level: ${e.message}")
        }

        // --- Крок 3: Збагачуємо контекст транскрипцією + історією + рівнем ---
        val isTongueTwisterBattle = exerciseType.contains("tongue_twister_battle", ignoreCase = true)
        val isTongueTwister = exerciseType.contains("tongue_twister", ignoreCase = true) && !isTongueTwisterBattle
        val enrichedContext = if (transcription != null) {
            buildString {
                append(context ?: "")
                // Завжди передаємо транскрипцію — для скоромовок з застереженням про ненадійність STT
                if (isTongueTwister) {
                    append("\n\nТРАНСКРИПЦІЯ АУДІО (STT для скоромовок може пропускати слова — якщо транскрипція і очікуваний текст розходяться, перевіряй аудіо перш ніж робити висновки): ")
                } else {
                    append("\n\nТРАНСКРИПЦІЯ АУДІО (зроблена окремим кроком, це ФАКТ — довіряй цьому): ")
                }
                append(transcription.ifBlank { "(порожній запис — тиша)" })

                if (transcription.isBlank()) {
                    append("\nУВАГА: Запис ПОРОЖНІЙ — тиша або нерозбірливе. Постав overallScore = 0!")
                } else if (completionPercent != null) {
                    // Вправи з очікуваним текстом — не передаємо числові метрики, AI сам порівнює тексти
                    if (!isTongueTwister) {
                        // Для не-скоромовок completionPercent надійний — передаємо тільки критичні випадки
                        if (completionPercent < 15) {
                            append("\nУВАГА: Людина сказала ЗОВСІМ ІНШІ СЛОВА — НЕ ті, що в завданні! Це означає що завдання НЕ ВИКОНАНО. Оцінка має бути ДУЖЕ НИЗЬКОЮ (overallScore максимум 10), незалежно від якості вимови.")
                        } else if (completionPercent < 100) {
                            append("\nУВАГА: текст виконано НЕ ПОВНІСТЮ.")
                            append("\nЛюдина сказала ТІЛЬКИ те, що є в транскрипції вище.")
                            append("\nЗАБОРОНЕНО: згадувати, коментувати чи оцінювати БУДЬ-ЯКІ слова яких НЕМАЄ в транскрипції!")
                            append("\nstrengths/improvements — ТІЛЬКИ про слова з транскрипції, НЕ вигадуй інших!")
                        }
                    }
                    // Для скоромовок: AI сам порівнює очікуваний текст і транскрипцію з аудіо
                } else if (completionPercent == null) {
                    // Імпровізація (без очікуваного тексту) — повідомляємо кількість слів
                    append("\nКількість слів: ${wordCount ?: 0}")
                    if (wordCount != null && wordCount < 15) {
                        append("\nУВАГА: Людина сказала ДУЖЕ МАЛО слів ($wordCount). Для цієї вправи це НЕДОСТАТНЬО — оцінки мають бути НИЗЬКИМИ!")
                    }
                }

                // Додаємо попередні спроби
                if (previousAttempts.isNotEmpty()) {
                    append("\n\nПОПЕРЕДНІ СПРОБИ (від новіших до старіших):")
                    previousAttempts.forEachIndexed { index, prev ->
                        append("\nСпроба ${index + 1}: overallScore=${prev.overallScore}, diction=${prev.diction}, tempo=${prev.tempo}, tip=\"${prev.tip}\"")
                    }
                }

                // Додаємо рівень юзера
                if (userLevelContext != null) {
                    append("\n\n$userLevelContext")
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
        // Для скоромовок completion% ненадійний при швидкому мовленні — STT не встигає розпізнати.
        // Для батлу (3 скоромовки) — completion% надійний: якщо сказав 1/3, STT це покаже.
        if (completionPercent != null && (!isTongueTwister || isTongueTwisterBattle)) {
            finalResult = capScoreByCompletion(finalResult, completionPercent)
        }

        // 4a2: Cap tempo для скоромовок та батлу за реальною швидкістю мовлення
        // Якщо юзер говорив повільно (паузи, розтягнуто) — tempo не може бути високим.
        if ((isTongueTwister || isTongueTwisterBattle) && recordingDurationMs > 0 && !expectedText.isNullOrBlank()) {
            val expectedWordCount = normalizeToWords(expectedText).size
            if (expectedWordCount > 0) {
                val wordsPerSecond = expectedWordCount.toFloat() / (recordingDurationMs / 1000f)
                val tempoCap = when {
                    wordsPerSecond >= 2.5f -> 100  // швидко — не обмежуємо
                    wordsPerSecond >= 1.5f -> 75   // помірно — м'яке обмеження
                    wordsPerSecond >= 1.0f -> 50   // повільно
                    wordsPerSecond >= 0.5f -> 35   // дуже повільно
                    else -> 20                      // критично повільно (явні паузи між словами)
                }
                if (tempoCap < 100 && finalResult.tempo > tempoCap) {
                    Log.d("DiagFlow", "TongueTwister tempo cap: wps=$wordsPerSecond, tempoCap=$tempoCap, original=${finalResult.tempo}")
                    val scoreDelta = finalResult.overallScore - finalResult.tempo
                    val newTempo = tempoCap
                    val newOverall = (newTempo * 0.35f + finalResult.diction * 0.50f + finalResult.intonation * 0.15f).toInt()
                    finalResult = finalResult.copy(tempo = newTempo, overallScore = newOverall)
                }
            }
        }

        // 4b: Cap за порядком слів — ТІЛЬКИ якщо completion >= 85%
        // При низькому completion% STT пропускає слова → sequential matching дає хибно-низький
        // orderPercent навіть коли юзер насправді не переплутував слова.
        val orderPercentReliable = completionPercent != null && completionPercent >= 85
        if (orderPercent != null && orderPercent < 80 && orderPercentReliable) {
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
        } else if (orderPercent != null && orderPercent < 80 && !orderPercentReliable) {
            Log.d("DiagFlow", "Skipping word order cap: orderPercent=$orderPercent% but completion=$completionPercent% (unreliable STT)")
        }

        // 4c: Cap за кількістю слів (для імпровізації — без очікуваного тексту)
        if (wordCount != null && expectedText.isNullOrBlank()) {
            finalResult = capScoreByWordCount(finalResult, wordCount)
        }

        // 4d: Мінімальний поріг — тільки справді порожній/дефолтний результат відкидаємо
        // Перевіряємо ВСІ метрики: якщо хоч одна ненульова — це реальний результат
        val allCoreMetricsZero = finalResult.diction == 0 && finalResult.tempo == 0 && finalResult.intonation == 0
        if (allCoreMetricsZero && finalResult.overallScore == 0) {
            Log.d("DiagFlow", "All core metrics=0, treating as empty/failed recording")
            return@withContext Result.success(VoiceAnalysisResult.default())
        }
        // Якщо overallScore=0 але метрики ненульові — рахуємо overallScore з метрик
        if (finalResult.overallScore == 0 && !allCoreMetricsZero) {
            Log.w("DiagFlow", "overallScore=0 but metrics non-zero — calculating from metrics")
            val nonZeroMetrics = listOf(finalResult.diction, finalResult.tempo, finalResult.intonation,
                finalResult.confidence, finalResult.structure, finalResult.persuasiveness)
                .filter { it > 0 }
            val calculatedScore = if (nonZeroMetrics.isNotEmpty()) nonZeroMetrics.average().toInt() else 0
            Log.d("DiagFlow", "Calculated overallScore=$calculatedScore from metrics")
            finalResult = finalResult.copy(overallScore = calculatedScore)
        }

        // 4e: Видаляємо strengths що суперечать низьким оцінкам (< 70)
        // ШІ часто пише "Висока чіткість вимови" при diction=55 — це вводить в оману
        finalResult = filterInconsistentStrengths(finalResult)

        // --- Крок 5: Зберігаємо результат аналізу в історію (ДО оновлення навичок!) ---
        // ВАЖЛИВО: збереження відбувається ДО skillUpdateService, щоб AchievementChecker
        // міг знайти запис в analysis_results і правильно видати "Перший запис" після 1-го запису.
        if (finalResult.overallScore > 0) {
            try {
                val entity = AnalysisResultEntity(
                    id = UUID.randomUUID().toString(),
                    exerciseId = exerciseId,
                    exerciseType = normalizedType,
                    overallScore = finalResult.overallScore,
                    diction = finalResult.diction,
                    tempo = finalResult.tempo,
                    intonation = finalResult.intonation,
                    volume = finalResult.volume,
                    confidence = finalResult.confidence,
                    fillerWords = finalResult.fillerWords,
                    structure = finalResult.structure,
                    persuasiveness = finalResult.persuasiveness,
                    tip = finalResult.tip,
                    strengths = gson.toJson(finalResult.strengths),
                    improvements = gson.toJson(finalResult.improvements),
                    divergences = finalResult.divergences
                )
                analysisResultDao.insert(entity)
                Log.d("DiagFlow", "Analysis result saved: exerciseId=$exerciseId, type=$normalizedType, score=${finalResult.overallScore}")
            } catch (e: Exception) {
                Log.w("DiagFlow", "Failed to save analysis result: ${e.message}")
            }
        }

        // --- Крок 6: Оновлення навичок + авто-синхронізація ---
        if (finalResult.overallScore > 0) {
            try {
                skillUpdateService.updateFromAnalysis(finalResult, exerciseType)
                Log.d("DiagFlow", "Skill levels updated for exerciseType=$exerciseType")
            } catch (e: Exception) {
                Log.e("DiagFlow", "Failed to update skill levels: ${e.message}")
            }

            // Fire-and-forget: sync progress + achievements to Firestore after each analysis
            syncScope.launch {
                try {
                    cloudSyncRepository.syncUserProgress()
                    cloudSyncRepository.syncAchievements()
                    Log.d("DiagFlow", "Auto-sync: progress + achievements synced to Firestore")
                } catch (e: Exception) {
                    Log.w("DiagFlow", "Auto-sync failed (non-critical): ${e.message}")
                }
            }
        }

        Result.success(finalResult)
    }

    /** Нормалізує тип вправи до базового формату для збереження та пошуку */
    private fun normalizeExerciseType(rawType: String): String {
        val type = rawType.lowercase().let {
            if (it.startsWith("daily_challenge_")) it.removePrefix("daily_challenge_") else it
        }
        return when {
            type.contains("tongue_twister") || type.contains("tonguetwister") || type == "task_2" || type == "diction" -> "tongue_twister"
            type.contains("slow_motion") || type.contains("slowmotion") -> "slow_motion"
            type.contains("minimal_pairs") || type.contains("minimalpairs") || type.contains("contrast_sounds") -> "minimal_pairs"
            type.contains("emotion_reading") || type.contains("emotionreading") || type == "task_3" -> "emotional_reading"
            type.contains("dialogue") -> "dialogue"
            type.contains("reading") || type == "task_1" -> "reading"
            type.contains("retelling") -> "retelling"
            type.contains("storytelling") || type.contains("story") -> "storytelling"
            type.contains("debate") || type.contains("opposite_day") -> "debate"
            type.contains("sales") || type.contains("pitch") -> "sales"
            type.contains("interview") || type.contains("qa") -> "job_interview"
            type.contains("presentation") -> "presentation"
            type.contains("negotiation") -> "negotiation"
            type.contains("no_hesitation") || type.contains("nohesitation") -> "no_hesitation"
            type.contains("metaphor") -> "metaphor_master"
            type.contains("emotion_switch") || type.contains("emotionswitch") -> "emotion_switch"
            type.contains("speed_round") || type.contains("speedround") -> "speed_round"
            type.contains("character") -> "character_voice"
            type.contains("free_speech") || type.contains("freespeech") || type.contains("spontaneous") || type == "task_4" || type.contains("random_topic") -> "free_speech"
            else -> type
        }
    }

    /** Нормалізує текст: lowercase, тільки літери, розбиває на слова */
    private fun normalizeToWords(text: String): List<String> =
        text.lowercase()
            .replace(Regex("[^а-яіїєґa-z'\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

    /**
     * Нечітке порівняння двох слів. Толерантне до:
     * - різних закінчень (тупогуб/тупогубе)
     * - помилок транскрипції (тупогуб/тупогуп)
     * - скорочень (тупогуб/тупог)
     */
    private fun fuzzyMatch(spoken: String, expected: String): Boolean {
        if (spoken == expected) return true

        // Фонетичні еквіваленти для українських помилок транскрипції
        val spokenNorm = phoneticNormalize(spoken)
        val expectedNorm = phoneticNormalize(expected)
        if (spokenNorm == expectedNorm) return true

        // Префікс: перші 3 літери збігаються (для слів >= 3 символи)
        if (spoken.length >= 3 && expected.length >= 3 &&
            (spoken.startsWith(expected.take(3)) || expected.startsWith(spoken.take(3)))) {
            return true
        }

        // Те саме з нормалізованими
        if (spokenNorm.length >= 3 && expectedNorm.length >= 3 &&
            (spokenNorm.startsWith(expectedNorm.take(3)) || expectedNorm.startsWith(spokenNorm.take(3)))) {
            return true
        }

        // Одне слово містить інше (для коротких слів: "біб" в "бібу")
        if (spoken.length >= 2 && expected.length >= 2 &&
            (spoken.contains(expected) || expected.contains(spoken))) {
            return true
        }

        // Відстань Левенштейна <= 1 для слів однакової довжини (біб/біп, бик/бій)
        if (spoken.length == expected.length && spoken.length >= 2) {
            val diff = spoken.zip(expected).count { (a, b) -> a != b }
            if (diff <= 1) return true
        }

        // Відстань Левенштейна <= 1 для слів різної довжини ±1 символ
        if (kotlin.math.abs(spoken.length - expected.length) <= 1 && spoken.length >= 3) {
            if (levenshteinDistance(spoken, expected) <= 1) return true
        }

        return false
    }

    /** Нормалізує типові фонетичні варіації українських літер */
    private fun phoneticNormalize(word: String): String =
        word.replace('ґ', 'г')
            .replace('ї', 'і')
            .replace('є', 'е')
            .replace("щ", "шч")

    /** Обчислює відстань Левенштейна між двома рядками */
    private fun levenshteinDistance(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[a.length][b.length]
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

        val rawPercent = ((matchedCount.toFloat() / expectedWords.size) * 100).toInt().coerceIn(0, 100)

        // Якщо пропущено лише 1 слово і raw completion ≥ 80% — вважаємо помилкою
        // транскрипції Gemini Flash Lite, а не реальним пропуском юзера.
        // Це покриває: скоромовки (5 слів), мінімальні пари (12+ слів), читання тощо.
        val missedWords = expectedWords.size - matchedCount
        if (missedWords == 1 && rawPercent >= 80) {
            return maxOf(rawPercent, 95)
        }

        return rawPercent
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
            completionPercent < 95 -> 80
            else -> 100 // 95-100%: не обмежуємо (1 пропущене слово = похибка транскрипції)
        }

        // Обрізаємо і окремі метрики, щоб юзер бачив узгоджені оцінки
        // (не було diction: 58, overallScore: 30 — це плутає)
        val metricCap = when {
            completionPercent < 15 -> 20
            completionPercent < 25 -> 25
            completionPercent < 50 -> 40
            completionPercent < 75 -> 55
            completionPercent < 90 -> 70
            else -> 100 // 90%+: окремі метрики не обрізаємо
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

    /**
     * Видаляє strengths, які суперечать реальним оцінкам метрик.
     * Поріг 70: якщо метрика < 70 — хвалити її некоректно.
     * Наприклад: "Висока чіткість вимови" при diction=55 — вводить юзера в оману.
     */
    private fun filterInconsistentStrengths(result: VoiceAnalysisResult): VoiceAnalysisResult {
        val threshold = 70
        val filtered = result.strengths.filter { strength ->
            val s = strength.lowercase()
            // Дикція / вимова / чіткість / артикуляція / звуки
            val isDictionPraise = s.contains("вимов") || s.contains("дикц") || s.contains("чіткіст") ||
                s.contains("артикул") || s.contains("приголосн") || s.contains("голосн")
            if (isDictionPraise && result.diction in 1 until threshold) return@filter false
            // Темп / ритм / швидкість
            val isTempoPraise = s.contains("темп") || s.contains("ритм")
            if (isTempoPraise && result.tempo in 1 until threshold) return@filter false
            // Інтонація / мелодика
            val isIntonationPraise = s.contains("інтонац") || s.contains("мелодик")
            if (isIntonationPraise && result.intonation in 1 until threshold) return@filter false
            true
        }
        if (filtered.size < result.strengths.size) {
            Log.d("DiagFlow", "Filtered ${result.strengths.size - filtered.size} inconsistent strengths")
            return result.copy(strengths = filtered)
        }
        return result
    }
}
