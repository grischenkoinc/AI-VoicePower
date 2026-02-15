package com.aivoicepower.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.*
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.home.*
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.ui.navigation.Screen
import com.aivoicepower.utils.PremiumChecker
import com.aivoicepower.utils.SkillLevelUtils
import com.aivoicepower.utils.constants.FreeTierLimits
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
    private val courseProgressDao: CourseProgressDao,
    private val courseRepository: CourseRepository,
    private val dailyTipsRepository: com.aivoicepower.data.repository.DailyTipsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
        observeSkills()
        observeCourseProgress()
        observeDailyPlan()
        observeAnalysisLimits()
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

                // Load daily tip
                val dailyTip = getDailyTip()

                // Don't load currentCourse here - it will be loaded by observeCourseProgress()

                val coachMessage = generateCoachMessage(progress, preferences)

                _state.update {
                    it.copy(
                        userName = preferences.userName,
                        currentStreak = preferences.currentStreak,
                        greeting = greeting,
                        todayPlan = todayPlan,
                        weekProgress = weekProgress,
                        quickActions = quickActions,
                        dailyTip = dailyTip,
                        coachMessage = coachMessage,
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
            "CLEAR_SPEECH" -> "course_1" // Чітке мовлення (завжди доступний)
            "PUBLIC_SPEAKING" -> if (preferences.isPremium) "course_3" else "course_1"
            "BETTER_VOICE" -> if (preferences.isPremium) "course_2" else "course_1"
            else -> "course_1"
        }

        // Find next incomplete lesson
        val courseProgress = courseProgressDao.getCourseProgress(recommendedCourse).first()
        var nextLessonNumber = 1
        var nextLessonId = getLessonIdFormat(recommendedCourse, nextLessonNumber)
        var isLessonCompleted = false

        // Спочатку шукаємо останній урок завершений сьогодні
        var lastLessonCompletedToday: Pair<Int, String>? = null

        for (lessonNumber in 1..21) {
            val lessonId = getLessonIdFormat(recommendedCourse, lessonNumber)
            val lessonProgress = courseProgress.find { it.lessonId == lessonId }

            if (lessonProgress != null && lessonProgress.isCompleted) {
                // Перевіряємо чи урок був завершений сьогодні
                val completedToday = lessonProgress.completedAt?.let { completedTime ->
                    val completedDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Date(completedTime))
                    completedDate == today
                } ?: false

                if (completedToday) {
                    lastLessonCompletedToday = Pair(lessonNumber, lessonId)
                }
            }
        }

        // Якщо є урок завершений сьогодні - показуємо його
        if (lastLessonCompletedToday != null) {
            nextLessonNumber = lastLessonCompletedToday.first
            nextLessonId = lastLessonCompletedToday.second
            isLessonCompleted = true
            android.util.Log.d("HomeViewModel", "Found lesson completed today: $nextLessonNumber")
        } else {
            // Інакше шукаємо перший незавершений урок, доступний для користувача
            for (lessonNumber in 1..21) {
                val lessonId = getLessonIdFormat(recommendedCourse, lessonNumber)
                val lessonProgress = courseProgress.find { it.lessonId == lessonId }

                // Перевіряємо чи урок доступний для Free користувача
                val lessonIndex = lessonNumber - 1 // 0-based index
                val canAccess = com.aivoicepower.utils.PremiumChecker.canAccessLesson(
                    isPremium = preferences.isPremium,
                    lessonIndex = lessonIndex
                )

                if (!canAccess) {
                    android.util.Log.d("HomeViewModel", "Lesson $lessonNumber not accessible for Free user, skipping")
                    continue
                }

                if (lessonProgress == null || !lessonProgress.isCompleted) {
                    nextLessonNumber = lessonNumber
                    nextLessonId = lessonId
                    isLessonCompleted = false
                    android.util.Log.d("HomeViewModel", "Found next incomplete lesson: $nextLessonNumber")
                    break
                }
            }
        }

        val lessonTitle = try {
            val lesson = courseRepository.getLessonById(recommendedCourse, nextLessonId).first()
            lesson?.title ?: "Урок $nextLessonNumber"
        } catch (e: Exception) {
            "Урок $nextLessonNumber"
        }

        activities.add(
            PlanActivity(
                id = "lesson_${recommendedCourse}_$nextLessonNumber",
                type = ActivityType.LESSON,
                title = lessonTitle,
                subtitle = getCourseName(recommendedCourse),
                estimatedMinutes = 15,
                isCompleted = isLessonCompleted,
                navigationRoute = Screen.Lesson.createRoute(recommendedCourse, nextLessonId)
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

    @Suppress("UNUSED_PARAMETER")
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

    private fun generateCoachMessage(
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity?,
        preferences: com.aivoicepower.data.local.datastore.UserPreferences
    ): String {
        // First time — coach introduces himself
        if (progress == null || progress.totalExercises == 0) {
            val name = if (preferences.userName != null) "${preferences.userName}, привіт" else "Привіт"
            return "$name! Я — твій AI-тренер з мовлення. Аналізую твої вправи, відстежую прогрес і підказую, що покращити. Почнімо з розминки або першого уроку!"
        }

        val streak = progress.currentStreak
        val skills = mapOf(
            "дикцією" to progress.dictionLevel,
            "темпом" to progress.tempoLevel,
            "інтонацією" to progress.intonationLevel,
            "впевненістю" to progress.confidenceLevel,
            "структурою" to progress.structureLevel
        )
        val weakest = skills.minByOrNull { it.value }
        val allAbove70 = skills.values.all { it >= 70f }

        return when {
            // Streak broken
            streak == 0 && progress.totalExercises > 5 ->
                "Повернись до тренувань! Навіть 5 хвилин на день зберігають прогрес. Ти це можеш!"

            // Great streak
            streak >= 7 ->
                "Вау, $streak днів поспіль! Це серйозна дисципліна. Продовжуй — результат вже помітний."

            // Active streak
            streak >= 3 ->
                "Чудова серія — $streak днів! Тримай темп, і навички закріпляться надовго."

            // All skills high
            allAbove70 ->
                "Відмінний рівень! Спробуй складніші вправи — імпровізації та дебати покажуть реальну силу."

            // Weakest skill below 40
            weakest != null && weakest.value < 40f ->
                "Зверни увагу на роботу з ${weakest.key} — це твоя головна зона росту зараз."

            // Default — encouraging
            else ->
                "Ти робиш прогрес! Продовжуй тренуватись щодня — кожна вправа наближає до мети."
        }
    }

    private suspend fun loadWeekProgress(): WeekProgress {
        val calendar = Calendar.getInstance()

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
                id = "tongue_twisters",
                title = "Скоромовки",
                icon = "👅",
                route = Screen.TongueTwisters.route
            ),
            com.aivoicepower.domain.model.home.QuickAction(
                id = "weakest_skill",
                title = "Найслабша навичка",
                icon = "🎯",
                route = Screen.WeakestSkill.route
            ),
            com.aivoicepower.domain.model.home.QuickAction(
                id = "quick_warmup",
                title = "Швидка розминка",
                icon = "⚡",
                route = Screen.WarmupQuick.route
            ),
            com.aivoicepower.domain.model.home.QuickAction(
                id = "recording_history",
                title = "Історія записів",
                icon = "📊",
                route = Screen.RecordingHistory.route
            )
        )
    }

    private fun observeSkills() {
        viewModelScope.launch {
            userProgressDao.getProgressFlow().collect { progress ->
                val skills = buildSkillsList(progress)
                _state.update { it.copy(skills = skills) }
            }
        }
    }

    private fun buildSkillsList(
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity?
    ): List<com.aivoicepower.domain.model.home.Skill> {
        val dictionLevel = progress?.dictionLevel?.toInt() ?: 1
        val tempoLevel = progress?.tempoLevel?.toInt() ?: 1
        val intonationLevel = progress?.intonationLevel?.toInt() ?: 1

        return listOf(
            com.aivoicepower.domain.model.home.Skill(
                id = "diction",
                name = "Дикція",
                emoji = "📢",
                percentage = dictionLevel,
                growth = calculateGrowth(dictionLevel, progress?.lastDictionLevel?.toInt()),
                gradientColors = listOf("#6366F1", "#8B5CF6"),
                statusLabel = SkillLevelUtils.getSkillLabel(dictionLevel)
            ),
            com.aivoicepower.domain.model.home.Skill(
                id = "tempo",
                name = "Темп",
                emoji = "⚡",
                percentage = tempoLevel,
                growth = calculateGrowth(tempoLevel, progress?.lastTempoLevel?.toInt()),
                gradientColors = listOf("#EC4899", "#F43F5E"),
                statusLabel = SkillLevelUtils.getSkillLabel(tempoLevel)
            ),
            com.aivoicepower.domain.model.home.Skill(
                id = "intonation",
                name = "Емоції",
                emoji = "🎭",
                percentage = intonationLevel,
                growth = calculateGrowth(intonationLevel, progress?.lastIntonationLevel?.toInt()),
                gradientColors = listOf("#F59E0B", "#F97316"),
                statusLabel = SkillLevelUtils.getSkillLabel(intonationLevel)
            )
        )
    }

    private fun calculateGrowth(currentLevel: Int, previousLevel: Int?): String {
        // Якщо немає попереднього значення - використовуємо евристику
        if (previousLevel == null || previousLevel == 0) {
            val estimatedGrowth = when {
                currentLevel < 20 -> 2
                currentLevel < 40 -> 3
                currentLevel < 60 -> 4
                currentLevel < 80 -> 5
                else -> 3
            }
            return "+$estimatedGrowth%"
        }

        // Реальний розрахунок росту
        val growth = currentLevel - previousLevel
        return if (growth > 0) "+$growth%" else if (growth < 0) "$growth%" else "0%"
    }

    private suspend fun getDailyTip(): com.aivoicepower.domain.model.home.DailyTip {
        val preferences = userPreferencesDataStore.userPreferencesFlow.first()
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferences.lastTipUpdateTime
        val fourHoursInMillis = 4 * 60 * 60 * 1000L

        val isColdStart = dailyTipsRepository.isColdStart
        val timeExpired = currentTime - lastUpdateTime > fourHoursInMillis
        val noTipYet = preferences.currentTipId == null

        if (isColdStart || timeExpired || noTipYet) {
            dailyTipsRepository.markColdStartHandled()

            val newTip = dailyTipsRepository.getRandomTip(excludeId = preferences.currentTipId)
            userPreferencesDataStore.updateDailyTip(newTip.id, currentTime)
            return newTip.copy(date = getCurrentDateString())
        } else {
            val allTips = dailyTipsRepository.loadTips()
            val cachedTip = allTips.find { it.id == preferences.currentTipId }
                ?: dailyTipsRepository.getRandomTip()

            return cachedTip.copy(date = getCurrentDateString())
        }
    }

    private suspend fun getCurrentCourse(preferences: com.aivoicepower.data.local.datastore.UserPreferences): CurrentCourse? {
        // Список всіх курсів по порядку
        val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6", "course_7")

        // Знайти курс з найбільш недавньою активністю (останній завершений урок)
        var mostRecentCourse: String? = null
        var mostRecentTimestamp = 0L
        var mostRecentLessonId: String? = null

        for (courseId in allCourses) {
            val progress = courseProgressDao.getCourseProgress(courseId).first()
            val completedLessons = progress.filter { it.isCompleted }

            if (completedLessons.isNotEmpty()) {
                val latestLesson = completedLessons.maxByOrNull { it.completedAt ?: 0 }
                val timestamp = latestLesson?.completedAt ?: 0

                android.util.Log.d("HomeViewModel", "Course $courseId: latest lesson = ${latestLesson?.lessonId}, timestamp = $timestamp")

                if (timestamp > mostRecentTimestamp) {
                    mostRecentTimestamp = timestamp
                    mostRecentCourse = courseId
                    mostRecentLessonId = latestLesson?.lessonId
                }
            }
        }

        android.util.Log.d("HomeViewModel", "Most recent course: $mostRecentCourse, lesson: $mostRecentLessonId")

        // Якщо є активний курс - показати наступний урок після останнього виконаного
        if (mostRecentCourse != null && mostRecentLessonId != null) {
            // Витягнути номер останнього виконаного уроку з різних форматів:
            // lesson_1, voice_lesson_1, speaker_lesson_1, etc.
            val substringResult = mostRecentLessonId.substringAfterLast("_")
            android.util.Log.d("HomeViewModel", "Parsing lessonId: '$mostRecentLessonId' -> substring: '$substringResult'")

            val lastCompletedNumber = substringResult.toIntOrNull()
            android.util.Log.d("HomeViewModel", "Last completed number: $lastCompletedNumber")

            if (lastCompletedNumber != null) {
                val nextLessonNumber = lastCompletedNumber + 1

                android.util.Log.d("HomeViewModel", "Next lesson number: $nextLessonNumber")

                // Перевірити, чи не виходить за межі курсу
                if (nextLessonNumber <= 21) {
                    // Використовуємо функцію для визначення правильного формату lessonId
                    val nextLessonId = getLessonIdFormat(mostRecentCourse, nextLessonNumber)

                    android.util.Log.d("HomeViewModel", "Next lesson ID: $nextLessonId")

                    // Є незавершений урок - показати його
                    val (courseName, courseColor, courseIcon) = getCourseData(mostRecentCourse)
                    val lessonTitle = getLessonTitle(mostRecentCourse, nextLessonId)
                    android.util.Log.d("HomeViewModel", "Returning: $mostRecentCourse lesson $nextLessonNumber")
                    return CurrentCourse(
                        courseId = mostRecentCourse,
                        courseName = courseName,
                        nextLessonNumber = nextLessonNumber,
                        nextLessonId = nextLessonId,
                        nextLessonTitle = lessonTitle,
                        totalLessons = 21,
                        color = courseColor,
                        icon = courseIcon,
                        navigationRoute = Screen.Lesson.createRoute(mostRecentCourse, nextLessonId)
                    )
                }

                // Курс завершено - знайти наступний курс зі списку
                val currentIndex = allCourses.indexOf(mostRecentCourse)
                if (currentIndex < allCourses.size - 1) {
                    val nextCourse = allCourses[currentIndex + 1]
                    val nextLessonId = getLessonIdFormat(nextCourse, 1)
                    val (courseName, courseColor, courseIcon) = getCourseData(nextCourse)
                    val lessonTitle = getLessonTitle(nextCourse, nextLessonId)
                    return CurrentCourse(
                        courseId = nextCourse,
                        courseName = courseName,
                        nextLessonNumber = 1,
                        nextLessonId = nextLessonId,
                        nextLessonTitle = lessonTitle,
                        totalLessons = 21,
                        color = courseColor,
                        icon = courseIcon,
                        navigationRoute = Screen.Lesson.createRoute(nextCourse, nextLessonId)
                    )
                }
            }
        }

        // Якщо немає активного курсу - рекомендувати курс на основі цілі
        val recommendedCourse = when (preferences.userGoal) {
            "CLEAR_SPEECH" -> "course_1"
            "PUBLIC_SPEAKING" -> "course_3"
            "BETTER_VOICE" -> "course_2"
            else -> "course_1"
        }

        android.util.Log.d("HomeViewModel", "No recent course, returning recommended: $recommendedCourse")

        val firstLessonId = getLessonIdFormat(recommendedCourse, 1)
        val (courseName, courseColor, courseIcon) = getCourseData(recommendedCourse)
        val lessonTitle = getLessonTitle(recommendedCourse, firstLessonId)
        return CurrentCourse(
            courseId = recommendedCourse,
            courseName = courseName,
            nextLessonNumber = 1,
            nextLessonId = firstLessonId,
            nextLessonTitle = lessonTitle,
            totalLessons = 21,
            color = courseColor,
            icon = courseIcon,
            navigationRoute = Screen.Lesson.createRoute(recommendedCourse, firstLessonId)
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
            "course_3" -> "Сила голосу"
            "course_4" -> "Впевнений спікер"
            "course_5" -> "Чисте мовлення"
            "course_6" -> "Ділова комунікація"
            "course_7" -> "Харизматичний оратор"
            else -> "Курс"
        }
    }

    private fun getCourseData(courseId: String): Triple<String, String, String> {
        return when (courseId) {
            "course_1" -> Triple("Чітке мовлення", "#667EEA", "🗣️")
            "course_2" -> Triple("Магія інтонації", "#EC4899", "🎭")
            "course_3" -> Triple("Сила голосу", "#F59E0B", "💪")
            "course_4" -> Triple("Впевнений спікер", "#8B5CF6", "💼")
            "course_5" -> Triple("Чисте мовлення", "#10B981", "✨")
            "course_6" -> Triple("Ділова комунікація", "#3B82F6", "📊")
            "course_7" -> Triple("Харизматичний оратор", "#EF4444", "🎤")
            else -> Triple("Курс", "#667EEA", "📖")
        }
    }

    private fun getLessonIdFormat(courseId: String, lessonNumber: Int): String {
        // Визначає формат lessonId для різних курсів
        return when (courseId) {
            "course_1" -> "lesson_$lessonNumber"
            "course_2" -> "intonation_lesson_$lessonNumber"
            "course_3" -> "voice_lesson_$lessonNumber"
            "course_4" -> "speaker_lesson_$lessonNumber"
            "course_5" -> "clean_lesson_$lessonNumber"
            "course_6" -> "business_lesson_$lessonNumber"
            "course_7" -> "charisma_lesson_$lessonNumber"
            else -> "lesson_$lessonNumber"
        }
    }

    private suspend fun getLessonTitle(courseId: String, lessonId: String): String {
        return try {
            val lesson = courseRepository.getLessonById(courseId, lessonId).first()
            lesson?.title ?: "Урок $lessonId"
        } catch (e: Exception) {
            "Урок $lessonId"
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
            val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6", "course_7")
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

    private fun observeAnalysisLimits() {
        viewModelScope.launch {
            userPreferencesDataStore.checkAndResetDailyLimits()
        }

        viewModelScope.launch {
            userPreferencesDataStore.userPreferencesFlow.collect { prefs ->
                val remainingAnalyses = PremiumChecker.getRemainingAnalyses(
                    prefs.isPremium, prefs.freeAnalysesToday, prefs.freeAdAnalysesToday
                )
                val remainingImprov = PremiumChecker.getRemainingImprovAnalyses(
                    prefs.isPremium, prefs.freeImprovAnalysesToday, prefs.freeAdImprovToday
                )
                val remainingMessages = PremiumChecker.getRemainingAiMessages(
                    prefs.isPremium, 0 // AI Coach messages tracked separately
                )
                _state.update {
                    it.copy(
                        isPremium = prefs.isPremium,
                        remainingAnalyses = remainingAnalyses,
                        remainingImprovAnalyses = remainingImprov,
                        remainingAiMessages = remainingMessages,
                        maxFreeAnalyses = FreeTierLimits.FREE_ANALYSES_PER_DAY + prefs.freeAdAnalysesToday,
                        maxFreeImprovAnalyses = FreeTierLimits.FREE_IMPROV_ANALYSES_PER_DAY + prefs.freeAdImprovToday,
                        maxFreeAiMessages = FreeTierLimits.FREE_MESSAGES_PER_DAY
                    )
                }
            }
        }
    }

    private fun observeDailyPlan() {
        viewModelScope.launch {
            // Підписуємося на зміни в warmup completions
            warmupCompletionDao.getRecentCompletions(1000).collect {
                android.util.Log.d("HomeViewModel", "Warmup completions changed, refreshing daily plan")
                refreshDailyPlan()
            }
        }

        viewModelScope.launch {
            // Підписуємося на зміни в course progress
            val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6", "course_7")
            allCourses.forEach { courseId ->
                launch {
                    courseProgressDao.getCourseProgress(courseId).collect {
                        android.util.Log.d("HomeViewModel", "Course $courseId progress changed, refreshing daily plan")
                        refreshDailyPlan()
                    }
                }
            }
        }

        // Також перевіряємо чи новий день при кожній зміні preferences
        viewModelScope.launch {
            userPreferencesDataStore.userPreferencesFlow.collect { preferences ->
                val today = getCurrentDateString()
                if (preferences.lastDailyPlanDate != today) {
                    android.util.Log.d("HomeViewModel", "New day detected, updating daily plan")
                    userPreferencesDataStore.updateDailyPlanDate(today)
                    refreshDailyPlan()
                }
            }
        }
    }

    private suspend fun refreshDailyPlan() {
        try {
            val preferences = userPreferencesDataStore.userPreferencesFlow.first()
            val progress = userProgressDao.getUserProgressOnce()
            val todayPlan = generateTodayPlan(preferences, progress)
            _state.update { it.copy(todayPlan = todayPlan) }
        } catch (e: Exception) {
            android.util.Log.e("HomeViewModel", "Error refreshing daily plan", e)
        }
    }
}
