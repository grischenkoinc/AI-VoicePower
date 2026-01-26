package com.aivoicepower.ui.screens.courses

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Course Detail
 */
@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository,
    private val courseProgressDao: CourseProgressDao
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

                // Load progress from database
                courseProgressDao.getCourseProgress(courseId).collect { progressList ->
                    val progressMap = progressList.associateBy { it.lessonId }

                    // Convert lessons to LessonWithProgress
                    val lessonsWithProgress = course.lessons.mapIndexed { index, lesson ->
                        val progress = progressMap[lesson.id]
                        LessonWithProgress(
                            lesson = lesson,
                            isCompleted = progress?.isCompleted ?: false,
                            isLocked = false, // TEMP: All lessons unlocked for testing
                            weekNumber = (index / 7) + 1
                        )
                    }

                    val completedCount = lessonsWithProgress.count { it.isCompleted }

                    _state.value = _state.value.copy(
                        course = course,
                        lessonsWithProgress = lessonsWithProgress,
                        totalLessons = course.totalLessons,
                        completedLessons = completedCount,
                        progressPercent = if (course.totalLessons > 0) {
                            (completedCount * 100) / course.totalLessons
                        } else 0,
                        isPremium = course.isPremium,
                        isLoading = false
                    )
                }
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
