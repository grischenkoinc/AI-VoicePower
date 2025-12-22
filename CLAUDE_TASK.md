# Промпт для Claude Code — Phase 7: Progress + Gamification

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1-2.5 — Warmup
- ✅ Phase 3 — Home Screen
- ✅ Phase 4.1-4.4 — Courses (повністю)
- ✅ Phase 5.1-5.3 — Improvisation (повністю)
- ✅ Phase 6.1-6.3 — AI Coach (повністю)

Зараз **Phase 7 — Progress + Gamification** — візуалізація прогресу та мотивація користувача.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: Середня складність, можна зробити цільною фазою або розбити на 2 підфази.

**Рекомендація:** ЦІЛЬНА ФАЗА (тісно пов'язані екрани).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.9 (Progress Screen).

**Складність:** 🟡 СЕРЕДНЯ  
**Час:** ⏱️ 3-4 години

---

## Ключова ідея

**Phase 7** створює мотиваційну систему з візуалізацією прогресу:

### 1. **ProgressScreen** (головний екран прогресу)
```
Progress Overview
├── Overall Level (circular progress 0-100)
├── Current Streak 🔥 (днів поспіль)
├── Skill Levels (7 metrics radar/bars)
├── Total Stats (exercises, minutes, recordings)
└── Charts (week/month progress)
```

### 2. **CompareScreen** ("до/після")
```
Diagnostic Comparison
├── Initial diagnostic (Phase 1.4)
├── Latest recordings
├── Side-by-side metrics
├── Improvement % (+15% дикція)
└── Audio playback
```

### 3. **AchievementsScreen** (бейджі)
```
Achievements Grid
├── Unlocked badges
├── Locked badges (with progress)
├── Categories (streak, courses, skills)
└── Share button
```

### 4. **RecordingHistoryScreen** (історія записів)
```
All Recordings
├── Filter by type (diagnostic, course, improvisation)
├── Sort by date/score
├── Play recording
├── View analysis
└── Delete option
```

---

## Навігація

```
Bottom Navigation → Progress Tab
    │
    ├─── ProgressScreen (main)
    │    ├─ Stats overview
    │    ├─ Charts
    │    └─ Quick actions:
    │        ├─ "Порівняти з початком" → CompareScreen
    │        ├─ "Досягнення" → AchievementsScreen
    │        └─ "Історія записів" → RecordingHistoryScreen
    │
    ├─── CompareScreen
    │    ├─ Initial vs Latest
    │    └─ Detailed metrics comparison
    │
    ├─── AchievementsScreen
    │    ├─ Badges grid
    │    └─ Progress tracking
    │
    └─── RecordingHistoryScreen
         ├─ List of all recordings
         └─ Filter/sort options
```

---

## Структура файлів

```
ui/screens/progress/
├── ProgressScreen.kt
├── ProgressViewModel.kt
├── ProgressState.kt
├── ProgressEvent.kt
│
├── CompareScreen.kt
├── CompareViewModel.kt
├── CompareState.kt
│
├── AchievementsScreen.kt
├── AchievementsViewModel.kt
├── AchievementsState.kt
│
├── RecordingHistoryScreen.kt
├── RecordingHistoryViewModel.kt
├── RecordingHistoryState.kt
│
└── components/
    ├── OverallLevelCard.kt
    ├── StreakCard.kt
    ├── SkillRadarChart.kt
    ├── SkillBarChart.kt
    ├── ProgressLineChart.kt
    ├── StatsCard.kt
    ├── ComparisonMetricCard.kt
    ├── AchievementBadge.kt
    ├── RecordingListItem.kt
    └── AudioPlayerBar.kt

domain/model/
└── achievement/
    ├── Achievement.kt (UPDATE — add more types)
    └── AchievementType.kt (UPDATE)

data/content/
└── AchievementsProvider.kt (NEW)
```

---

## Повний код

### 1. ProgressState.kt

```kotlin
package com.aivoicepower.ui.screens.progress

import com.aivoicepower.domain.model.achievement.Achievement
import com.aivoicepower.domain.model.analysis.SkillType

data class ProgressState(
    val isLoading: Boolean = true,
    
    // Overall
    val overallLevel: Int = 0, // 0-100
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    
    // Stats
    val totalExercises: Int = 0,
    val totalMinutes: Int = 0,
    val totalRecordings: Int = 0,
    
    // Skill levels (current)
    val skillLevels: Map<SkillType, Int> = emptyMap(),
    
    // Progress over time (last 7 days)
    val weeklyProgress: List<DailyProgress> = emptyList(),
    
    // Recent achievements
    val recentAchievements: List<Achievement> = emptyList(),
    val totalAchievements: Int = 0,
    val unlockedAchievements: Int = 0,
    
    val error: String? = null
)

data class DailyProgress(
    val date: String, // "2024-12-18"
    val exercises: Int,
    val minutes: Int
)
```

### 2. ProgressEvent.kt

```kotlin
package com.aivoicepower.ui.screens.progress

sealed class ProgressEvent {
    object Refresh : ProgressEvent()
    object NavigateToCompare : ProgressEvent()
    object NavigateToAchievements : ProgressEvent()
    object NavigateToHistory : ProgressEvent()
}
```

### 3. ProgressViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.analysis.SkillType
import com.aivoicepower.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val userProgressRepository: UserRepository,
    private val diagnosticRepository: DiagnosticRepository,
    private val achievementRepository: AchievementRepository,
    private val courseProgressRepository: CourseRepository,
    private val warmupCompletionRepository: WarmupRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()
    
    init {
        loadProgress()
    }
    
    fun onEvent(event: ProgressEvent) {
        when (event) {
            ProgressEvent.Refresh -> loadProgress()
            ProgressEvent.NavigateToCompare -> { /* Handled by Screen */ }
            ProgressEvent.NavigateToAchievements -> { /* Handled by Screen */ }
            ProgressEvent.NavigateToHistory -> { /* Handled by Screen */ }
        }
    }
    
    private fun loadProgress() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                // Load user progress
                userProgressRepository.getUserProgress().collect { progress ->
                    val skillLevels = mapOf(
                        SkillType.DICTION to progress.dictionLevel,
                        SkillType.TEMPO to progress.tempoLevel,
                        SkillType.INTONATION to progress.intonationLevel,
                        SkillType.VOLUME to progress.volumeLevel,
                        SkillType.STRUCTURE to progress.structureLevel,
                        SkillType.CONFIDENCE to progress.confidenceLevel,
                        SkillType.FILLER_WORDS to progress.fillerWordsLevel
                    )
                    
                    // Calculate overall level (average of all skills)
                    val overallLevel = skillLevels.values.average().toInt()
                    
                    _state.update {
                        it.copy(
                            overallLevel = overallLevel,
                            currentStreak = progress.currentStreak,
                            longestStreak = progress.longestStreak,
                            totalExercises = progress.totalExercises,
                            totalMinutes = progress.totalMinutes,
                            totalRecordings = progress.totalRecordings,
                            skillLevels = skillLevels,
                            isLoading = false
                        )
                    }
                }
                
                // Load weekly progress
                loadWeeklyProgress()
                
                // Load achievements
                loadAchievements()
                
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Помилка завантаження: ${e.message}"
                    )
                }
            }
        }
    }
    
    private suspend fun loadWeeklyProgress() {
        // TODO: Implement actual weekly data aggregation
        // For now, generate mock data
        val weeklyProgress = (0..6).map { daysAgo ->
            val date = java.time.LocalDate.now().minusDays(daysAgo.toLong())
            DailyProgress(
                date = date.toString(),
                exercises = if (daysAgo < 3) (5..15).random() else 0,
                minutes = if (daysAgo < 3) (10..30).random() else 0
            )
        }.reversed()
        
        _state.update { it.copy(weeklyProgress = weeklyProgress) }
    }
    
    private suspend fun loadAchievements() {
        achievementRepository.getUnlockedAchievements().collect { achievements ->
            _state.update {
                it.copy(
                    recentAchievements = achievements.take(3),
                    unlockedAchievements = achievements.size
                )
            }
        }
        
        achievementRepository.getAllAchievements().collect { allAchievements ->
            _state.update { it.copy(totalAchievements = allAchievements.size) }
        }
    }
}
```

### 4. ProgressScreen.kt

```kotlin
package com.aivoicepower.ui.screens.progress

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
import com.aivoicepower.ui.screens.progress.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
    onNavigateToCompare: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогрес") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ProgressEvent.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Оновити")
                    }
                }
            )
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overall Level
                item {
                    OverallLevelCard(
                        level = state.overallLevel,
                        levelLabel = getLevelLabel(state.overallLevel)
                    )
                }
                
                // Streak
                item {
                    StreakCard(
                        currentStreak = state.currentStreak,
                        longestStreak = state.longestStreak
                    )
                }
                
                // Stats Overview
                item {
                    StatsCard(
                        totalExercises = state.totalExercises,
                        totalMinutes = state.totalMinutes,
                        totalRecordings = state.totalRecordings
                    )
                }
                
                // Skill Levels
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Навички",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Radar Chart
                            SkillRadarChart(
                                skillLevels = state.skillLevels,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Bar Chart
                            SkillBarChart(
                                skillLevels = state.skillLevels
                            )
                        }
                    }
                }
                
                // Weekly Progress Chart
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Активність (7 днів)",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            ProgressLineChart(
                                weeklyProgress = state.weeklyProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                    }
                }
                
                // Recent Achievements
                if (state.recentAchievements.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Останні досягнення",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    TextButton(onClick = onNavigateToAchievements) {
                                        Text("Всі")
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                state.recentAchievements.forEach { achievement ->
                                    AchievementBadge(
                                        achievement = achievement,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Відкрито: ${state.unlockedAchievements}/${state.totalAchievements}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // Quick Actions
                item {
                    Text(
                        text = "Дії",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                item {
                    OutlinedCard(
                        onClick = onNavigateToCompare,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Compare, contentDescription = null)
                                Text("Порівняти з початком")
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
                
                item {
                    OutlinedCard(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.History, contentDescription = null)
                                Text("Історія записів")
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

private fun getLevelLabel(level: Int): String {
    return when {
        level < 20 -> "Початківець"
        level < 40 -> "Практикуючий"
        level < 60 -> "Досвідчений"
        level < 80 -> "Майстер"
        else -> "Професіонал"
    }
}
```

### 5. CompareScreen.kt

```kotlin
package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.progress.components.ComparisonMetricCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    viewModel: CompareViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Порівняння \"До/Після\"") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
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
        } else if (state.initialDiagnostic == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = "Недостатньо даних",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Пройдіть діагностику та зробіть кілька вправ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Загальний прогрес",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Було",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "${state.initialDiagnostic?.overall() ?: 0}",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                                )
                                
                                Column {
                                    Text(
                                        text = "Зараз",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "${state.currentLevel}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Покращення",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "+${state.improvement}%",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
                
                item {
                    Text(
                        text = "Деталізація по навичках",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                items(state.comparisons.size) { index ->
                    val comparison = state.comparisons[index]
                    ComparisonMetricCard(
                        skillName = comparison.skillName,
                        initialValue = comparison.initialValue,
                        currentValue = comparison.currentValue,
                        improvement = comparison.improvement
                    )
                }
            }
        }
    }
}
```

### 6. CompareViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.analysis.SkillType
import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.repository.DiagnosticRepository
import com.aivoicepower.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompareState(
    val isLoading: Boolean = true,
    val initialDiagnostic: DiagnosticResult? = null,
    val currentLevel: Int = 0,
    val improvement: Int = 0,
    val comparisons: List<SkillComparison> = emptyList()
)

data class SkillComparison(
    val skillName: String,
    val initialValue: Int,
    val currentValue: Int,
    val improvement: Int
)

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val diagnosticRepository: DiagnosticRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CompareState())
    val state: StateFlow<CompareState> = _state.asStateFlow()
    
    init {
        loadComparison()
    }
    
    private fun loadComparison() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                // Get initial diagnostic
                diagnosticRepository.getInitialDiagnostic().collect { diagnostic ->
                    if (diagnostic == null) {
                        _state.update { it.copy(isLoading = false) }
                        return@collect
                    }
                    
                    // Get current levels
                    userRepository.getUserProgress().collect { progress ->
                        val comparisons = listOf(
                            SkillComparison(
                                skillName = "Дикція",
                                initialValue = diagnostic.diction,
                                currentValue = progress.dictionLevel,
                                improvement = calculateImprovement(diagnostic.diction, progress.dictionLevel)
                            ),
                            SkillComparison(
                                skillName = "Темп",
                                initialValue = diagnostic.tempo,
                                currentValue = progress.tempoLevel,
                                improvement = calculateImprovement(diagnostic.tempo, progress.tempoLevel)
                            ),
                            SkillComparison(
                                skillName = "Інтонація",
                                initialValue = diagnostic.intonation,
                                currentValue = progress.intonationLevel,
                                improvement = calculateImprovement(diagnostic.intonation, progress.intonationLevel)
                            ),
                            SkillComparison(
                                skillName = "Гучність",
                                initialValue = diagnostic.volume,
                                currentValue = progress.volumeLevel,
                                improvement = calculateImprovement(diagnostic.volume, progress.volumeLevel)
                            ),
                            SkillComparison(
                                skillName = "Структура",
                                initialValue = diagnostic.structure,
                                currentValue = progress.structureLevel,
                                improvement = calculateImprovement(diagnostic.structure, progress.structureLevel)
                            ),
                            SkillComparison(
                                skillName = "Впевненість",
                                initialValue = diagnostic.confidence,
                                currentValue = progress.confidenceLevel,
                                improvement = calculateImprovement(diagnostic.confidence, progress.confidenceLevel)
                            ),
                            SkillComparison(
                                skillName = "Чистота мовлення",
                                initialValue = diagnostic.fillerWords,
                                currentValue = progress.fillerWordsLevel,
                                improvement = calculateImprovement(diagnostic.fillerWords, progress.fillerWordsLevel)
                            )
                        )
                        
                        val initialOverall = diagnostic.overall()
                        val currentOverall = comparisons.map { it.currentValue }.average().toInt()
                        val overallImprovement = calculateImprovement(initialOverall, currentOverall)
                        
                        _state.update {
                            it.copy(
                                isLoading = false,
                                initialDiagnostic = diagnostic,
                                currentLevel = currentOverall,
                                improvement = overallImprovement,
                                comparisons = comparisons
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    private fun calculateImprovement(initial: Int, current: Int): Int {
        if (initial == 0) return 0
        return ((current - initial).toFloat() / initial * 100).toInt()
    }
}

private fun DiagnosticResult.overall(): Int {
    return ((diction + tempo + intonation + volume + structure + confidence + fillerWords) / 7.0).toInt()
}
```

### 7. AchievementsScreen.kt

```kotlin
package com.aivoicepower.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.progress.components.AchievementBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Досягнення")
                        Text(
                            text = "${state.unlockedCount}/${state.totalCount} відкрито",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
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
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.achievements) { achievement ->
                    AchievementBadge(
                        achievement = achievement,
                        isLarge = true
                    )
                }
            }
        }
    }
}
```

### 8. AchievementsViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.domain.model.achievement.Achievement
import com.aivoicepower.domain.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsState(
    val isLoading: Boolean = true,
    val achievements: List<Achievement> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()
    
    init {
        loadAchievements()
    }
    
    private fun loadAchievements() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                achievementRepository.getAllAchievements().collect { achievements ->
                    val unlocked = achievements.count { it.isUnlocked }
                    
                    _state.update {
                        it.copy(
                            isLoading = false,
                            achievements = achievements,
                            unlockedCount = unlocked,
                            totalCount = achievements.size
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
```

### 9. Components

#### components/OverallLevelCard.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OverallLevelCard(
    level: Int,
    levelLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Загальний рівень",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Circular Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    progress = { level / 100f },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = levelLabel,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
```

#### components/StreakCard.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "$currentStreak",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "днів поспіль",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp)
            )
            
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text(
                    text = "🏆",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "$longestStreak",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "рекорд",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

#### components/StatsCard.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatsCard(
    totalExercises: Int,
    totalMinutes: Int,
    totalRecordings: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Статистика",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "✏️",
                    value = "$totalExercises",
                    label = "вправ"
                )
                
                StatItem(
                    icon = "⏱️",
                    value = "$totalMinutes",
                    label = "хвилин"
                )
                
                StatItem(
                    icon = "🎤",
                    value = "$totalRecordings",
                    label = "записів"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

#### components/SkillBarChart.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.analysis.SkillType

@Composable
fun SkillBarChart(
    skillLevels: Map<SkillType, Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skillLevels.forEach { (skill, level) ->
            SkillBar(
                name = getSkillName(skill),
                level = level
            )
        }
    }
}

@Composable
private fun SkillBar(
    name: String,
    level: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$level",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { level / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

private fun getSkillName(skill: SkillType): String {
    return when (skill) {
        SkillType.DICTION -> "Дикція"
        SkillType.TEMPO -> "Темп"
        SkillType.INTONATION -> "Інтонація"
        SkillType.VOLUME -> "Гучність"
        SkillType.STRUCTURE -> "Структура"
        SkillType.CONFIDENCE -> "Впевненість"
        SkillType.FILLER_WORDS -> "Чистота мовлення"
    }
}
```

#### components/ComparisonMetricCard.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ComparisonMetricCard(
    skillName: String,
    initialValue: Int,
    currentValue: Int,
    improvement: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skillName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(
                            text = "Було",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$initialValue",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Зараз",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currentValue",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            if (improvement > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "+$improvement%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
```

#### components/AchievementBadge.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.achievement.Achievement

@Composable
fun AchievementBadge(
    achievement: Achievement,
    isLarge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = if (achievement.isUnlocked) {
            CardDefaults.cardColors()
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLarge) 16.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.icon,
                style = if (isLarge) {
                    MaterialTheme.typography.displayMedium
                } else {
                    MaterialTheme.typography.headlineMedium
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = achievement.title,
                style = if (isLarge) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleSmall
                },
                maxLines = 2
            )
            
            if (!achievement.isUnlocked && achievement.progress != null && achievement.target != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${achievement.progress}/${achievement.target}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    LinearProgressIndicator(
                        progress = { achievement.progress.toFloat() / achievement.target },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(4.dp)
                    )
                }
            }
        }
    }
}
```

#### components/SkillRadarChart.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aivoicepower.domain.model.analysis.SkillType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SkillRadarChart(
    skillLevels: Map<SkillType, Int>,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f
        val numberOfSkills = skillLevels.size
        val angleStep = (2 * PI / numberOfSkills).toFloat()
        
        // Draw grid circles
        for (i in 1..4) {
            val r = radius * i / 4
            drawCircle(
                color = gridColor,
                radius = r,
                center = center,
                style = Stroke(width = 1f)
            )
        }
        
        // Draw axes
        skillLevels.keys.forEachIndexed { index, _ ->
            val angle = angleStep * index - PI.toFloat() / 2
            val end = Offset(
                center.x + radius * cos(angle),
                center.y + radius * sin(angle)
            )
            drawLine(
                color = gridColor,
                start = center,
                end = end,
                strokeWidth = 1f
            )
        }
        
        // Draw data polygon
        val path = Path()
        skillLevels.values.forEachIndexed { index, level ->
            val angle = angleStep * index - PI.toFloat() / 2
            val distance = radius * (level / 100f)
            val point = Offset(
                center.x + distance * cos(angle),
                center.y + distance * sin(angle)
            )
            
            if (index == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }
        path.close()
        
        // Fill
        drawPath(
            path = path,
            color = color.copy(alpha = 0.3f)
        )
        
        // Stroke
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2f)
        )
    }
}
```

#### components/ProgressLineChart.kt

```kotlin
package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aivoicepower.ui.screens.progress.DailyProgress

@Composable
fun ProgressLineChart(
    weeklyProgress: List<DailyProgress>,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = modifier.fillMaxSize()) {
        if (weeklyProgress.isEmpty()) return@Canvas
        
        val maxMinutes = weeklyProgress.maxOf { it.minutes }.coerceAtLeast(1)
        val width = size.width
        val height = size.height
        val stepX = width / (weeklyProgress.size - 1).coerceAtLeast(1)
        
        // Draw line
        val path = Path()
        weeklyProgress.forEachIndexed { index, progress ->
            val x = index * stepX
            val y = height - (progress.minutes.toFloat() / maxMinutes * height)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3f)
        )
        
        // Draw points
        weeklyProgress.forEachIndexed { index, progress ->
            val x = index * stepX
            val y = height - (progress.minutes.toFloat() / maxMinutes * height)
            
            drawCircle(
                color = color,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}
```

---

## Оновити NavGraph.kt

```kotlin
composable(NavRoutes.Progress.route) {
    ProgressScreen(
        onNavigateToCompare = {
            navController.navigate(NavRoutes.Compare.route)
        },
        onNavigateToAchievements = {
            navController.navigate(NavRoutes.Achievements.route)
        },
        onNavigateToHistory = {
            navController.navigate(NavRoutes.RecordingHistory.route)
        }
    )
}

composable(NavRoutes.Compare.route) {
    CompareScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}

composable(NavRoutes.Achievements.route) {
    AchievementsScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}

composable(NavRoutes.RecordingHistory.route) {
    RecordingHistoryScreen(
        onNavigateBack = { navController.popBackStack() },
        onNavigateToResults = { recordingId ->
            navController.navigate(NavRoutes.Results.createRoute(recordingId))
        }
    )
}
```

---

## Тестування

### 1. ProgressScreen
- [ ] Overall level displayed
- [ ] Streak correct
- [ ] Stats accurate
- [ ] Skill levels (radar + bars)
- [ ] Weekly chart
- [ ] Quick actions navigate

### 2. CompareScreen
- [ ] Initial diagnostic loaded
- [ ] Current levels correct
- [ ] Improvement % accurate
- [ ] All 7 skills compared
- [ ] Empty state (if no diagnostic)

### 3. AchievementsScreen
- [ ] Grid layout (2 columns)
- [ ] Unlocked vs locked styling
- [ ] Progress bars for locked
- [ ] Counter in AppBar

### 4. Charts
- [ ] Radar chart renders
- [ ] Bar chart renders
- [ ] Line chart renders
- [ ] Responsive to data

---

## Перевірка

```bash
./gradlew assembleDebug
```

**Checklist:**

**Progress:**
- [ ] All widgets render
- [ ] Data accurate
- [ ] Navigation works

**Compare:**
- [ ] Metrics comparison
- [ ] Improvement calculation
- [ ] Empty state

**Achievements:**
- [ ] Grid display
- [ ] Unlock logic
- [ ] Progress tracking

**Charts:**
- [ ] Radar chart
- [ ] Bar chart
- [ ] Line chart

---

## Очікуваний результат

✅ ProgressScreen з повною статистикою
✅ Skill radar + bar charts
✅ Streak tracking
✅ CompareScreen ("до/після")
✅ AchievementsScreen (badges grid)
✅ RecordingHistoryScreen
✅ Weekly progress chart
✅ Motivation system готова!

---

## 🎉 PHASE 7 ЗАВЕРШЕНО!

```
✅ Phase 0-6 — Всі основні features
✅ Phase 7 — Progress + Gamification
⏳ Phase 8 — Content
⏳ Phase 9 — Freemium + Polish
```

**Прогрес:** 7 з 9 фаз (78%)

---

## 🚀 Наступний крок: Phase 8 — Content

**Phase 8** заповнює застосунок контентом:
- 100+ скоромовки
- Тексти для читання
- Уроки 8-21 для всіх курсів
- Теми для дебатів
- Товари для продажу
- Теми для імпровізації

**Складність:** 🟢 НИЗЬКА (pure data)  
**Час:** ~4-6 годин

---

**Час на Phase 7:** ~3-4 години

**Progress система готова до мотивації користувачів!**