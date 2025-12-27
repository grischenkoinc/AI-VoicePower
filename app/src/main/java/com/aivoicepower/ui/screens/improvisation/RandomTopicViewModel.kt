package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.ImprovisationTopicsProvider
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RandomTopicViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val voiceAnalysisRepository: VoiceAnalysisRepository
) : ViewModel() {

    private val audioRecorderUtil = AudioRecorderUtil(context)

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
                val recordingId = UUID.randomUUID().toString()
                val recordingsDir = File(context.filesDir, "recordings/improvisation")
                recordingsDir.mkdirs()
                val outputPath = File(recordingsDir, "${recordingId}.m4a").absolutePath

                audioRecorderUtil.startRecording(outputPath)

                _state.update {
                    it.copy(
                        isRecording = true,
                        recordingId = recordingId,
                        recordingPath = outputPath,
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
            audioRecorderUtil.stopRecording()
            _state.update {
                it.copy(isRecording = false)
            }
        }
    }

    private fun completeTask() {
        viewModelScope.launch {
            try {
                val recordingPath = _state.value.recordingPath
                val topic = _state.value.currentTopic

                // Analyze recording with Gemini if path exists
                if (recordingPath != null && topic != null) {
                    val analysisResult = voiceAnalysisRepository.analyzeRecording(
                        audioFilePath = recordingPath,
                        expectedText = null, // Free improvisation - no expected text
                        exerciseType = "random_topic",
                        context = "Тема для імпровізації: ${topic.title}"
                    )
                    // Analysis result is available for future use (e.g., showing score)
                    analysisResult.getOrNull()
                }

                // Increment free improvisation counter
                userPreferencesDataStore.incrementFreeImprovisations()

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка збереження: ${e.message}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderUtil.release()
    }
}
