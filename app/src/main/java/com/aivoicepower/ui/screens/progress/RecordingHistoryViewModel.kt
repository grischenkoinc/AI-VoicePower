package com.aivoicepower.ui.screens.progress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.utils.audio.AudioPlayerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecordingHistoryState(
    val isLoading: Boolean = true,
    val recordings: List<RecordingEntity> = emptyList(),
    val selectedFilter: RecordingFilter = RecordingFilter.ALL,
    val playingRecordingId: String? = null,
    val error: String? = null
)

enum class RecordingFilter {
    ALL, DIAGNOSTIC, COURSE, IMPROVISATION
}

sealed class RecordingHistoryEvent {
    data class FilterSelected(val filter: RecordingFilter) : RecordingHistoryEvent()
    data class PlayRecording(val recording: RecordingEntity) : RecordingHistoryEvent()
    object StopPlayback : RecordingHistoryEvent()
    data class DeleteRecording(val recordingId: String) : RecordingHistoryEvent()
    data class ViewResults(val recordingId: String) : RecordingHistoryEvent()
}

@HiltViewModel
class RecordingHistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao
) : ViewModel() {

    private val _state = MutableStateFlow(RecordingHistoryState())
    val state: StateFlow<RecordingHistoryState> = _state.asStateFlow()

    private val audioPlayer = AudioPlayerUtil(context)

    init {
        loadRecordings()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    fun onEvent(event: RecordingHistoryEvent) {
        when (event) {
            is RecordingHistoryEvent.FilterSelected -> {
                _state.update { it.copy(selectedFilter = event.filter) }
                loadRecordings()
            }
            is RecordingHistoryEvent.PlayRecording -> playRecording(event.recording)
            RecordingHistoryEvent.StopPlayback -> stopPlayback()
            is RecordingHistoryEvent.DeleteRecording -> deleteRecording(event.recordingId)
            is RecordingHistoryEvent.ViewResults -> {
                // Handled by Screen
            }
        }
    }

    private fun loadRecordings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                recordingDao.getAllRecordings().collect { recordings ->
                    val filteredRecordings = when (_state.value.selectedFilter) {
                        RecordingFilter.ALL -> recordings
                        RecordingFilter.DIAGNOSTIC -> recordings.filter { it.type == "diagnostic" }
                        RecordingFilter.COURSE -> recordings.filter { it.type == "exercise" }
                        RecordingFilter.IMPROVISATION -> recordings.filter { it.type == "improvisation" }
                    }.sortedByDescending { it.createdAt }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            recordings = filteredRecordings
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Помилка завантаження: ${e.message}"
                    )
                }
            }
        }
    }

    private fun playRecording(recording: RecordingEntity) {
        viewModelScope.launch {
            try {
                if (_state.value.playingRecordingId == recording.id) {
                    stopPlayback()
                    return@launch
                }

                stopPlayback()
                _state.update { it.copy(playingRecordingId = recording.id) }

                audioPlayer.play(recording.filePath) {
                    _state.update { it.copy(playingRecordingId = null) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        playingRecordingId = null,
                        error = "Помилка вiдтворення: ${e.message}"
                    )
                }
            }
        }
    }

    private fun stopPlayback() {
        audioPlayer.stop()
        _state.update { it.copy(playingRecordingId = null) }
    }

    private fun deleteRecording(recordingId: String) {
        viewModelScope.launch {
            try {
                recordingDao.deleteById(recordingId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка видалення: ${e.message}") }
            }
        }
    }
}
