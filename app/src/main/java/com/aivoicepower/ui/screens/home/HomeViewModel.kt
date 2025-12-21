package com.aivoicepower.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.*
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.home.*
import com.aivoicepower.ui.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val userProgressDao: UserProgressDao,
    private val diagnosticResultDao: DiagnosticResultDao,
    private val warmupCompletionDao: WarmupCompletionDao,
    private val courseProgressDao: CourseProgressDao
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> {
                loadHomeData()
            }
            is HomeEvent.ActivityClicked -> {
                // Navigation handled in Screen
            }
            HomeEvent.WarmupClicked,
            HomeEvent.CoursesClicked,
            HomeEvent.ImprovisationClicked,
            HomeEvent.ProgressClicked,
            HomeEvent.AiCoachClicked -> {
                // Navigation handled in Screen
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Load user data
                val preferences = userPreferencesDataStore.userPreferencesFlow.first()
                val progress = userProgressDao.getUserProgressOnce()

                // Load personalized plan
                val todayPlan = generateTodayPlan(preferences, progress)

                // Load week progress
                val weekProgress = loadWeekProgress()

                // Determine greeting
                val greeting = getGreetingByTime()

                _state.update {
                    it.copy(
                        userName = null, // TODO: Add name field to UserPreferences if needed
                        currentStreak = preferences.currentStreak,
                        greeting = greeting,
                        todayPlan = todayPlan,
                        weekProgress = weekProgress,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити дані"
                    )
                }
            }
        }
    }

    private suspend fun generateTodayPlan(
        preferences: com.aivoicepower.data.local.datastore.UserPreferences,
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity?
    ): TodayPlan {
        val today = getCurrentDateString()
        val activities = mutableListOf<PlanActivity>()

        // 1. Check warmup completion
        val warmupCompletion = warmupCompletionDao.getCompletion(today, "quick")
        if (warmupCompletion == null) {
            activities.add(
                PlanActivity(
                    id = "warmup_quick",
                    type = ActivityType.WARMUP,
                    title = "Швидка розминка",
                    subtitle = "Почни день правильно",
                    estimatedMinutes = 5,
                    isCompleted = false,
                    navigationRoute = NavRoutes.QuickWarmup.route
                )
            )
        } else {
            activities.add(
                PlanActivity(
                    id = "warmup_quick",
                    type = ActivityType.WARMUP,
                    title = "Швидка розминка",
                    subtitle = "Виконано сьогодні",
                    estimatedMinutes = 5,
                    isCompleted = true,
                    navigationRoute = NavRoutes.QuickWarmup.route
                )
            )
        }

        // 2. Recommend course lesson based on goal
        val recommendedCourse = when (preferences.userGoal) {
            "CLEAR_SPEECH" -> "course_1" // Чітке мовлення
            "PUBLIC_SPEAKING" -> "course_3" // Впевнений спікер
            "BETTER_VOICE" -> "course_2" // Магія інтонації
            else -> "course_1"
        }

        // Find next incomplete lesson
        val courseProgress = courseProgressDao.getCourseProgress(recommendedCourse).first()
        val nextLesson = (1..21).firstOrNull { lessonNumber ->
            val lessonId = "lesson_$lessonNumber"
            courseProgress.none { it.lessonId == lessonId && it.isCompleted }
        } ?: 1

        activities.add(
            PlanActivity(
                id = "lesson_${recommendedCourse}_$nextLesson",
                type = ActivityType.LESSON,
                title = "Урок $nextLesson: ${getCourseName(recommendedCourse)}",
                subtitle = "Курс \"${getCourseName(recommendedCourse)}\"",
                estimatedMinutes = 15,
                isCompleted = false,
                navigationRoute = NavRoutes.Lesson.createRoute(recommendedCourse, "lesson_$nextLesson")
            )
        )

        // 3. Improvisation or AI Coach
        if (preferences.todayExercises >= 2) {
            // Already did some work, suggest relaxed activity
            activities.add(
                PlanActivity(
                    id = "ai_coach",
                    type = ActivityType.AI_COACH,
                    title = "Попрактикуйся з AI",
                    subtitle = "Обговори свій прогрес",
                    estimatedMinutes = 10,
                    isCompleted = false,
                    navigationRoute = NavRoutes.AiCoach.route
                )
            )
        } else {
            activities.add(
                PlanActivity(
                    id = "improvisation",
                    type = ActivityType.IMPROVISATION,
                    title = "Імпровізація",
                    subtitle = "Спонтанне мовлення",
                    estimatedMinutes = 5,
                    isCompleted = false,
                    navigationRoute = NavRoutes.RandomTopic.route
                )
            )
        }

        // 4. Recommendation based on weakest skill
        val recommendedFocus = getRecommendedFocus(progress, preferences)

        return TodayPlan(
            activities = activities,
            recommendedFocus = recommendedFocus
        )
    }

    private fun getRecommendedFocus(
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity?,
        preferences: com.aivoicepower.data.local.datastore.UserPreferences
    ): String {
        if (progress == null) return "Почни з діагностики, щоб визначити свій рівень"

        // Find weakest skill
        val skills = mapOf(
            "дикцією" to progress.dictionLevel,
            "темпом мовлення" to progress.tempoLevel,
            "інтонацією" to progress.intonationLevel,
            "структурою мовлення" to progress.structureLevel,
            "впевненістю" to progress.confidenceLevel
        )

        val weakest = skills.minByOrNull { it.value }

        return if (weakest != null && weakest.value < 60) {
            "Сьогодні попрацюй над ${weakest.key}"
        } else {
            "Чудовий прогрес! Продовжуй в тому ж дусі"
        }
    }

    private suspend fun loadWeekProgress(): WeekProgress {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        // Get start of week (Monday)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = mutableListOf<DayProgress>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale("uk", "UA"))

        for (i in 0..6) {
            val date = calendar.time
            val dateString = dateFormat.format(date)
            val dayName = dayFormat.format(date).take(2).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            // Load activity for this day
            val prefs = userPreferencesDataStore.userPreferencesFlow.first()
            val minutes = if (dateString == getCurrentDateString()) {
                prefs.todayMinutes
            } else {
                // TODO: Load from historical data (not implemented yet)
                0
            }

            days.add(
                DayProgress(
                    dayName = dayName,
                    date = dateString,
                    minutes = minutes,
                    isCompleted = minutes > 0
                )
            )

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return WeekProgress(days = days)
    }

    private fun getGreetingByTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Доброго ранку"
            in 12..17 -> "Доброго дня"
            in 18..22 -> "Доброго вечора"
            else -> "Доброї ночі"
        }
    }

    private fun getCourseName(courseId: String): String {
        return when (courseId) {
            "course_1" -> "Чітке мовлення"
            "course_2" -> "Магія інтонації"
            "course_3" -> "Впевнений спікер"
            "course_4" -> "Чисте мовлення"
            "course_5" -> "Ділова комунікація"
            "course_6" -> "Харизматичний оратор"
            else -> "Курс"
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
