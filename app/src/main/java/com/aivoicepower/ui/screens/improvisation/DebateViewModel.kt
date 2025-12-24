package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.GeminiApiClient
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
class DebateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiApiClient: GeminiApiClient,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(DebateState())
    val state: StateFlow<DebateState> = _state.asStateFlow()

    private val audioRecorder = AudioRecorderUtil(context)
    private var recordingTimerJob: Job? = null
    private var recordingPath: String? = null

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        recordingTimerJob?.cancel()
    }

    fun onEvent(event: DebateEvent) {
        when (event) {
            is DebateEvent.TopicSelected -> {
                _state.update {
                    it.copy(
                        selectedTopic = event.topic,
                        phase = DebatePhase.PositionSelection
                    )
                }
            }
            is DebateEvent.PositionSelected -> {
                _state.update {
                    it.copy(
                        userPosition = event.position,
                        phase = DebatePhase.UserArgument,
                        currentRound = 1
                    )
                }
            }
            DebateEvent.StartRecordingClicked -> {
                startRecording()
            }
            DebateEvent.StopRecordingClicked -> {
                stopRecording()
            }
            DebateEvent.NextRoundClicked -> {
                startNextRound()
            }
            DebateEvent.FinishDebateClicked -> {
                finishDebate()
            }
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/debate_${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()

                audioRecorder.startRecording(outputFile.absolutePath)
                recordingPath = outputFile.absolutePath

                _state.update {
                    it.copy(
                        isRecording = true,
                        recordingSeconds = 0
                    )
                }

                // Timer
                recordingTimerJob = launch {
                    var elapsed = 0
                    while (elapsed < _state.value.maxRecordingSeconds && _state.value.isRecording) {
                        delay(1000)
                        elapsed++
                        _state.update { it.copy(recordingSeconds = elapsed) }
                    }

                    if (elapsed >= _state.value.maxRecordingSeconds) {
                        stopRecording()
                    }
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
            try {
                recordingTimerJob?.cancel()
                audioRecorder.stopRecording()
                _state.update { it.copy(isRecording = false) }

                // Simulate transcription (placeholder until Phase 8)
                delay(1500)
                val mockTranscription = "[Аргумент користувача - транскрипція буде доступна в Phase 8]"

                handleTranscribedArgument(mockTranscription)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка зупинки: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }

    private fun handleTranscribedArgument(transcription: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAiThinking = true, phase = DebatePhase.AiResponse) }

            try {
                val topic = _state.value.selectedTopic?.topic ?: ""
                val position = when (_state.value.userPosition) {
                    DebatePosition.FOR -> "ЗА"
                    DebatePosition.AGAINST -> "ПРОТИ"
                    else -> ""
                }
                val roundNumber = _state.value.currentRound
                val history = _state.value.rounds.map { it.userArgument to it.aiResponse }

                val result = geminiApiClient.generateDebateResponse(
                    topic = topic,
                    userPosition = position,
                    userArgument = transcription,
                    roundNumber = roundNumber,
                    conversationHistory = history
                )

                result.onSuccess { aiResponse ->
                    val newRound = DebateRound(
                        roundNumber = roundNumber,
                        userArgument = transcription,
                        aiResponse = aiResponse
                    )

                    // Save recording to DB
                    saveRecording(transcription)

                    _state.update {
                        it.copy(
                            rounds = it.rounds + newRound,
                            isAiThinking = false
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            error = "Помилка AI: ${error.message}",
                            isAiThinking = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка: ${e.message}",
                        isAiThinking = false
                    )
                }
            }
        }
    }

    private suspend fun saveRecording(transcription: String) {
        try {
            val path = recordingPath ?: return
            val topic = _state.value.selectedTopic?.id ?: ""

            val recordingEntity = RecordingEntity(
                id = UUID.randomUUID().toString(),
                filePath = path,
                durationMs = _state.value.recordingSeconds * 1000L,
                type = "improvisation",
                contextId = "debate_$topic",
                transcription = transcription,
                isAnalyzed = false
            )

            recordingDao.insert(recordingEntity)
        } catch (e: Exception) {
            // Log error
        }
    }

    private fun startNextRound() {
        val nextRound = _state.value.currentRound + 1
        if (nextRound <= _state.value.maxRounds) {
            _state.update {
                it.copy(
                    currentRound = nextRound,
                    phase = DebatePhase.UserArgument,
                    recordingSeconds = 0
                )
            }
        } else {
            finishDebate()
        }
    }

    private fun finishDebate() {
        viewModelScope.launch {
            try {
                userPreferencesDataStore.incrementFreeImprovisations()
                _state.update { it.copy(phase = DebatePhase.DebateComplete) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка завершення: ${e.message}") }
            }
        }
    }
}
