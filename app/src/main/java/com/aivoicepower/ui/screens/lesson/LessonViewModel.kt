package com.aivoicepower.ui.screens.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.domain.model.exercise.Exercise
import com.aivoicepower.domain.model.exercise.ExerciseContent
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.domain.repository.LessonRepository
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
                    // Знаходимо наступний урок
                    val allLessons = lessonRepository.getAllLessons().first()
                    val courseLessons = allLessons
                        .filter { it.courseId == lesson.courseId }
                        .sortedBy { it.order }
                    val currentIndex = courseLessons.indexOfFirst { it.id == lesson.id }
                    val nextLesson = if (currentIndex >= 0 && currentIndex < courseLessons.size - 1) {
                        courseLessons[currentIndex + 1]
                    } else null

                    _uiState.value = LessonUiState.Success(
                        lesson = lesson,
                        currentExercise = lesson.exercises.firstOrNull(),
                        currentExerciseIndex = 0,
                        currentStepIndex = 0,
                        nextLessonId = nextLesson?.id,
                        nextLessonTitle = nextLesson?.title,
                        isLastLessonInCourse = nextLesson == null
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
                    val context = when (exercise.type) {
                        ExerciseType.TONGUE_TWISTER -> {
                            val content = exercise.content as? ExerciseContent.TongueTwister
                            buildString {
                                append("СКОРОМОВКА\n")
                                append("Оригінальний текст: ${content?.text ?: ""}\n")
                                append("Цільові звуки: ${content?.targetSounds?.joinToString(", ") ?: ""}\n")
                                append("Інструкція: ${exercise.instruction}")
                            }
                        }
                        ExerciseType.READING -> {
                            val content = exercise.content as? ExerciseContent.ReadingText
                            buildString {
                                append("ЧИТАННЯ ТЕКСТУ\n")
                                append("Текст для читання: ${content?.text ?: ""}\n")
                                append("Інструкція: ${exercise.instruction}")
                            }
                        }
                        ExerciseType.MINIMAL_PAIRS -> {
                            val content = exercise.content as? ExerciseContent.MinimalPairs
                            buildString {
                                append("СХОЖІ СЛОВА\n")
                                append("Пари слів: ${content?.pairs?.joinToString(", ") { "${it.first}/${it.second}" } ?: ""}\n")
                                append("Цільові звуки: ${content?.targetSounds?.joinToString(", ") ?: ""}\n")
                                append("Інструкція: ${exercise.instruction}")
                            }
                        }
                        ExerciseType.CONTRAST_SOUNDS -> {
                            val content = exercise.content as? ExerciseContent.ContrastSounds
                            buildString {
                                append("ЧЕРГУВАННЯ ЗВУКІВ\n")
                                append("Послідовність: ${content?.sequence ?: ""}\n")
                                append("Цільові звуки: ${content?.targetSounds?.joinToString(", ") ?: ""}\n")
                                append("Інструкція: ${exercise.instruction}")
                            }
                        }
                        ExerciseType.TONGUE_TWISTER_BATTLE -> {
                            val content = exercise.content as? ExerciseContent.TongueTwisterBattle
                            buildString {
                                append("БАТЛ СКОРОМОВОК (3 поспіль)\n")
                                content?.twisters?.forEachIndexed { index, twister ->
                                    append("${index + 1}. ${twister.text}\n")
                                }
                                append("Інструкція: ${exercise.instruction}")
                            }
                        }
                        ExerciseType.SLOW_MOTION -> {
                            val content = exercise.content as? ExerciseContent.SlowMotion
                            buildString {
                                append("ПОВІЛЬНА СКОРОМОВКА\n")
                                append("Текст: ${content?.text ?: ""}\n")
                                append("Цільові звуки: ${content?.targetSounds?.joinToString(", ") ?: ""}\n")
                                append("Мінімальний час: ${content?.minDurationSeconds ?: 30} сек\n")
                                append("Інструкція: ${exercise.instruction}")
                            }
                        }
                        else -> "${exercise.title}: ${exercise.instruction}"
                    }
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
                    currentExerciseIndex = currentExerciseIndex,
                    currentStepIndex = 0
                )
                resetRecording()
            } else {
                // Урок завершено
                _uiState.value = currentState.copy(isCompleted = true)
            }
        }
    }

    fun moveToPreviousExercise() {
        val currentState = _uiState.value
        if (currentState is LessonUiState.Success && currentExerciseIndex > 0) {
            currentExerciseIndex--
            currentStepIndex = 0
            _uiState.value = currentState.copy(
                currentExercise = currentState.lesson.exercises[currentExerciseIndex],
                currentExerciseIndex = currentExerciseIndex,
                currentStepIndex = 0
            )
            resetRecording()
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

    fun loadNextLesson(nextLessonId: String) {
        currentExerciseIndex = 0
        currentStepIndex = 0
        _recordingState.value = RecordingState.Idle
        _uiState.value = LessonUiState.Loading

        viewModelScope.launch {
            try {
                val lesson = lessonRepository.getLessonById(nextLessonId)
                if (lesson != null) {
                    // Знаходимо наступний урок
                    val allLessons = lessonRepository.getAllLessons().first()
                    val courseLessons = allLessons
                        .filter { it.courseId == lesson.courseId }
                        .sortedBy { it.order }
                    val currentIndex = courseLessons.indexOfFirst { it.id == lesson.id }
                    val nextLesson = if (currentIndex >= 0 && currentIndex < courseLessons.size - 1) {
                        courseLessons[currentIndex + 1]
                    } else null

                    _uiState.value = LessonUiState.Success(
                        lesson = lesson,
                        currentExercise = lesson.exercises.firstOrNull(),
                        currentExerciseIndex = 0,
                        currentStepIndex = 0,
                        nextLessonId = nextLesson?.id,
                        nextLessonTitle = nextLesson?.title,
                        isLastLessonInCourse = nextLesson == null
                    )
                } else {
                    _uiState.value = LessonUiState.Error("Урок не знайдено")
                }
            } catch (e: Exception) {
                _uiState.value = LessonUiState.Error(e.message ?: "Помилка завантаження уроку")
            }
        }
    }
}

sealed interface LessonUiState {
    object Loading : LessonUiState
    data class Success(
        val lesson: Lesson,
        val currentExercise: Exercise?,
        val currentExerciseIndex: Int = 0,
        val currentStepIndex: Int = 0,
        val isCompleted: Boolean = false,
        val nextLessonId: String? = null,
        val nextLessonTitle: String? = null,
        val isLastLessonInCourse: Boolean = false
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
