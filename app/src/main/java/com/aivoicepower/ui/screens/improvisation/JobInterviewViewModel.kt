package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.ImprovisationContentProvider
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.content.ImprovisationTask
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JobInterviewViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(JobInterviewState())
    val state: StateFlow<JobInterviewState> = _state.asStateFlow()

    private val audioRecorder = AudioRecorderUtil(context)
    private var recordingTimerJob: Job? = null
    private var recordingPath: String? = null
    private var exerciseId: String = ""

    init {
        loadExercise()
    }

    private fun loadExercise() {
        val exercise = ImprovisationContentProvider.getJobInterviewExercise()
        exerciseId = exercise.id

        val steps = when (val task = exercise.task) {
            is ImprovisationTask.JobInterview -> {
                task.steps.map { step ->
                    JobInterviewStep(
                        stepNumber = step.stepNumber,
                        question = step.question,
                        hint = step.hint
                    )
                }
            }
            else -> emptyList()
        }

        _state.update { it.copy(steps = steps) }
    }

    fun onEvent(event: JobInterviewEvent) {
        when (event) {
            JobInterviewEvent.StartSimulation -> {
                _state.update { it.copy(isStarted = true, currentStepIndex = 0) }
            }

            JobInterviewEvent.StartRecording -> {
                startRecording()
            }

            JobInterviewEvent.StopRecording -> {
                stopRecording()
            }

            JobInterviewEvent.NextStep -> {
                moveToNextStep()
            }
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/interview_${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()

                audioRecorder.startRecording(outputFile.absolutePath)
                recordingPath = outputFile.absolutePath

                _state.update { it.copy(isRecording = true, recordingDurationMs = 0) }

                recordingTimerJob = launch {
                    while (_state.value.isRecording) {
                        delay(100)
                        _state.update { it.copy(recordingDurationMs = it.recordingDurationMs + 100) }
                    }
                }
            } catch (e: Exception) {
                Log.e("JobInterviewVM", "Recording start error", e)
                _state.update { it.copy(isRecording = false) }
            }
        }
    }

    private fun stopRecording() {
        viewModelScope.launch {
            try {
                recordingTimerJob?.cancel()
                audioRecorder.stopRecording()

                val currentStep = _state.value.steps.getOrNull(_state.value.currentStepIndex)

                _state.update {
                    it.copy(
                        isRecording = false,
                        recordingIds = it.recordingIds + "${exerciseId}_step_${it.currentStepIndex}_${System.currentTimeMillis()}"
                    )
                }

                saveRecording(currentStep)
                moveToNextStep()
            } catch (e: Exception) {
                Log.e("JobInterviewVM", "Recording stop error", e)
                _state.update { it.copy(isRecording = false) }
                moveToNextStep()
            }
        }
    }

    private suspend fun saveRecording(step: JobInterviewStep?) {
        try {
            val path = recordingPath ?: return

            voiceAnalysisRepository.analyzeRecording(
                audioFilePath = path,
                expectedText = null,
                exerciseType = "job_interview",
                context = "Співбесіда, крок ${step?.stepNumber ?: 0}: ${step?.question ?: ""}"
            )

            val recordingEntity = RecordingEntity(
                id = UUID.randomUUID().toString(),
                filePath = path,
                durationMs = _state.value.recordingDurationMs,
                type = "improvisation",
                contextId = "interview_step_${_state.value.currentStepIndex}",
                transcription = null,
                isAnalyzed = true,
                exerciseId = exerciseId
            )

            recordingDao.insert(recordingEntity)
        } catch (e: Exception) {
            Log.e("JobInterviewVM", "Save recording error", e)
        }
    }

    private fun moveToNextStep() {
        _state.update {
            it.copy(currentStepIndex = it.currentStepIndex + 1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        recordingTimerJob?.cancel()
    }
}
