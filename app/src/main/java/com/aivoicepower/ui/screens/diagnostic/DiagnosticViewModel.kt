package com.aivoicepower.ui.screens.diagnostic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.DiagnosticResultEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.domain.model.VoiceAnalysisResult
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
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao,
    private val diagnosticResultDao: DiagnosticResultDao,
    private val voiceAnalysisRepository: VoiceAnalysisRepository
) : ViewModel() {

    private val audioRecorderUtil = AudioRecorderUtil(context)

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
                        currentTaskIndex = null,
                        error = null
                    )
                }
            }

            DiagnosticEvent.StartRecordingClicked -> {
                // Clear error before attempting to start recording
                _state.update { it.copy(error = null) }
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

            DiagnosticEvent.PermissionDenied -> {
                _state.update {
                    it.copy(
                        error = "Для запису потрібен дозвіл на мікрофон. Надайте дозвіл у налаштуваннях."
                    )
                }
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
        val task = _state.value.selectedTask ?: return

        // Create real recording path
        val recordingsDir = File(context.filesDir, "recordings/diagnostic")
        recordingsDir.mkdirs()
        val outputPath = File(recordingsDir, "${task.id}_${System.currentTimeMillis()}.m4a").absolutePath

        try {
            audioRecorderUtil.startRecording(outputPath)

            _state.update {
                it.copy(
                    showInstructionDialog = false,
                    isRecording = true,
                    recordingSeconds = 0,
                    currentRecordingPath = outputPath
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
        } catch (e: Exception) {
            e.printStackTrace()
            // Show error to user - keep instruction dialog open
            _state.update {
                it.copy(
                    error = "Помилка запису: ${e.message ?: "Перевірте дозволи на мікрофон"}"
                )
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

        // Stop real recording
        val result = audioRecorderUtil.stopRecording()
        val recordingPath = result?.filePath ?: _state.value.currentRecordingPath

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

            // Якщо всі задачі завершені, аналізуємо всі записи з Gemini
            if (completedCount == updatedTasks.size) {
                analyzeAllRecordings()
            }
        }
    }

    private suspend fun analyzeAllRecordings() {
        val tasks = _state.value.tasks
        val analysisResults = mutableListOf<VoiceAnalysisResult>()

        // Analyze each recording with Gemini
        for (task in tasks) {
            val recordingPath = task.recordingPath ?: continue
            val file = File(recordingPath)
            if (!file.exists()) continue

            val result = voiceAnalysisRepository.analyzeRecording(
                audioFilePath = recordingPath,
                expectedText = task.contentText,
                exerciseType = task.id,
                context = task.instruction
            )

            result.getOrNull()?.let { analysisResults.add(it) }
        }

        // If we have analysis results, average them
        val diagnosticResult = if (analysisResults.isNotEmpty()) {
            val avgDiction = analysisResults.map { it.diction }.average().toInt()
            val avgTempo = analysisResults.map { it.tempo }.average().toInt()
            val avgIntonation = analysisResults.map { it.intonation }.average().toInt()
            val avgVolume = analysisResults.map { it.volume }.average().toInt()
            val avgConfidence = analysisResults.map { it.confidence }.average().toInt()
            val avgFillerWords = analysisResults.map { it.fillerWords }.average().toInt()
            val avgOverall = analysisResults.map { it.overallScore }.average().toInt()

            // Combine recommendations
            val allImprovements = analysisResults.flatMap { it.improvements }.distinct().take(5)

            DiagnosticResultEntity(
                id = UUID.randomUUID().toString(),
                diction = avgDiction,
                tempo = avgTempo,
                intonation = avgIntonation,
                volume = avgVolume,
                structure = avgOverall, // Using overall score as structure proxy
                confidence = avgConfidence,
                fillerWords = avgFillerWords,
                recommendations = allImprovements.joinToString(","),
                isInitial = true
            )
        } else {
            // Fallback to default values if analysis failed
            DiagnosticResultEntity(
                id = UUID.randomUUID().toString(),
                diction = 50,
                tempo = 50,
                intonation = 50,
                volume = 50,
                structure = 50,
                confidence = 50,
                fillerWords = 50,
                recommendations = "Спробуй записати ще раз",
                isInitial = true
            )
        }

        diagnosticResultDao.insert(diagnosticResult)
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderUtil.release()
    }
}
