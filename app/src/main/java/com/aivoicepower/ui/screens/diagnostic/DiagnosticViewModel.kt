package com.aivoicepower.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    private val recordingDao: RecordingDao
) : ViewModel() {

    private val _state = MutableStateFlow(DiagnosticState())
    val state: StateFlow<DiagnosticState> = _state.asStateFlow()

    private var recordingTimerJob: Job? = null

    fun onEvent(event: DiagnosticEvent) {
        when (event) {
            is DiagnosticEvent.TaskClicked -> {
                handleTaskClicked(event.taskIndex)
            }

            DiagnosticEvent.InstructionDialogDismissed -> {
                _state.update {
                    it.copy(
                        showInstructionDialog = false,
                        selectedTask = null,
                        currentTaskIndex = null
                    )
                }
            }

            DiagnosticEvent.StartRecordingClicked -> {
                startRecording()
            }

            DiagnosticEvent.StopRecordingClicked -> {
                stopRecording()
            }

            is DiagnosticEvent.RecordingTick -> {
                handleRecordingTick(event.seconds)
            }

            DiagnosticEvent.RetakeRecordingClicked -> {
                _state.update {
                    it.copy(
                        showRecordingPreview = false,
                        showInstructionDialog = true,
                        currentRecordingPath = null,
                        recordingSeconds = 0
                    )
                }
            }

            DiagnosticEvent.SaveRecordingClicked -> {
                saveRecording()
            }

            DiagnosticEvent.PreviewDialogDismissed -> {
                _state.update {
                    it.copy(
                        showRecordingPreview = false,
                        selectedTask = null,
                        currentTaskIndex = null,
                        currentRecordingPath = null,
                        recordingSeconds = 0
                    )
                }
            }

            DiagnosticEvent.CompleteDiagnosticClicked -> {
                // Will be handled in navigation
                _state.update { it.copy(isCompleted = true) }
            }
        }
    }

    private fun handleTaskClicked(taskIndex: Int) {
        val task = _state.value.tasks.getOrNull(taskIndex) ?: return

        // Only allow clicking on pending or recorded tasks
        when (task.status) {
            TaskStatus.PENDING, TaskStatus.RECORDED -> {
                _state.update {
                    it.copy(
                        selectedTask = task,
                        currentTaskIndex = taskIndex,
                        showInstructionDialog = true
                    )
                }
            }
            else -> {
                // Task is in progress or completed - do nothing
            }
        }
    }

    private fun startRecording() {
        _state.update {
            it.copy(
                showInstructionDialog = false,
                isRecording = true,
                recordingSeconds = 0
            )
        }

        // Start timer
        recordingTimerJob?.cancel()
        recordingTimerJob = viewModelScope.launch {
            var seconds = 0
            while (_state.value.isRecording) {
                delay(1000)
                seconds++
                onEvent(DiagnosticEvent.RecordingTick(seconds))
            }
        }
    }

    private fun handleRecordingTick(seconds: Int) {
        _state.update { it.copy(recordingSeconds = seconds) }

        // Auto-stop when max duration reached
        val maxDuration = _state.value.selectedTask?.durationSeconds ?: 60
        if (seconds >= maxDuration) {
            stopRecording()
        }
    }

    private fun stopRecording() {
        recordingTimerJob?.cancel()

        // FAKE recording path (no actual audio file created)
        val recordingPath = "recordings/diagnostic_${UUID.randomUUID()}.m4a"

        _state.update {
            it.copy(
                isRecording = false,
                currentRecordingPath = recordingPath,
                showRecordingPreview = true
            )
        }
    }

    private fun saveRecording() {
        viewModelScope.launch {
            val currentState = _state.value
            val task = currentState.selectedTask ?: return@launch
            val taskIndex = currentState.currentTaskIndex ?: return@launch
            val recordingPath = currentState.currentRecordingPath ?: return@launch
            val durationSeconds = currentState.recordingSeconds

            // Save to Room database
            val recordingEntity = RecordingEntity(
                id = UUID.randomUUID().toString(),
                filePath = recordingPath,
                durationMs = durationSeconds * 1000L,
                type = "diagnostic",
                contextId = "initial_diagnostic",
                exerciseId = task.id,
                isAnalyzed = false
            )

            recordingDao.insert(recordingEntity)

            // Update task status
            val updatedTasks = _state.value.tasks.toMutableList()
            updatedTasks[taskIndex] = task.copy(
                status = TaskStatus.RECORDED,
                recordingPath = recordingPath
            )

            val completedCount = updatedTasks.count { it.status == TaskStatus.RECORDED }

            _state.update {
                it.copy(
                    tasks = updatedTasks,
                    completedTasksCount = completedCount,
                    showRecordingPreview = false,
                    selectedTask = null,
                    currentTaskIndex = null,
                    currentRecordingPath = null,
                    recordingSeconds = 0
                )
            }
        }
    }
}
