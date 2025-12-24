package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.SalesProductsProvider
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.data.remote.SalesStage
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SalesPitchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiApiClient: GeminiApiClient,
    private val salesProductsProvider: SalesProductsProvider,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(SalesPitchState())
    val state: StateFlow<SalesPitchState> = _state.asStateFlow()

    private val audioRecorder = AudioRecorderUtil(context)
    private var recordingTimerJob: Job? = null
    private var recordingPath: String? = null

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
        recordingTimerJob?.cancel()
    }

    fun onEvent(event: SalesPitchEvent) {
        when (event) {
            is SalesPitchEvent.ProductSelected -> {
                val customer = salesProductsProvider.getRandomCustomer()
                _state.update {
                    it.copy(
                        selectedProduct = event.product,
                        customerProfile = customer,
                        phase = SalesPhase.CustomerProfile
                    )
                }
            }
            SalesPitchEvent.StartPitchClicked -> {
                _state.update { it.copy(phase = SalesPhase.OpeningPitch) }
            }
            SalesPitchEvent.StartRecordingClicked -> {
                startRecording()
            }
            SalesPitchEvent.StopRecordingClicked -> {
                stopRecording()
            }
            SalesPitchEvent.ContinueToObjectionClicked -> {
                _state.update { it.copy(phase = SalesPhase.HandlingObjection) }
            }
            SalesPitchEvent.FinishSalesClicked -> {
                finishSales()
            }
        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/sales_${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()

                audioRecorder.startRecording(outputFile.absolutePath)
                recordingPath = outputFile.absolutePath

                _state.update {
                    it.copy(
                        isRecording = true,
                        recordingSeconds = 0
                    )
                }

                // Timer
                recordingTimerJob = launch {
                    var elapsed = 0
                    while (elapsed < _state.value.maxRecordingSeconds && _state.value.isRecording) {
                        delay(1000)
                        elapsed++
                        _state.update { it.copy(recordingSeconds = elapsed) }
                    }

                    if (elapsed >= _state.value.maxRecordingSeconds) {
                        stopRecording()
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка запису: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }

    private fun stopRecording() {
        viewModelScope.launch {
            try {
                recordingTimerJob?.cancel()
                audioRecorder.stopRecording()
                _state.update { it.copy(isRecording = false) }

                // Simulate transcription
                delay(1500)
                val mockTranscription = "[Презентація продавця - транскрипція буде доступна в Phase 8]"

                handleTranscribedPitch(mockTranscription)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка зупинки: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }

    private fun handleTranscribedPitch(transcription: String) {
        viewModelScope.launch {
            val currentPhase = _state.value.phase

            when (currentPhase) {
                SalesPhase.OpeningPitch -> {
                    // Save user pitch
                    _state.update {
                        it.copy(
                            userPitch = transcription,
                            isAiThinking = true,
                            phase = SalesPhase.CustomerReaction
                        )
                    }

                    // Generate customer reaction
                    generateCustomerResponse(transcription, SalesStage.INITIAL_PITCH)
                }
                SalesPhase.HandlingObjection -> {
                    // Save user objection response
                    _state.update {
                        it.copy(
                            userObjectionResponse = transcription,
                            isAiThinking = true,
                            phase = SalesPhase.FinalDecision
                        )
                    }

                    // Generate final decision
                    generateCustomerResponse(transcription, SalesStage.HANDLING_OBJECTION)
                }
                else -> {}
            }

            // Save recording
            saveRecording(transcription)
        }
    }

    private suspend fun generateCustomerResponse(userMessage: String, stage: SalesStage) {
        try {
            val product = _state.value.selectedProduct?.name ?: ""
            val customerType = _state.value.customerProfile?.type ?: ""

            val result = geminiApiClient.generateSalesResponse(
                product = product,
                customerType = customerType,
                userPitch = userMessage,
                interactionStage = stage
            )

            result.onSuccess { aiResponse ->
                when (stage) {
                    SalesStage.INITIAL_PITCH -> {
                        _state.update {
                            it.copy(
                                customerResponse = aiResponse,
                                isAiThinking = false
                            )
                        }
                    }
                    SalesStage.HANDLING_OBJECTION -> {
                        _state.update {
                            it.copy(
                                finalDecision = aiResponse,
                                isAiThinking = false
                            )
                        }
                    }
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        error = "Помилка AI: ${error.message}",
                        isAiThinking = false
                    )
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    error = "Помилка: ${e.message}",
                    isAiThinking = false
                )
            }
        }
    }

    private suspend fun saveRecording(transcription: String) {
        try {
            val path = recordingPath ?: return
            val productId = _state.value.selectedProduct?.id ?: ""

            val recordingEntity = RecordingEntity(
                id = UUID.randomUUID().toString(),
                filePath = path,
                durationMs = _state.value.recordingSeconds * 1000L,
                type = "improvisation",
                contextId = "sales_$productId",
                transcription = transcription,
                isAnalyzed = false
            )

            recordingDao.insert(recordingEntity)
        } catch (e: Exception) {
            // Log error
        }
    }

    private fun finishSales() {
        viewModelScope.launch {
            try {
                userPreferencesDataStore.incrementFreeImprovisations()
                _state.update { it.copy(phase = SalesPhase.SalesComplete) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Помилка завершення: ${e.message}") }
            }
        }
    }
}
