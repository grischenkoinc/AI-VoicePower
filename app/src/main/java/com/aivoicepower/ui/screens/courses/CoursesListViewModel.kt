package com.aivoicepower.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stub ViewModel for Courses List
 * TODO: Implement fully in Phase 3-4
 */
@HiltViewModel
class CoursesListViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val courseProgressDao: CourseProgressDao
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
                // Підписуємося на зміни прогресу всіх курсів
                combine(
                    courses.map { course ->
                        courseProgressDao.getCourseProgress(course.id).map { progressList ->
                            val completedCount = progressList.count { it.isCompleted }
                            CourseWithProgress(
                                course = course,
                                completedLessons = completedCount,
                                totalLessons = course.totalLessons,
                                progressPercent = if (course.totalLessons > 0) {
                                    (completedCount * 100) / course.totalLessons
                                } else 0
                            )
                        }
                    }
                ) { coursesArray ->
                    coursesArray.toList()
                }.collect { coursesWithProgress ->
                    _state.value = _state.value.copy(
                        courses = coursesWithProgress,
                        isLoading = false
                    )
                }
            }
        }
    }
}
