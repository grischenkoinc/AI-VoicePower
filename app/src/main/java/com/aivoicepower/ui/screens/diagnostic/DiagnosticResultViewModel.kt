package com.aivoicepower.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.DiagnosticResultEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.firebase.sync.CloudSyncRepositoryImpl
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.ui.navigation.DiagnosticDataHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

sealed class DiagnosticResultUiState {
    object Loading : DiagnosticResultUiState()
    data class Success(val result: DiagnosticResult) : DiagnosticResultUiState()
    data class Error(val message: String) : DiagnosticResultUiState()
}

@HiltViewModel
class DiagnosticResultViewModel @Inject constructor(
    private val diagnosticResultDao: DiagnosticResultDao,
    private val recordingDao: RecordingDao,
    private val userProgressDao: UserProgressDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val cloudSyncRepository: CloudSyncRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiagnosticResultUiState>(DiagnosticResultUiState.Loading)
    val uiState: StateFlow<DiagnosticResultUiState> = _uiState.asStateFlow()

    init {
        loadResults()
    }

    fun loadResults() {
        viewModelScope.launch {
            _uiState.value = DiagnosticResultUiState.Loading

            try {
                val result = DiagnosticDataHolder.result
                if (result != null) {
                    // Save to database
                    saveDiagnosticToDb(result)
                    saveRecordingsToDb()
                    setInitialSkillLevels(result)

                    // Mark diagnostic as completed locally
                    userPreferencesDataStore.setDiagnosticCompleted(true)

                    // Show results immediately, sync to cloud in background
                    _uiState.value = DiagnosticResultUiState.Success(result)

                    // Fire-and-forget cloud sync
                    viewModelScope.launch {
                        try { cloudSyncRepository.saveUserFlags() } catch (_: Exception) { }
                    }
                } else {
                    _uiState.value = DiagnosticResultUiState.Error("Результати діагностики не знайдені")
                }
            } catch (e: Exception) {
                _uiState.value = DiagnosticResultUiState.Error(
                    e.message ?: "Помилка завантаження результатів"
                )
            }
        }
    }

    private suspend fun saveDiagnosticToDb(result: DiagnosticResult) {
        val existingCount = diagnosticResultDao.getCount()
        val entity = DiagnosticResultEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            diction = result.dictionScore,
            tempo = result.tempoScore,
            intonation = result.intonationScore,
            volume = 50, // not measured in diagnostic
            structure = result.structureScore,
            confidence = result.confidenceScore,
            fillerWords = result.fillerWordsScore,
            recommendations = result.recommendations.joinToString("||"),
            isInitial = existingCount == 0
        )
        diagnosticResultDao.insert(entity)
    }

    private suspend fun saveRecordingsToDb() {
        val paths = DiagnosticDataHolder.recordingPaths
        val tasks = listOf("Читання", "Дикція", "Емоційність", "Вільна мова")

        paths.forEachIndexed { index, path ->
            val file = File(path)
            if (file.exists()) {
                val recording = RecordingEntity(
                    id = UUID.randomUUID().toString(),
                    filePath = path,
                    durationMs = 0, // duration not tracked during diagnostic
                    type = "diagnostic",
                    contextId = "initial",
                    exerciseId = "diagnostic_${index + 1}",
                    transcription = tasks.getOrNull(index),
                    isAnalyzed = true,
                    createdAt = System.currentTimeMillis()
                )
                recordingDao.insert(recording)
            }
        }
    }

    private suspend fun setInitialSkillLevels(result: DiagnosticResult) {
        val existing = userProgressDao.getProgress()
        if (existing == null) {
            // First diagnostic — set initial levels
            val progress = com.aivoicepower.data.local.database.entity.UserProgressEntity(
                id = "default",
                dictionLevel = result.dictionScore.toFloat(),
                tempoLevel = result.tempoScore.toFloat(),
                intonationLevel = result.intonationScore.toFloat(),
                volumeLevel = 50f,
                structureLevel = result.structureScore.toFloat(),
                confidenceLevel = result.confidenceScore.toFloat(),
                fillerWordsLevel = result.fillerWordsScore.toFloat()
            )
            userProgressDao.insertProgress(progress)
        }
    }
}
