package com.aivoicepower.ui.screens.tonguetwister

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.TongueTwistersProvider
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.domain.model.content.TongueTwister
import com.aivoicepower.domain.repository.VoiceAnalysisRepository
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TongueTwistersViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val voiceAnalysisRepository: VoiceAnalysisRepository,
    private val recordingDao: RecordingDao
) : ViewModel() {

    private val _state = MutableStateFlow(TongueTwistersState())
    val state: StateFlow<TongueTwistersState> = _state.asStateFlow()

    private val audioRecorder = AudioRecorderUtil(context)
    private var recordingTimerJob: Job? = null
    private var recordingPath: String? = null

    init {
        loadTongueTwisters()
    }

    private fun loadTongueTwisters() {
        val allTwisters = TongueTwistersProvider.getAllTongueTwisters()
        _state.update {
            it.copy(
                allTongueTwisters = allTwisters,
                filteredTongueTwisters = allTwisters
            )
        }
    }

    fun onEvent(event: TongueTwistersEvent) {
        when (event) {
            is TongueTwistersEvent.SelectCategory -> selectCategory(event.category)
            is TongueTwistersEvent.SelectDifficulty -> selectDifficulty(event.difficulty)
            is TongueTwistersEvent.UpdateSearch -> updateSearch(event.query)
            is TongueTwistersEvent.ToggleExpand -> toggleExpand(event.twisterId)
            is TongueTwistersEvent.StartPractice -> startPractice(event.twister)
            TongueTwistersEvent.StopPractice -> stopPractice()
            TongueTwistersEvent.StartRecording -> startRecording()
            TongueTwistersEvent.StopRecording -> stopRecording()
            TongueTwistersEvent.DismissAnalysis -> dismissAnalysis()
        }
    }

    private fun selectCategory(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    private fun selectDifficulty(difficulty: Int?) {
        _state.update { it.copy(selectedDifficulty = difficulty) }
        applyFilters()
    }

    private fun updateSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _state.value
        var filtered = currentState.allTongueTwisters

        // Filter by category
        currentState.selectedCategory?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        // Filter by difficulty
        currentState.selectedDifficulty?.let { difficulty ->
            filtered = filtered.filter { it.difficulty == difficulty }
        }

        // Filter by search query
        if (currentState.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.text.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        _state.update { it.copy(filteredTongueTwisters = filtered) }
    }

    private fun toggleExpand(twisterId: String) {
        _state.update {
            it.copy(expandedTwisterId = if (it.expandedTwisterId == twisterId) null else twisterId)
        }
    }

    private fun startPractice(twister: TongueTwister) {
        _state.update {
            it.copy(
                isPracticing = true,
                practicingTwister = twister,
                isRecording = false,
                recordingDurationMs = 0
            )
        }
    }

    private fun stopPractice() {
        recordingTimerJob?.cancel()
        if (_state.value.isRecording) {
            try { audioRecorder.stopRecording() } catch (_: Exception) {}
        }
        _state.update {
            it.copy(
                isPracticing = false,
                practicingTwister = null,
                isRecording = false,
                recordingDurationMs = 0,
                isAnalyzing = false,
                analysisResult = null
            )
        }
    }

    private fun dismissAnalysis() {
        _state.update {
            it.copy(
                isPracticing = false,
                practicingTwister = null,
                analysisResult = null,
                isAnalyzing = false
            )
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/tongue_twister_${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()

                audioRecorder.startRecording(outputFile.absolutePath)
                recordingPath = outputFile.absolutePath

                _state.update { it.copy(isRecording = true, recordingDurationMs = 0) }

                recordingTimerJob = launch {
                    while (_state.value.isRecording) {
                        delay(100)
                        _state.update { it.copy(recordingDurationMs = it.recordingDurationMs + 100) }
                    }
                }
            } catch (e: Exception) {
                Log.e("TongueTwistersVM", "Recording start error", e)
                _state.update { it.copy(isRecording = false) }
            }
        }
    }

    private fun stopRecording() {
        viewModelScope.launch {
            try {
                recordingTimerJob?.cancel()
                audioRecorder.stopRecording()

                val twister = _state.value.practicingTwister

                _state.update { it.copy(isRecording = false, isAnalyzing = true) }

                saveRecording(twister)
            } catch (e: Exception) {
                Log.e("TongueTwistersVM", "Recording stop error", e)
                _state.update { it.copy(isRecording = false, isAnalyzing = false) }
            }
        }
    }

    private suspend fun saveRecording(twister: TongueTwister?) {
        try {
            val path = recordingPath ?: run {
                _state.update { it.copy(isAnalyzing = false) }
                return
            }

            val result = voiceAnalysisRepository.analyzeRecording(
                audioFilePath = path,
                expectedText = twister?.text,
                exerciseType = "tongue_twister",
                context = "Скоромовка: ${twister?.text ?: ""}, цільові звуки: ${twister?.targetSounds?.joinToString() ?: ""}"
            )

            val analysisResult = result.getOrNull()

            // Save recording to database — only meaningful recordings (score > 0)
            if (analysisResult != null && analysisResult.overallScore > 0) {
                val recordingEntity = RecordingEntity(
                    id = UUID.randomUUID().toString(),
                    filePath = path,
                    durationMs = _state.value.recordingDurationMs,
                    type = "tongue_twister",
                    contextId = "tongue_twister_${twister?.id ?: ""}",
                    transcription = null,
                    isAnalyzed = true,
                    exerciseId = twister?.id
                )
                recordingDao.insert(recordingEntity)
            }

            _state.update {
                it.copy(
                    isAnalyzing = false,
                    analysisResult = analysisResult
                )
            }
        } catch (e: Exception) {
            Log.e("TongueTwistersVM", "Save recording error", e)
            _state.update { it.copy(isAnalyzing = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        recordingTimerJob?.cancel()
    }
}
