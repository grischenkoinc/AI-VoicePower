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
import com.aivoicepower.utils.AnalyticsTracker
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
class PresentationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiApiClient: GeminiApiClient,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val skillUpdateService: SkillUpdateService,
    private val soundManager: SoundManager,
    val ttsManager: CloudTtsManager,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _state = MutableStateFlow(PresentationState())
    val state: StateFlow<PresentationState> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingPath: String? = null
    private var recordingTimerJob: Job? = null

    init {
        ttsManager.warmUp()
        ttsManager.setTtsContext("presentation")
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

    fun onEvent(event: PresentationEvent) {
        when (event) {
            is PresentationEvent.TopicSelected -> {
                _state.update { it.copy(selectedTopic = event.topic) }
            }
            PresentationEvent.StartSimulation -> {
                _state.update { it.copy(isStarted = true) }
            }
            PresentationEvent.CountdownComplete -> startPresentation()
            PresentationEvent.StartRecording -> startVoiceInput()
            PresentationEvent.StopRecording -> stopVoiceInput()
            PresentationEvent.FinishPresentation -> finishPresentation()
            PresentationEvent.AnalyzeClicked -> analyzeExercise()
            PresentationEvent.SkipClicked -> finishPresentation()
            PresentationEvent.DismissAnalysis -> {
                finishPresentation()
                _state.update { it.copy(analysisResult = null) }
            }
        }
    }

    private fun startPresentation() {
        viewModelScope.launch {
            analyticsTracker.logExerciseStarted("presentation", "improvisation", false)
            val topic = _state.value.selectedTopic
            _state.update {
                it.copy(
                    currentRound = 1,
                    orbState = OrbState.THINKING,
                    isAiThinking = true,
                    aiText = "",
                    hint = "Аудиторія чекає на вашу презентацію..."
                )
            }
            // Передаємо тему як контекст для першого раунду
            val topicContext = if (!topic.isNullOrBlank()) "Тема презентації: $topic" else ""
            generateAiResponse(topicContext)
        }
    }

    private fun generateAiResponse(userSpeech: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isAiThinking = true, orbState = OrbState.THINKING)
            }

            try {
                val round = _state.value.currentRound
                val history = _state.value.rounds.map { it.userSpeech to it.aiResponse }

                val result = geminiApiClient.generatePresentationResponse(
                    userSpeech = userSpeech,
                    roundNumber = round,
                    totalRounds = _state.value.maxRounds,
                    conversationHistory = history
                )

                result.onSuccess { aiResponse ->
                    val newRounds = if (userSpeech.isNotBlank()) {
                        _state.value.rounds + PresentationRound(
                            roundNumber = _state.value.currentRound,
                            userSpeech = userSpeech,
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
                                round == 1 && userSpeech.isBlank() -> "Розкажіть про проблему та актуальність"
                                round == 2 -> "Представте ваше рішення"
                                round == 3 -> "Розкажіть про реалізацію"
                                else -> "Підсумуйте та закрийте презентацію"
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
        analyticsTracker.logRecordingCompleted("presentation", _state.value.recordingSeconds * 1000L, false)

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
                    handleUserSpeech(text)
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

    private fun handleUserSpeech(text: String) {
        val nextRound = _state.value.currentRound + 1
        _state.update { it.copy(currentRound = nextRound) }
        generateAiResponse(text)
    }

    private fun analyzeExercise() {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, error = null) }

            val currentState = _state.value
            val rounds = currentState.rounds.map { it.userSpeech to it.aiResponse }
            val context = "Презентацiя перед аудиторiєю"

            geminiApiClient.analyzeImprovisationExercise(
                exerciseType = "Презентацiя",
                rounds = rounds,
                exerciseContext = context
            ).onSuccess { result ->
                try {
                    skillUpdateService.updateFromAnalysis(result, "presentation")
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

    private fun finishPresentation() {
        viewModelScope.launch {
            try {
                userPreferencesDataStore.incrementFreeImprovisations()
            } catch (_: Exception) {}
        }
    }
}
