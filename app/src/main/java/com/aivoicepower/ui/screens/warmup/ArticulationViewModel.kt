package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
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
class ArticulationViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ArticulationState())
    val state: StateFlow<ArticulationState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTodayProgress()
    }

    fun onEvent(event: ArticulationEvent) {
        when (event) {
            is ArticulationEvent.ExerciseClicked -> {
                _state.update {
                    it.copy(
                        selectedExercise = event.exercise,
                        isExerciseDialogOpen = true,
                        timerSeconds = event.exercise.durationSeconds,
                        isTimerRunning = false
                    )
                }
            }

            ArticulationEvent.ExerciseDialogDismissed -> {
                stopTimer()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false,
                        timerSeconds = 0,
                        isTimerRunning = false
                    )
                }
            }

            ArticulationEvent.StartTimer -> {
                startTimer()
            }

            ArticulationEvent.PauseTimer -> {
                stopTimer()
            }

            is ArticulationEvent.TimerTick -> {
                _state.update { it.copy(timerSeconds = event.secondsRemaining) }

                if (event.secondsRemaining <= 0) {
                    stopTimer()
                    // Auto-mark as completed
                    markCurrentAsCompleted()
                }
            }

            ArticulationEvent.MarkAsCompleted -> {
                markCurrentAsCompleted()
            }

            ArticulationEvent.SkipExercise -> {
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false
                    )
                }
            }

            ArticulationEvent.FinishWarmup -> {
                finishWarmup()
            }
        }
    }

    private fun loadTodayProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val completion = warmupCompletionDao.getCompletion(today, "articulation")

            if (completion != null) {
                // Парсимо які вправи виконано (можна зберігати як JSON або bitmap)
                // Поки що просто вважаємо exercisesCompleted = кількість
                val completed = (1..completion.exercisesCompleted).toSet()
                _state.update { it.copy(completedToday = completed) }
            }
        }
    }

    private fun startTimer() {
        stopTimer() // Зупиняємо попередній таймер

        _state.update { it.copy(isTimerRunning = true) }

        timerJob = viewModelScope.launch {
            while (_state.value.isTimerRunning && _state.value.timerSeconds > 0) {
                delay(1000)
                val newSeconds = _state.value.timerSeconds - 1
                onEvent(ArticulationEvent.TimerTick(newSeconds))
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isTimerRunning = false) }
    }

    private fun markCurrentAsCompleted() {
        val exerciseId = _state.value.selectedExercise?.id ?: return

        viewModelScope.launch {
            // Чекаємо 1 секунду, щоб анімація таймера встигла закінчитися
            delay(1000)

            // Показуємо completion overlay
            _state.update {
                it.copy(
                    completedToday = it.completedToday + exerciseId,
                    showCompletionOverlay = true
                )
            }

            saveProgress()

            // Через 4 секунди закриваємо діалог
            delay(4000)
            _state.update {
                it.copy(
                    selectedExercise = null,
                    isExerciseDialogOpen = false,
                    showCompletionOverlay = false
                )
            }
        }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val totalExercises = _state.value.exercises.size
            val completedCount = _state.value.completedToday.size

            val entity = WarmupCompletionEntity(
                id = "${today}_articulation",
                date = today,
                category = "articulation",
                completedAt = System.currentTimeMillis(),
                exercisesCompleted = completedCount,
                totalExercises = totalExercises
            )

            warmupCompletionDao.insertCompletion(entity)

            // Оновлюємо todayMinutes в DataStore
            val estimatedMinutes = 3 // Артикуляція ~3 хв
            if (completedCount == totalExercises) {
                userPreferencesDataStore.addMinutes(estimatedMinutes)
            }
        }
    }

    private fun finishWarmup() {
        saveProgress()
        // Navigation handled in Screen
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
