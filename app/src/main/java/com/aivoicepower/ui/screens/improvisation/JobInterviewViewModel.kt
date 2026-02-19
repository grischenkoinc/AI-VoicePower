package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.GeminiApiClient
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
class JobInterviewViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiApiClient: GeminiApiClient,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val skillUpdateService: SkillUpdateService,
    val ttsManager: CloudTtsManager
) : ViewModel() {

    private val _state = MutableStateFlow(JobInterviewState())
    val state: StateFlow<JobInterviewState> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingPath: String? = null
    private var recordingTimerJob: Job? = null

    companion object {
        private val HR_NAMES = listOf(
            "Олена Петренко", "Андрій Коваленко", "Марина Шевченко",
            "Ігор Бондаренко", "Наталія Ткаченко", "Дмитро Кравченко",
            "Оксана Мельник", "Сергій Литвиненко"
        )

        private val COMPANIES_IT = listOf(
            "TechStar Solutions", "NovaTech Ukraine", "Digital Dynamics", "CloudBridge"
        )
        private val COMPANIES_MARKETING = listOf(
            "BrandForce Agency", "MediaPro Group", "SalesDrive Ukraine", "AdVantage Studio"
        )
        private val COMPANIES_FINANCE = listOf(
            "FinGroup Capital", "Prime Analytics", "InvestPro Ukraine", "MoneyWise Consulting"
        )
        private val COMPANIES_GENERAL = listOf(
            "GlobalCore", "NextLevel Group", "ProActive Solutions", "Synergy Partners"
        )

        private val IT_KEYWORDS = listOf(
            "developer", "розробник", "frontend", "backend", "devops", "qa", "тестувальник",
            "програміст", "software", "full-stack", "fullstack", "data", "engineer", "інженер",
            "ui/ux", "дизайнер", "designer", "product manager", "системний адміністратор",
            "it", "mobile"
        )
        private val MARKETING_KEYWORDS = listOf(
            "маркетолог", "marketing", "smm", "pr", "реклам", "копірайтер", "copywriter",
            "контент", "content", "бренд", "brand", "продаж", "sales", "менеджер з продажу",
            "комерц"
        )
        private val FINANCE_KEYWORDS = listOf(
            "фінанс", "finance", "бухгалтер", "accountant", "аналітик", "analyst",
            "аудитор", "auditor", "банк", "bank", "інвест", "invest", "економіст"
        )
    }

    init {
        ttsManager.warmUp()
        observeTts()
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            userPreferencesDataStore.userPreferencesFlow
                .map { it.userName }
                .distinctUntilChanged()
                .collect { name ->
                    _state.update { it.copy(userName = name) }
                }
        }
    }

    private fun getCompanyForProfession(profession: String): String {
        val lowerProfession = profession.lowercase()
        return when {
            IT_KEYWORDS.any { lowerProfession.contains(it) } -> COMPANIES_IT.random()
            MARKETING_KEYWORDS.any { lowerProfession.contains(it) } -> COMPANIES_MARKETING.random()
            FINANCE_KEYWORDS.any { lowerProfession.contains(it) } -> COMPANIES_FINANCE.random()
            else -> COMPANIES_GENERAL.random()
        }
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

    fun onEvent(event: JobInterviewEvent) {
        when (event) {
            is JobInterviewEvent.ProfessionSelected -> selectProfession(event.profession)
            JobInterviewEvent.StartSimulation -> {
                _state.update { it.copy(isStarted = true) }
            }
            JobInterviewEvent.CountdownComplete -> startInterview()
            JobInterviewEvent.StartRecording -> startVoiceInput()
            JobInterviewEvent.StopRecording -> stopVoiceInput()
            JobInterviewEvent.FinishInterview -> finishInterview()
            JobInterviewEvent.AnalyzeClicked -> analyzeExercise()
            JobInterviewEvent.SkipClicked -> finishInterview()
            JobInterviewEvent.DismissAnalysis -> {
                finishInterview()
                _state.update { it.copy(analysisResult = null) }
            }
        }
    }

    private fun selectProfession(profession: String) {
        val hrName = HR_NAMES.random()
        val companyName = getCompanyForProfession(profession)
        _state.update {
            it.copy(
                selectedProfession = profession,
                hrName = hrName,
                companyName = companyName
            )
        }
    }

    private fun startInterview() {
        viewModelScope.launch {
            val hrName = _state.value.hrName
            _state.update {
                it.copy(
                    currentRound = 1,
                    orbState = OrbState.THINKING,
                    isAiThinking = true,
                    aiText = "",
                    hint = "$hrName готується до співбесіди..."
                )
            }
            generateAiResponse("")
        }
    }

    private fun generateAiResponse(userAnswer: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isAiThinking = true, orbState = OrbState.THINKING)
            }

            try {
                val currentState = _state.value
                val round = currentState.currentRound
                val history = currentState.rounds.map { it.userAnswer to it.aiResponse }

                val result = geminiApiClient.generateInterviewResponse(
                    userAnswer = userAnswer,
                    roundNumber = round,
                    totalRounds = currentState.maxRounds,
                    conversationHistory = history,
                    profession = currentState.selectedProfession ?: "",
                    hrName = currentState.hrName,
                    companyName = currentState.companyName,
                    userName = currentState.userName
                )

                result.onSuccess { aiResponse ->
                    val newRounds = if (userAnswer.isNotBlank()) {
                        _state.value.rounds + InterviewRound(
                            roundNumber = _state.value.currentRound,
                            userAnswer = userAnswer,
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
                                round == 1 && userAnswer.isBlank() -> "Розкажіть про себе та свій досвід"
                                round == 2 -> "Підкресліть свої сильні сторони"
                                round == 3 -> "Використовуйте метод STAR"
                                round == 4 -> "Наведіть приклади командної роботи"
                                else -> "Будьте конкретним та впевненим"
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
                    handleUserAnswer(text)
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

    private fun handleUserAnswer(text: String) {
        val nextRound = _state.value.currentRound + 1
        _state.update { it.copy(currentRound = nextRound) }
        generateAiResponse(text)
    }

    private fun analyzeExercise() {
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, error = null) }

            val currentState = _state.value
            val rounds = currentState.rounds.map { it.userAnswer to it.aiResponse }
            val context = "Позицiя: ${currentState.selectedProfession}, Компанiя: ${currentState.companyName}"

            geminiApiClient.analyzeImprovisationExercise(
                exerciseType = "Спiвбесiда",
                rounds = rounds,
                exerciseContext = context
            ).onSuccess { result ->
                try {
                    skillUpdateService.updateFromAnalysis(result, "job_interview")
                } catch (_: Exception) {}
                _state.update { it.copy(isAnalyzing = false, analysisResult = result) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        error = "Помилка аналiзу: ${error.message}"
                    )
                }
            }
        }
    }

    private fun finishInterview() {
        viewModelScope.launch {
            try {
                userPreferencesDataStore.incrementFreeImprovisations()
            } catch (_: Exception) {}
        }
    }
}
