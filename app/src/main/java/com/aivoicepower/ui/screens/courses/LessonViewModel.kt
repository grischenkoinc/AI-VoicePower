package com.aivoicepower.ui.screens.courses

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.CourseProgressEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.audio.AudioPlayerUtil
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository,
    private val courseProgressDao: CourseProgressDao,
    private val recordingDao: RecordingDao,
    private val voiceAnalysisRepository: VoiceAnalysisRepository
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""
    private val lessonId: String = savedStateHandle["lessonId"] ?: ""

    private val _state = MutableStateFlow(LessonState())
    val state: StateFlow<LessonState> = _state.asStateFlow()

    private val audioRecorder = AudioRecorderUtil(context)
    private val audioPlayer = AudioPlayerUtil(context)

    init {
        loadLesson()
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        audioPlayer.release()
    }

    fun onEvent(event: LessonEvent) {
        when (event) {
            LessonEvent.StartExercisesClicked -> startExercises()
            LessonEvent.NavigateBackClicked -> navigateBackToTheory()
            LessonEvent.StartRecordingClicked -> startRecording()
            LessonEvent.StopRecordingClicked -> stopRecording()
            LessonEvent.PlayRecordingClicked -> playRecording()
            LessonEvent.StopPlaybackClicked -> stopPlayback()
            LessonEvent.ReRecordClicked -> reRecord()
            LessonEvent.CompleteExerciseClicked -> completeCurrentExercise()
            LessonEvent.NextExerciseClicked -> moveToNextExercise()
            LessonEvent.PreviousExerciseClicked -> moveToPreviousExercise()
            LessonEvent.SkipExerciseClicked -> skipCurrentExercise()
            LessonEvent.FinishLessonClicked -> {
                // Navigation handled in Screen
            }
            LessonEvent.NextLessonClicked -> {
                // Navigation handled in Screen
            }
        }
    }

    fun clearToastMessage() {
        _state.update { it.copy(toastMessage = null) }
    }

    private fun navigateBackToTheory() {
        val lesson = _state.value.lesson
        if (lesson?.theory != null) {
            _state.update { it.copy(currentPhase = LessonPhase.Theory) }
        }
    }

    private fun loadLesson() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            Log.d("LessonVM", "Looking for courseId=$courseId, lessonId=$lessonId")

            try {
                courseRepository.getLessonById(courseId, lessonId)
                    .collect { lesson ->
                        Log.d("LessonVM", "Lesson found: ${lesson != null}, lesson.id=${lesson?.id}")
                        if (lesson == null) {
                            Log.e("LessonVM", "Lesson NOT FOUND! courseId=$courseId, lessonId=$lessonId")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Урок не знайдено"
                                )
                            }
                            return@collect
                        }

                        val exerciseStates = lesson.exercises.map { exercise ->
                            ExerciseState(
                                exercise = exercise,
                                status = ExerciseStatus.NotStarted
                            )
                        }

                        // Find next lesson
                        val nextLesson = findNextLesson(lesson.dayNumber)

                        _state.update {
                            it.copy(
                                lesson = lesson,
                                nextLesson = nextLesson,
                                currentPhase = if (lesson.theory != null) {
                                    LessonPhase.Theory
                                } else {
                                    LessonPhase.Exercise
                                },
                                exerciseStates = exerciseStates,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити урок: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun findNextLesson(currentDayNumber: Int): com.aivoicepower.domain.model.course.Lesson? {
        return try {
            // Get all lessons from course and find the next one
            val course = courseRepository.getCourseById(courseId)
            course?.lessons?.firstOrNull { it.dayNumber == currentDayNumber + 1 }
        } catch (e: Exception) {
            null
        }
    }

    private fun startExercises() {
        _state.update { it.copy(currentPhase = LessonPhase.Exercise) }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()

                audioRecorder.startRecording(outputFile.absolutePath)

                updateCurrentExerciseState { it.copy(status = ExerciseStatus.Recording) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка запису: ${e.message}") }
            }
        }
    }

    private fun stopRecording() {
        viewModelScope.launch {
            try {
                val result = audioRecorder.stopRecording()

                if (result != null) {
                    updateCurrentExerciseState {
                        it.copy(
                            status = ExerciseStatus.Recorded,
                            recordingPath = result.filePath,
                            recordingDurationMs = result.durationMs
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка зупинки запису: ${e.message}") }
            }
        }
    }

    private fun playRecording() {
        val recordingPath = getCurrentExerciseState()?.recordingPath ?: return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isPlaying = true) }
                audioPlayer.play(recordingPath) {
                    _state.update { it.copy(isPlaying = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка відтворення: ${e.message}",
                        isPlaying = false
                    )
                }
            }
        }
    }

    private fun stopPlayback() {
        audioPlayer.stop()
        _state.update { it.copy(isPlaying = false) }
    }

    private fun reRecord() {
        updateCurrentExerciseState {
            it.copy(
                status = ExerciseStatus.NotStarted,
                recordingPath = null,
                recordingDurationMs = 0
            )
        }
    }

    private fun completeCurrentExercise() {
        val currentExerciseState = getCurrentExerciseState() ?: return
        val lesson = _state.value.lesson ?: return

        viewModelScope.launch {
            try {
                var score = 0

                // Validate recording before analysis
                if (currentExerciseState.recordingPath != null) {
                    // Check minimum recording duration (2 seconds)
                    if (currentExerciseState.recordingDurationMs < 2000) {
                        _state.update {
                            it.copy(toastMessage = "Запис занадто короткий. Говоріть принаймні 2 секунди.")
                        }
                        return@launch
                    }

                    // Check if file exists and has content
                    val recordingFile = java.io.File(currentExerciseState.recordingPath)
                    if (!recordingFile.exists() || recordingFile.length() < 1000) {
                        _state.update {
                            it.copy(toastMessage = "Запис порожній або пошкоджений. Спробуйте ще раз.")
                        }
                        return@launch
                    }
                }

                // Analyze recording with Gemini if path exists
                if (currentExerciseState.recordingPath != null) {
                    // Extract expected text from ExerciseContent
                    val expectedText = when (val content = currentExerciseState.exercise.content) {
                        is com.aivoicepower.domain.model.exercise.ExerciseContent.TongueTwister -> content.text
                        is com.aivoicepower.domain.model.exercise.ExerciseContent.ReadingText -> content.text
                        else -> null
                    }
                    val analysisResult = voiceAnalysisRepository.analyzeRecording(
                        audioFilePath = currentExerciseState.recordingPath,
                        expectedText = expectedText,
                        exerciseType = currentExerciseState.exercise.type.name,
                        context = "Урок: ${lesson.title}, вправа: ${currentExerciseState.exercise.title}"
                    )
                    score = analysisResult.getOrNull()?.overallScore ?: 0

                    // Save recording to database
                    val recordingEntity = RecordingEntity(
                        id = UUID.randomUUID().toString(),
                        filePath = currentExerciseState.recordingPath,
                        durationMs = currentExerciseState.recordingDurationMs,
                        type = "exercise",
                        contextId = "${courseId}_${lessonId}",
                        exerciseId = currentExerciseState.exercise.id,
                        isAnalyzed = true
                    )
                    recordingDao.insert(recordingEntity)
                }

                // Mark exercise as completed
                updateCurrentExerciseState {
                    it.copy(status = ExerciseStatus.Completed)
                }

                // Check if all exercises completed
                val updatedStates = _state.value.exerciseStates.toMutableList()
                val currentIndex = _state.value.currentExerciseIndex
                updatedStates[currentIndex] = updatedStates[currentIndex].copy(status = ExerciseStatus.Completed)

                val allCompleted = updatedStates.all {
                    it.status == ExerciseStatus.Completed
                }

                if (allCompleted) {
                    // Mark lesson as completed
                    val progressEntity = CourseProgressEntity(
                        id = "${courseId}_${lessonId}",
                        courseId = courseId,
                        lessonId = lessonId,
                        isCompleted = true,
                        completedAt = System.currentTimeMillis(),
                        bestScore = score, // Real score from Gemini analysis
                        attemptsCount = 1,
                        lastAttemptAt = System.currentTimeMillis()
                    )
                    courseProgressDao.insertOrUpdate(progressEntity)

                    _state.update {
                        it.copy(
                            currentPhase = LessonPhase.Completed,
                            exerciseStates = updatedStates
                        )
                    }
                } else {
                    // Move to next exercise
                    _state.update { it.copy(exerciseStates = updatedStates) }
                    moveToNextExercise()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка збереження: ${e.message}") }
            }
        }
    }

    private fun moveToNextExercise() {
        val currentIndex = _state.value.currentExerciseIndex
        val maxIndex = _state.value.exerciseStates.size - 1

        if (currentIndex < maxIndex) {
            _state.update { it.copy(currentExerciseIndex = currentIndex + 1) }
        }
    }

    private fun moveToPreviousExercise() {
        val currentIndex = _state.value.currentExerciseIndex

        if (currentIndex > 0) {
            _state.update { it.copy(currentExerciseIndex = currentIndex - 1) }
        }
    }

    private fun skipCurrentExercise() {
        updateCurrentExerciseState {
            it.copy(status = ExerciseStatus.Completed)
        }

        val currentIndex = _state.value.currentExerciseIndex
        val maxIndex = _state.value.exerciseStates.size - 1

        if (currentIndex < maxIndex) {
            moveToNextExercise()
        } else {
            // Last exercise skipped - complete lesson
            viewModelScope.launch {
                val progressEntity = CourseProgressEntity(
                    id = "${courseId}_${lessonId}",
                    courseId = courseId,
                    lessonId = lessonId,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    bestScore = 0, // Skipped exercises get 0 score
                    attemptsCount = 1,
                    lastAttemptAt = System.currentTimeMillis()
                )
                courseProgressDao.insertOrUpdate(progressEntity)
                _state.update { it.copy(currentPhase = LessonPhase.Completed) }
            }
        }
    }

    private fun getCurrentExerciseState() = _state.value.exerciseStates.getOrNull(
        _state.value.currentExerciseIndex
    )

    private fun updateCurrentExerciseState(update: (ExerciseState) -> ExerciseState) {
        _state.update { state ->
            val updatedStates = state.exerciseStates.toMutableList()
            val currentState = updatedStates.getOrNull(state.currentExerciseIndex)

            if (currentState != null) {
                updatedStates[state.currentExerciseIndex] = update(currentState)
            }

            state.copy(exerciseStates = updatedStates)
        }
    }
}
