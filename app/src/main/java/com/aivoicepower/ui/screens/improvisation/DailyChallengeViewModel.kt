package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.data.firebase.sync.ServerLimitService
import com.aivoicepower.data.local.database.dao.DailyChallengeDao
import com.aivoicepower.data.local.database.entity.DailyChallengeEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.provider.DailyChallengeProvider
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dailyChallengeProvider: DailyChallengeProvider,
    private val dailyChallengeDao: DailyChallengeDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val rewardedAdManager: RewardedAdManager,
    private val serverLimitService: ServerLimitService
) : ViewModel() {

    private val audioRecorderUtil = AudioRecorderUtil(context)
    private var currentRecordingPath: String? = null

    private val _state = MutableStateFlow(DailyChallengeState())
    val state: StateFlow<DailyChallengeState> = _state.asStateFlow()

    private var preparationTimerJob: Job? = null
    private var recordingTimerJob: Job? = null

    init {
        loadTodayChallenge()
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

    fun onEvent(event: DailyChallengeEvent) {
        when (event) {
            DailyChallengeEvent.LoadChallenge -> loadTodayChallenge()
            DailyChallengeEvent.StartPreparation -> startPreparationTimer()
            DailyChallengeEvent.SkipPreparation -> {
                preparationTimerJob?.cancel()
                startRecording()
            }
            DailyChallengeEvent.StartRecording -> startRecording()
            DailyChallengeEvent.StopRecording -> stopRecording()
            DailyChallengeEvent.CompleteChallenge -> completeChallenge()
            DailyChallengeEvent.DismissAnalysis -> {
                _state.value = _state.value.copy(analysisResult = null)
            }
            DailyChallengeEvent.DismissAnalysisLimitSheet -> {
                _state.value = _state.value.copy(showAnalysisLimitSheet = false)
            }
            DailyChallengeEvent.WatchAdForAnalysis -> {
                _state.value = _state.value.copy(showAnalysisLimitSheet = false)
            }
            DailyChallengeEvent.ContinueWithoutAnalysis -> {
                _state.value = _state.value.copy(showAnalysisLimitSheet = false)
                completeWithoutAnalysis()
            }
        }
    }

    private fun loadTodayChallenge() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val today = LocalDate.now()
                val dateString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

                // Check if challenge already exists in database
                var challengeEntity = dailyChallengeDao.getChallengeByDate(dateString)

                if (challengeEntity == null) {
                    // Generate new challenge for today
                    val challenge = dailyChallengeProvider.getTodayChallenge()

                    // Save to database
                    challengeEntity = DailyChallengeEntity(
                        challengeId = challenge.id,
                        date = dateString,
                        challengeType = challenge.type.name,
                        title = challenge.title,
                        description = challenge.description,
                        timeLimit = challenge.timeLimit,
                        difficulty = challenge.difficulty,
                        completed = false
                    )
                    dailyChallengeDao.insertChallenge(challengeEntity)
                }

                // Convert entity to provider challenge
                val challenge = DailyChallengeProvider.DailyChallenge(
                    id = challengeEntity.challengeId,
                    type = DailyChallengeProvider.ChallengeType.valueOf(challengeEntity.challengeType),
                    title = challengeEntity.title,
                    description = challengeEntity.description,
                    timeLimit = challengeEntity.timeLimit,
                    difficulty = challengeEntity.difficulty
                )

                _state.value = _state.value.copy(
                    challenge = challenge,
                    isLoading = false,
                    isPreparationPhase = !challengeEntity.completed,
                    isCompleted = challengeEntity.completed,
                    completedAt = challengeEntity.completedAt,
                    recordingId = challengeEntity.recordingId,
                    recordingDurationMs = challengeEntity.recordingDurationMs
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Помилка завантаження виклику: ${e.message}"
                )
            }
        }
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
        val recordingsDir = File(context.filesDir, "recordings/daily_challenge")
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
                val challengeTimeLimit = _state.value.challenge?.timeLimit ?: 120
                val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                val maxRecording = if (prefs.isPremium) FreeTierLimits.PRO_RECORDING_DURATION_SECONDS
                    else FreeTierLimits.FREE_RECORDING_DURATION_SECONDS
                val effectiveLimit = minOf(challengeTimeLimit, maxRecording)
                while (_state.value.isRecording && _state.value.recordingDurationMs < effectiveLimit * 1000L) {
                    delay(100)
                    _state.value = _state.value.copy(
                        recordingDurationMs = _state.value.recordingDurationMs + 100
                    )
                }

                // Auto-stop when time limit reached
                if (_state.value.recordingDurationMs >= effectiveLimit * 1000L) {
                    stopRecording()
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isRecording = false,
                error = "Помилка запису: ${e.message}"
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

    private fun completeChallenge() {
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
                val challenge = _state.value.challenge ?: return@launch
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val completedAt = System.currentTimeMillis()
                val recordingPath = currentRecordingPath

                if (recordingPath == null) {
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        error = "Помилка: запис не знайдено. Спробуйте записати ще раз."
                    )
                    return@launch
                }

                val apiResult = voiceAnalysisRepository.analyzeRecording(
                    audioFilePath = recordingPath,
                    expectedText = null,
                    exerciseType = "daily_challenge_${challenge.type.name.lowercase()}",
                    context = "Щоденний виклик: ${challenge.title}"
                )
                val analysisResult = apiResult.getOrNull()

                // Update database
                val challengeEntity = dailyChallengeDao.getChallengeByDate(today)
                challengeEntity?.let {
                    val updated = it.copy(
                        completed = true,
                        recordingId = _state.value.recordingId,
                        recordingDurationMs = _state.value.recordingDurationMs,
                        completedAt = completedAt
                    )
                    dailyChallengeDao.updateChallenge(updated)
                }

                // Increment counters (local + server)
                val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                if (!prefs.isPremium) {
                    userPreferencesDataStore.incrementFreeImprovAnalyses()
                    serverLimitService.incrementAnalysis(isImprov = true)
                }
                userPreferencesDataStore.incrementFreeImprovisations()

                if (analysisResult != null) {
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        analysisResult = analysisResult,
                        completedAt = completedAt
                    )
                } else {
                    val errorMsg = apiResult.exceptionOrNull()?.message ?: "Невідома помилка"
                    _state.value = _state.value.copy(
                        isAnalyzing = false,
                        isCompleted = true,
                        completedAt = completedAt,
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

    private fun completeWithoutAnalysis() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val completedAt = System.currentTimeMillis()

                val challengeEntity = dailyChallengeDao.getChallengeByDate(today)
                challengeEntity?.let {
                    val updated = it.copy(
                        completed = true,
                        recordingId = _state.value.recordingId,
                        recordingDurationMs = _state.value.recordingDurationMs,
                        completedAt = completedAt
                    )
                    dailyChallengeDao.updateChallenge(updated)
                }

                userPreferencesDataStore.incrementFreeImprovisations()

                _state.value = _state.value.copy(
                    isCompleted = true,
                    completedAt = completedAt
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Помилка збереження: ${e.message}"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        preparationTimerJob?.cancel()
        recordingTimerJob?.cancel()
        audioRecorderUtil.release()
    }
}
