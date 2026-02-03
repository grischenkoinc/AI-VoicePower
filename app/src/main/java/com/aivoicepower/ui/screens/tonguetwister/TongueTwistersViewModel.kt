package com.aivoicepower.ui.screens.tonguetwister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.TongueTwistersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TongueTwistersViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TongueTwistersState())
    val state: StateFlow<TongueTwistersState> = _state.asStateFlow()

    private var recordingJob: Job? = null

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

    private fun startPractice(twister: com.aivoicepower.domain.model.content.TongueTwister) {
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
        recordingJob?.cancel()
        _state.update {
            it.copy(
                isPracticing = false,
                practicingTwister = null,
                isRecording = false,
                recordingDurationMs = 0
            )
        }
    }

    private fun startRecording() {
        _state.update { it.copy(isRecording = true, recordingDurationMs = 0) }

        recordingJob = viewModelScope.launch {
            while (_state.value.isRecording) {
                delay(100)
                _state.update { it.copy(recordingDurationMs = it.recordingDurationMs + 100) }
            }
        }
    }

    private fun stopRecording() {
        recordingJob?.cancel()
        _state.update { it.copy(isRecording = false) }
    }

    override fun onCleared() {
        super.onCleared()
        recordingJob?.cancel()
    }
}
