package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
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
    }

    private fun markCurrentExerciseCompleted() {
        val currentIndex = _state.value.currentExerciseIndex
        val currentExerciseId = _state.value.exercises.getOrNull(currentIndex)?.id ?: return

        _state.update {
            it.copy(
                completedExercises = it.completedExercises + currentExerciseId
            )
        }

        // Переходимо до наступної вправи
        val nextIndex = currentIndex + 1

        if (nextIndex >= _state.value.exercises.size) {
            // Всі вправи виконано
            completeQuickWarmup()
        } else {
            _state.update {
                it.copy(currentExerciseIndex = nextIndex)
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
