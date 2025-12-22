package com.aivoicepower.ui.screens.results

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.utils.audio.AudioPlayerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val recordingDao: RecordingDao,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val recordingId: String = savedStateHandle["recordingId"] ?: ""

    private val _state = MutableStateFlow(ResultsState(recordingId = recordingId))
    val state: StateFlow<ResultsState> = _state.asStateFlow()

    private val audioPlayer = AudioPlayerUtil(context)

    init {
        loadResults()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    fun onEvent(event: ResultsEvent) {
        when (event) {
            ResultsEvent.PlayRecordingClicked -> playRecording()
            ResultsEvent.StopPlaybackClicked -> stopPlayback()
            ResultsEvent.RetryExerciseClicked -> {
                // Navigation handled in Screen
            }
            ResultsEvent.NextExerciseClicked -> {
                // Navigation handled in Screen
            }
            ResultsEvent.BackToCourseClicked -> {
                // Navigation handled in Screen
            }
        }
    }

    private fun loadResults() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                recordingDao.getByIdFlow(recordingId)
                    .collect { recordingEntity ->
                        if (recordingEntity == null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Запис не знайдено"
                                )
                            }
                            return@collect
                        }

                        // Load exercise info
                        val exercise = if (recordingEntity.exerciseId != null &&
                            recordingEntity.contextId != null
                        ) {
                            val parts = recordingEntity.contextId.split("_")
                            if (parts.size == 2) {
                                val (courseId, lessonId) = parts
                                courseRepository.getLessonById(courseId, lessonId)
                                    .first()?.exercises?.find { it.id == recordingEntity.exerciseId }
                            } else null
                        } else null

                        val recordingInfo = RecordingInfo(
                            id = recordingEntity.id,
                            filePath = recordingEntity.filePath,
                            durationMs = recordingEntity.durationMs,
                            createdAt = recordingEntity.createdAt,
                            exerciseTitle = exercise?.title ?: "Вправа",
                            exerciseType = exercise?.type?.name ?: "Unknown"
                        )

                        val analysis = if (recordingEntity.isAnalyzed) {
                            // TODO Phase 6: Parse analysisJson
                            AnalysisResult(
                                isAnalyzed = true,
                                overallScore = recordingEntity.overallScore,
                                feedback = null // Will be parsed from analysisJson in Phase 6
                            )
                        } else {
                            AnalysisResult(
                                isAnalyzed = false,
                                overallScore = null,
                                feedback = null
                            )
                        }

                        _state.update {
                            it.copy(
                                recording = recordingInfo,
                                exercise = exercise,
                                analysis = analysis,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити результати: ${e.message}"
                    )
                }
            }
        }
    }

    private fun playRecording() {
        val filePath = _state.value.recording?.filePath ?: return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isPlaying = true) }
                audioPlayer.play(filePath) {
                    _state.update { it.copy(isPlaying = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка відтворення: ${e.message}",
                        isPlaying = false
                    )
                }
            }
        }
    }

    private fun stopPlayback() {
        audioPlayer.stop()
        _state.update { it.copy(isPlaying = false) }
    }
}
