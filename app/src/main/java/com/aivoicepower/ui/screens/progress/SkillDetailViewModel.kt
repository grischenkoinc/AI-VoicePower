package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.SkillSnapshotDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.utils.SkillLevelUtils
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
    private val userProgressDao: UserProgressDao,
    private val diagnosticResultDao: DiagnosticResultDao,
    private val skillSnapshotDao: SkillSnapshotDao,
    private val recordingDao: RecordingDao
) : ViewModel() {

    private val skillTypeString: String = savedStateHandle["skillType"] ?: "DICTION"
    private val skillType: SkillType = SkillType.valueOf(skillTypeString)

    private val _state = MutableStateFlow(SkillDetailState(skillType = skillType))
    val state: StateFlow<SkillDetailState> = _state.asStateFlow()

    init {
        loadSkillDetails()
        observeCurrentLevel()
    }

    private fun observeCurrentLevel() {
        viewModelScope.launch {
            userProgressDao.getProgressFlow().collect { progress ->
                if (progress != null) {
                    val level = getSkillFromProgress(progress)
                    _state.update {
                        it.copy(
                            currentLevel = level,
                            statusLabel = SkillLevelUtils.getSkillLabel(level)
                        )
                    }
                }
            }
        }
    }

    private fun loadSkillDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Current level from DB
                val progress = userProgressDao.getProgress()
                val currentLevel = if (progress != null) getSkillFromProgress(progress) else 0

                // Initial level from first diagnostic
                val diagnostic = diagnosticResultDao.getInitialDiagnostic().first()
                val initialLevel = if (diagnostic != null) {
                    when (skillType) {
                        SkillType.DICTION -> diagnostic.diction
                        SkillType.TEMPO -> diagnostic.tempo
                        SkillType.INTONATION -> diagnostic.intonation
                        SkillType.VOLUME -> diagnostic.volume
                        SkillType.STRUCTURE -> diagnostic.structure
                        SkillType.CONFIDENCE -> diagnostic.confidence
                        SkillType.FILLER_WORDS -> diagnostic.fillerWords
                        else -> 0
                    }
                } else 0

                // Real history from snapshots
                val snapshots = skillSnapshotDao.getRecent(30).first()
                val historyPoints = snapshots.map { snapshot ->
                    SkillHistoryPoint(
                        date = snapshot.date,
                        level = getSkillFromSnapshot(snapshot)
                    )
                }

                // Real practice minutes from recordings
                val allRecordings = recordingDao.getAllRecordings().first()
                val totalPracticeMinutes = (allRecordings.sumOf { it.durationMs } / 60000).toInt()

                // Curated exercises and recommendations per skill
                val exercises = getExercisesForSkill(skillType)
                val recommendations = getRecommendationsForSkill(skillType)

                _state.update {
                    it.copy(
                        isLoading = false,
                        currentLevel = currentLevel,
                        initialLevel = initialLevel,
                        statusLabel = SkillLevelUtils.getSkillLabel(currentLevel),
                        historyPoints = historyPoints,
                        impactfulExercises = exercises,
                        recommendations = recommendations,
                        totalPracticeMinutes = totalPracticeMinutes
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

    private fun getSkillFromProgress(
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity
    ): Int = when (skillType) {
        SkillType.DICTION -> progress.dictionLevel.toInt()
        SkillType.TEMPO -> progress.tempoLevel.toInt()
        SkillType.INTONATION -> progress.intonationLevel.toInt()
        SkillType.VOLUME -> progress.volumeLevel.toInt()
        SkillType.STRUCTURE -> progress.structureLevel.toInt()
        SkillType.CONFIDENCE -> progress.confidenceLevel.toInt()
        SkillType.FILLER_WORDS -> progress.fillerWordsLevel.toInt()
        else -> 0
    }

    private fun getSkillFromSnapshot(
        snapshot: com.aivoicepower.data.local.database.entity.SkillSnapshotEntity
    ): Int = when (skillType) {
        SkillType.DICTION -> snapshot.diction.toInt()
        SkillType.TEMPO -> snapshot.tempo.toInt()
        SkillType.INTONATION -> snapshot.intonation.toInt()
        SkillType.VOLUME -> snapshot.volume.toInt()
        SkillType.STRUCTURE -> snapshot.structure.toInt()
        SkillType.CONFIDENCE -> snapshot.confidence.toInt()
        SkillType.FILLER_WORDS -> snapshot.fillerWords.toInt()
        else -> 0
    }

    private fun getExercisesForSkill(skillType: SkillType): List<ExerciseImpact> {
        return when (skillType) {
            SkillType.DICTION -> listOf(
                ExerciseImpact("Скоромовки", "tongue_twister", 85),
                ExerciseImpact("Артикуляційна розминка", "articulation", 72),
                ExerciseImpact("Читання вголос", "reading", 68)
            )
            SkillType.TEMPO -> listOf(
                ExerciseImpact("Повільне читання", "slow_motion", 82),
                ExerciseImpact("Дебати", "debate", 78),
                ExerciseImpact("Швидкий раунд", "speed_round", 75)
            )
            SkillType.INTONATION -> listOf(
                ExerciseImpact("Читання з емоціями", "emotion_reading", 88),
                ExerciseImpact("Зміна емоцій", "emotion_switch", 80),
                ExerciseImpact("Голосова розминка", "voice", 69)
            )
            SkillType.VOLUME -> listOf(
                ExerciseImpact("Дихальні вправи", "breathing", 80),
                ExerciseImpact("Презентація", "presentation", 76),
                ExerciseImpact("Голосова розминка", "voice", 71)
            )
            SkillType.STRUCTURE -> listOf(
                ExerciseImpact("Вільне мовлення", "free_speech", 85),
                ExerciseImpact("Переказ", "retelling", 79),
                ExerciseImpact("Сторітелінг", "storytelling", 75)
            )
            SkillType.CONFIDENCE -> listOf(
                ExerciseImpact("Співбесіда", "job_interview", 90),
                ExerciseImpact("Презентація", "presentation", 82),
                ExerciseImpact("Дебати", "debate", 75)
            )
            SkillType.FILLER_WORDS -> listOf(
                ExerciseImpact("Без зупинок", "no_hesitation", 85),
                ExerciseImpact("Вільне мовлення", "free_speech", 77),
                ExerciseImpact("Переговори", "negotiation", 70)
            )
            else -> emptyList()
        }
    }

    private fun getRecommendationsForSkill(skillType: SkillType): List<SkillRecommendation> {
        return when (skillType) {
            SkillType.DICTION -> listOf(
                SkillRecommendation("Промовляйте складні слова повільно", "Скоромовки"),
                SkillRecommendation("Практикуйте скоромовки щодня", "Артикуляція"),
                SkillRecommendation("Записуйте себе і слухайте")
            )
            SkillType.TEMPO -> listOf(
                SkillRecommendation("Тримайте середній темп 120-150 слів/хв"),
                SkillRecommendation("Робіть паузи для наголосу", "Повільне читання"),
                SkillRecommendation("Варіюйте швидкість для драматизму")
            )
            SkillType.INTONATION -> listOf(
                SkillRecommendation("Підвищуйте голос на питаннях"),
                SkillRecommendation("Знижуйте на твердженнях"),
                SkillRecommendation("Використовуйте емоційні наголоси", "Читання з емоціями")
            )
            SkillType.VOLUME -> listOf(
                SkillRecommendation("Дихайте діафрагмою", "Дихання"),
                SkillRecommendation("Проектуйте голос вперед"),
                SkillRecommendation("Контролюйте силу без крику")
            )
            SkillType.STRUCTURE -> listOf(
                SkillRecommendation("Плануйте виступ заздалегідь"),
                SkillRecommendation("Використовуйте чіткі переходи", "Вільне мовлення"),
                SkillRecommendation("Дотримуйтесь логічної послідовності")
            )
            SkillType.CONFIDENCE -> listOf(
                SkillRecommendation("Підтримуйте зоровий контакт"),
                SkillRecommendation("Стійте впевнено"),
                SkillRecommendation("Говоріть з переконанням", "Презентація")
            )
            SkillType.FILLER_WORDS -> listOf(
                SkillRecommendation("Робіть паузи замість \"ммм\"", "Без зупинок"),
                SkillRecommendation("Усвідомлюйте свої паразити"),
                SkillRecommendation("Практикуйте чисте мовлення")
            )
            else -> emptyList()
        }
    }
}
