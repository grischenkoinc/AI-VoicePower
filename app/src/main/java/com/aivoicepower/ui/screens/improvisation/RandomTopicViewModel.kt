package com.aivoicepower.ui.screens.improvisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.ImprovisationTopicsProvider
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RandomTopicViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(RandomTopicState())
    val state: StateFlow<RandomTopicState> = _state.asStateFlow()

    init {
        generateNewTopic()
    }

    fun onEvent(event: RandomTopicEvent) {
        when (event) {
            RandomTopicEvent.GenerateNewTopic -> {
                generateNewTopic()
            }
            RandomTopicEvent.StartPreparation -> {
                startPreparationTimer()
            }
            RandomTopicEvent.StartRecording -> {
                startRecording()
            }
            RandomTopicEvent.StopRecording -> {
                stopRecording()
            }
            RandomTopicEvent.CompleteTask -> {
                completeTask()
            }
        }
    }

    private fun generateNewTopic() {
        val allTopics = ImprovisationTopicsProvider.getAllTopics()
        val randomTopic = allTopics.random()

        _state.update {
            it.copy(
                currentTopic = randomTopic,
                preparationTimeLeft = 15,
                isPreparationPhase = true,
                isRecording = false,
                recordingPath = null,
                recordingId = null
            )
        }
    }

    private fun startPreparationTimer() {
        viewModelScope.launch {
            for (i in 15 downTo 0) {
                _state.update { it.copy(preparationTimeLeft = i) }
                delay(1000)
            }

            // Timer finished
            _state.update { it.copy(isPreparationPhase = false) }
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                // TODO: Implement actual recording with AudioRecorderUtil in future phases
                val recordingId = UUID.randomUUID().toString()

                _state.update {
                    it.copy(
                        isRecording = true,
                        recordingId = recordingId,
                        isPreparationPhase = false
                    )
                }

                // Track recording duration
                val startTime = System.currentTimeMillis()
                while (_state.value.isRecording) {
                    val duration = System.currentTimeMillis() - startTime
                    _state.update { it.copy(recordingDurationMs = duration) }
                    delay(100)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка запису: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }

    private fun stopRecording() {
        viewModelScope.launch {
            _state.update {
                it.copy(isRecording = false)
            }
        }
    }

    private fun completeTask() {
        viewModelScope.launch {
            try {
                // TODO: Save recording to database when RecordingDao is implemented

                // Increment free improvisation counter
                userPreferencesDataStore.incrementFreeImprovisations()

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка збереження: ${e.message}")
                }
            }
        }
    }
}
