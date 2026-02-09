package com.aivoicepower.ui.screens.diagnostic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.audio.AudioPlayer
import com.aivoicepower.data.audio.AudioRecorder
import com.aivoicepower.data.remote.GeminiApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    val audioRecorder: AudioRecorder,
    val audioPlayer: AudioPlayer,
    private val geminiApiClient: GeminiApiClient
) : ViewModel() {

    fun analyzeDiagnostic(
        recordingPaths: List<String>,
        expectedTexts: List<String?> = emptyList(),
        onSuccess: (DiagnosticResult) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("DiagnosticVM", "=== Starting diagnostic analysis ===")
                Log.d("DiagnosticVM", "Recording paths: $recordingPaths")

                // Аналізуємо всі 4 записи паралельно з Gemini
                val tasks = listOf("Читання", "Дикція", "Емоційність", "Вільна мова")
                val exerciseTypes = listOf(
                    "reading",       // Завдання 1: Читання
                    "diction",       // Завдання 2: Дикція (скоромовка)
                    "emotional",     // Завдання 3: Емоційність
                    "spontaneous"    // Завдання 4: Вільна мова (спонтанна)
                )

                // Запускаємо аналіз паралельно для всіх 4 записів
                val analysisResults = recordingPaths.mapIndexed { index, path ->
                    async {
                        Log.d("DiagnosticVM", "Analyzing task ${index + 1}: ${tasks[index]}")
                        geminiApiClient.analyzeVoiceRecording(
                            audioFilePath = path,
                            expectedText = expectedTexts.getOrNull(index),
                            exerciseType = exerciseTypes[index],
                            additionalContext = "Діагностичне завдання: ${tasks[index]}"
                        )
                    }
                }.awaitAll()

                Log.d("DiagnosticVM", "All analyses completed")

                // Перевіряємо чи всі аналізи успішні
                val allSuccess = analysisResults.all { it.isSuccess }
                if (!allSuccess) {
                    val errors = analysisResults.filter { it.isFailure }
                        .mapNotNull { it.exceptionOrNull()?.message }
                    Log.e("DiagnosticVM", "Some analyses failed: $errors")
                    onError("Не вдалося проаналізувати деякі записи: ${errors.joinToString()}")
                    return@launch
                }

                // Отримуємо результати
                val voiceResults = analysisResults.map { it.getOrThrow() }

                // Конвертуємо в DiagnosticResult з 6 метриками
                val diagnosticResult = convertToDiagnosticResult(voiceResults)

                Log.d("DiagnosticVM", "Diagnostic result: overallScore=${diagnosticResult.overallScore}")
                onSuccess(diagnosticResult)

            } catch (e: Exception) {
                Log.e("DiagnosticVM", "Analysis failed", e)
                onError(e.message ?: "Помилка аналізу")
            }
        }
    }

    private fun convertToDiagnosticResult(
        voiceResults: List<com.aivoicepower.domain.model.VoiceAnalysisResult>
    ): DiagnosticResult {
        // Рахуємо середні значення для кожної з 6 core metrics з усіх 4 записів
        val avgDiction = voiceResults.map { it.diction }.average().toInt()
        val avgTempo = voiceResults.map { it.tempo }.average().toInt()
        val avgIntonation = voiceResults.map { it.intonation }.average().toInt()
        val avgStructure = voiceResults.map { it.structure }.average().toInt()
        val avgConfidence = voiceResults.map { it.confidence }.average().toInt()
        val avgFillerWords = voiceResults.map { it.fillerWords }.average().toInt()

        // Загальна оцінка - середнє всіх метрик
        val overallScore = (avgDiction + avgTempo + avgIntonation + avgStructure + avgConfidence + avgFillerWords) / 6

        // Збираємо загальні сильні сторони та зони покращення
        val allStrengths = voiceResults.flatMap { it.strengths }.distinct().take(5)
        val allImprovements = voiceResults.flatMap { it.improvements }.distinct().take(5)

        // Генеруємо рекомендації на основі найслабших метрик
        val recommendations = generateRecommendations(
            avgDiction,
            avgTempo,
            avgIntonation,
            avgStructure,
            avgConfidence,
            avgFillerWords,
            voiceResults
        )

        return DiagnosticResult(
            overallScore = overallScore,
            dictionScore = avgDiction,
            tempoScore = avgTempo,
            intonationScore = avgIntonation,
            structureScore = avgStructure,
            confidenceScore = avgConfidence,
            fillerWordsScore = avgFillerWords,
            strengths = allStrengths.ifEmpty { listOf("Гарний початок!", "Є потенціал для розвитку") },
            areasToImprove = allImprovements.ifEmpty { listOf("Продовжуй практикуватись", "Тренуйся регулярно") },
            recommendations = recommendations
        )
    }

    private fun generateRecommendations(
        dictionScore: Int,
        tempoScore: Int,
        intonationScore: Int,
        structureScore: Int,
        confidenceScore: Int,
        fillerWordsScore: Int,
        voiceResults: List<com.aivoicepower.domain.model.VoiceAnalysisResult>
    ): List<String> {
        val recommendations = mutableListOf<String>()

        // Додаємо конкретні поради від Gemini
        voiceResults.forEach { result ->
            if (result.tip.isNotBlank() && !recommendations.contains(result.tip)) {
                recommendations.add(result.tip)
            }
        }

        // Додаємо загальні рекомендації на основі найслабших навичок
        val scores = mapOf(
            "Дикція" to dictionScore,
            "Темп" to tempoScore,
            "Інтонація" to intonationScore,
            "Структура" to structureScore,
            "Впевненість" to confidenceScore,
            "Слова-паразити" to fillerWordsScore
        )

        val weakestSkill = scores.minByOrNull { it.value }
        if (weakestSkill != null && weakestSkill.value < 60) {
            val recommendation = when (weakestSkill.key) {
                "Дикція" -> "Практикуй скоромовки щодня для покращення дикції"
                "Темп" -> "Контролюй швидкість мовлення, роби паузи"
                "Інтонація" -> "Тренуй емоційне читання з різними інтонаціями"
                "Структура" -> "Плануй свої думки: вступ, основна частина, висновок"
                "Впевненість" -> "Практикуй виступи перед дзеркалом для впевненості"
                "Слова-паразити" -> "Усвідомлюй та замінюй 'е-е', 'ну', 'типу' на паузи"
                else -> "Продовжуй регулярні тренування"
            }
            if (!recommendations.contains(recommendation)) {
                recommendations.add(recommendation)
            }
        }

        return recommendations.take(3)
    }
}
