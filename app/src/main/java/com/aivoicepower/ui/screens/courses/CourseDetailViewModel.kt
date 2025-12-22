package com.aivoicepower.ui.screens.courses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository,
    private val courseProgressDao: CourseProgressDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _state = MutableStateFlow(CourseDetailState())
    val state: StateFlow<CourseDetailState> = _state.asStateFlow()

    init {
        loadCourseDetail()
    }

    fun onEvent(event: CourseDetailEvent) {
        when (event) {
            is CourseDetailEvent.LessonClicked -> {
                // Navigation handled in Screen
            }
            CourseDetailEvent.UpgradeToPremiumClicked -> {
                // Navigation to Premium screen handled in Screen
            }
            CourseDetailEvent.Refresh -> {
                loadCourseDetail()
            }
        }
    }

    private fun loadCourseDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                courseRepository.getCourseById(courseId)
                    .combine(userPreferencesDataStore.isPremium) { course, isPremium ->
                        course to isPremium
                    }
                    .combine(getLessonProgressFlow()) { (course, isPremium), lessonProgressMap ->
                        Triple(course, isPremium, lessonProgressMap)
                    }
                    .collect { (course, isPremium, lessonProgressMap) ->
                        if (course == null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Курс не знайдено"
                                )
                            }
                            return@collect
                        }

                        val lessonsWithProgress = course.lessons.map { lesson ->
                            val isCompleted = lessonProgressMap[lesson.id] ?: false
                            val isLocked = lesson.dayNumber > 7 && !isPremium
                            val weekNumber = ((lesson.dayNumber - 1) / 7) + 1

                            LessonWithProgress(
                                lesson = lesson,
                                isCompleted = isCompleted,
                                isLocked = isLocked,
                                weekNumber = weekNumber
                            )
                        }

                        val completedCount = lessonProgressMap.count { it.value }

                        _state.update {
                            it.copy(
                                course = course,
                                lessonsWithProgress = lessonsWithProgress,
                                completedLessons = completedCount,
                                totalLessons = course.totalLessons,
                                progressPercent = (completedCount * 100) / course.totalLessons,
                                isPremium = isPremium,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити курс"
                    )
                }
            }
        }
    }

    private fun getLessonProgressFlow(): Flow<Map<String, Boolean>> {
        return courseProgressDao.getCourseProgress(courseId).map { progressList ->
            progressList.associate { it.lessonId to it.isCompleted }
        }
    }
}
