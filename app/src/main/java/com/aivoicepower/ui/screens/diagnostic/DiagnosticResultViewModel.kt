package com.aivoicepower.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.ui.navigation.DiagnosticDataHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DiagnosticResultUiState {
    object Loading : DiagnosticResultUiState()
    data class Success(val result: DiagnosticResult) : DiagnosticResultUiState()
    data class Error(val message: String) : DiagnosticResultUiState()
}

@HiltViewModel
class DiagnosticResultViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<DiagnosticResultUiState>(DiagnosticResultUiState.Loading)
    val uiState: StateFlow<DiagnosticResultUiState> = _uiState.asStateFlow()

    init {
        loadResults()
    }

    fun loadResults() {
        viewModelScope.launch {
            _uiState.value = DiagnosticResultUiState.Loading

            try {
                // Simulate loading delay
                delay(500)

                // Get result from DiagnosticDataHolder
                val result = DiagnosticDataHolder.result
                if (result != null) {
                    _uiState.value = DiagnosticResultUiState.Success(result)
                } else {
                    // Fallback to mock data
                    _uiState.value = DiagnosticResultUiState.Success(DiagnosticResult.mock())
                }
            } catch (e: Exception) {
                _uiState.value = DiagnosticResultUiState.Error(
                    e.message ?: "Помилка завантаження результатів"
                )
            }
        }
    }
}
