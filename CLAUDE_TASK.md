# Промпт для Claude Code — Phase 2.3: Breathing Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1 — Warmup Main Screen
- ✅ Phase 2.2 — Articulation Screen

Зараз **Phase 2.3 — Breathing Screen** — дихальні вправи з **Canvas animations**.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: ВИСОКА складність (Canvas animations, haptic feedback).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.4 + 5.4 (Warmup Exercise, Breathing).

**Складність:** 🔴 ВИСОКА  
**Час:** ⏱️ 3-4 години

---

## Ключова ідея

⚠️ **БЕЗ запису аудіо!** Дихання — це фізична вправа.

**Механіка:**
1. Показати список 8 дихальних вправ
2. Клік на вправу → повноекранний діалог з **анімацією дихання**
3. Анімація керує ритмом (вдих/видих/затримка)
4. **Вібрація** на переходах (вдих → видих)
5. Користувач слідує анімації
6. Після завершення → позначається як виконано ✅
7. Прогрес зберігається в Room

---

## Задача Phase 2.3

Створити екран з **8 дихальними вправами**:

| # | Назва | Тривалість | Паттерн дихання |
|---|-------|-----------|-----------------|
| 1 | Діафрагмальне дихання | 60 сек | 4 сек вдих, 4 сек видих |
| 2 | Квадратне дихання | 60 сек | 4-4-4-4 (вдих-затримка-видих-затримка) |
| 3 | 4-7-8 дихання | 60 сек | 4 сек вдих, 7 сек затримка, 8 сек видих |
| 4 | Спокійне дихання | 45 сек | 3 сек вдих, 5 сек видих |
| 5 | Енергійне дихання | 30 сек | 2 сек вдих, 2 сек видих (швидко) |
| 6 | Глибоке дихання | 60 сек | 6 сек вдих, 6 сек видих |
| 7 | Розслаблююче дихання | 60 сек | 4 сек вдих, 8 сек видих |
| 8 | Ритмічне дихання | 45 сек | 3-3-3-3 |

**Загальний час:** ~7 хвилин (якщо виконувати всі)

---

## Структура файлів

```
ui/screens/warmup/
├── BreathingScreen.kt
├── BreathingViewModel.kt
├── BreathingState.kt
├── BreathingEvent.kt
└── components/
    ├── BreathingExerciseItem.kt (картка вправи)
    └── BreathingExerciseDialog.kt (повноекранний з анімацією)

ui/components/breathing/ (нові компоненти)
├── BreathingAnimation.kt (Canvas коло що розширюється/стискається)
├── BreathingCircle.kt (Canvas implementation)
└── BreathingPhaseText.kt ("Вдих...", "Видих...", "Затримка...")

utils/
└── HapticFeedbackUtil.kt (вібрація)
```

---

## UI Design

```
Step 1: Exercise List (як в Phase 2.2)
┌──────────────────────────────────────────────┐
│  ← Дихальні вправи                           │
│  Виконано: 3/8                               │
├──────────────────────────────────────────────┤
│  ━━━━━━━○○○○○○○○○○○○○○  38%                  │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ✅ 1. Діафрагмальне дихання   60 сек   │ │
│  └────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────┐ │
│  │ ○ 2. Квадратне дихання        60 сек   │ │
│  └────────────────────────────────────────┘ │
│  ...                                        │
└──────────────────────────────────────────────┘

Step 2: Breathing Dialog (FULLSCREEN)
┌──────────────────────────────────────────────┐
│  Квадратне дихання                     [X]   │
├──────────────────────────────────────────────┤
│                                              │
│                                              │
│              ╭─────────────╮                 │
│             ╱               ╲                │
│            │     ВДИХ...     │               │  ← Animated
│            │                 │               │     Circle
│            │    00:04 / 60   │               │
│             ╲               ╱                │
│              ╰─────────────╯                 │
│                                              │
│                                              │
│          ━━━━━━━━━━━━━━○○○○○  60%            │
│                                              │
│                                              │
│  Паттерн: 4 сек вдих → 4 сек затримка →     │
│           4 сек видих → 4 сек затримка       │
│                                              │
│              [⏸️ Пауза]                       │
│                                              │
│  [Пропустити]          [Готово ✓]           │
│                                              │
└──────────────────────────────────────────────┘

Анімація кола:
- Вдих: коло розширюється (scale 1.0 → 1.5)
- Затримка: коло статичне
- Видих: коло стискається (scale 1.5 → 1.0)
- Затримка: коло статичне

Вібрація:
- На початку вдиху (короткий пульс)
- На початку видиху (короткий пульс)
```

---

## Повний код

### 1. BreathingState.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

data class BreathingState(
    val exercises: List<BreathingExercise> = getBreathingExercises(),
    val completedToday: Set<Int> = emptySet(),
    val selectedExercise: BreathingExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val totalSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val currentPhase: BreathingPhase = BreathingPhase.INHALE,
    val phaseProgress: Float = 0f, // 0.0 - 1.0
    val isRunning: Boolean = false
)

data class BreathingExercise(
    val id: Int,
    val title: String,
    val durationSeconds: Int,
    val pattern: BreathingPattern,
    val description: String
)

data class BreathingPattern(
    val inhaleSeconds: Int,
    val inhaleHoldSeconds: Int = 0,
    val exhaleSeconds: Int,
    val exhaleHoldSeconds: Int = 0
) {
    val cycleDurationSeconds: Int
        get() = inhaleSeconds + inhaleHoldSeconds + exhaleSeconds + exhaleHoldSeconds
}

enum class BreathingPhase {
    INHALE,        // Вдих
    INHALE_HOLD,   // Затримка після вдиху
    EXHALE,        // Видих
    EXHALE_HOLD    // Затримка після видиху
}

private fun getBreathingExercises(): List<BreathingExercise> {
    return listOf(
        BreathingExercise(
            id = 1,
            title = "Діафрагмальне дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                exhaleSeconds = 4
            ),
            description = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху."
        ),
        BreathingExercise(
            id = 2,
            title = "Квадратне дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                inhaleHoldSeconds = 4,
                exhaleSeconds = 4,
                exhaleHoldSeconds = 4
            ),
            description = "Рівні інтервали для кожної фази. Допомагає зосередитися та заспокоїтися."
        ),
        BreathingExercise(
            id = 3,
            title = "4-7-8 дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                inhaleHoldSeconds = 7,
                exhaleSeconds = 8
            ),
            description = "Техніка для швидкого заспокоєння. Довгий видих активує парасимпатичну нервову систему."
        ),
        BreathingExercise(
            id = 4,
            title = "Спокійне дихання",
            durationSeconds = 45,
            pattern = BreathingPattern(
                inhaleSeconds = 3,
                exhaleSeconds = 5
            ),
            description = "Довший видих допомагає розслабитися. Ідеально перед сном."
        ),
        BreathingExercise(
            id = 5,
            title = "Енергійне дихання",
            durationSeconds = 30,
            pattern = BreathingPattern(
                inhaleSeconds = 2,
                exhaleSeconds = 2
            ),
            description = "Швидке ритмічне дихання для підвищення енергії. Будьте обережні, не перестарайтеся."
        ),
        BreathingExercise(
            id = 6,
            title = "Глибоке дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 6,
                exhaleSeconds = 6
            ),
            description = "Повільне глибоке дихання насичує організм киснем. Дихайте через ніс."
        ),
        BreathingExercise(
            id = 7,
            title = "Розслаблююче дихання",
            durationSeconds = 60,
            pattern = BreathingPattern(
                inhaleSeconds = 4,
                exhaleSeconds = 8
            ),
            description = "Подвійна тривалість видиху максимально розслабляє. Відчуйте напругу що йде."
        ),
        BreathingExercise(
            id = 8,
            title = "Ритмічне дихання",
            durationSeconds = 45,
            pattern = BreathingPattern(
                inhaleSeconds = 3,
                inhaleHoldSeconds = 3,
                exhaleSeconds = 3,
                exhaleHoldSeconds = 3
            ),
            description = "Рівний ритм створює медитативний стан. Зосередьтеся на рахунку."
        )
    )
}
```

### 2. BreathingEvent.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

sealed class BreathingEvent {
    data class ExerciseClicked(val exercise: BreathingExercise) : BreathingEvent()
    object ExerciseDialogDismissed : BreathingEvent()
    object StartBreathing : BreathingEvent()
    object PauseBreathing : BreathingEvent()
    data class Tick(val elapsedSeconds: Int, val phase: BreathingPhase, val phaseProgress: Float) : BreathingEvent()
    object MarkAsCompleted : BreathingEvent()
    object SkipExercise : BreathingEvent()
}
```

### 3. BreathingViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(BreathingState())
    val state: StateFlow<BreathingState> = _state.asStateFlow()
    
    private var breathingJob: Job? = null
    
    init {
        loadTodayProgress()
    }
    
    fun onEvent(event: BreathingEvent) {
        when (event) {
            is BreathingEvent.ExerciseClicked -> {
                _state.update {
                    it.copy(
                        selectedExercise = event.exercise,
                        isExerciseDialogOpen = true,
                        totalSeconds = event.exercise.durationSeconds,
                        elapsedSeconds = 0,
                        currentPhase = BreathingPhase.INHALE,
                        phaseProgress = 0f,
                        isRunning = false
                    )
                }
            }
            
            BreathingEvent.ExerciseDialogDismissed -> {
                stopBreathing()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false
                    )
                }
            }
            
            BreathingEvent.StartBreathing -> {
                startBreathing()
            }
            
            BreathingEvent.PauseBreathing -> {
                stopBreathing()
            }
            
            is BreathingEvent.Tick -> {
                _state.update {
                    it.copy(
                        elapsedSeconds = event.elapsedSeconds,
                        currentPhase = event.phase,
                        phaseProgress = event.phaseProgress
                    )
                }
                
                // Auto-complete when done
                if (event.elapsedSeconds >= _state.value.totalSeconds) {
                    stopBreathing()
                    markCurrentAsCompleted()
                }
            }
            
            BreathingEvent.MarkAsCompleted -> {
                markCurrentAsCompleted()
            }
            
            BreathingEvent.SkipExercise -> {
                stopBreathing()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false
                    )
                }
            }
        }
    }
    
    private fun loadTodayProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val completion = warmupCompletionDao.getCompletion(today, "breathing")
            
            if (completion != null) {
                val completed = (1..completion.exercisesCompleted).toSet()
                _state.update { it.copy(completedToday = completed) }
            }
        }
    }
    
    private fun startBreathing() {
        stopBreathing()
        
        _state.update { it.copy(isRunning = true) }
        
        val pattern = _state.value.selectedExercise?.pattern ?: return
        
        breathingJob = viewModelScope.launch {
            var elapsed = _state.value.elapsedSeconds
            var cycleStart = elapsed % pattern.cycleDurationSeconds
            
            while (_state.value.isRunning && elapsed < _state.value.totalSeconds) {
                delay(100) // Оновлюємо кожні 100ms для плавної анімації
                
                elapsed += 0.1f.toInt()
                cycleStart += 0.1f.toInt()
                
                val (phase, progress) = calculatePhaseAndProgress(cycleStart, pattern)
                
                onEvent(BreathingEvent.Tick(elapsed, phase, progress))
                
                // Reset cycle
                if (cycleStart >= pattern.cycleDurationSeconds) {
                    cycleStart = 0
                }
            }
        }
    }
    
    private fun stopBreathing() {
        breathingJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }
    
    private fun calculatePhaseAndProgress(
        secondsInCycle: Int,
        pattern: BreathingPattern
    ): Pair<BreathingPhase, Float> {
        var remaining = secondsInCycle
        
        // INHALE
        if (remaining < pattern.inhaleSeconds) {
            return BreathingPhase.INHALE to (remaining.toFloat() / pattern.inhaleSeconds)
        }
        remaining -= pattern.inhaleSeconds
        
        // INHALE_HOLD
        if (pattern.inhaleHoldSeconds > 0 && remaining < pattern.inhaleHoldSeconds) {
            return BreathingPhase.INHALE_HOLD to (remaining.toFloat() / pattern.inhaleHoldSeconds)
        }
        remaining -= pattern.inhaleHoldSeconds
        
        // EXHALE
        if (remaining < pattern.exhaleSeconds) {
            return BreathingPhase.EXHALE to (remaining.toFloat() / pattern.exhaleSeconds)
        }
        remaining -= pattern.exhaleSeconds
        
        // EXHALE_HOLD
        if (pattern.exhaleHoldSeconds > 0 && remaining < pattern.exhaleHoldSeconds) {
            return BreathingPhase.EXHALE_HOLD to (remaining.toFloat() / pattern.exhaleHoldSeconds)
        }
        
        return BreathingPhase.INHALE to 0f
    }
    
    private fun markCurrentAsCompleted() {
        val exerciseId = _state.value.selectedExercise?.id ?: return
        
        _state.update {
            it.copy(
                completedToday = it.completedToday + exerciseId,
                selectedExercise = null,
                isExerciseDialogOpen = false
            )
        }
        
        saveProgress()
    }
    
    private fun saveProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val totalExercises = _state.value.exercises.size
            val completedCount = _state.value.completedToday.size
            
            val entity = WarmupCompletionEntity(
                id = "${today}_breathing",
                date = today,
                category = "breathing",
                completedAt = System.currentTimeMillis(),
                exercisesCompleted = completedCount,
                totalExercises = totalExercises
            )
            
            warmupCompletionDao.insertOrUpdate(entity)
            
            // Оновлюємо DataStore
            val estimatedMinutes = 2
            if (completedCount == totalExercises) {
                userPreferencesDataStore.updateSessionStats(
                    date = today,
                    minutes = estimatedMinutes,
                    exercises = 1
                )
            }
        }
    }
    
    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
```

### 4. BreathingScreen.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    viewModel: BreathingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дихальні вправи") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Виконано: ${state.completedToday.size}/${state.exercises.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    LinearProgressIndicator(
                        progress = { state.completedToday.size.toFloat() / state.exercises.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }
            
            // Exercise list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(state.exercises) { index, exercise ->
                    BreathingExerciseItem(
                        exercise = exercise,
                        isCompleted = state.completedToday.contains(exercise.id),
                        onClick = {
                            viewModel.onEvent(BreathingEvent.ExerciseClicked(exercise))
                        }
                    )
                }
                
                // Finish button
                item {
                    if (state.completedToday.size == state.exercises.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Завершити розминку ✓")
                        }
                    }
                }
            }
        }
        
        // Exercise dialog (fullscreen)
        if (state.isExerciseDialogOpen && state.selectedExercise != null) {
            BreathingExerciseDialog(
                exercise = state.selectedExercise!!,
                elapsedSeconds = state.elapsedSeconds,
                totalSeconds = state.totalSeconds,
                currentPhase = state.currentPhase,
                phaseProgress = state.phaseProgress,
                isRunning = state.isRunning,
                onDismiss = {
                    viewModel.onEvent(BreathingEvent.ExerciseDialogDismissed)
                },
                onStart = {
                    viewModel.onEvent(BreathingEvent.StartBreathing)
                },
                onPause = {
                    viewModel.onEvent(BreathingEvent.PauseBreathing)
                },
                onMarkCompleted = {
                    viewModel.onEvent(BreathingEvent.MarkAsCompleted)
                },
                onSkip = {
                    viewModel.onEvent(BreathingEvent.SkipExercise)
                }
            )
        }
    }
}
```

### 5. components/BreathingExerciseItem.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.warmup.BreathingExercise

@Composable
fun BreathingExerciseItem(
    exercise: BreathingExercise,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = if (isCompleted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Column {
                    Text(
                        text = "${exercise.id}. ${exercise.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = formatPattern(exercise.pattern),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "${exercise.durationSeconds} сек",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatPattern(pattern: com.aivoicepower.ui.screens.warmup.BreathingPattern): String {
    val parts = mutableListOf<String>()
    
    parts.add("${pattern.inhaleSeconds}с вдих")
    if (pattern.inhaleHoldSeconds > 0) parts.add("${pattern.inhaleHoldSeconds}с затримка")
    parts.add("${pattern.exhaleSeconds}с видих")
    if (pattern.exhaleHoldSeconds > 0) parts.add("${pattern.exhaleHoldSeconds}с затримка")
    
    return parts.joinToString(", ")
}
```

### 6. components/BreathingExerciseDialog.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aivoicepower.ui.screens.warmup.BreathingExercise
import com.aivoicepower.ui.screens.warmup.BreathingPhase
import com.aivoicepower.ui.components.breathing.BreathingAnimation

@Composable
fun BreathingExerciseDialog(
    exercise: BreathingExercise,
    elapsedSeconds: Int,
    totalSeconds: Int,
    currentPhase: BreathingPhase,
    phaseProgress: Float,
    isRunning: Boolean,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onMarkCompleted: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    
    // Haptic feedback on phase change
    var lastPhase by remember { mutableStateOf(currentPhase) }
    
    LaunchedEffect(currentPhase) {
        if (currentPhase != lastPhase && isRunning) {
            triggerHapticFeedback(context)
            lastPhase = currentPhase
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
                    }
                }
                
                // Breathing Animation (CENTER)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Animated Circle
                        BreathingAnimation(
                            phase = currentPhase,
                            progress = phaseProgress,
                            modifier = Modifier.size(250.dp)
                        )
                        
                        // Phase text
                        Text(
                            text = getPhaseText(currentPhase),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Timer
                        Text(
                            text = "%02d:%02d / %02d:%02d".format(
                                elapsedSeconds / 60,
                                elapsedSeconds % 60,
                                totalSeconds / 60,
                                totalSeconds % 60
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Bottom controls
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress
                    LinearProgressIndicator(
                        progress = { elapsedSeconds.toFloat() / totalSeconds },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Pattern description
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Паттерн:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = formatPatternDescription(exercise.pattern),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    // Start/Pause button
                    Button(
                        onClick = if (isRunning) onPause else onStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isRunning) "⏸️ Пауза" else "▶️ Старт")
                    }
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Пропустити")
                        }
                        
                        Button(
                            onClick = onMarkCompleted,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Готово ✓")
                        }
                    }
                }
            }
        }
    }
}

private fun getPhaseText(phase: BreathingPhase): String {
    return when (phase) {
        BreathingPhase.INHALE -> "Вдих..."
        BreathingPhase.INHALE_HOLD -> "Затримка..."
        BreathingPhase.EXHALE -> "Видих..."
        BreathingPhase.EXHALE_HOLD -> "Затримка..."
    }
}

private fun formatPatternDescription(pattern: com.aivoicepower.ui.screens.warmup.BreathingPattern): String {
    val parts = mutableListOf<String>()
    
    parts.add("${pattern.inhaleSeconds} сек вдих")
    if (pattern.inhaleHoldSeconds > 0) parts.add("${pattern.inhaleHoldSeconds} сек затримка")
    parts.add("${pattern.exhaleSeconds} сек видих")
    if (pattern.exhaleHoldSeconds > 0) parts.add("${pattern.exhaleHoldSeconds} сек затримка")
    
    return parts.joinToString(" → ")
}

@Suppress("DEPRECATION")
private fun triggerHapticFeedback(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    } catch (e: Exception) {
        // Ignore vibration errors
    }
}
```

### 7. ui/components/breathing/BreathingAnimation.kt

```kotlin
package com.aivoicepower.ui.components.breathing

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.aivoicepower.ui.screens.warmup.BreathingPhase

@Composable
fun BreathingAnimation(
    phase: BreathingPhase,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    
    // Calculate scale based on phase
    val targetScale = when (phase) {
        BreathingPhase.INHALE -> 0.5f + (progress * 0.5f) // 0.5 → 1.0
        BreathingPhase.INHALE_HOLD -> 1.0f
        BreathingPhase.EXHALE -> 1.0f - (progress * 0.5f) // 1.0 → 0.5
        BreathingPhase.EXHALE_HOLD -> 0.5f
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "breathing_scale"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f
        val currentRadius = maxRadius * animatedScale
        
        // Gradient circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.3f),
                    secondaryColor.copy(alpha = 0.1f)
                ),
                center = center,
                radius = currentRadius
            ),
            radius = currentRadius,
            center = center
        )
        
        // Outer ring
        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = currentRadius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
    }
}
```

---

## AndroidManifest.xml

Додай дозвіл для вібрації:

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

---

## Перевірка

### 1. Компіляція
```bash
./gradlew assembleDebug
```

### 2. Testing Flow

**Тест 1: Exercise List**
- [ ] 8 вправ відображаються з паттернами
- [ ] Progress bar показує 0/8

**Тест 2: Breathing Animation**
- [ ] Клік на вправу → fullscreen dialog
- [ ] Коло розширюється на вдиху
- [ ] Коло стискається на видиху
- [ ] Коло статичне при затримці
- [ ] Анімація плавна (60 FPS)

**Тест 3: Phase Transitions**
- [ ] Текст міняється: "Вдих..." → "Затримка..." → "Видих..."
- [ ] Вібрація спрацьовує на переходах
- [ ] Progress bar оновлюється

**Тест 4: Timer**
- [ ] Таймер йде від 00:00 до XX:XX
- [ ] "Старт" → анімація починається
- [ ] "Пауза" → анімація зупиняється
- [ ] Auto-complete коли час закінчується

**Тест 5: Completion**
- [ ] "Готово" → вправа позначається ✅
- [ ] Дані зберігаються в Room
- [ ] Progress оновлюється

---

## Очікуваний результат

✅ BreathingScreen з 8 вправами створено
✅ Canvas animation (коло що дихає)
✅ Haptic feedback на переходах
✅ Fullscreen breathing dialog
✅ Phase text ("Вдих", "Видих", "Затримка")
✅ Progress tracking з Room
✅ Auto-complete по таймеру

---

## Наступний крок

**Phase 2.4: Voice Warmup Screen** — 6 вправ з аудіо прикладами (optional).

---

**Час на Phase 2.3:** ~3-4 години

**⚠️ Це найскладніша підфаза Phase 2** — Canvas animations, haptic feedback, складна логіка фаз.