package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.provider.StoryElementsProvider
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StorytellingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storyElementsProvider: StoryElementsProvider,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val voiceAnalysisRepository: VoiceAnalysisRepository
) : ViewModel() {

    private val audioRecorderUtil = AudioRecorderUtil(context)
    private var currentRecordingPath: String? = null

    private val _state = MutableStateFlow(StorytellingState())
    val state: StateFlow<StorytellingState> = _state.asStateFlow()

    private var preparationTimerJob: Job? = null
    private var recordingTimerJob: Job? = null

    fun onEvent(event: StorytellingEvent) {
        when (event) {
            is StorytellingEvent.SelectFormat -> selectFormat(event.format)
            StorytellingEvent.GenerateNewPrompt -> generateNewPrompt()
            StorytellingEvent.StartPreparation -> startPreparationTimer()
            StorytellingEvent.StartRecording -> startRecording()
            StorytellingEvent.StopRecording -> stopRecording()
            StorytellingEvent.CompleteTask -> completeTask()
            StorytellingEvent.ResetToFormatSelection -> resetToFormatSelection()
        }
    }

    private fun selectFormat(format: StoryFormat) {
        val prompt = storyElementsProvider.getStoryPrompt(format)
        _state.value = _state.value.copy(
            selectedFormat = format,
            storyPrompt = prompt,
            isPreparationPhase = true,
            preparationTimeLeft = 30
        )
    }

    private fun generateNewPrompt() {
        val currentFormat = _state.value.selectedFormat ?: return
        val newPrompt = storyElementsProvider.getStoryPrompt(currentFormat)
        _state.value = _state.value.copy(
            storyPrompt = newPrompt,
            isPreparationPhase = true,
            preparationTimeLeft = 30,
            isRecording = false,
            recordingDurationMs = 0,
            recordingId = null
        )
        preparationTimerJob?.cancel()
        recordingTimerJob?.cancel()
    }

    private fun startPreparationTimer() {
        preparationTimerJob?.cancel()
        preparationTimerJob = viewModelScope.launch {
            while (_state.value.preparationTimeLeft > 0) {
                delay(1000)
                _state.value = _state.value.copy(
                    preparationTimeLeft = _state.value.preparationTimeLeft - 1
                )
            }
        }
    }

    private fun startRecording() {
        val recordingId = UUID.randomUUID().toString()
        val recordingsDir = File(context.filesDir, "recordings/storytelling")
        recordingsDir.mkdirs()
        val outputPath = File(recordingsDir, "${recordingId}.m4a").absolutePath

        try {
            audioRecorderUtil.startRecording(outputPath)
            currentRecordingPath = outputPath

            _state.value = _state.value.copy(
                isPreparationPhase = false,
                isRecording = true,
                recordingDurationMs = 0,
                recordingId = recordingId
            )

            recordingTimerJob?.cancel()
            recordingTimerJob = viewModelScope.launch {
                while (_state.value.isRecording) {
                    delay(100)
                    _state.value = _state.value.copy(
                        recordingDurationMs = _state.value.recordingDurationMs + 100
                    )
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isRecording = false
            )
        }
    }

    private fun stopRecording() {
        recordingTimerJob?.cancel()
        audioRecorderUtil.stopRecording()
        _state.value = _state.value.copy(
            isRecording = false
        )
    }

    private fun completeTask() {
        viewModelScope.launch {
            val recordingPath = currentRecordingPath
            val storyPrompt = _state.value.storyPrompt
            val format = _state.value.selectedFormat

            // Analyze recording with Gemini if path exists
            if (recordingPath != null && storyPrompt.isNotEmpty()) {
                voiceAnalysisRepository.analyzeRecording(
                    audioFilePath = recordingPath,
                    expectedText = null,
                    exerciseType = "storytelling_${format?.name?.lowercase() ?: "classic"}",
                    context = "Формат історії: ${format?.name ?: "Класична"}, промпт: $storyPrompt"
                )
            }

            // Increment free improvisation count
            userPreferencesDataStore.incrementFreeImprovisations()
        }
    }

    private fun resetToFormatSelection() {
        preparationTimerJob?.cancel()
        recordingTimerJob?.cancel()
        _state.value = StorytellingState()
    }

    override fun onCleared() {
        super.onCleared()
        preparationTimerJob?.cancel()
        recordingTimerJob?.cancel()
        audioRecorderUtil.release()
    }
}
