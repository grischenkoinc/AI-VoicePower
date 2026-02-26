package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.audio.SoundEffect
import com.aivoicepower.audio.SoundManager
import com.aivoicepower.domain.service.SkillUpdateService
import com.aivoicepower.ui.screens.improvisation.components.OrbState
import com.aivoicepower.utils.CloudTtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NegotiationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiApiClient: GeminiApiClient,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val skillUpdateService: SkillUpdateService,
    private val soundManager: SoundManager,
    val ttsManager: CloudTtsManager
) : ViewModel() {

    private val _state = MutableStateFlow(NegotiationState())
    val state: StateFlow<NegotiationState> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingPath: String? = null
    private var recordingTimerJob: Job? = null

    init {
        ttsManager.warmUp()
        observeTts()
    }

    private fun observeTts() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                _state.update {
                    it.copy(
                        isTtsSpeaking = speaking,
                        orbState = when {
                            speaking -> OrbState.SPEAKING
                            it.isListening -> OrbState.LISTENING
                            it.isAiThinking -> OrbState.THINKING
                            !it.isStarted -> OrbState.IDLE
                            it.currentRound > it.maxRounds -> OrbState.COMPLETE
                            else -> OrbState.IDLE
                        }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        releaseRecorder()
        ttsManager.stop()
        recordingTimerJob?.cancel()
    }

    fun onEvent(event: NegotiationEvent) {
        when (event) {
            is NegotiationEvent.ScenarioSelected -> {
                _state.update { it.copy(selectedScenario = event.scenario) }
            }
            NegotiationEvent.StartSimulation -> {
                _state.update { it.copy(isStarted = true) }
            }
            NegotiationEvent.CountdownComplete -> startNegotiation()
            NegotiationEvent.StartRecording -> startVoiceInput()
            NegotiationEvent.StopRecording -> stopVoiceInput()
            NegotiationEvent.FinishNegotiation -> finishNegotiation()
            NegotiationEvent.AnalyzeClicked -> analyzeExercise()
            NegotiationEvent.SkipClicked -> finishNegotiation()
            NegotiationEvent.DismissAnalysis -> {
                finishNegotiation()
                _state.update { it.copy(analysisResult = null) }
            }
        }
    }

    private fun startNegotiation() {
        viewModelScope.launch {
            val scenario = _state.value.selectedScenario
            _state.update {
                it.copy(
                    currentRound = 1,
                    orbState = OrbState.THINKING,
                    isAiThinking = true,
                    aiText = "",
                    hint = scenario?.let { "${it.aiRole} готує свою позицію..." } ?: "Партнер готує свою позицію..."
                )
            }
            generateAiResponse("")
        }
    }

    private fun generateAiResponse(userProposal: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isAiThinking = true, orbState = OrbState.THINKING)
            }

            try {
                val round = _state.value.currentRound
                val history = _state.value.rounds.map { it.userProposal to it.aiResponse }
                val scenario = _state.value.selectedScenario

                val result = geminiApiClient.generateNegotiationResponse(
                    userProposal = userProposal,
                    roundNumber = round,
                    totalRounds = _state.value.maxRounds,
                    conversationHistory = history,
                    scenarioRole = scenario?.aiRole,
                    scenarioPosition = scenario?.aiPosition,
                    scenarioName = scenario?.name
                )

                result.onSuccess { aiResponse ->
                    val newRounds = if (userProposal.isNotBlank()) {
                        _state.value.rounds + NegotiationRound(
                            roundNumber = _state.value.currentRound,
                            userProposal = userProposal,
                            aiResponse = aiResponse
                        )
                    } else _state.value.rounds

                    val isComplete = _state.value.currentRound > _state.value.maxRounds

                    _state.update {
                        it.copy(
                            aiText = aiResponse,
                            isAiThinking = false,
                            orbState = OrbState.SPEAKING,
                            rounds = newRounds,
                            hint = when {
                                isComplete -> null
                                round == 1 && userProposal.isBlank() -> scenario?.userGoal ?: "Вислухайте позицію партнера"
                                round == 2 -> "Висуньте зустрічну пропозицію"
                                round == 3 -> "Шукайте компроміс та закрийте угоду"
                                else -> "Закрийте угоду"
                            }
                        )
                    }

                    ttsManager.speak(aiResponse)
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            error = "Помилка AI: ${error.message}",
                            isAiThinking = false,
                            orbState = OrbState.IDLE
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка: ${e.message}",
                        isAiThinking = false,
                        orbState = OrbState.IDLE
                    )
                }
            }
        }
    }

    private fun startVoiceInput() {
        ttsManager.stop()

        try {
            val recordingsDir = File(context.filesDir, "improv_recordings")
            recordingsDir.mkdirs()
            val outputFile = File(recordingsDir, "${UUID.randomUUID()}.m4a")
            currentRecordingPath = outputFile.absolutePath

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            soundManager.play(SoundEffect.RECORD_START)

            _state.update {
                it.copy(
                    orbState = OrbState.LISTENING,
                    isRecording = true,
                    isListening = true,
                    recordingSeconds = 0
                )
            }

            startRecordingTimer()
        } catch (e: Exception) {
            _state.update { it.copy(error = "Помилка мікрофону: ${e.message}") }
            releaseRecorder()
        }
    }

    private fun stopVoiceInput() {
        recordingTimerJob?.cancel()
        val filePath = currentRecordingPath

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {}
        mediaRecorder = null

        soundManager.play(SoundEffect.RECORD_STOP)

        _state.update {
            it.copy(
                isListening = false,
                isRecording = false,
                audioLevel = 0f,
                orbState = OrbState.THINKING
            )
        }

        if (filePath != null && File(filePath).exists()) {
            transcribeAndProcess(filePath)
        } else {
            _state.update { it.copy(orbState = OrbState.IDLE) }
        }
    }

    private fun transcribeAndProcess(filePath: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAiThinking = true, hint = "Розпізнаю мовлення...") }

            geminiApiClient.transcribeAudio(filePath).onSuccess { text ->
                try { File(filePath).delete() } catch (_: Exception) {}

                if (text.isNotBlank()) {
                    handleUserProposal(text)
                } else {
                    _state.update {
                        it.copy(
                            isAiThinking = false,
                            orbState = OrbState.IDLE,
                            hint = "Не вдалося розпізнати мовлення. Спробуйте ще раз."
                        )
                    }
                }
            }.onFailure { error ->
                try { File(filePath).delete() } catch (_: Exception) {}
                _state.update {
                    it.copy(
                        isAiThinking = false,
                        orbState = OrbState.IDLE,
                        error = "Помилка розпізнавання: ${error.message}"
                    )
                }
            }
        }
    }

    private fun releaseRecorder() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {}
        mediaRecorder = null
        currentRecordingPath?.let {
            try { File(it).delete() } catch (_: Exception) {}
        }
        currentRecordingPath = null
    }

    private fun startRecordingTimer() {
        recordingTimerJob?.cancel()
        recordingTimerJob = viewModelScope.launch {
            var elapsed = 0
            while (elapsed < _state.value.maxRecordingSeconds) {
                delay(1000)
                elapsed++
                _state.update { it.copy(recordingSeconds = elapsed) }
            }
            stopVoiceInput()
        }
    }

    private fun handleUserProposal(text: String) {
        val nextRound = _state.value.currentRound + 1
        _state.update { it.copy(currentRound = nextRound) }
        generateAiResponse(text)
    }

    private fun analyzeExercise() {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, error = null) }

            val currentState = _state.value
            val rounds = currentState.rounds.map { it.userProposal to it.aiResponse }
            val scenario = currentState.selectedScenario
            val context = if (scenario != null) {
                "Перемовини: ${scenario.name}. Роль партнера: ${scenario.aiRole}. Позицiя партнера: ${scenario.aiPosition}"
            } else {
                "Бiзнес-перемовини з партнером"
            }

            geminiApiClient.analyzeImprovisationExercise(
                exerciseType = "Перемовини",
                rounds = rounds,
                exerciseContext = context
            ).onSuccess { result ->
                try {
                    skillUpdateService.updateFromAnalysis(result, "negotiation")
                } catch (_: Exception) {}
                if (result.overallScore > 0) {
                    soundManager.play(SoundEffect.ANALYSIS_SUCCESS)
                } else {
                    soundManager.play(SoundEffect.ANALYSIS_ERROR)
                }
                _state.update { it.copy(isAnalyzing = false, analysisResult = result) }
            }.onFailure { error ->
                soundManager.play(SoundEffect.ANALYSIS_ERROR)
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        error = "Помилка аналiзу: ${error.message}"
                    )
                }
            }
        }
    }

    private fun finishNegotiation() {
        viewModelScope.launch {
            try {
                userPreferencesDataStore.incrementFreeImprovisations()
            } catch (_: Exception) {}
        }
    }
}
