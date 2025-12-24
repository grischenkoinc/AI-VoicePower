package com.aivoicepower.ui.screens.courses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aivoicepower.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Stub ViewModel for Course Detail
 * TODO: Implement fully in Phase 3-4
 */
@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""

    private val _state = MutableStateFlow(CourseDetailState())
    val state: StateFlow<CourseDetailState> = _state.asStateFlow()

    fun onEvent(event: CourseDetailEvent) {
        // Stub - no-op
    }
}
