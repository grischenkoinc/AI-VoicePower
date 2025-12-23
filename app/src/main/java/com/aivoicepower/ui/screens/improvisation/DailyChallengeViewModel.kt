package com.aivoicepower.ui.screens.improvisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DailyChallengeDao
import com.aivoicepower.data.local.database.entity.DailyChallengeEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.provider.DailyChallengeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    private val dailyChallengeProvider: DailyChallengeProvider,
    private val dailyChallengeDao: DailyChallengeDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(DailyChallengeState())
    val state: StateFlow<DailyChallengeState> = _state.asStateFlow()

    private var preparationTimerJob: Job? = null
    private var recordingTimerJob: Job? = null

    init {
        loadTodayChallenge()
    }

    fun onEvent(event: DailyChallengeEvent) {
        when (event) {
            DailyChallengeEvent.LoadChallenge -> loadTodayChallenge()
            DailyChallengeEvent.StartPreparation -> startPreparationTimer()
            DailyChallengeEvent.StartRecording -> startRecording()
            DailyChallengeEvent.StopRecording -> stopRecording()
            DailyChallengeEvent.CompleteChallenge -> completeChallenge()
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
        }
    }

    private fun startRecording() {
        _state.value = _state.value.copy(
            isPreparationPhase = false,
            isRecording = true,
            recordingDurationMs = 0,
            recordingId = UUID.randomUUID().toString()
        )

        // TODO Phase 8: Інтеграція з AudioRecorderUtil
        recordingTimerJob?.cancel()
        recordingTimerJob = viewModelScope.launch {
            val timeLimit = _state.value.challenge?.timeLimit ?: 120
            while (_state.value.isRecording && _state.value.recordingDurationMs < timeLimit * 1000L) {
                delay(100)
                _state.value = _state.value.copy(
                    recordingDurationMs = _state.value.recordingDurationMs + 100
                )
            }

            // Auto-stop when time limit reached
            if (_state.value.recordingDurationMs >= timeLimit * 1000L) {
                stopRecording()
            }
        }
    }

    private fun stopRecording() {
        recordingTimerJob?.cancel()
        _state.value = _state.value.copy(
            isRecording = false
        )

        // TODO Phase 8: Зберегти запис через AudioRecorderUtil та RecordingDao
    }

    private fun completeChallenge() {
        viewModelScope.launch {
            try {
                val challenge = _state.value.challenge ?: return@launch
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val completedAt = System.currentTimeMillis()

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

                // Increment free improvisation count
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
    }
}
