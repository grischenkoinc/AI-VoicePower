package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.service.SkillUpdateService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val skillUpdateService: SkillUpdateService
) : ViewModel() {

    private val _state = MutableStateFlow(BreathingState())
    val state: StateFlow<BreathingState> = _state.asStateFlow()

    private var breathingJob: Job? = null

    init {
        loadTodayProgress()
    }

    fun onEvent(event: BreathingEvent) {
        when (event) {
            is BreathingEvent.ExerciseClicked -> {
                _state.update {
                    it.copy(
                        selectedExercise = event.exercise,
                        isExerciseDialogOpen = true,
                        showInstructions = true,
                        totalSeconds = event.exercise.durationSeconds,
                        elapsedSeconds = 0,
                        currentPhase = BreathingPhase.INHALE,
                        phaseProgress = 0f,
                        isRunning = false
                    )
                }
            }

            BreathingEvent.HideInstructions -> {
                _state.update {
                    it.copy(showInstructions = false)
                }
            }

            BreathingEvent.ExerciseDialogDismissed -> {
                stopBreathing()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false,
                        showInstructions = false
                    )
                }
            }

            BreathingEvent.StartBreathing -> {
                startBreathing()
            }

            BreathingEvent.PauseBreathing -> {
                stopBreathing()
            }

            is BreathingEvent.Tick -> {
                _state.update {
                    it.copy(
                        elapsedSeconds = event.elapsedSeconds,
                        currentPhase = event.phase,
                        phaseProgress = event.phaseProgress
                    )
                }

                // Auto-complete when done
                if (event.elapsedSeconds >= _state.value.totalSeconds) {
                    stopBreathing()
                    markCurrentAsCompleted()
                }
            }

            BreathingEvent.MarkAsCompleted -> {
                markCurrentAsCompleted()
            }

            BreathingEvent.SkipExercise -> {
                stopBreathing()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false,
                        showInstructions = false
                    )
                }
            }
        }
    }

    private fun loadTodayProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val completion = warmupCompletionDao.getCompletion(today, "breathing")

            // Only load completions if they're from today (24-hour reset logic)
            if (completion != null && completion.date == today) {
                val completed = (1..completion.exercisesCompleted).toSet()
                _state.update { it.copy(completedToday = completed) }
            } else {
                // Reset completions if date doesn't match or no completion exists
                _state.update { it.copy(completedToday = emptySet()) }
            }
        }
    }

    private fun startBreathing() {
        stopBreathing()

        _state.update { it.copy(isRunning = true) }

        val pattern = _state.value.selectedExercise?.pattern ?: return

        breathingJob = viewModelScope.launch {
            var elapsed = _state.value.elapsedSeconds
            val startTime = System.currentTimeMillis() - (elapsed * 1000L)

            while (_state.value.isRunning && elapsed < _state.value.totalSeconds) {
                delay(16) // Оновлюємо кожні 16ms (~60 FPS) для максимальної плавності

                // Розрахунок реального часу в мілісекундах
                val currentTime = System.currentTimeMillis()
                val elapsedMillis = currentTime - startTime
                val elapsedFloat = (elapsedMillis / 1000f)
                elapsed = elapsedFloat.toInt()
                elapsed = minOf(elapsed, _state.value.totalSeconds)

                // Розрахунок позиції в поточному циклі (Float для плавності)
                val cyclePositionFloat = elapsedFloat % pattern.cycleDurationSeconds

                val (phase, progress) = calculatePhaseAndProgress(cyclePositionFloat, pattern)

                onEvent(BreathingEvent.Tick(elapsed, phase, progress))
            }
        }
    }

    private fun stopBreathing() {
        breathingJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }

    private fun calculatePhaseAndProgress(
        secondsInCycle: Float,
        pattern: BreathingPattern
    ): Pair<BreathingPhase, Float> {
        var remaining = secondsInCycle

        // INHALE
        if (remaining < pattern.inhaleSeconds) {
            return BreathingPhase.INHALE to (remaining / pattern.inhaleSeconds)
        }
        remaining -= pattern.inhaleSeconds

        // INHALE_HOLD
        if (pattern.inhaleHoldSeconds > 0 && remaining < pattern.inhaleHoldSeconds) {
            return BreathingPhase.INHALE_HOLD to (remaining / pattern.inhaleHoldSeconds)
        }
        remaining -= pattern.inhaleHoldSeconds

        // EXHALE
        if (remaining < pattern.exhaleSeconds) {
            return BreathingPhase.EXHALE to (remaining / pattern.exhaleSeconds)
        }
        remaining -= pattern.exhaleSeconds

        // EXHALE_HOLD
        if (pattern.exhaleHoldSeconds > 0 && remaining < pattern.exhaleHoldSeconds) {
            return BreathingPhase.EXHALE_HOLD to (remaining / pattern.exhaleHoldSeconds)
        }

        return BreathingPhase.INHALE to 0f
    }

    private fun markCurrentAsCompleted() {
        val exerciseId = _state.value.selectedExercise?.id ?: return

        _state.update {
            it.copy(
                completedToday = it.completedToday + exerciseId,
                selectedExercise = null,
                isExerciseDialogOpen = false
            )
        }

        saveProgress()
        viewModelScope.launch {
            skillUpdateService.updateFromWarmup("breathing")
        }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val totalExercises = _state.value.exercises.size
            val completedCount = _state.value.completedToday.size

            val entity = WarmupCompletionEntity(
                id = "${today}_breathing",
                date = today,
                category = "breathing",
                completedAt = System.currentTimeMillis(),
                exercisesCompleted = completedCount,
                totalExercises = totalExercises
            )

            warmupCompletionDao.insertCompletion(entity)

            // Оновлюємо DataStore
            val estimatedMinutes = 2
            if (completedCount == totalExercises) {
                userPreferencesDataStore.addMinutes(estimatedMinutes)
            }
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
