package com.aivoicepower.ui.screens.improvisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.provider.StoryElementsProvider
import com.aivoicepower.domain.model.exercise.StoryFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StorytellingViewModel @Inject constructor(
    private val storyElementsProvider: StoryElementsProvider,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

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
        _state.value = _state.value.copy(
            isPreparationPhase = false,
            isRecording = true,
            recordingDurationMs = 0,
            recordingId = UUID.randomUUID().toString()
        )

        // TODO Phase 8: Інтеграція з AudioRecorderUtil
        recordingTimerJob?.cancel()
        recordingTimerJob = viewModelScope.launch {
            while (_state.value.isRecording) {
                delay(100)
                _state.value = _state.value.copy(
                    recordingDurationMs = _state.value.recordingDurationMs + 100
                )
            }
        }
    }

    private fun stopRecording() {
        recordingTimerJob?.cancel()
        _state.value = _state.value.copy(
            isRecording = false
        )

        // TODO Phase 8: Зберегти запис через AudioRecorderUtil та RecordingDao
    }

    private fun completeTask() {
        viewModelScope.launch {
            // Increment free improvisation count
            userPreferencesDataStore.incrementFreeImprovisations()

            // TODO Phase 8: Зберегти завершення в базі даних
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
    }
}
