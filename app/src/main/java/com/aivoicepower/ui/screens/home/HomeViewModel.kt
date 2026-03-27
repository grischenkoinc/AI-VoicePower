package com.aivoicepower.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.aivoicepower.data.firebase.sync.CloudSyncRepositoryImpl
import com.aivoicepower.data.local.database.dao.*
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.home.*
import com.aivoicepower.domain.repository.AchievementRepository
import com.aivoicepower.domain.repository.AuthRepository
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.ui.navigation.Screen
import com.aivoicepower.utils.PremiumChecker
import com.aivoicepower.utils.SkillLevelUtils
import com.aivoicepower.utils.constants.FreeTierLimits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val dailyTipsRepository: com.aivoicepower.data.repository.DailyTipsRepository,
    private val achievementRepository: AchievementRepository,
    private val authRepository: AuthRepository,
    private val cloudSyncRepository: CloudSyncRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Cache date formatters (expensive to create)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEE", Locale("uk", "UA"))

    init {
        autoRestoreIfNeeded()
        loadHomeData()
        observeSkills()
        observeCourseAndDailyPlan()
        observeAnalysisLimits()
        checkDiagnosticAchievement()
    }

    /**
     * Після оновлення застосунку Room DB може бути знищена destructive migration.
     * Якщо юзер залогінений і мав діагностику, але локальний прогрес порожній — відновлюємо з Firestore.
     */
    private fun autoRestoreIfNeeded() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.currentUser.first()
                if (currentUser == null) return@launch

                val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                if (!prefs.hasCompletedDiagnostic) return@launch

                val localProgress = userProgressDao.getProgress()
                if (localProgress == null) {
                    Log.w("HomeVM", "User logged in + diagnostic done, but local DB empty — restoring from Firestore")
                    cloudSyncRepository.restoreAllData()
                    Log.d("HomeVM", "Auto-restore from Firestore completed")
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Auto-restore failed: ${e.message}")
            }
        }
    }

    private fun checkDiagnosticAchievement() {
        viewModelScope.launch {
            try {
                achievementRepository.checkDiagnosticAchievement()
            } catch (_: Exception) { }
        }
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
                // Load base data in parallel
                coroutineScope {
                    val preferencesDeferred = async { userPreferencesDataStore.userPreferencesFlow.first() }
                    val progressDeferred = async { userProgressDao.getUserProgressOnce() }

                    val preferences = preferencesDeferred.await()
                    val progress = progressDeferred.await()

                    // Now load dependent data in parallel
                    val todayPlanDeferred = async { generateTodayPlan(preferences, progress) }
                    val weekProgressDeferred = async { loadWeekProgress(preferences) }
                    val dailyTipDeferred = async { getDailyTip(preferences) }

                    val todayPlan = todayPlanDeferred.await()
                    val weekProgress = weekProgressDeferred.await()
                    val dailyTip = dailyTipDeferred.await()

                    val greeting = getGreetingByTime()
                    val quickActions = getQuickActions()
                    val coachMessage = generateCoachMessage(progress, preferences, todayPlan)

                    _state.update {
                        it.copy(
                            userName = preferences.userName,
                            currentStreak = progress?.currentStreak ?: 0,
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
        // Index by lessonId for O(1) lookup instead of O(n) find per iteration
        val progressMap = courseProgress.associateBy { it.lessonId }
        var nextLessonNumber = 1
        var nextLessonId = getLessonIdFormat(recommendedCourse, nextLessonNumber)
        var isLessonCompleted = false

        // Спочатку шукаємо останній урок завершений сьогодні
        var lastLessonCompletedToday: Pair<Int, String>? = null

        for (lessonNumber in 1..21) {
            val lessonId = getLessonIdFormat(recommendedCourse, lessonNumber)
            val lessonProgress = progressMap[lessonId]

            if (lessonProgress != null && lessonProgress.isCompleted) {
                val completedToday = lessonProgress.completedAt?.let { completedTime ->
                    dateFormat.format(java.util.Date(completedTime)) == today
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
        } else {
            // Інакше шукаємо перший незавершений урок, доступний для користувача
            for (lessonNumber in 1..21) {
                val lessonId = getLessonIdFormat(recommendedCourse, lessonNumber)
                val lessonProgress = progressMap[lessonId]

                val lessonIndex = lessonNumber - 1
                val canAccess = com.aivoicepower.utils.PremiumChecker.canAccessLesson(
                    isPremium = preferences.isPremium,
                    lessonIndex = lessonIndex
                )

                if (!canAccess) continue

                if (lessonProgress == null || !lessonProgress.isCompleted) {
                    nextLessonNumber = lessonNumber
                    nextLessonId = lessonId
                    isLessonCompleted = false
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
        val improvCompleted = preferences.freeImprovisationsToday > 0
        activities.add(
            PlanActivity(
                id = "improvisation",
                type = ActivityType.IMPROVISATION,
                title = "Імпровізація",
                subtitle = if (improvCompleted) "Виконано сьогодні" else "Спонтанне мовлення",
                estimatedMinutes = 1,
                isCompleted = improvCompleted,
                navigationRoute = Screen.RandomTopic.route,
                metaText = "1 вправа"
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
        preferences: com.aivoicepower.data.local.datastore.UserPreferences,
        todayPlan: TodayPlan? = null
    ): String {
        // All daily goals completed — congratulate!
        if (todayPlan != null && todayPlan.activities.isNotEmpty() && todayPlan.activities.all { it.isCompleted }) {
            return "Всі цілі на сьогодні виконано! Чудова робота! Відпочинь або спробуй додаткові вправи для ще кращого результату."
        }

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

    private suspend fun loadWeekProgress(
        preferences: com.aivoicepower.data.local.datastore.UserPreferences? = null
    ): WeekProgress {
        val calendar = Calendar.getInstance()

        // Get start of week (Monday)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = mutableListOf<DayProgress>()
        val today = getCurrentDateString()
        // Load preferences once instead of 7 times in the loop
        val prefs = preferences ?: userPreferencesDataStore.userPreferencesFlow.first()

        for (i in 0..6) {
            val date = calendar.time
            val dateString = dateFormat.format(date)
            val dayName = dayFormat.format(date).take(2).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            val minutes = if (dateString == today) {
                prefs.todayMinutes
            } else {
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
                _state.update { it.copy(
                    skills = skills,
                    currentStreak = progress?.currentStreak ?: 0
                ) }
            }
        }
    }

    private fun buildSkillsList(
        progress: com.aivoicepower.data.local.database.entity.UserProgressEntity?
    ): List<com.aivoicepower.domain.model.home.Skill> {
        val dictionLevel = progress?.dictionLevel?.toInt() ?: 1
        val tempoLevel = progress?.tempoLevel?.toInt() ?: 1
        val intonationLevel = progress?.intonationLevel?.toInt() ?: 1
        val structureLevel = progress?.structureLevel?.toInt() ?: 1
        val confidenceLevel = progress?.confidenceLevel?.toInt() ?: 1

        return listOf(
            com.aivoicepower.domain.model.home.Skill(
                id = "diction",
                name = "Дикція",
                emoji = "📢",
                percentage = dictionLevel,
                growth = calculateGrowth(dictionLevel, progress?.lastDictionLevel?.toInt()),
                gradientColors = listOf("#667EEA", "#764BA2"),
                statusLabel = SkillLevelUtils.getSkillLabel(dictionLevel)
            ),
            com.aivoicepower.domain.model.home.Skill(
                id = "intonation",
                name = "Інтонація",
                emoji = "🎵",
                percentage = intonationLevel,
                growth = calculateGrowth(intonationLevel, progress?.lastIntonationLevel?.toInt()),
                gradientColors = listOf("#F59E0B", "#F97316"),
                statusLabel = SkillLevelUtils.getSkillLabel(intonationLevel)
            ),
            com.aivoicepower.domain.model.home.Skill(
                id = "tempo",
                name = "Темп",
                emoji = "⚡",
                percentage = tempoLevel,
                growth = calculateGrowth(tempoLevel, progress?.lastTempoLevel?.toInt()),
                gradientColors = listOf("#06B6D4", "#0891B2"),
                statusLabel = SkillLevelUtils.getSkillLabel(tempoLevel)
            ),
            com.aivoicepower.domain.model.home.Skill(
                id = "emotion",
                name = "Емоції",
                emoji = "🎭",
                percentage = structureLevel,
                growth = calculateGrowth(structureLevel, progress?.lastStructureLevel?.toInt()),
                gradientColors = listOf("#EC4899", "#DB2777"),
                statusLabel = SkillLevelUtils.getSkillLabel(structureLevel)
            ),
            com.aivoicepower.domain.model.home.Skill(
                id = "clarity",
                name = "Чіткість",
                emoji = "✨",
                percentage = confidenceLevel,
                growth = calculateGrowth(confidenceLevel, progress?.lastConfidenceLevel?.toInt()),
                gradientColors = listOf("#22C55E", "#16A34A"),
                statusLabel = SkillLevelUtils.getSkillLabel(confidenceLevel)
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

    private suspend fun getDailyTip(
        preferences: com.aivoicepower.data.local.datastore.UserPreferences? = null
    ): com.aivoicepower.domain.model.home.DailyTip {
        val prefs = preferences ?: userPreferencesDataStore.userPreferencesFlow.first()
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = prefs.lastTipUpdateTime
        val fourHoursInMillis = 4 * 60 * 60 * 1000L

        val isColdStart = dailyTipsRepository.isColdStart
        val timeExpired = currentTime - lastUpdateTime > fourHoursInMillis
        val noTipYet = prefs.currentTipId == null

        if (isColdStart || timeExpired || noTipYet) {
            dailyTipsRepository.markColdStartHandled()

            val newTip = dailyTipsRepository.getRandomTip(excludeId = prefs.currentTipId)
            userPreferencesDataStore.updateDailyTip(newTip.id, currentTime)
            return newTip.copy(date = getCurrentDateString())
        } else {
            val allTips = dailyTipsRepository.loadTips()
            val cachedTip = allTips.find { it.id == prefs.currentTipId }
                ?: dailyTipsRepository.getRandomTip()

            return cachedTip.copy(date = getCurrentDateString())
        }
    }

    private suspend fun getCurrentCourse(preferences: com.aivoicepower.data.local.datastore.UserPreferences): CurrentCourse? {
        val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6", "course_7")

        // Load all course progress in parallel instead of 7 sequential queries
        val allProgress = coroutineScope {
            allCourses.map { courseId ->
                async { courseId to courseProgressDao.getCourseProgress(courseId).first() }
            }.map { it.await() }
        }

        // Find course with most recent activity
        var mostRecentCourse: String? = null
        var mostRecentTimestamp = 0L
        var mostRecentLessonId: String? = null

        for ((courseId, progress) in allProgress) {
            val completedLessons = progress.filter { it.isCompleted }

            if (completedLessons.isNotEmpty()) {
                val latestLesson = completedLessons.maxByOrNull { it.completedAt ?: 0 }
                val timestamp = latestLesson?.completedAt ?: 0

                if (timestamp > mostRecentTimestamp) {
                    mostRecentTimestamp = timestamp
                    mostRecentCourse = courseId
                    mostRecentLessonId = latestLesson?.lessonId
                }
            }
        }

        // Якщо є активний курс - показати наступний урок після останнього виконаного
        if (mostRecentCourse != null && mostRecentLessonId != null) {
            val substringResult = mostRecentLessonId.substringAfterLast("_")
            val lastCompletedNumber = substringResult.toIntOrNull()

            if (lastCompletedNumber != null) {
                val nextLessonNumber = lastCompletedNumber + 1

                if (nextLessonNumber <= 21) {
                    val nextLessonId = getLessonIdFormat(mostRecentCourse, nextLessonNumber)
                    val (courseName, courseColor, courseIcon) = getCourseData(mostRecentCourse)
                    val lessonTitle = getLessonTitle(mostRecentCourse, nextLessonId)
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
        return dateFormat.format(Date())
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

    /**
     * Unified observer for course progress + daily plan.
     * Replaces separate observeCourseProgress() (8 coroutines) + observeDailyPlan() (9 coroutines)
     * with just 3 coroutines total, eliminating duplicate subscriptions.
     */
    private fun observeCourseAndDailyPlan() {
        val allCourses = listOf("course_1", "course_2", "course_3", "course_4", "course_5", "course_6", "course_7")

        // 1. Single subscription per course — updates both currentCourse and dailyPlan
        viewModelScope.launch {
            allCourses.forEach { courseId ->
                launch {
                    courseProgressDao.getCourseProgress(courseId).collect {
                        val preferences = userPreferencesDataStore.userPreferencesFlow.first()
                        val updatedCourse = getCurrentCourse(preferences)
                        _state.update { it.copy(currentCourse = updatedCourse) }
                        refreshDailyPlan()
                    }
                }
            }
        }

        // 2. Preferences changes — update currentCourse + refresh daily plan
        viewModelScope.launch {
            userPreferencesDataStore.userPreferencesFlow.collect { preferences ->
                val updatedCourse = getCurrentCourse(preferences)
                _state.update { it.copy(currentCourse = updatedCourse) }

                val today = getCurrentDateString()
                if (preferences.lastDailyPlanDate != today) {
                    userPreferencesDataStore.updateDailyPlanDate(today)
                }
                // Always refresh to pick up completion changes (improv, warmup, etc.)
                refreshDailyPlan()
            }
        }

        // 3. Warmup completions
        viewModelScope.launch {
            warmupCompletionDao.getRecentCompletions(1000).collect {
                refreshDailyPlan()
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
