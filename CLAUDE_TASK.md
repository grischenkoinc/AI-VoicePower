# Промпт для Claude Code — Phase 2.1: Warmup Main Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic

Зараз **Phase 2 — Warmup (Розминка)** — щоденні вправи для мовленнєвого апарату **БЕЗ AI-аналізу аудіо**.

**Phase 2.1 — Warmup Main Screen** — головний hub екран з навігацією до підрозділів.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: ПРОСТА підфаза (hub screen + navigation).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.4 (Warmup Screen).

**Складність:** 🟢 НИЗЬКА  
**Час:** ⏱️ 1 година

---

## Ключова ідея Warmup

⚠️ **ВАЖЛИВО:** Розминка **НЕ використовує AI-аналіз аудіо**!

| Warmup (Розминка) | Exercises (Вправи курсів) |
|-------------------|---------------------------|
| ❌ БЕЗ запису аудіо | ✅ З записом аудіо |
| ✅ Відео + таймер + чекліст | ✅ AI-аналіз запису |
| ✅ Локальне збереження completion | ✅ Збереження recording + analysis |
| 🎯 Фізичні вправи (артикуляція, дихання, голос) | 🎯 Практика з контентом |

**Чому логічно?** Не має сенсу записувати дихання або артикуляційну гімнастику — це фізичні вправи, як розтяжка перед бігом.

---

## Задача Phase 2.1

Створити **головний екран Warmup** з:
1. **3 категорії розминки** (Articulation, Breathing, Voice)
2. **Quick Warmup** (експрес-розминка 5 хв)
3. **Статистика** (скільки днів підряд, загальний час)
4. **Навігація** до підекранів (Phase 2.2-2.5)

### Структура файлів

```
ui/screens/warmup/
├── WarmupScreen.kt (main hub)
├── WarmupViewModel.kt
├── WarmupState.kt
├── WarmupEvent.kt
└── components/
    ├── WarmupCategoryCard.kt (3 картки категорій)
    ├── QuickWarmupCard.kt (експрес-розминка)
    └── WarmupStatsCard.kt (статистика)
```

---

## UI Design

```
┌──────────────────────────────────────────────┐
│  Розминка                          [Settings]│
├──────────────────────────────────────────────┤
│  (scroll)                                    │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │  🔥 Streak: 7 днів      ⏱️ Сьогодні: 0 хв│ │
│  │  📊 Всього: 42 розминки  ⭐ Рівень: 3   │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  💨 Швидка розминка (5 хв)                  │
│  ┌────────────────────────────────────────┐ │
│  │  Комбінація найважливіших вправ        │ │
│  │  Артикуляція + Дихання + Голос         │ │
│  │                                        │ │
│  │  [▶️ Почати →]                         │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                              │
│  📚 Категорії розминки                      │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 👅 Артикуляційна гімнастика            │ │
│  │ 12 вправ • ~3 хвилини                  │ │
│  │                                        │ │
│  │ Розминка м'язів обличчя та язика      │ │
│  │                                        │ │
│  │ Останнє: вчора                         │ │
│  │ ━━━━━━━━━━━○○○○○○○○○○  60%            │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 🫁 Дихальні вправи                     │ │
│  │ 8 вправ • ~2 хвилини                   │ │
│  │                                        │ │
│  │ Розвиток діафрагмального дихання       │ │
│  │                                        │ │
│  │ Останнє: 3 дні тому                    │ │
│  │ ━━━━━○○○○○○○○○○○○○○○○  25%            │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 🎵 Розминка голосу                     │ │
│  │ 6 вправ • ~2 хвилини                   │ │
│  │                                        │ │
│  │ Вокальні вправи для розігріву          │ │
│  │                                        │ │
│  │ Останнє: ніколи                        │ │
│  │ ○○○○○○○○○○○○○○○○○○○○○  0%             │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                              │
│  💡 Підказка:                               │
│  Виконуй розминку щодня перед основними    │
│  вправами для кращих результатів!          │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Повний код

### 1. WarmupState.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

data class WarmupState(
    val isLoading: Boolean = true,
    val stats: WarmupStats? = null,
    val categories: List<WarmupCategory> = emptyList(),
    val error: String? = null
)

data class WarmupStats(
    val currentStreak: Int = 0,
    val todayMinutes: Int = 0,
    val totalCompletions: Int = 0,
    val level: Int = 1
)

data class WarmupCategory(
    val id: String,
    val icon: String,
    val title: String,
    val exerciseCount: Int,
    val estimatedMinutes: Int,
    val description: String,
    val lastCompletedDate: String?, // "2024-12-15" або null
    val completionRate: Float // 0.0 - 1.0
)

enum class WarmupCategoryType {
    ARTICULATION,  // Артикуляція
    BREATHING,     // Дихання
    VOICE,         // Голос
    QUICK          // Швидка розминка
}
```

### 2. WarmupEvent.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

sealed class WarmupEvent {
    data class CategoryClicked(val categoryId: String) : WarmupEvent()
    object QuickWarmupClicked : WarmupEvent()
    object Refresh : WarmupEvent()
}
```

### 3. WarmupViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WarmupViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(WarmupState())
    val state: StateFlow<WarmupState> = _state.asStateFlow()
    
    init {
        loadWarmupData()
    }
    
    fun onEvent(event: WarmupEvent) {
        when (event) {
            is WarmupEvent.CategoryClicked -> {
                // Navigation handled in Screen
            }
            WarmupEvent.QuickWarmupClicked -> {
                // Navigation handled in Screen
            }
            WarmupEvent.Refresh -> {
                loadWarmupData()
            }
        }
    }
    
    private fun loadWarmupData() {
        viewModelScope.launch {
            try {
                // Завантажуємо статистику + категорії
                combine(
                    loadStats(),
                    loadCategories()
                ) { stats, categories ->
                    WarmupState(
                        isLoading = false,
                        stats = stats,
                        categories = categories
                    )
                }.collect { newState ->
                    _state.value = newState
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалося завантажити дані"
                    )
                }
            }
        }
    }
    
    private fun loadStats(): Flow<WarmupStats> = flow {
        // Завантажуємо з DataStore + WarmupCompletionDao
        userPreferencesDataStore.userPreferencesFlow.collect { prefs ->
            val totalCompletions = warmupCompletionDao.getTotalWarmupDays().first()
            
            emit(
                WarmupStats(
                    currentStreak = prefs.currentStreak,
                    todayMinutes = prefs.todayMinutes,
                    totalCompletions = totalCompletions,
                    level = calculateLevel(totalCompletions)
                )
            )
        }
    }
    
    private fun loadCategories(): Flow<List<WarmupCategory>> = flow {
        val today = getCurrentDateString()
        
        // Завантажуємо completion data для кожної категорії
        val articulationCompletion = warmupCompletionDao.getCompletion(today, "articulation")
        val breathingCompletion = warmupCompletionDao.getCompletion(today, "breathing")
        val voiceCompletion = warmupCompletionDao.getCompletion(today, "voice")
        
        // Завантажуємо останні дати
        val recentCompletions = warmupCompletionDao.getRecentCompletions(30).first()
        
        val categories = listOf(
            WarmupCategory(
                id = "articulation",
                icon = "👅",
                title = "Артикуляційна гімнастика",
                exerciseCount = 12,
                estimatedMinutes = 3,
                description = "Розминка м'язів обличчя та язика",
                lastCompletedDate = recentCompletions
                    .lastOrNull { it.category == "articulation" }
                    ?.date,
                completionRate = articulationCompletion?.let {
                    it.exercisesCompleted.toFloat() / it.totalExercises
                } ?: 0f
            ),
            WarmupCategory(
                id = "breathing",
                icon = "🫁",
                title = "Дихальні вправи",
                exerciseCount = 8,
                estimatedMinutes = 2,
                description = "Розвиток діафрагмального дихання",
                lastCompletedDate = recentCompletions
                    .lastOrNull { it.category == "breathing" }
                    ?.date,
                completionRate = breathingCompletion?.let {
                    it.exercisesCompleted.toFloat() / it.totalExercises
                } ?: 0f
            ),
            WarmupCategory(
                id = "voice",
                icon = "🎵",
                title = "Розминка голосу",
                exerciseCount = 6,
                estimatedMinutes = 2,
                description = "Вокальні вправи для розігріву",
                lastCompletedDate = recentCompletions
                    .lastOrNull { it.category == "voice" }
                    ?.date,
                completionRate = voiceCompletion?.let {
                    it.exercisesCompleted.toFloat() / it.totalExercises
                } ?: 0f
            )
        )
        
        emit(categories)
    }
    
    private fun calculateLevel(totalCompletions: Int): Int {
        // Простий рівень: кожні 10 розминок = +1 рівень
        return (totalCompletions / 10) + 1
    }
    
    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
```

### 4. WarmupScreen.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmupScreen(
    viewModel: WarmupViewModel = hiltViewModel(),
    onNavigateToArticulation: () -> Unit,
    onNavigateToBreathing: () -> Unit,
    onNavigateToVoice: () -> Unit,
    onNavigateToQuick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Розминка") },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Налаштування")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(state.error ?: "Помилка")
                }
            }
            
            else -> {
                WarmupContent(
                    state = state,
                    onCategoryClick = { categoryId ->
                        when (categoryId) {
                            "articulation" -> onNavigateToArticulation()
                            "breathing" -> onNavigateToBreathing()
                            "voice" -> onNavigateToVoice()
                        }
                    },
                    onQuickWarmupClick = onNavigateToQuick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun WarmupContent(
    state: WarmupState,
    onCategoryClick: (String) -> Unit,
    onQuickWarmupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Card
        item {
            if (state.stats != null) {
                WarmupStatsCard(stats = state.stats)
            }
        }
        
        // Quick Warmup
        item {
            Text(
                text = "💨 Швидка розминка (5 хв)",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        item {
            QuickWarmupCard(onClick = onQuickWarmupClick)
        }
        
        item {
            Divider()
        }
        
        // Categories
        item {
            Text(
                text = "📚 Категорії розминки",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        items(state.categories) { category ->
            WarmupCategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
        
        // Tip
        item {
            Divider()
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "💡",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = "Підказка:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Виконуй розминку щодня перед основними вправами для кращих результатів!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
```

### 5. components/WarmupStatsCard.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.warmup.WarmupStats

@Composable
fun WarmupStatsCard(
    stats: WarmupStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Streak
            StatItem(
                icon = "🔥",
                label = "Streak",
                value = "${stats.currentStreak} днів"
            )
            
            VerticalDivider(modifier = Modifier.height(40.dp))
            
            // Today
            StatItem(
                icon = "⏱️",
                label = "Сьогодні",
                value = "${stats.todayMinutes} хв"
            )
            
            VerticalDivider(modifier = Modifier.height(40.dp))
            
            // Total
            StatItem(
                icon = "📊",
                label = "Всього",
                value = "${stats.totalCompletions}"
            )
            
            VerticalDivider(modifier = Modifier.height(40.dp))
            
            // Level
            StatItem(
                icon = "⭐",
                label = "Рівень",
                value = "${stats.level}"
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
```

### 6. components/QuickWarmupCard.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickWarmupCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Комбінація найважливіших вправ",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Артикуляція + Дихання + Голос",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("▶️ Почати →")
            }
        }
    }
}
```

### 7. components/WarmupCategoryCard.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.warmup.WarmupCategory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WarmupCategoryCard(
    category: WarmupCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = category.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${category.exerciseCount} вправ • ~${category.estimatedMinutes} хвилини",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Description
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Last completed
            Text(
                text = "Останнє: ${formatLastCompleted(category.lastCompletedDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Progress
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { category.completionRate },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                )
                Text(
                    text = "${(category.completionRate * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun formatLastCompleted(date: String?): String {
    if (date == null) return "ніколи"
    
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val completedDate = sdf.parse(date) ?: return "невідомо"
    val today = Date()
    
    val diffInDays = ((today.time - completedDate.time) / (1000 * 60 * 60 * 24)).toInt()
    
    return when (diffInDays) {
        0 -> "сьогодні"
        1 -> "вчора"
        in 2..7 -> "$diffInDays дні тому"
        else -> "$diffInDays днів тому"
    }
}
```

---

## Перевірка

### 1. Компіляція
```bash
./gradlew assembleDebug
```

### 2. Testing Flow

**Тест 1: Stats Display**
- [ ] Stats card показує streak, сьогодні хв, всього, рівень
- [ ] Дані завантажуються з DataStore + Room

**Тест 2: Quick Warmup Card**
- [ ] Показується опис
- [ ] Кнопка "Почати" працює (navigation)

**Тест 3: Categories**
- [ ] 3 категорії відображаються (Articulation, Breathing, Voice)
- [ ] Іконки, назви, кількість вправ відображаються
- [ ] "Останнє:" показує правильну дату або "ніколи"
- [ ] Progress bar показує completion rate
- [ ] Клік на картку → навігація до відповідного екрану

**Тест 4: Tip Card**
- [ ] Підказка відображається внизу

---

## Очікуваний результат

✅ WarmupScreen (hub) створено
✅ Stats card працює з DataStore/Room
✅ 3 category cards відображаються
✅ Quick Warmup card створено
✅ Navigation готова для Phase 2.2-2.5
✅ Completion tracking підготовлено

---

## Що НЕ робити

❌ НЕ робити екрани Articulation/Breathing/Voice (Phase 2.2-2.5)
❌ НЕ робити таймери для вправ
❌ НЕ робити animations
❌ НЕ робити контент вправ (12+8+6)

---

## Наступний крок

**Phase 2.2: Articulation Screen** — 12 вправ з таймером та чеклістом.

---

**Час на Phase 2.1:** ~1 година