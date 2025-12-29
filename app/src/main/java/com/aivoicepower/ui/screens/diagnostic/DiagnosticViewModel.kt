package com.aivoicepower.ui.screens.diagnostic

import android.content.Context
import android.util.Log
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
            Log.d("DiagFlow", "=== saveRecording() CALLED ===")

            val currentState = _state.value
            val task = currentState.selectedTask
            val taskIndex = currentState.currentTaskIndex
            val recordingPath = currentState.currentRecordingPath
            val durationSeconds = currentState.recordingSeconds

            Log.d("DiagFlow", "task: ${task?.id}, taskIndex: $taskIndex")
            Log.d("DiagFlow", "recordingPath: $recordingPath")
            Log.d("DiagFlow", "durationSeconds: $durationSeconds")

            if (task == null) {
                Log.e("DiagFlow", "ABORT: task is null!")
                return@launch
            }
            if (taskIndex == null) {
                Log.e("DiagFlow", "ABORT: taskIndex is null!")
                return@launch
            }
            if (recordingPath == null) {
                Log.e("DiagFlow", "ABORT: recordingPath is null!")
                return@launch
            }

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
            Log.d("DiagFlow", "completedCount: $completedCount, totalTasks: ${updatedTasks.size}")
            if (completedCount == updatedTasks.size) {
                Log.d("DiagFlow", "ALL TASKS COMPLETED! Calling analyzeAllRecordings()...")
                analyzeAllRecordings()
            } else {
                Log.d("DiagFlow", "Not all tasks completed yet, skipping analysis")
            }
        }
    }

    private suspend fun analyzeAllRecordings() {
        Log.d("DiagFlow", "=== analyzeAllRecordings() ENTERED ===")
        Log.d("Diagnostic", "=== analyzeAllRecordings START ===")

        // Показуємо стан "Аналізуємо..."
        _state.update { it.copy(isAnalyzing = true, error = null) }
        Log.d("DiagFlow", "State updated: isAnalyzing=true")

        try {
            val tasks = _state.value.tasks
            Log.d("DiagFlow", "Tasks to analyze: ${tasks.size}")
            Log.d("Diagnostic", "Recordings to analyze: ${tasks.size}")

            // Log all task paths
            tasks.forEachIndexed { index, task ->
                Log.d("DiagFlow", "Task[$index]: id=${task.id}, path=${task.recordingPath}, exists=${task.recordingPath?.let { File(it).exists() }}")
            }

            val analysisResults = mutableListOf<VoiceAnalysisResult>()

            // Analyze each recording with Gemini (with timeout)
            for (task in tasks) {
                val recordingPath = task.recordingPath
                if (recordingPath == null) {
                    Log.w("DiagFlow", "SKIP task ${task.id}: recordingPath is null")
                    continue
                }

                val file = File(recordingPath)
                if (!file.exists()) {
                    Log.w("DiagFlow", "SKIP task ${task.id}: file does NOT exist at $recordingPath")
                    continue
                }

                Log.d("DiagFlow", "Processing task ${task.id}, file size: ${file.length()} bytes")

                try {
                    Log.d("DiagFlow", ">>> Calling voiceAnalysisRepository.analyzeRecording() for ${task.id}")
                    Log.d("Diagnostic", "Analyzing recording: ${task.id}")
                    // Timeout 90 секунд на кожен запис (3 retry * 30 сек API timeout)
                    val result = kotlinx.coroutines.withTimeout(90_000L) {
                        voiceAnalysisRepository.analyzeRecording(
                            audioFilePath = recordingPath,
                            expectedText = task.contentText,
                            exerciseType = task.id,
                            context = task.instruction
                        )
                    }
                    Log.d("DiagFlow", "<<< voiceAnalysisRepository returned for ${task.id}")
                    result.getOrNull()?.let {
                        Log.d("DiagFlow", "SUCCESS: ${task.id} analyzed, score: ${it.overallScore}")
                        Log.d("Diagnostic", "Recording ${task.id} analyzed, score: ${it.overallScore}")
                        analysisResults.add(it)
                    } ?: run {
                        Log.w("DiagFlow", "Result was null or failure for ${task.id}")
                    }
                } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                    Log.e("DiagFlow", "!!! TIMEOUT for ${task.id} after 90 seconds !!!")
                    Log.w("Diagnostic", "Timeout analyzing ${task.id}")
                    continue
                } catch (e: kotlinx.coroutines.CancellationException) {
                    Log.e("DiagFlow", "!!! JOB CANCELLED for ${task.id} !!!")
                    Log.e("DiagFlow", "CancellationException: ${e.message}")
                    // Пробросити CancellationException далі - не ігнорувати
                    throw e
                } catch (e: Exception) {
                    Log.e("DiagFlow", "!!! EXCEPTION for ${task.id}: ${e.javaClass.simpleName} - ${e.message}")
                    Log.e("Diagnostic", "Error analyzing ${task.id}: ${e.message}")
                    continue
                }
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

            Log.d("Diagnostic", "Saving to DB: diction=${diagnosticResult.diction}, tempo=${diagnosticResult.tempo}, overall=${diagnosticResult.structure}")
            diagnosticResultDao.insert(diagnosticResult)
            Log.d("Diagnostic", "Saved to DB successfully, id=${diagnosticResult.id}")

            // Завершуємо аналіз і переходимо до результатів
            Log.d("Diagnostic", "Setting isCompleted=true, navigating to results...")
            _state.update {
                it.copy(
                    isAnalyzing = false,
                    isCompleted = true
                )
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            // Не перехоплюємо CancellationException - пробросимо далі
            Log.e("DiagFlow", "!!! analyzeAllRecordings CANCELLED !!!")
            Log.e("DiagFlow", "CancellationException: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e("DiagFlow", "!!! analyzeAllRecordings FAILED: ${e.javaClass.simpleName} !!!")
            Log.e("Diagnostic", "Error in analyzeAllRecordings: ${e.message}", e)
            // Помилка аналізу - зберігаємо дефолтні результати
            val fallbackResult = DiagnosticResultEntity(
                id = UUID.randomUUID().toString(),
                diction = 50,
                tempo = 50,
                intonation = 50,
                volume = 50,
                structure = 50,
                confidence = 50,
                fillerWords = 50,
                recommendations = "Аналіз не вдався, спробуй пізніше",
                isInitial = true
            )
            Log.d("DiagFlow", "Saving fallback result to DB...")
            Log.d("Diagnostic", "Saving fallback result to DB...")
            diagnosticResultDao.insert(fallbackResult)
            Log.d("DiagFlow", "Fallback saved, navigating to results...")
            Log.d("Diagnostic", "Fallback saved, setting isCompleted=true")

            _state.update {
                it.copy(
                    isAnalyzing = false,
                    isCompleted = true,
                    error = null // Не показуємо помилку, бо все одно переходимо до результатів
                )
            }
        }
        Log.d("Diagnostic", "=== analyzeAllRecordings END ===")
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderUtil.release()
    }
}
