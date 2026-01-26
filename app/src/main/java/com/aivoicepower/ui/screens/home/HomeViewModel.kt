package com.aivoicepower.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.*
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.home.*
import com.aivoicepower.ui.navigation.Screen
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
        observeCourseProgress()
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

                // Get quick actions
                val quickActions = getQuickActions()

                // Don't load currentCourse here - it will be loaded by observeCourseProgress()

                _state.update {
                    it.copy(
                        userName = null, // TODO: Add name field to UserPreferences if needed
                        currentStreak = preferences.currentStreak,
                        greeting = greeting,
                        todayPlan = todayPlan,
                        weekProgress = weekProgress,
                        quickActions = quickActions,
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
                    navigationRoute = Screen.WarmupQuick.route
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
                    navigationRoute = Screen.WarmupQuick.route
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
                navigationRoute = Screen.Lesson.createRoute(recommendedCourse, "lesson_$nextLesson")
            )
        )

        // 3. Improvisation or AI Coach
        // Suggest improvisation practice
        activities.add(
            PlanActivity(
                id = "improvisation",
                type = ActivityType.IMPROVISATION,
                title = "Імпровізація",
                subtitle = "Спонтанне мовлення",
                estimatedMinutes = 5,
                isCompleted = false,
                navigationRoute = Screen.RandomTopic.route
            )
        )

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

    private fun getQuickActions(): List<com.aivoicepower.domain.model.home.QuickAction> {
        return listOf(
            com.aivoicepower.domain.model.home.QuickAction(
                id = "quick_warmup",
                title = "Швидка розминка",
                icon = "⚡",
                route = Screen.WarmupQuick.route
            ),
            com.aivoicepower.domain.model.home.QuickAction(
                id = "random_topic",
                title = "Випадкова тема",
                icon = "🎲",
                route = Screen.RandomTopic.route
            ),
            com.aivoicepower.domain.model.home.QuickAction(
                id = "ai_coach",
                title = "AI Тренер",
                icon = "🤖",
                route = Screen.AiCoach.route
            ),
            com.aivoicepower.domain.model.home.QuickAction(
                id = "tongue_twisters",
                title = "Скоромовки",
                icon = "👅",
                route = Screen.Courses.route  // Поки що веде на курси
            )
        )
    }

    private suspend fun getCurrentCourse(preferences: com.aivoicepower.data.local.datastore.UserPreferences): CurrentCourse? {
        // Список всіх курсів по порядку
        val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6")

        // Знайти курс з найбільш недавньою активністю (останній завершений урок)
        var mostRecentCourse: String? = null
        var mostRecentTimestamp = 0L

        for (courseId in allCourses) {
            val progress = courseProgressDao.getCourseProgress(courseId).first()
            val latestLesson = progress.filter { it.isCompleted }.maxByOrNull { it.completedAt ?: 0 }
            if (latestLesson != null && (latestLesson.completedAt ?: 0) > mostRecentTimestamp) {
                mostRecentTimestamp = latestLesson.completedAt ?: 0
                mostRecentCourse = courseId
            }
        }

        // Якщо є активний курс - перевірити, чи є в ньому незавершені уроки
        if (mostRecentCourse != null) {
            val courseProgress = courseProgressDao.getCourseProgress(mostRecentCourse).first()

            // Знайти останній виконаний урок
            val lastCompletedLesson = courseProgress
                .filter { it.isCompleted }
                .mapNotNull {
                    val lessonNumber = it.lessonId.removePrefix("lesson_").toIntOrNull()
                    lessonNumber
                }
                .maxOrNull()

            // Наступний урок = останній виконаний + 1 (або 1, якщо немає виконаних)
            val nextLessonNumber = if (lastCompletedLesson != null) {
                lastCompletedLesson + 1
            } else {
                1
            }

            // Перевірити, чи не виходить за межі курсу
            if (nextLessonNumber <= 21) {
                // Є незавершений урок - показати його
                val (courseName, courseColor, courseIcon) = getCourseData(mostRecentCourse)
                return CurrentCourse(
                    courseId = mostRecentCourse,
                    courseName = courseName,
                    nextLessonNumber = nextLessonNumber,
                    totalLessons = 21,
                    color = courseColor,
                    icon = courseIcon,
                    navigationRoute = Screen.Lesson.createRoute(mostRecentCourse, "lesson_$nextLessonNumber")
                )
            }

            // Курс завершено - знайти наступний курс зі списку
            val currentIndex = allCourses.indexOf(mostRecentCourse)
            if (currentIndex < allCourses.size - 1) {
                val nextCourse = allCourses[currentIndex + 1]
                val (courseName, courseColor, courseIcon) = getCourseData(nextCourse)
                return CurrentCourse(
                    courseId = nextCourse,
                    courseName = courseName,
                    nextLessonNumber = 1,
                    totalLessons = 21,
                    color = courseColor,
                    icon = courseIcon,
                    navigationRoute = Screen.Lesson.createRoute(nextCourse, "lesson_1")
                )
            }
        }

        // Якщо немає активного курсу - рекомендувати курс на основі цілі
        val recommendedCourse = when (preferences.userGoal) {
            "CLEAR_SPEECH" -> "course_1"
            "PUBLIC_SPEAKING" -> "course_3"
            "BETTER_VOICE" -> "course_2"
            else -> "course_1"
        }

        val (courseName, courseColor, courseIcon) = getCourseData(recommendedCourse)
        return CurrentCourse(
            courseId = recommendedCourse,
            courseName = courseName,
            nextLessonNumber = 1,
            totalLessons = 21,
            color = courseColor,
            icon = courseIcon,
            navigationRoute = Screen.Lesson.createRoute(recommendedCourse, "lesson_1")
        )
    }

    private fun getGreetingByTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 6..11 -> "Доброго ранку! ☀️"
            hour in 12..17 -> "Добрий день! 👋"
            hour in 18..21 -> "Добрий вечір! 🌅"
            else -> "Доброї ночі! 🌙"
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

    private fun getCourseData(courseId: String): Triple<String, String, String> {
        return when (courseId) {
            "course_1" -> Triple("Чітке мовлення", "#667EEA", "🗣️")
            "course_2" -> Triple("Магія інтонації", "#EC4899", "🎭")
            "course_3" -> Triple("Впевнений спікер", "#F59E0B", "💼")
            "course_4" -> Triple("Чисте мовлення", "#8B5CF6", "✨")
            "course_5" -> Triple("Ділова комунікація", "#10B981", "📊")
            "course_6" -> Triple("Харизматичний оратор", "#EF4444", "🎤")
            else -> Triple("Курс", "#667EEA", "📖")
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun observeCourseProgress() {
        viewModelScope.launch {
            // Просто підписуємося на preferences і перезавантажуємо currentCourse
            // при будь-якій зміні (getCurrentCourse() сам завантажить актуальні дані з DAO)
            userPreferencesDataStore.userPreferencesFlow.collect { preferences ->
                val updatedCourse = getCurrentCourse(preferences)
                _state.update { it.copy(currentCourse = updatedCourse) }
            }
        }

        // Також створюємо окремі підписки на зміни прогресу кожного курсу
        viewModelScope.launch {
            val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6")
            allCourses.forEach { courseId ->
                launch {
                    courseProgressDao.getCourseProgress(courseId).collect {
                        // При зміні будь-якого курсу - перезавантажуємо currentCourse
                        val preferences = userPreferencesDataStore.userPreferencesFlow.first()
                        val updatedCourse = getCurrentCourse(preferences)
                        _state.update { it.copy(currentCourse = updatedCourse) }
                    }
                }
            }
        }
    }
}
