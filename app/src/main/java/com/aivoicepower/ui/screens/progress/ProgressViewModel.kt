package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.repository.AchievementRepository
import com.aivoicepower.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    init {
        loadProgress()
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Load user progress
                userRepository.getUserProgress().collect { progress ->
                    if (progress == null) {
                        // Create default state if no progress exists
                        _state.update {
                            it.copy(
                                isLoading = false,
                                skillLevels = SkillType.entries.associateWith { 0 }
                            )
                        }
                        return@collect
                    }

                    val overallLevel = progress.calculateOverallLevel()

                    _state.update {
                        it.copy(
                            overallLevel = overallLevel,
                            currentStreak = progress.currentStreak,
                            longestStreak = progress.longestStreak,
                            totalExercises = progress.totalExercises,
                            totalMinutes = progress.totalMinutes,
                            totalRecordings = progress.totalRecordings,
                            skillLevels = progress.skillLevels,
                            isLoading = false
                        )
                    }
                }

                // Load weekly progress
                loadWeeklyProgress()

                // Load achievements
                loadAchievements()

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

    private fun loadWeeklyProgress() {
        viewModelScope.launch {
            // Generate weekly data (mock for now - will be replaced with actual data)
            val weeklyProgress = (0..6).map { daysAgo ->
                val date = LocalDate.now().minusDays(daysAgo.toLong())
                DailyProgress(
                    date = date.toString(),
                    exercises = if (daysAgo < 3) (5..15).random() else 0,
                    minutes = if (daysAgo < 3) (10..30).random() else 0
                )
            }.reversed()

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
