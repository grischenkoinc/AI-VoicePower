package com.aivoicepower.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stub ViewModel for Courses List
 * TODO: Implement fully in Phase 3-4
 */
@HiltViewModel
class CoursesListViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CoursesListState())
    val state: StateFlow<CoursesListState> = _state.asStateFlow()

    init {
        loadCourses()
    }

    fun onEvent(event: CoursesListEvent) {
        when (event) {
            CoursesListEvent.Refresh -> loadCourses()
            is CoursesListEvent.SearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = event.query)
            }
            is CoursesListEvent.CourseClicked -> {
                // TODO: Navigate to course detail
            }
        }
    }

    private fun loadCourses() {
        viewModelScope.launch {
            courseRepository.getAllCourses().collect { courses ->
                // Convert Course to CourseWithProgress
                val coursesWithProgress = courses.map { course ->
                    CourseWithProgress(
                        course = course,
                        completedLessons = course.completedLessons,
                        totalLessons = course.totalLessons,
                        progressPercent = if (course.totalLessons > 0) {
                            (course.completedLessons * 100) / course.totalLessons
                        } else 0
                    )
                }
                _state.value = _state.value.copy(
                    courses = coursesWithProgress,
                    isLoading = false
                )
            }
        }
    }
}
