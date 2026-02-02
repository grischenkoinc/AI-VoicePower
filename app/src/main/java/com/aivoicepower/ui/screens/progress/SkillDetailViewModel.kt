package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val skillTypeString: String = savedStateHandle["skillType"] ?: "DICTION"
    private val skillType: SkillType = SkillType.valueOf(skillTypeString)

    private val _state = MutableStateFlow(SkillDetailState(skillType = skillType))
    val state: StateFlow<SkillDetailState> = _state.asStateFlow()

    init {
        loadSkillDetails()
    }

    private fun loadSkillDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Get current skill level
                val progress = userRepository.getUserProgress().first()
                val currentLevel = progress?.skillLevels?.get(skillType) ?: 0

                // Get initial level from diagnostic
                val diagnostic = userRepository.getInitialDiagnostic().first()
                val initialLevel = when (skillType) {
                    SkillType.DICTION -> diagnostic?.diction ?: 0
                    SkillType.TEMPO -> diagnostic?.tempo ?: 0
                    SkillType.INTONATION -> diagnostic?.intonation ?: 0
                    SkillType.VOLUME -> diagnostic?.volume ?: 0
                    SkillType.STRUCTURE -> diagnostic?.structure ?: 0
                    SkillType.CONFIDENCE -> diagnostic?.confidence ?: 0
                    SkillType.FILLER_WORDS -> diagnostic?.fillerWords ?: 0
                    else -> 0
                }

                // Generate mock data
                val historyPoints = generateMockHistory(initialLevel, currentLevel)
                val exercises = generateMockExercises(skillType)
                val recommendations = generateRecommendations(skillType)

                _state.update {
                    it.copy(
                        isLoading = false,
                        currentLevel = currentLevel,
                        initialLevel = initialLevel,
                        historyPoints = historyPoints,
                        impactfulExercises = exercises,
                        recommendations = recommendations,
                        totalPracticeMinutes = (Math.random() * 60 + 30).toInt()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Помилка завантаження: ${e.message}"
                    )
                }
            }
        }
    }

    private fun generateMockHistory(initialLevel: Int, currentLevel: Int): List<SkillHistoryPoint> {
        val points = mutableListOf<SkillHistoryPoint>()
        val steps = 8
        val increment = (currentLevel - initialLevel).toFloat() / steps

        for (i in 0 until steps) {
            val daysAgo = (steps - i - 1) * 4
            val date = java.time.LocalDate.now().minusDays(daysAgo.toLong())
            val level = (initialLevel + increment * i).toInt()
            points.add(SkillHistoryPoint(date.toString(), level))
        }
        points.add(SkillHistoryPoint(java.time.LocalDate.now().toString(), currentLevel))

        return points
    }

    private fun generateMockExercises(skillType: SkillType): List<ExerciseImpact> {
        return when (skillType) {
            SkillType.DICTION -> listOf(
                ExerciseImpact("Скоромовки", 12, 85),
                ExerciseImpact("Артикуляційна гімнастика", 8, 72),
                ExerciseImpact("Читання вголос", 15, 68)
            )
            SkillType.TEMPO -> listOf(
                ExerciseImpact("Вправи на паузи", 10, 78),
                ExerciseImpact("Читання з метрономом", 7, 82),
                ExerciseImpact("Дебати", 5, 65)
            )
            SkillType.INTONATION -> listOf(
                ExerciseImpact("Читання з емоціями", 11, 88),
                ExerciseImpact("Імітація інтонацій", 9, 74),
                ExerciseImpact("Вправи на наголоси", 6, 69)
            )
            SkillType.VOLUME -> listOf(
                ExerciseImpact("Вправи на гучність", 8, 80),
                ExerciseImpact("Практика проекції", 10, 76),
                ExerciseImpact("Дихальні вправи", 14, 71)
            )
            SkillType.STRUCTURE -> listOf(
                ExerciseImpact("Структурована імпровізація", 7, 85),
                ExerciseImpact("Підготовлені промови", 9, 79),
                ExerciseImpact("Аналіз структури", 5, 68)
            )
            SkillType.CONFIDENCE -> listOf(
                ExerciseImpact("Публічні виступи", 6, 90),
                ExerciseImpact("Відеозаписи себе", 12, 75),
                ExerciseImpact("Позитивні афірмації", 20, 62)
            )
            SkillType.FILLER_WORDS -> listOf(
                ExerciseImpact("Відстеження паразитів", 15, 82),
                ExerciseImpact("Вправи на паузи", 10, 77),
                ExerciseImpact("Усвідомлене мовлення", 18, 70)
            )
            else -> emptyList()
        }
    }

    private fun generateRecommendations(skillType: SkillType): List<SkillRecommendation> {
        return when (skillType) {
            SkillType.DICTION -> listOf(
                SkillRecommendation("Промовляйте складні слова повільно", "Скоромовки"),
                SkillRecommendation("Практикуйте скоромовки щодня", "Артикуляція"),
                SkillRecommendation("Записуйте себе і слухайте", "Самоаналіз")
            )
            SkillType.TEMPO -> listOf(
                SkillRecommendation("Тримайте середній темп 120-150 слів/хв"),
                SkillRecommendation("Робіть паузи для наголосу", "Вправи на паузи"),
                SkillRecommendation("Варіюйте швидкість для драматизму")
            )
            SkillType.INTONATION -> listOf(
                SkillRecommendation("Підвищуйте голос на питаннях"),
                SkillRecommendation("Знижуйте на твердженнях"),
                SkillRecommendation("Використовуйте емоційні наголоси", "Читання з емоціями")
            )
            SkillType.VOLUME -> listOf(
                SkillRecommendation("Дихайте діафрагмою", "Дихання"),
                SkillRecommendation("Проектуйте голос вперед", "Проекція голосу"),
                SkillRecommendation("Контролюйте силу без крику")
            )
            SkillType.STRUCTURE -> listOf(
                SkillRecommendation("Плануйте виступ заздалегідь"),
                SkillRecommendation("Використовуйте чіткі переходи", "Структура мовлення"),
                SkillRecommendation("Дотримуйтесь логічної послідовності")
            )
            SkillType.CONFIDENCE -> listOf(
                SkillRecommendation("Підтримуйте зоровий контакт"),
                SkillRecommendation("Стійте впевнено", "Мова тіла"),
                SkillRecommendation("Говоріть з переконанням", "Впевненість")
            )
            SkillType.FILLER_WORDS -> listOf(
                SkillRecommendation("Робіть паузи замість \"ммм\"", "Паузи"),
                SkillRecommendation("Усвідомлюйте свої паразити", "Самоконтроль"),
                SkillRecommendation("Практикуйте чисте мовлення")
            )
            else -> emptyList()
        }
    }
}
