package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.SkillSnapshotDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.service.SkillUpdateService
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
    private val recordingDao: RecordingDao,
    private val skillUpdateService: SkillUpdateService
) : ViewModel() {

    private val skillTypeString: String = savedStateHandle["skillType"] ?: "DICTION"
    private val skillType: SkillType = SkillType.valueOf(skillTypeString)

    private val _state = MutableStateFlow(SkillDetailState(skillType = skillType))
    val state: StateFlow<SkillDetailState> = _state.asStateFlow()

    // Exercise type display names (Ukrainian)
    private val exerciseDisplayNames = mapOf(
        "tongue_twister" to "Скоромовки",
        "reading" to "Читання вголос",
        "slow_motion" to "Повільне читання",
        "minimal_pairs" to "Мінімальні пари",
        "emotion_reading" to "Читання з емоціями",
        "free_speech" to "Вільне мовлення",
        "storytelling" to "Сторітелінг",
        "debate" to "Дебати",
        "sales" to "Продажі",
        "job_interview" to "Співбесіда",
        "presentation" to "Презентація",
        "negotiation" to "Переговори",
        "retelling" to "Переказ",
        "dialogue" to "Діалог",
        "no_hesitation" to "Без зупинок",
        "metaphor_master" to "Майстер метафор",
        "emotion_switch" to "Зміна емоцій",
        "speed_round" to "Швидкий раунд",
        "character_voice" to "Голос персонажа"
    )

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

                // Real exercises and recommendations based on actual data
                val exercises = calculateExercisesForSkill(allRecordings.map { it.type }.distinct().toSet())
                val recommendations = calculateRecommendationsForSkill(
                    currentLevel,
                    allRecordings.map { it.type }.distinct().toSet()
                )

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

    private fun calculateExercisesForSkill(practicedTypes: Set<String>): List<ExerciseImpact> {
        val skillKey = mapSkillTypeToKey(skillType) ?: return emptyList()
        val impacts = mutableListOf<ExerciseImpact>()

        for ((exerciseType, skillWeights) in skillUpdateService.exerciseSkillMap) {
            if (exerciseType == "diagnostic") continue
            val weight = skillWeights[skillKey] ?: continue
            if (weight == 0f) continue
            // Only show exercises user has actually practiced
            if (exerciseType !in practicedTypes) continue

            val impactScore = (weight * 100).toInt()
            val displayName = exerciseDisplayNames[exerciseType] ?: exerciseType

            impacts.add(ExerciseImpact(
                exerciseName = displayName,
                exerciseType = exerciseType,
                impactScore = impactScore
            ))
        }

        return impacts.sortedByDescending { it.impactScore }.take(5)
    }

    private fun calculateRecommendationsForSkill(
        currentLevel: Int,
        practicedTypes: Set<String>
    ): List<SkillRecommendation> {
        val skillKey = mapSkillTypeToKey(skillType) ?: return emptyList()
        val recommendations = mutableListOf<SkillRecommendation>()

        // If no recordings at all
        if (practicedTypes.isEmpty()) {
            recommendations.add(SkillRecommendation("Почніть з будь-якої вправи, щоб побачити персональні рекомендації"))
            recommendations.add(SkillRecommendation("Пройдіть діагностику для визначення початкового рівня"))
            return recommendations
        }

        // 1. Find unpracticed exercise types that have high weight for this skill
        val unpracticed = skillUpdateService.exerciseSkillMap.entries
            .filter { (type, weights) ->
                type != "diagnostic" &&
                type !in practicedTypes &&
                (weights[skillKey] ?: 0f) > 0f
            }
            .sortedByDescending { (_, weights) -> weights[skillKey] ?: 0f }

        for ((type, weights) in unpracticed.take(2)) {
            val weight = weights[skillKey] ?: continue
            val displayName = exerciseDisplayNames[type] ?: type
            val isPrimary = weight >= SkillUpdateService.PRIMARY

            recommendations.add(SkillRecommendation(
                tip = if (isPrimary) {
                    "Спробуйте \"$displayName\" — це одна з найефективніших вправ для цієї навички"
                } else {
                    "Додайте \"$displayName\" до практики для різнобічного розвитку"
                },
                exerciseName = displayName
            ))
        }

        // 2. Level-based advice
        when {
            currentLevel < 30 -> recommendations.add(SkillRecommendation(
                tip = "Практикуйте щодня по 5-10 хвилин — регулярність важливіша за тривалість"
            ))
            currentLevel < 50 -> recommendations.add(SkillRecommendation(
                tip = "Гарний початок! Збільшуйте тривалість практики та додавайте нові типи вправ"
            ))
            currentLevel < 70 -> recommendations.add(SkillRecommendation(
                tip = "Ви на вірному шляху! Зосередьтесь на складніших вправах для подальшого росту"
            ))
            currentLevel < 90 -> recommendations.add(SkillRecommendation(
                tip = "Відмінний рівень! Для майстерності практикуйте в різних контекстах і стилях"
            ))
            else -> recommendations.add(SkillRecommendation(
                tip = "Чудовий результат! Підтримуйте рівень регулярною практикою"
            ))
        }

        // 3. Recommend continuing the most impactful practiced exercise
        val highImpactPracticed = skillUpdateService.exerciseSkillMap.entries
            .filter { (type, weights) ->
                type != "diagnostic" &&
                type in practicedTypes &&
                (weights[skillKey] ?: 0f) >= SkillUpdateService.PRIMARY
            }
            .maxByOrNull { (_, weights) -> weights[skillKey] ?: 0f }

        if (highImpactPracticed != null) {
            val displayName = exerciseDisplayNames[highImpactPracticed.key] ?: highImpactPracticed.key
            recommendations.add(SkillRecommendation(
                tip = "Продовжуйте практикувати \"$displayName\" — ви вже маєте досвід і це ефективно працює",
                exerciseName = displayName
            ))
        }

        return recommendations.take(4)
    }

    private fun mapSkillTypeToKey(skillType: SkillType): SkillUpdateService.Skill? = when (skillType) {
        SkillType.DICTION -> SkillUpdateService.Skill.DICTION
        SkillType.TEMPO -> SkillUpdateService.Skill.TEMPO
        SkillType.INTONATION -> SkillUpdateService.Skill.INTONATION
        SkillType.VOLUME -> SkillUpdateService.Skill.VOLUME
        SkillType.CONFIDENCE -> SkillUpdateService.Skill.CONFIDENCE
        SkillType.FILLER_WORDS -> SkillUpdateService.Skill.FILLER_WORDS
        SkillType.STRUCTURE -> SkillUpdateService.Skill.STRUCTURE
        else -> null
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
}
