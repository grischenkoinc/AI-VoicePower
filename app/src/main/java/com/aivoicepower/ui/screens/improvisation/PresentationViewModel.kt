package com.aivoicepower.ui.screens.improvisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.SimulationScenariosProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresentationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PresentationState())
    val state: StateFlow<PresentationState> = _state.asStateFlow()

    private var recordingJob: Job? = null

    init {
        loadSteps()
    }

    private fun loadSteps() {
        val scenario = SimulationScenariosProvider.getAllScenarios()
            .find { it.id == "presentation" }
            ?: return

        val steps = scenario.steps.map { step ->
            InterviewStep(
                stepNumber = step.stepNumber,
                question = step.aiPrompt,
                hint = step.userHint
            )
        }

        _state.update { it.copy(steps = steps) }
    }

    fun onEvent(event: PresentationEvent) {
        when (event) {
            PresentationEvent.StartSimulation -> {
                _state.update { it.copy(isStarted = true, currentStepIndex = 0) }
            }

            PresentationEvent.StartRecording -> {
                startRecording()
            }

            PresentationEvent.StopRecording -> {
                stopRecording()
                moveToNextStep()
            }

            PresentationEvent.NextStep -> {
                moveToNextStep()
            }
        }
    }

    private fun startRecording() {
        _state.update { it.copy(isRecording = true, recordingDurationMs = 0) }

        recordingJob = viewModelScope.launch {
            while (true) {
                delay(100)
                _state.update {
                    it.copy(recordingDurationMs = it.recordingDurationMs + 100)
                }
            }
        }
    }

    private fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null

        val recordingId = "presentation_step_${_state.value.currentStepIndex}_${System.currentTimeMillis()}"

        _state.update {
            it.copy(
                isRecording = false,
                recordingIds = it.recordingIds + recordingId
            )
        }

        // TODO: Save recording to database via repository
    }

    private fun moveToNextStep() {
        _state.update {
            it.copy(currentStepIndex = it.currentStepIndex + 1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        recordingJob?.cancel()
    }
}
