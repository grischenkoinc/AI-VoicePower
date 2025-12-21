# Промпт для Claude Code — Phase 3: Home Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1-2.5 — Warmup (всі 5 підфаз)

Зараз **Phase 3 — Home Screen** — головний екран застосунку.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: ЦІЛЬНА ФАЗА (не розбивати на підфази).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.3 (Home Screen).

**Складність:** 🟡 СЕРЕДНЯ  
**Час:** ⏱️ 2-3 години

---

## Ключова ідея

**Home Screen** — це **центральний хаб** застосунку, персоналізований план на день.

**Основні функції:**
1. **Привітання + Streak** — "Доброго ранку, [ім'я]! 🔥 5 днів поспіль"
2. **Сьогоднішній план** — рекомендовані активності на основі:
   - UserGoal (мета користувача)
   - DiagnosticResult (слабкі місця)
   - Прогресу (що вже зробив сьогодні)
3. **Швидкі дії** — 4 кнопки до основних розділів
4. **Прогрес тижня** — міні-графік активності

**Персоналізація:**
- Якщо мета "CLEAR_SPEECH" → рекомендувати курс "Чітке мовлення"
- Якщо низька дикція з діагностики → пропонувати артикуляцію
- Якщо сьогодні ще не робив розминку → "Почни з розминки"

---

## Задача Phase 3

Створити головний екран з **4 секціями**:

### 1. Header (привітання + streak)
```
┌────────────────────────────────────┐
│ Доброго ранку, Євгеній! ☀️         │
│ 🔥 5 днів поспіль                  │
└────────────────────────────────────┘
```

### 2. Сьогоднішній план (персоналізований)
```
┌────────────────────────────────────┐
│ 📋 Сьогоднішній план               │
│                                    │
│ ┌────────────────────────────────┐ │
│ │ ✅ Швидка розминка (виконано)  │ │
│ └────────────────────────────────┘ │
│                                    │
│ ┌────────────────────────────────┐ │
│ │ ▶️ Урок 8: Чітке мовлення      │ │
│ │    Курс "Чітке мовлення"       │ │
│ │    ~15 хв                      │ │
│ └────────────────────────────────┘ │
│                                    │
│ ┌────────────────────────────────┐ │
│ │ 💬 Попрактикуйся з AI          │ │
│ │    Обговори свій прогрес       │ │
│ └────────────────────────────────┘ │
│                                    │
└────────────────────────────────────┘
```

### 3. Швидкі дії
```
┌────────────────────────────────────┐
│ 🚀 Швидкі дії                      │
│                                    │
│ [💪 Розминка] [📚 Курси]           │
│ [🎤 Імпровізація] [📊 Прогрес]     │
│                                    │
└────────────────────────────────────┘
```

### 4. Прогрес тижня
```
┌────────────────────────────────────┐
│ 📈 Прогрес тижня                   │
│                                    │
│  Пн Вт Ср Чт Пт Сб Нд             │
│  ■  ■  ■  ■  □  □  □              │
│  15 20 15 10  0  0  0  (хв)       │
│                                    │
└────────────────────────────────────┘
```

---

## Структура файлів

```
ui/screens/home/
├── HomeScreen.kt
├── HomeViewModel.kt
├── HomeState.kt
├── HomeEvent.kt
└── components/
    ├── WelcomeHeader.kt
    ├── TodayPlanCard.kt
    ├── PlanActivityItem.kt
    ├── QuickActionsGrid.kt
    └── WeekProgressChart.kt

domain/model/
└── home/
    ├── TodayPlan.kt
    └── PlanActivity.kt

data/repository/
└── HomeRepositoryImpl.kt (new)
```

---

## Повний код

### 1. Domain Models

#### domain/model/home/TodayPlan.kt

```kotlin
package com.aivoicepower.domain.model.home

data class TodayPlan(
    val activities: List<PlanActivity>,
    val recommendedFocus: String // "Сьогодні попрацюй над дикцією"
)

data class PlanActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val subtitle: String?,
    val estimatedMinutes: Int,
    val isCompleted: Boolean,
    val navigationRoute: String
)

enum class ActivityType {
    WARMUP,         // Розминка
    LESSON,         // Урок курсу
    IMPROVISATION,  // Імпровізація
    AI_COACH,       // AI тренер
    DIAGNOSTIC,     // Повторна діагностика
    DAILY_CHALLENGE // Щоденний челендж
}
```

#### domain/model/home/WeekProgress.kt

```kotlin
package com.aivoicepower.domain.model.home

data class WeekProgress(
    val days: List<DayProgress>
)

data class DayProgress(
    val dayName: String,      // "Пн", "Вт", ...
    val date: String,         // "2024-12-15"
    val minutes: Int,
    val isCompleted: Boolean  // Чи була активність
)
```

### 2. HomeState.kt

```kotlin
package com.aivoicepower.ui.screens.home

import com.aivoicepower.domain.model.home.TodayPlan
import com.aivoicepower.domain.model.home.WeekProgress

data class HomeState(
    val userName: String? = null,
    val currentStreak: Int = 0,
    val greeting: String = "Доброго дня",
    val todayPlan: TodayPlan? = null,
    val weekProgress: WeekProgress? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
```

### 3. HomeEvent.kt

```kotlin
package com.aivoicepower.ui.screens.home

sealed class HomeEvent {
    object Refresh : HomeEvent()
    data class ActivityClicked(val navigationRoute: String) : HomeEvent()
    object WarmupClicked : HomeEvent()
    object CoursesClicked : HomeEvent()
    object ImprovisationClicked : HomeEvent()
    object ProgressClicked : HomeEvent()
    object AiCoachClicked : HomeEvent()
}
```

### 4. HomeViewModel.kt

```kotlin
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
                        userName = preferences.name,
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
            val dayName = dayFormat.format(date).take(2).capitalize(Locale.getDefault())
            
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
```

### 5. HomeScreen.kt

```kotlin
package com.aivoicepower.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.home.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCourse: (String) -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToLesson: (courseId: String, lessonId: String) -> Unit,
    onNavigateToWarmup: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToImprovisation: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToQuickWarmup: () -> Unit,
    onNavigateToRandomTopic: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI VoicePower") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.onEvent(HomeEvent.AiCoachClicked)
                    onNavigateToAiCoach()
                }
            ) {
                Icon(Icons.Default.Assistant, contentDescription = "AI Тренер")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(state.error!!)
                    Button(onClick = { viewModel.onEvent(HomeEvent.Refresh) }) {
                        Text("Повторити")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Header
                item {
                    WelcomeHeader(
                        greeting = state.greeting,
                        userName = state.userName,
                        currentStreak = state.currentStreak
                    )
                }
                
                // Today's Plan
                item {
                    state.todayPlan?.let { plan ->
                        TodayPlanCard(
                            plan = plan,
                            onActivityClick = { activity ->
                                when (activity.navigationRoute) {
                                    com.aivoicepower.ui.navigation.NavRoutes.QuickWarmup.route -> 
                                        onNavigateToQuickWarmup()
                                    com.aivoicepower.ui.navigation.NavRoutes.AiCoach.route -> 
                                        onNavigateToAiCoach()
                                    com.aivoicepower.ui.navigation.NavRoutes.RandomTopic.route -> 
                                        onNavigateToRandomTopic()
                                    else -> {
                                        // Parse lesson route
                                        val parts = activity.navigationRoute.split("/")
                                        if (parts.size >= 4 && parts[0] == "courses") {
                                            onNavigateToLesson(parts[1], parts[3])
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                
                // Quick Actions
                item {
                    QuickActionsGrid(
                        onWarmupClick = {
                            viewModel.onEvent(HomeEvent.WarmupClicked)
                            onNavigateToWarmup()
                        },
                        onCoursesClick = {
                            viewModel.onEvent(HomeEvent.CoursesClicked)
                            onNavigateToCourses()
                        },
                        onImprovisationClick = {
                            viewModel.onEvent(HomeEvent.ImprovisationClicked)
                            onNavigateToImprovisation()
                        },
                        onProgressClick = {
                            viewModel.onEvent(HomeEvent.ProgressClicked)
                            onNavigateToProgress()
                        }
                    )
                }
                
                // Week Progress
                item {
                    state.weekProgress?.let { weekProgress ->
                        WeekProgressChart(weekProgress = weekProgress)
                    }
                }
            }
        }
    }
}
```

### 6. Components

#### components/WelcomeHeader.kt

```kotlin
package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeHeader(
    greeting: String,
    userName: String?,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (userName != null) "$greeting, $userName! ☀️" else "$greeting! ☀️",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            if (currentStreak > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "$currentStreak ${getDaysText(currentStreak)} поспіль",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun getDaysText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "день"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "дні"
        else -> "днів"
    }
}
```

#### components/TodayPlanCard.kt

```kotlin
package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.home.TodayPlan
import com.aivoicepower.domain.model.home.PlanActivity

@Composable
fun TodayPlanCard(
    plan: TodayPlan,
    onActivityClick: (PlanActivity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📋 Сьогоднішній план",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = plan.recommendedFocus,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Divider()
            
            plan.activities.forEach { activity ->
                PlanActivityItem(
                    activity = activity,
                    onClick = { onActivityClick(activity) }
                )
            }
        }
    }
}
```

#### components/PlanActivityItem.kt

```kotlin
package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.home.PlanActivity
import com.aivoicepower.domain.model.home.ActivityType

@Composable
fun PlanActivityItem(
    activity: PlanActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = if (activity.isCompleted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = when (activity.type) {
                        ActivityType.WARMUP -> Icons.Default.FitnessCenter
                        ActivityType.LESSON -> Icons.Default.MenuBook
                        ActivityType.IMPROVISATION -> Icons.Default.Mic
                        ActivityType.AI_COACH -> Icons.Default.Assistant
                        ActivityType.DIAGNOSTIC -> Icons.Default.Assessment
                        ActivityType.DAILY_CHALLENGE -> Icons.Default.EmojiEvents
                    },
                    contentDescription = null,
                    tint = if (activity.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Column {
                    Text(
                        text = if (activity.isCompleted) "✅ ${activity.title}" else "▶️ ${activity.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    activity.subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Text(
                text = "~${activity.estimatedMinutes} хв",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

#### components/QuickActionsGrid.kt

```kotlin
package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickActionsGrid(
    onWarmupClick: () -> Unit,
    onCoursesClick: () -> Unit,
    onImprovisationClick: () -> Unit,
    onProgressClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🚀 Швидкі дії",
                style = MaterialTheme.typography.titleLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.FitnessCenter,
                    label = "Розминка",
                    onClick = onWarmupClick,
                    modifier = Modifier.weight(1f)
                )
                
                QuickActionButton(
                    icon = Icons.Default.MenuBook,
                    label = "Курси",
                    onClick = onCoursesClick,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Mic,
                    label = "Імпровізація",
                    onClick = onImprovisationClick,
                    modifier = Modifier.weight(1f)
                )
                
                QuickActionButton(
                    icon = Icons.Default.TrendingUp,
                    label = "Прогрес",
                    onClick = onProgressClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
```

#### components/WeekProgressChart.kt

```kotlin
package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.home.WeekProgress

@Composable
fun WeekProgressChart(
    weekProgress: WeekProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📈 Прогрес тижня",
                style = MaterialTheme.typography.titleLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekProgress.days.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = day.dayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp, 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    color = if (day.isCompleted) {
                                        androidx.compose.ui.graphics.Color(0xFF6366F1)
                                    } else {
                                        androidx.compose.ui.graphics.Color(0xFFE2E8F0)
                                    },
                                    topLeft = Offset(0f, 0f),
                                    size = Size(size.width, size.height)
                                )
                            }
                        }
                        
                        Text(
                            text = "${day.minutes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
```

---

## Оновити NavGraph.kt

Оновити `HomeScreen` composable в `NavGraph.kt`:

```kotlin
composable(NavRoutes.Home.route) {
    HomeScreen(
        onNavigateToCourse = { courseId ->
            navController.navigate(NavRoutes.CourseDetail.createRoute(courseId))
        },
        onNavigateToAiCoach = {
            navController.navigate(NavRoutes.AiCoach.route)
        },
        onNavigateToLesson = { courseId, lessonId ->
            navController.navigate(NavRoutes.Lesson.createRoute(courseId, lessonId))
        },
        onNavigateToWarmup = {
            navController.navigate(NavRoutes.Warmup.route)
        },
        onNavigateToCourses = {
            navController.navigate(NavRoutes.Courses.route)
        },
        onNavigateToImprovisation = {
            navController.navigate(NavRoutes.Improvisation.route)
        },
        onNavigateToProgress = {
            navController.navigate(NavRoutes.Progress.route)
        },
        onNavigateToQuickWarmup = {
            navController.navigate(NavRoutes.QuickWarmup.route)
        },
        onNavigateToRandomTopic = {
            navController.navigate(NavRoutes.RandomTopic.route)
        }
    )
}
```

---

## Перевірка

### 1. Компіляція
```bash
./gradlew assembleDebug
```

### 2. Testing Flow

**Тест 1: Welcome Header**
- [ ] Показується правильне привітання (ранок/день/вечір)
- [ ] Якщо є ім'я → "Доброго ранку, [ім'я]"
- [ ] Якщо streak > 0 → показується 🔥 X днів поспіль

**Тест 2: Today's Plan**
- [ ] Показується 3-4 активності
- [ ] Якщо розминка виконана → позначена ✅
- [ ] Клік на активність → правильна навігація
- [ ] Рекомендація показується (based on weakest skill)

**Тест 3: Quick Actions**
- [ ] 4 кнопки: Розминка, Курси, Імпровізація, Прогрес
- [ ] Кожна кнопка веде на правильний екран

**Тест 4: Week Progress**
- [ ] 7 днів (Пн-Нд)
- [ ] Поточний день показує правильні хвилини
- [ ] Дні з активністю підсвічені

**Тест 5: FAB (AI Coach)**
- [ ] Кнопка показується
- [ ] Клік → навігація до AI Coach

**Тест 6: Personalization**
- [ ] План змінюється на основі UserGoal
- [ ] Якщо goal = "CLEAR_SPEECH" → рекомендує курс 1
- [ ] Якщо goal = "PUBLIC_SPEAKING" → рекомендує курс 3

---

## Очікуваний результат

✅ HomeScreen з 4 секціями створено
✅ Персоналізований план (based on goal + progress)
✅ Привітання + streak
✅ Швидкі дії (4 кнопки)
✅ Прогрес тижня (міні-графік)
✅ FAB для AI Coach
✅ Навігація до всіх розділів
✅ Loading/Error states

---

## 🎉 Phase 3 Завершена!

**Наступний крок:** Phase 4 — Courses (розбити на 4 підфази згідно PHASE_STRUCTURE_GUIDE.md)

---

**Час на Phase 3:** ~2-3 години

**Примітка:** Week Progress показує тільки поточний тиждень. Історичні дані будуть додані в Phase 7 (Progress).