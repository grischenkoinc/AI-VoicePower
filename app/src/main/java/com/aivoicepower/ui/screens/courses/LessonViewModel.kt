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
import com.aivoicepower.domain.model.exercise.ExerciseContent
import com.aivoicepower.domain.model.exercise.ExerciseType
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementCategory
import com.aivoicepower.domain.model.user.AchievementDefinitions
import com.aivoicepower.domain.repository.AchievementRepository
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.domain.service.SkillUpdateService
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
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val skillUpdateService: SkillUpdateService,
    private val achievementRepository: AchievementRepository
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
            LessonEvent.ContinueAfterAnalysisClicked -> continueAfterAnalysis()
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

                        // Find next lesson and detect course completion
                        val nextLesson = findNextLesson(lesson.dayNumber)
                        val course = courseRepository.getCourseById(courseId)
                        val isLastLesson = course != null && lesson.dayNumber == course.lessons.size
                        val courseName = course?.title ?: ""

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
                                error = null,
                                isLastLessonInCourse = isLastLesson,
                                courseName = courseName
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

        // Validate recording before proceeding
        if (currentExerciseState.recordingPath != null) {
            // Check minimum recording duration (2 seconds)
            if (currentExerciseState.recordingDurationMs < 2000) {
                _state.update {
                    it.copy(toastMessage = "Запис занадто короткий. Говоріть принаймні 2 секунди.")
                }
                return
            }

            // Check if file exists and has content
            val recordingFile = java.io.File(currentExerciseState.recordingPath)
            if (!recordingFile.exists() || recordingFile.length() < 1000) {
                _state.update {
                    it.copy(toastMessage = "Запис порожній або пошкоджений. Спробуйте ще раз.")
                }
                return
            }
        }

        val exerciseType = currentExerciseState.exercise.type
        val hasRecording = exerciseType != ExerciseType.ARTICULATION &&
                exerciseType != ExerciseType.BREATHING &&
                currentExerciseState.recordingPath != null

        if (hasRecording) {
            // Show analysis UI - blocking flow
            updateCurrentExerciseState { it.copy(status = ExerciseStatus.Analyzing) }

            viewModelScope.launch {
                try {
                    // Extract expected text from ExerciseContent
                    val expectedText = extractExpectedText(currentExerciseState.exercise.content)

                    val analysisResult = voiceAnalysisRepository.analyzeRecording(
                        audioFilePath = currentExerciseState.recordingPath!!,
                        expectedText = expectedText,
                        exerciseType = currentExerciseState.exercise.type.name,
                        context = "Урок: ${lesson.title}, вправа: ${currentExerciseState.exercise.title}"
                    )

                    val result = analysisResult.getOrNull()

                    // Update skills from analysis
                    if (result != null) {
                        val mappedType = mapExerciseTypeForSkills(exerciseType)
                        skillUpdateService.updateFromAnalysis(result, mappedType)
                    }

                    // Show results to user
                    updateCurrentExerciseState {
                        it.copy(
                            status = ExerciseStatus.ShowingResults,
                            analysisResult = result
                        )
                    }

                    // Save recording to database — only meaningful recordings (score > 0)
                    if (result != null && result.overallScore > 0) {
                        val recordingEntity = RecordingEntity(
                            id = UUID.randomUUID().toString(),
                            filePath = currentExerciseState.recordingPath,
                            durationMs = currentExerciseState.recordingDurationMs,
                            type = mapExerciseTypeForSkills(exerciseType),
                            contextId = "${courseId}_${lessonId}",
                            exerciseId = currentExerciseState.exercise.id,
                            isAnalyzed = true
                        )
                        recordingDao.insert(recordingEntity)
                    }
                } catch (e: Exception) {
                    Log.e("LessonVM", "Analysis error: ${e.message}", e)
                    // On error, skip analysis and proceed
                    updateCurrentExerciseState { it.copy(status = ExerciseStatus.Completed) }
                    proceedAfterExercise()
                }
            }
        } else {
            // No recording exercise (ARTICULATION/BREATHING) - complete immediately
            updateCurrentExerciseState { it.copy(status = ExerciseStatus.Completed) }
            proceedAfterExercise()
        }
    }

    private fun continueAfterAnalysis() {
        updateCurrentExerciseState { it.copy(status = ExerciseStatus.Completed) }
        proceedAfterExercise()
    }

    private fun proceedAfterExercise() {
        val updatedStates = _state.value.exerciseStates.toMutableList()
        val currentIndex = _state.value.currentExerciseIndex
        updatedStates[currentIndex] = updatedStates[currentIndex].copy(status = ExerciseStatus.Completed)

        val allCompleted = updatedStates.all { it.status == ExerciseStatus.Completed }

        if (allCompleted) {
            _state.update {
                it.copy(
                    currentPhase = LessonPhase.Completed,
                    exerciseStates = updatedStates
                )
            }
            // Save progress and handle course completion
            viewModelScope.launch {
                saveLessonProgress()
                if (_state.value.isLastLessonInCourse) {
                    handleCourseCompletion()
                }
            }
        } else {
            _state.update { it.copy(exerciseStates = updatedStates) }
            moveToNextExercise()
        }
    }

    private suspend fun saveLessonProgress() {
        try {
            val bestScore = _state.value.exerciseStates
                .mapNotNull { it.analysisResult?.overallScore }
                .maxOrNull() ?: 0

            val progressEntity = CourseProgressEntity(
                id = "${courseId}_${lessonId}",
                courseId = courseId,
                lessonId = lessonId,
                isCompleted = true,
                completedAt = System.currentTimeMillis(),
                bestScore = bestScore,
                attemptsCount = 1,
                lastAttemptAt = System.currentTimeMillis()
            )
            courseProgressDao.insertOrUpdate(progressEntity)
        } catch (e: Exception) {
            Log.e("LessonVM", "Save progress error: ${e.message}", e)
        }
    }

    private suspend fun handleCourseCompletion() {
        try {
            achievementRepository.checkAndUnlock()

            val badgeDef = AchievementDefinitions.getForCourse(courseId)
            if (badgeDef != null) {
                _state.update {
                    it.copy(
                        courseCompletionBadge = Achievement(
                            id = badgeDef.id,
                            category = badgeDef.category,
                            title = badgeDef.title,
                            description = badgeDef.description,
                            icon = badgeDef.icon,
                            unlockedAt = System.currentTimeMillis(),
                            progress = null,
                            target = null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("LessonVM", "Course completion error: ${e.message}", e)
        }
    }

    private fun extractExpectedText(content: ExerciseContent): String? {
        return when (content) {
            is ExerciseContent.TongueTwister -> content.text
            is ExerciseContent.ReadingText -> content.text
            is ExerciseContent.SlowMotion -> content.text
            is ExerciseContent.TongueTwisterBattle -> content.twisters.joinToString(". ") { it.text }
            is ExerciseContent.Retelling -> content.sourceText
            is ExerciseContent.MinimalPairs -> content.pairs.joinToString(", ") { "${it.first} — ${it.second}" }
            is ExerciseContent.ContrastSounds -> content.sequence
            is ExerciseContent.Dialogue -> content.lines.joinToString(". ") { it.text }
            is ExerciseContent.FreeSpeechTopic -> null
            is ExerciseContent.Pitch -> content.scenario
            is ExerciseContent.ArticulationExercise -> null
            is ExerciseContent.BreathingExercise -> null
        }
    }

    private fun mapExerciseTypeForSkills(type: ExerciseType): String {
        return when (type) {
            ExerciseType.TONGUE_TWISTER -> "tongue_twister"
            ExerciseType.TONGUE_TWISTER_BATTLE -> "tongue_twister"
            ExerciseType.READING -> "reading"
            ExerciseType.EMOTION_READING -> "emotion_reading"
            ExerciseType.FREE_SPEECH -> "free_speech"
            ExerciseType.RETELLING -> "retelling"
            ExerciseType.DIALOGUE -> "dialogue"
            ExerciseType.PITCH -> "presentation"
            ExerciseType.QA -> "job_interview"
            ExerciseType.MINIMAL_PAIRS -> "minimal_pairs"
            ExerciseType.CONTRAST_SOUNDS -> "minimal_pairs"
            ExerciseType.SLOW_MOTION -> "slow_motion"
            ExerciseType.ARTICULATION -> "reading"
            ExerciseType.BREATHING -> "reading"
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
                saveLessonProgress()
                if (_state.value.isLastLessonInCourse) {
                    handleCourseCompletion()
                }
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
