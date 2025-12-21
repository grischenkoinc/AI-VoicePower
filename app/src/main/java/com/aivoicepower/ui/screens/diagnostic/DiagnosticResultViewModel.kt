package com.aivoicepower.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticResultViewModel @Inject constructor(
    private val diagnosticResultDao: DiagnosticResultDao,
    private val userProgressDao: UserProgressDao
) : ViewModel() {

    private val _state = MutableStateFlow(DiagnosticResultState())
    val state: StateFlow<DiagnosticResultState> = _state.asStateFlow()

    init {
        loadDiagnosticResult()
    }

    private fun loadDiagnosticResult() {
        viewModelScope.launch {
            try {
                // Завантажуємо останній результат діагностики
                diagnosticResultDao.getLatestDiagnostic().collect { entity ->
                    if (entity != null) {
                        // Конвертуємо Entity → Display model
                        val display = DiagnosticResultDisplay(
                            overall = calculateOverall(entity),
                            metrics = listOf(
                                MetricDisplay(
                                    name = "Дикція",
                                    score = entity.diction,
                                    label = getScoreLabel(entity.diction),
                                    description = getScoreDescription("diction", entity.diction)
                                ),
                                MetricDisplay(
                                    name = "Темп мовлення",
                                    score = entity.tempo,
                                    label = getScoreLabel(entity.tempo),
                                    description = getScoreDescription("tempo", entity.tempo)
                                ),
                                MetricDisplay(
                                    name = "Інтонація",
                                    score = entity.intonation,
                                    label = getScoreLabel(entity.intonation),
                                    description = getScoreDescription("intonation", entity.intonation)
                                ),
                                MetricDisplay(
                                    name = "Гучність",
                                    score = entity.volume,
                                    label = getScoreLabel(entity.volume),
                                    description = getScoreDescription("volume", entity.volume)
                                ),
                                MetricDisplay(
                                    name = "Структура",
                                    score = entity.structure,
                                    label = getScoreLabel(entity.structure),
                                    description = getScoreDescription("structure", entity.structure)
                                ),
                                MetricDisplay(
                                    name = "Впевненість",
                                    score = entity.confidence,
                                    label = getScoreLabel(entity.confidence),
                                    description = getScoreDescription("confidence", entity.confidence)
                                ),
                                MetricDisplay(
                                    name = "Без паразитів",
                                    score = entity.fillerWords,
                                    label = getScoreLabel(entity.fillerWords),
                                    description = getScoreDescription("fillerWords", entity.fillerWords)
                                )
                            ),
                            strengths = generateStrengths(entity),
                            improvements = generateImprovements(entity),
                            recommendations = generateRecommendations(entity)
                        )

                        _state.update {
                            it.copy(
                                isLoading = false,
                                result = display
                            )
                        }

                        // Зберігаємо рівні навичок в UserProgress
                        saveToUserProgress(entity)
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалося завантажити результати"
                    )
                }
            }
        }
    }

    private fun calculateOverall(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): Int {
        return (entity.diction + entity.tempo + entity.intonation +
                entity.volume + entity.structure + entity.confidence +
                entity.fillerWords) / 7
    }

    private fun getScoreLabel(score: Int): String {
        return when {
            score >= 85 -> "Відмінно"
            score >= 70 -> "Добре"
            score >= 50 -> "Середньо"
            else -> "Потребує покращення"
        }
    }

    private fun getScoreDescription(metric: String, score: Int): String {
        // Fake descriptions based on score
        return when (metric) {
            "diction" -> when {
                score >= 70 -> "Чітке вимовляння звуків"
                else -> "Працюй над чіткістю вимови"
            }
            "tempo" -> when {
                score >= 70 -> "Гарний темп мовлення"
                score >= 50 -> "Невелика поспіх"
                else -> "Занадто швидко або повільно"
            }
            "intonation" -> when {
                score >= 70 -> "Виразна інтонація"
                else -> "Можна додати більше виразності"
            }
            "volume" -> when {
                score >= 70 -> "Гарна гучність голосу"
                else -> "Говори трохи голосніше"
            }
            "structure" -> when {
                score >= 70 -> "Структурована мова"
                else -> "Працюй над логікою викладу"
            }
            "confidence" -> when {
                score >= 70 -> "Впевнена манера мовлення"
                else -> "Додай більше впевненості"
            }
            "fillerWords" -> when {
                score >= 70 -> "Мало слів-паразитів"
                else -> "Зменш кількість слів-паразитів"
            }
            else -> "Гарний результат"
        }
    }

    private fun generateStrengths(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): List<String> {
        val strengths = mutableListOf<String>()

        if (entity.diction >= 70) strengths.add("Чітка дикція та вимова")
        if (entity.tempo >= 70) strengths.add("Гарний темп мовлення")
        if (entity.intonation >= 70) strengths.add("Виразна інтонація")
        if (entity.volume >= 70) strengths.add("Гарна гучність голосу")
        if (entity.structure >= 70) strengths.add("Структурована мова")
        if (entity.confidence >= 70) strengths.add("Впевнена манера мовлення")
        if (entity.fillerWords >= 70) strengths.add("Мало слів-паразитів")

        return if (strengths.size >= 2) {
            strengths.take(3)
        } else {
            listOf("Ти на правильному шляху!", "Є базові навички для розвитку")
        }
    }

    private fun generateImprovements(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): List<String> {
        val improvements = mutableListOf<String>()

        if (entity.diction < 70) improvements.add("Покращ чіткість дикції")
        if (entity.tempo < 70) improvements.add("Працюй над темпом мовлення")
        if (entity.intonation < 70) improvements.add("Додай більше емоційності")
        if (entity.volume < 70) improvements.add("Збільш гучність голосу")
        if (entity.structure < 70) improvements.add("Працюй над структурою думок")
        if (entity.confidence < 70) improvements.add("Розвивай впевненість")
        if (entity.fillerWords < 70) improvements.add("Зменш кількість слів-паразитів")

        return improvements.take(3)
    }

    private fun generateRecommendations(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): List<RecommendationDisplay> {
        val recommendations = mutableListOf<RecommendationDisplay>()

        // Рекомендації на основі найслабших метрик
        if (entity.fillerWords < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "📖",
                    title = "Курс: \"Чисте мовлення\"",
                    description = "Позбався слів-паразитів за 14 днів",
                    actionRoute = "courses/clean_speech"
                )
            )
        }

        if (entity.diction < 60 || entity.tempo < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "🎤",
                    title = "Щоденна розминка",
                    description = "Почни з артикуляційної гімнастики",
                    actionRoute = "warmup/articulation"
                )
            )
        }

        if (entity.intonation < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "🎭",
                    title = "Практика інтонації",
                    description = "Емоційне читання 10 хв щодня",
                    actionRoute = "courses/intonation"
                )
            )
        }

        if (entity.confidence < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "💪",
                    title = "Тренуй впевненість",
                    description = "Імпровізуй на випадкові теми",
                    actionRoute = "improvisation/random"
                )
            )
        }

        // Завжди додаємо загальну рекомендацію
        recommendations.add(
            RecommendationDisplay(
                icon = "🏠",
                title = "Почни з головного",
                description = "Переглянь персоналізований план на сьогодні",
                actionRoute = "home"
            )
        )

        return recommendations.take(3)
    }

    private suspend fun saveToUserProgress(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity) {
        val existingProgress = userProgressDao.getUserProgressOnce()

        if (existingProgress == null) {
            // Створюємо новий прогрес
            userProgressDao.insertOrUpdate(
                UserProgressEntity(
                    id = "default_progress",
                    dictionLevel = entity.diction,
                    tempoLevel = entity.tempo,
                    intonationLevel = entity.intonation,
                    volumeLevel = entity.volume,
                    structureLevel = entity.structure,
                    confidenceLevel = entity.confidence,
                    fillerWordsLevel = entity.fillerWords
                )
            )
        } else {
            // Оновлюємо існуючий
            userProgressDao.updateSkillLevels(
                diction = entity.diction,
                tempo = entity.tempo,
                intonation = entity.intonation,
                volume = entity.volume,
                structure = entity.structure,
                confidence = entity.confidence,
                fillerWords = entity.fillerWords
            )
        }
    }
}
