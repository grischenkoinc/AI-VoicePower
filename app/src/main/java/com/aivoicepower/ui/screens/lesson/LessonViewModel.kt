package com.aivoicepower.ui.screens.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.Exercise
import com.aivoicepower.domain.model.Lesson
import com.aivoicepower.domain.repository.LessonRepository
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lessonId: String = savedStateHandle["lessonId"] ?: ""

    private val _uiState = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private var currentExerciseIndex = 0
    private var currentStepIndex = 0

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            try {
                val lesson = lessonRepository.getLessonById(lessonId)
                if (lesson != null) {
                    _uiState.value = LessonUiState.Success(
                        lesson = lesson,
                        currentExercise = lesson.exercises.firstOrNull(),
                        currentStepIndex = 0
                    )
                } else {
                    _uiState.value = LessonUiState.Error("Урок не знайдено")
                }
            } catch (e: Exception) {
                _uiState.value = LessonUiState.Error(e.message ?: "Помилка завантаження уроку")
            }
        }
    }

    fun startRecording() {
        _recordingState.value = RecordingState.Recording
    }

    fun stopRecording(audioFile: File?) {
        _recordingState.value = RecordingState.Processing

        if (audioFile == null) {
            _recordingState.value = RecordingState.Error("Помилка запису аудіо")
            return
        }

        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is LessonUiState.Success) {
                val exercise = currentState.currentExercise
                if (exercise != null) {
                    val context = "${exercise.title}: ${exercise.instruction}"
                    val result = voiceAnalysisRepository.analyzeVoice(audioFile, context)

                    result.onSuccess { analysis ->
                        _recordingState.value = RecordingState.Completed(analysis)
                    }.onFailure { error ->
                        _recordingState.value = RecordingState.Error(
                            error.message ?: "Помилка аналізу"
                        )
                    }
                }
            }
        }
    }

    fun resetRecording() {
        _recordingState.value = RecordingState.Idle
    }

    fun moveToNextExercise() {
        val currentState = _uiState.value
        if (currentState is LessonUiState.Success) {
            currentExerciseIndex++
            currentStepIndex = 0
            if (currentExerciseIndex < currentState.lesson.exercises.size) {
                _uiState.value = currentState.copy(
                    currentExercise = currentState.lesson.exercises[currentExerciseIndex],
                    currentStepIndex = 0
                )
                resetRecording()
            }
        }
    }

    fun nextStep() {
        val currentState = _uiState.value
        if (currentState is LessonUiState.Success) {
            val exercise = currentState.currentExercise
            if (exercise != null && currentStepIndex < exercise.steps.size - 1) {
                currentStepIndex++
                _uiState.value = currentState.copy(currentStepIndex = currentStepIndex)
            }
        }
    }

    fun previousStep() {
        val currentState = _uiState.value
        if (currentState is LessonUiState.Success && currentStepIndex > 0) {
            currentStepIndex--
            _uiState.value = currentState.copy(currentStepIndex = currentStepIndex)
        }
    }

    fun skipToRecording() {
        val currentState = _uiState.value
        if (currentState is LessonUiState.Success) {
            val exercise = currentState.currentExercise
            if (exercise != null) {
                currentStepIndex = exercise.steps.size // Move past all steps
                _uiState.value = currentState.copy(currentStepIndex = currentStepIndex)
            }
        }
    }
}

sealed interface LessonUiState {
    object Loading : LessonUiState
    data class Success(
        val lesson: Lesson,
        val currentExercise: Exercise?,
        val currentStepIndex: Int = 0
    ) : LessonUiState
    data class Error(val message: String) : LessonUiState
}

sealed interface RecordingState {
    object Idle : RecordingState
    object Recording : RecordingState
    object Processing : RecordingState
    data class Completed(val analysis: com.aivoicepower.domain.model.VoiceAnalysis) : RecordingState
    data class Error(val message: String) : RecordingState
}
