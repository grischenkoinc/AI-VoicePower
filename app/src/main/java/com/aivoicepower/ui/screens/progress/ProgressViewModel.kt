package com.aivoicepower.ui.screens.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.repository.AchievementRepository
import com.aivoicepower.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository,
    private val userProgressDao: UserProgressDao
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    init {
        Log.d("ProgressViewModel", "ProgressViewModel initialized")
        loadProgress()
        observeSkills()
    }

    fun onEvent(event: ProgressEvent) {
        when (event) {
            ProgressEvent.Refresh -> loadProgress()
            ProgressEvent.NavigateToCompare -> { /* Handled by Screen */ }
            ProgressEvent.NavigateToAchievements -> { /* Handled by Screen */ }
            ProgressEvent.NavigateToHistory -> { /* Handled by Screen */ }
        }
    }

    private fun loadProgress() {
        Log.d("ProgressViewModel", "loadProgress() called")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Load user progress
                Log.d("ProgressViewModel", "Getting user progress from repository")
                val progress = userRepository.getUserProgress().first()
                Log.d("ProgressViewModel", "Got user progress: $progress")

                if (progress != null) {
                    _state.update {
                        it.copy(
                            currentStreak = progress.currentStreak,
                            longestStreak = progress.longestStreak,
                            totalExercises = progress.totalExercises,
                            totalMinutes = progress.totalMinutes,
                            totalRecordings = progress.totalRecordings,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }

                // Load weekly progress
                loadWeeklyProgress()

                // Load achievements
                loadAchievements()

            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error loading progress", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Помилка завантаження: ${e.message}"
                    )
                }
            }
        }
    }

    private fun observeSkills() {
        viewModelScope.launch {
            userProgressDao.getProgressFlow().collect { progress ->
                if (progress != null) {
                    val skillLevels = mapOf(
                        SkillType.DICTION to progress.dictionLevel.toInt(),
                        SkillType.TEMPO to progress.tempoLevel.toInt(),
                        SkillType.INTONATION to progress.intonationLevel.toInt(),
                        SkillType.VOLUME to progress.volumeLevel.toInt(),
                        SkillType.STRUCTURE to progress.structureLevel.toInt(),
                        SkillType.CONFIDENCE to progress.confidenceLevel.toInt(),
                        SkillType.FILLER_WORDS to progress.fillerWordsLevel.toInt()
                    )
                    val overallLevel = skillLevels.values.average().toInt()
                    _state.update {
                        it.copy(
                            skillLevels = skillLevels,
                            overallLevel = overallLevel
                        )
                    }
                }
            }
        }
    }

    private fun loadWeeklyProgress() {
        viewModelScope.launch {
            // Load real weekly activity data from recordings
            val weeklyProgress = userRepository.getWeeklyActivity()
            Log.d("ProgressViewModel", "loadWeeklyProgress: Loaded ${weeklyProgress.size} days")
            _state.update { it.copy(weeklyProgress = weeklyProgress) }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                achievementRepository.getUnlockedAchievements().collect { achievements ->
                    _state.update {
                        it.copy(
                            recentAchievements = achievements.take(3),
                            unlockedAchievements = achievements.size
                        )
                    }
                }
            } catch (_: Exception) {
                // Ignore achievement loading errors
            }
        }

        viewModelScope.launch {
            try {
                achievementRepository.getAllAchievements().collect { allAchievements ->
                    _state.update { it.copy(totalAchievements = allAchievements.size) }
                }
            } catch (_: Exception) {
                // Ignore
            }
        }
    }
}
