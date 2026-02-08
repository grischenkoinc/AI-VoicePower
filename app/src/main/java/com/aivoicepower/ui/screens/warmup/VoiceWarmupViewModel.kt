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
class VoiceWarmupViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val skillUpdateService: SkillUpdateService
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceWarmupState())
    val state: StateFlow<VoiceWarmupState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadTodayProgress()
    }

    fun onEvent(event: VoiceWarmupEvent) {
        when (event) {
            is VoiceWarmupEvent.ExerciseClicked -> {
                _state.update {
                    it.copy(
                        selectedExercise = event.exercise,
                        isExerciseDialogOpen = true,
                        timerSeconds = event.exercise.durationSeconds,
                        isTimerRunning = false
                    )
                }
            }

            VoiceWarmupEvent.ExerciseDialogDismissed -> {
                stopTimer()
                stopAudio()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false,
                        timerSeconds = 0,
                        isTimerRunning = false
                    )
                }
            }

            VoiceWarmupEvent.StartTimer -> {
                startTimer()
            }

            VoiceWarmupEvent.PauseTimer -> {
                stopTimer()
            }

            is VoiceWarmupEvent.TimerTick -> {
                _state.update { it.copy(timerSeconds = event.secondsRemaining) }

                if (event.secondsRemaining <= 0) {
                    stopTimer()
                    markCurrentAsCompleted()
                }
            }

            VoiceWarmupEvent.PlayAudioExample -> {
                playAudioExample()
            }

            VoiceWarmupEvent.StopAudioExample -> {
                stopAudio()
            }

            VoiceWarmupEvent.MarkAsCompleted -> {
                markCurrentAsCompleted()
            }

            VoiceWarmupEvent.SkipExercise -> {
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false
                    )
                }
            }
        }
    }

    private fun loadTodayProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val completion = warmupCompletionDao.getCompletion(today, "voice")

            if (completion != null) {
                val completed = (1..completion.exercisesCompleted).toSet()
                _state.update { it.copy(completedToday = completed) }
            }
        }
    }

    private fun startTimer() {
        stopTimer()

        _state.update { it.copy(isTimerRunning = true) }

        timerJob = viewModelScope.launch {
            while (_state.value.isTimerRunning && _state.value.timerSeconds > 0) {
                delay(1000)
                val newSeconds = _state.value.timerSeconds - 1
                onEvent(VoiceWarmupEvent.TimerTick(newSeconds))
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isTimerRunning = false) }
    }

    private fun playAudioExample() {
        // TODO: Implement audio playback in Phase 8 (Content)
        // For now, just toggle the playing state
        _state.update { it.copy(isAudioPlaying = true) }

        // Auto-stop after 3 seconds (placeholder)
        viewModelScope.launch {
            delay(3000)
            stopAudio()
        }
    }

    private fun stopAudio() {
        _state.update { it.copy(isAudioPlaying = false) }
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
            skillUpdateService.updateFromWarmup("voice")

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
                id = "${today}_voice",
                date = today,
                category = "voice",
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
