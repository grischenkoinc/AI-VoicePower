package com.aivoicepower.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            is CoursesListEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchCourses(event.query)
            }
            is CoursesListEvent.CourseClicked -> {
                // Navigation handled in Screen
            }
            CoursesListEvent.Refresh -> {
                loadCourses()
            }
        }
    }

    private fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                courseRepository.getAllCourses()
                    .combine(getCourseProgressFlow()) { courses, progressMap ->
                        courses.map { course ->
                            val completedCount = progressMap[course.id] ?: 0
                            CourseWithProgress(
                                course = course,
                                completedLessons = completedCount,
                                totalLessons = course.totalLessons,
                                progressPercent = (completedCount * 100) / course.totalLessons
                            )
                        }
                    }
                    .collect { coursesWithProgress ->
                        _state.update {
                            it.copy(
                                courses = coursesWithProgress,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити курси"
                    )
                }
            }
        }
    }

    private fun searchCourses(query: String) {
        if (query.isBlank()) {
            loadCourses()
            return
        }

        viewModelScope.launch {
            try {
                courseRepository.searchCourses(query)
                    .combine(getCourseProgressFlow()) { courses, progressMap ->
                        courses.map { course ->
                            val completedCount = progressMap[course.id] ?: 0
                            CourseWithProgress(
                                course = course,
                                completedLessons = completedCount,
                                totalLessons = course.totalLessons,
                                progressPercent = (completedCount * 100) / course.totalLessons
                            )
                        }
                    }
                    .collect { coursesWithProgress ->
                        _state.update {
                            it.copy(courses = coursesWithProgress)
                        }
                    }
            } catch (e: Exception) {
                // Ignore search errors
            }
        }
    }

    private fun getCourseProgressFlow(): Flow<Map<String, Int>> {
        return flow {
            val allCourseIds = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6")

            val progressMap = allCourseIds.associateWith { courseId ->
                courseProgressDao.getCompletedLessonsCount(courseId).first()
            }

            emit(progressMap)
        }
    }
}
