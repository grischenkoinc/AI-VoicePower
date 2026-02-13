package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.data.firebase.sync.ServerLimitService
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.provider.StoryElementsProvider
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.PremiumChecker
import com.aivoicepower.utils.audio.AudioRecorderUtil
import com.aivoicepower.utils.constants.FreeTierLimits
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StorytellingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storyElementsProvider: StoryElementsProvider,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val rewardedAdManager: RewardedAdManager,
    private val serverLimitService: ServerLimitService
) : ViewModel() {

    private val audioRecorderUtil = AudioRecorderUtil(context)
    private var currentRecordingPath: String? = null

    private val _state = MutableStateFlow(StorytellingState())
    val state: StateFlow<StorytellingState> = _state.asStateFlow()

    private var preparationTimerJob: Job? = null
    private var recordingTimerJob: Job? = null

    init {
        observeAnalysisLimits()
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
                val remainingAd = if (prefs.freeAdImprovToday < FreeTierLimits.FREE_AD_IMPROV_ANALYSES_PER_DAY) {
                    FreeTierLimits.FREE_AD_IMPROV_ANALYSES_PER_DAY - prefs.freeAdImprovToday
                } else 0
                _state.value = _state.value.copy(
                    isPremium = prefs.isPremium,
                    remainingImprovAnalyses = remaining,
                    remainingAdImprovAnalyses = remainingAd
                )
            }
        }

        viewModelScope.launch {
            rewardedAdManager.isAdLoaded.collect { loaded ->
                _state.value = _state.value.copy(isAdLoaded = loaded)
            }
        }
    }

    fun onEvent(event: StorytellingEvent) {
        when (event) {
            is StorytellingEvent.SelectFormat -> selectFormat(event.format)
            StorytellingEvent.GenerateNewPrompt -> generateNewPrompt()
            StorytellingEvent.StartPreparation -> startPreparationTimer()
            StorytellingEvent.SkipPreparation -> {
                preparationTimerJob?.cancel()
                startRecording()
            }
            StorytellingEvent.StartRecording -> startRecording()
            StorytellingEvent.StopRecording -> stopRecording()
            StorytellingEvent.CompleteTask -> completeTask()
            StorytellingEvent.DismissAnalysis -> {
                _state.value = _state.value.copy(analysisResult = null)
            }
            StorytellingEvent.ResetToFormatSelection -> resetToFormatSelection()
            StorytellingEvent.DismissAnalysisLimitSheet -> {
                _state.value = _state.value.copy(showAnalysisLimitSheet = false)
            }
            StorytellingEvent.WatchAdForAnalysis -> {
                _state.value = _state.value.copy(showAnalysisLimitSheet = false)
            }
            StorytellingEvent.ContinueWithoutAnalysis -> {
                _state.value = _state.value.copy(showAnalysisLimitSheet = false)
                completeTaskWithoutAnalysis()
            }
        }
    }

    private fun selectFormat(format: StoryFormat) {
        val prompt = storyElementsProvider.getStoryPrompt(format)
        _state.value = _state.value.copy(
            selectedFormat = format,
            storyPrompt = prompt,
            isPreparationPhase = true,
            preparationTimeLeft = 15
        )
    }

    private fun generateNewPrompt() {
        val currentFormat = _state.value.selectedFormat ?: return
        val newPrompt = storyElementsProvider.getStoryPrompt(currentFormat)
        _state.value = _state.value.copy(
            storyPrompt = newPrompt,
            isPreparationPhase = true,
            preparationTimeLeft = 15,
            isRecording = false,
            recordingDurationMs = 0,
            recordingId = null,
            analysisResult = null
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
            // Timer finished — auto-start recording
            startRecording()
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
                val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                val maxDurationMs = if (prefs.isPremium)
                    FreeTierLimits.PRO_RECORDING_DURATION_SECONDS * 1000L
                    else FreeTierLimits.FREE_RECORDING_DURATION_SECONDS * 1000L
                while (_state.value.isRecording) {
                    delay(100)
                    _state.value = _state.value.copy(
                        recordingDurationMs = _state.value.recordingDurationMs + 100
                    )
                    if (_state.value.recordingDurationMs >= maxDurationMs) {
                        stopRecording()
                        break
                    }
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
            val prefs = userPreferencesDataStore.userPreferencesFlow.first()
            val canAnalyze = PremiumChecker.canAnalyzeImprovisation(
                prefs.isPremium, prefs.freeImprovAnalysesToday, prefs.freeAdImprovToday
            )

            if (!canAnalyze) {
                _state.value = _state.value.copy(showAnalysisLimitSheet = true)
                return@launch
            }

            // Server-side limit check for free users
            if (!prefs.isPremium && !serverLimitService.canAnalyze(isImprov = true)) {
                _state.value = _state.value.copy(showAnalysisLimitSheet = true)
                return@launch
            }

            performAnalysis()
        }
    }

    private fun performAnalysis() {
        _state.value = _state.value.copy(isAnalyzing = true, error = null)

        viewModelScope.launch {
            try {
                val recordingPath = currentRecordingPath
                val storyPrompt = _state.value.storyPrompt
                val format = _state.value.selectedFormat

                if (recordingPath == null || storyPrompt.isEmpty()) {
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        error = "Помилка: запис не знайдено. Спробуйте записати ще раз."
                    )
                    return@launch
                }

                val apiResult = voiceAnalysisRepository.analyzeRecording(
                    audioFilePath = recordingPath,
                    expectedText = null,
                    exerciseType = "storytelling_${format?.name?.lowercase() ?: "classic"}",
                    context = "Формат історії: ${format?.name ?: "Класична"}, промпт: $storyPrompt"
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
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        analysisResult = result
                    )
                } else {
                    val errorMsg = apiResult.exceptionOrNull()?.message ?: "Невідома помилка"
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        error = "Помилка аналізу: $errorMsg"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isAnalyzing = false,
                    error = "Помилка аналізу: ${e.message}"
                )
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
