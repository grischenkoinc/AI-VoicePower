package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.data.content.ImprovisationTopicsProvider
import com.aivoicepower.data.firebase.sync.ServerLimitService
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.PremiumChecker
import com.aivoicepower.utils.audio.AudioRecorderUtil
import com.aivoicepower.utils.constants.FreeTierLimits
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
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
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val rewardedAdManager: RewardedAdManager,
    private val serverLimitService: ServerLimitService
) : ViewModel() {

    private val audioRecorderUtil = AudioRecorderUtil(context)
    private var preparationTimerJob: Job? = null

    private val _state = MutableStateFlow(RandomTopicState())
    val state: StateFlow<RandomTopicState> = _state.asStateFlow()

    init {
        generateNewTopic()
        observeAnalysisLimits()
    }

    fun onEvent(event: RandomTopicEvent) {
        when (event) {
            RandomTopicEvent.GenerateNewTopic -> {
                generateNewTopic()
            }
            RandomTopicEvent.StartPreparation -> {
                startPreparationTimer()
            }
            RandomTopicEvent.SkipPreparation -> {
                preparationTimerJob?.cancel()
                startRecording()
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
            RandomTopicEvent.DismissAnalysis -> {
                _state.update { it.copy(analysisResult = null) }
            }
            RandomTopicEvent.DismissAnalysisLimitSheet -> {
                _state.update { it.copy(showAnalysisLimitSheet = false) }
            }
            RandomTopicEvent.WatchAdForAnalysis -> {
                _state.update { it.copy(showAnalysisLimitSheet = false) }
            }
            RandomTopicEvent.ContinueWithoutAnalysis -> {
                _state.update { it.copy(showAnalysisLimitSheet = false) }
                completeTaskWithoutAnalysis()
            }
        }
    }

    private fun observeAnalysisLimits() {
        viewModelScope.launch {
            userPreferencesDataStore.checkAndResetDailyLimits()
        }

        viewModelScope.launch {
            userPreferencesDataStore.userPreferencesFlow.collect { prefs ->
                val remaining = PremiumChecker.getRemainingImprovAnalyses(
                    prefs.isPremium, prefs.freeImprovAnalysesToday, prefs.freeAdImprovToday
                )
                val remainingAd = if (prefs.freeAdImprovToday < com.aivoicepower.utils.constants.FreeTierLimits.FREE_AD_IMPROV_ANALYSES_PER_DAY) {
                    com.aivoicepower.utils.constants.FreeTierLimits.FREE_AD_IMPROV_ANALYSES_PER_DAY - prefs.freeAdImprovToday
                } else 0
                _state.update {
                    it.copy(
                        isPremium = prefs.isPremium,
                        remainingImprovAnalyses = remaining,
                        remainingAdImprovAnalyses = remainingAd
                    )
                }
            }
        }

        viewModelScope.launch {
            rewardedAdManager.isAdLoaded.collect { loaded ->
                _state.update { it.copy(isAdLoaded = loaded) }
            }
        }
    }

    private fun generateNewTopic() {
        preparationTimerJob?.cancel()
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
        preparationTimerJob?.cancel()
        preparationTimerJob = viewModelScope.launch {
            while (_state.value.preparationTimeLeft > 0) {
                delay(1000)
                _state.update { it.copy(preparationTimeLeft = _state.value.preparationTimeLeft - 1) }
            }
            // Timer finished — auto-start recording
            startRecording()
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

                // Track recording duration with limit
                val maxDurationMs = if (_state.value.isPremium)
                    FreeTierLimits.PRO_RECORDING_DURATION_SECONDS * 1000L
                    else FreeTierLimits.FREE_RECORDING_DURATION_SECONDS * 1000L
                val startTime = System.currentTimeMillis()
                while (_state.value.isRecording) {
                    val duration = System.currentTimeMillis() - startTime
                    _state.update { it.copy(recordingDurationMs = duration) }
                    if (duration >= maxDurationMs) {
                        stopRecording()
                        break
                    }
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
            val prefs = userPreferencesDataStore.userPreferencesFlow.first()
            val canAnalyze = PremiumChecker.canAnalyzeImprovisation(
                prefs.isPremium, prefs.freeImprovAnalysesToday, prefs.freeAdImprovToday
            )

            if (!canAnalyze) {
                _state.update { it.copy(showAnalysisLimitSheet = true) }
                return@launch
            }

            // Server-side limit check for free users
            if (!prefs.isPremium && !serverLimitService.canAnalyze(isImprov = true)) {
                _state.update { it.copy(showAnalysisLimitSheet = true) }
                return@launch
            }

            performAnalysis()
        }
    }

    private fun performAnalysis() {
        _state.update { it.copy(isAnalyzing = true, error = null) }

        viewModelScope.launch {
            try {
                val recordingPath = _state.value.recordingPath
                val topic = _state.value.currentTopic

                if (recordingPath == null || topic == null) {
                    _state.update {
                        it.copy(
                            isAnalyzing = false,
                            error = "Помилка: запис не знайдено. Спробуйте записати ще раз."
                        )
                    }
                    return@launch
                }

                val apiResult = voiceAnalysisRepository.analyzeRecording(
                    audioFilePath = recordingPath,
                    expectedText = null,
                    exerciseType = "random_topic",
                    context = "Тема для імпровізації: ${topic.title}"
                )

                val result = apiResult.getOrNull()

                // Increment counters (local + server)
                val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                if (!prefs.isPremium) {
                    userPreferencesDataStore.incrementFreeImprovAnalyses()
                    serverLimitService.incrementAnalysis(isImprov = true)
                }
                userPreferencesDataStore.incrementFreeImprovisations()

                if (result != null) {
                    _state.update {
                        it.copy(
                            isAnalyzing = false,
                            analysisResult = result
                        )
                    }
                } else {
                    val errorMsg = apiResult.exceptionOrNull()?.message ?: "Невідома помилка"
                    _state.update {
                        it.copy(
                            isAnalyzing = false,
                            error = "Помилка аналізу: $errorMsg"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        error = "Помилка аналізу: ${e.message}"
                    )
                }
            }
        }
    }

    fun proceedWithAnalysisAfterAd() {
        viewModelScope.launch {
            userPreferencesDataStore.incrementFreeAdImprov()
            performAnalysis()
        }
    }

    private fun completeTaskWithoutAnalysis() {
        viewModelScope.launch {
            // Just increment improvisation counter, no analysis
            userPreferencesDataStore.incrementFreeImprovisations()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderUtil.release()
    }
}
