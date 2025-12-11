package com.aivoicepower.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.Lesson
import com.aivoicepower.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val lessonRepository: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            try {
                lessonRepository.getAllLessons().collect { lessons ->
                    _uiState.value = HomeUiState.Success(lessons)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Помилка завантаження уроків")
            }
        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val lessons: List<Lesson>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
