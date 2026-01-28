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
class QuickWarmupViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(QuickWarmupState())
    val state: StateFlow<QuickWarmupState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var breathingJob: Job? = null

    fun onEvent(event: QuickWarmupEvent) {
        when (event) {
            QuickWarmupEvent.StartQuickWarmup -> {
                startQuickWarmup()
            }

            QuickWarmupEvent.CurrentExerciseCompleted -> {
                markCurrentExerciseCompleted()
            }

            is QuickWarmupEvent.UpdateElapsedTime -> {
                _state.update {
                    it.copy(totalElapsedSeconds = event.seconds)
                }
            }

            QuickWarmupEvent.FinishQuickWarmup -> {
                finishQuickWarmup()
            }

            QuickWarmupEvent.DismissCompletionDialog -> {
                _state.update {
                    it.copy(isCompleted = false)
                }
            }

            QuickWarmupEvent.StartTimer -> {
                startTimer()
            }

            QuickWarmupEvent.PauseTimer -> {
                stopTimer()
            }

            is QuickWarmupEvent.TimerTick -> {
                _state.update { it.copy(timerSeconds = event.secondsRemaining) }

                if (event.secondsRemaining <= 0) {
                    stopTimer()
                    markCurrentExerciseCompleted()
                }
            }

            QuickWarmupEvent.SkipExercise -> {
                stopTimer()
                stopBreathing()
                val nextIndex = _state.value.currentExerciseIndex + 1

                if (nextIndex >= _state.value.exercises.size) {
                    completeQuickWarmup()
                } else {
                    _state.update {
                        it.copy(currentExerciseIndex = nextIndex)
                    }
                    initializeCurrentExerciseTimer()
                }
            }

            QuickWarmupEvent.StartBreathing -> {
                startBreathing()
            }

            QuickWarmupEvent.PauseBreathing -> {
                stopBreathing()
            }

            is QuickWarmupEvent.BreathingTick -> {
                _state.update {
                    it.copy(
                        breathingElapsedSeconds = event.elapsedSeconds,
                        breathingCurrentPhase = event.currentPhase,
                        breathingPhaseProgress = event.phaseProgress
                    )
                }

                // Перевірка чи закінчилась вправа
                val currentExercise = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex)
                if (currentExercise != null && event.elapsedSeconds >= currentExercise.durationSeconds) {
                    stopBreathing()
                    markCurrentExerciseCompleted()
                }
            }
        }
    }

    private fun startQuickWarmup() {
        _state.update {
            it.copy(
                currentExerciseIndex = 0,
                completedExercises = emptySet(),
                totalElapsedSeconds = 0,
                isExerciseDialogOpen = true
            )
        }
        initializeCurrentExerciseTimer()
    }

    private fun initializeCurrentExerciseTimer() {
        val currentExercise = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex)

        // Скидаємо стейт для всіх типів вправ
        _state.update {
            it.copy(
                timerSeconds = currentExercise?.durationSeconds ?: 0,
                isTimerRunning = false,
                breathingElapsedSeconds = 0,
                breathingCurrentPhase = BreathingPhase.INHALE,
                breathingPhaseProgress = 0f,
                breathingIsRunning = false
            )
        }
    }

    private fun startTimer() {
        stopTimer()

        _state.update { it.copy(isTimerRunning = true) }

        timerJob = viewModelScope.launch {
            while (_state.value.isTimerRunning && _state.value.timerSeconds > 0) {
                delay(1000)
                val newSeconds = _state.value.timerSeconds - 1
                onEvent(QuickWarmupEvent.TimerTick(newSeconds))
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isTimerRunning = false) }
    }

    private fun startBreathing() {
        stopBreathing()

        val currentExercise = _state.value.exercises.getOrNull(_state.value.currentExerciseIndex)
        val breathingExercise = currentExercise?.breathingExercise ?: return

        _state.update {
            it.copy(
                breathingIsRunning = true,
                breathingElapsedSeconds = 0,
                breathingCurrentPhase = BreathingPhase.INHALE,
                breathingPhaseProgress = 0f
            )
        }

        breathingJob = viewModelScope.launch {
            val pattern = breathingExercise.pattern
            var totalMillis = 0
            var phaseElapsedMillis = 0
            var currentPhase = BreathingPhase.INHALE

            fun getCurrentPhaseDuration(): Int {
                return when (currentPhase) {
                    BreathingPhase.INHALE -> pattern.inhaleSeconds
                    BreathingPhase.INHALE_HOLD -> pattern.inhaleHoldSeconds
                    BreathingPhase.EXHALE -> pattern.exhaleSeconds
                    BreathingPhase.EXHALE_HOLD -> pattern.exhaleHoldSeconds
                }
            }

            fun getNextPhase(): BreathingPhase {
                return when (currentPhase) {
                    BreathingPhase.INHALE -> {
                        if (pattern.inhaleHoldSeconds > 0) BreathingPhase.INHALE_HOLD
                        else BreathingPhase.EXHALE
                    }
                    BreathingPhase.INHALE_HOLD -> BreathingPhase.EXHALE
                    BreathingPhase.EXHALE -> {
                        if (pattern.exhaleHoldSeconds > 0) BreathingPhase.EXHALE_HOLD
                        else BreathingPhase.INHALE
                    }
                    BreathingPhase.EXHALE_HOLD -> BreathingPhase.INHALE
                }
            }

            while (_state.value.breathingIsRunning) {
                delay(100) // Оновлення кожні 100мс для плавної анімації

                totalMillis += 100
                phaseElapsedMillis += 100

                val elapsedSeconds = totalMillis / 1000
                val phaseDuration = getCurrentPhaseDuration()
                val phaseElapsedSeconds = phaseElapsedMillis / 1000f
                val progress = (phaseElapsedSeconds / phaseDuration).coerceIn(0f, 1f)

                onEvent(QuickWarmupEvent.BreathingTick(
                    elapsedSeconds = elapsedSeconds,
                    currentPhase = currentPhase,
                    phaseProgress = progress
                ))

                // Перехід до наступної фази
                if (phaseElapsedMillis >= phaseDuration * 1000) {
                    currentPhase = getNextPhase()
                    phaseElapsedMillis = 0
                }
            }
        }
    }

    private fun stopBreathing() {
        breathingJob?.cancel()
        _state.update {
            it.copy(
                breathingIsRunning = false
            )
        }
    }

    private fun markCurrentExerciseCompleted() {
        val currentIndex = _state.value.currentExerciseIndex
        val currentExerciseId = _state.value.exercises.getOrNull(currentIndex)?.id ?: return

        viewModelScope.launch {
            // Чекаємо 1 секунду, щоб анімація таймера встигла закінчитися
            delay(1000)

            _state.update {
                it.copy(
                    completedExercises = it.completedExercises + currentExerciseId
                )
            }

            val nextIndex = currentIndex + 1

            if (nextIndex >= _state.value.exercises.size) {
                // Всі вправи виконано - показуємо completion
                _state.update { it.copy(showCompletionOverlay = true) }
                delay(4000)
                completeQuickWarmup()
            } else {
                // Переходимо до наступної вправи без overlay
                _state.update {
                    it.copy(currentExerciseIndex = nextIndex)
                }
                initializeCurrentExerciseTimer()
            }
        }
    }

    private fun completeQuickWarmup() {
        _state.update {
            it.copy(
                isExerciseDialogOpen = false,
                isCompleted = true
            )
        }

        saveProgress()
    }

    private fun finishQuickWarmup() {
        saveProgress()
        // Navigation handled in Screen
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val totalExercises = _state.value.exercises.size

            val entity = WarmupCompletionEntity(
                id = "${today}_quick",
                date = today,
                category = "quick",
                completedAt = System.currentTimeMillis(),
                exercisesCompleted = totalExercises,
                totalExercises = totalExercises
            )

            warmupCompletionDao.insertCompletion(entity)

            // Оновлюємо DataStore
            val estimatedMinutes = (_state.value.totalElapsedSeconds / 60).coerceAtLeast(1)
            userPreferencesDataStore.addMinutes(estimatedMinutes)
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
