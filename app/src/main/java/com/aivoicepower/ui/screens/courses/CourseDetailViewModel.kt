package com.aivoicepower.ui.screens.courses

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
 * ViewModel for Course Detail
 */
@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""

    private val _state = MutableStateFlow(CourseDetailState())
    val state: StateFlow<CourseDetailState> = _state.asStateFlow()

    init {
        Log.d("CourseDetail", "Loading course: $courseId")
        loadCourse()
    }

    private fun loadCourse() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val course = courseRepository.getCourseById(courseId)

                Log.d("CourseDetail", "Course loaded: ${course?.title ?: "null"}")

                if (course == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Курс не знайдено"
                    )
                    return@launch
                }

                // Convert lessons to LessonWithProgress
                val lessonsWithProgress = course.lessons.mapIndexed { index, lesson ->
                    LessonWithProgress(
                        lesson = lesson,
                        isCompleted = false, // TODO: Get from CourseProgressDao
                        isLocked = false, // TEMP: All lessons unlocked for testing
                        weekNumber = (index / 7) + 1
                    )
                }

                _state.value = _state.value.copy(
                    course = course,
                    lessonsWithProgress = lessonsWithProgress,
                    totalLessons = course.totalLessons,
                    completedLessons = course.completedLessons,
                    progressPercent = if (course.totalLessons > 0) {
                        (course.completedLessons * 100) / course.totalLessons
                    } else 0,
                    isPremium = course.isPremium,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("CourseDetail", "Error loading course", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Помилка завантаження курсу: ${e.message}"
                )
            }
        }
    }

    fun onEvent(event: CourseDetailEvent) {
        // TODO: Handle events
    }
}
