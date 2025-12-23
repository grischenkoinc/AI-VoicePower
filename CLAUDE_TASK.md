# Промпт для Claude Code — Phase 2.2: Articulation Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1 — Warmup Main Screen

Зараз **Phase 2.2 — Articulation Screen** — артикуляційна гімнастика (12 вправ).

**Згідно з PHASE_STRUCTURE_GUIDE.md**: СЕРЕДНЯ складність (UI + timer + checklist).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.4 + 5.4 (Warmup Exercise).

**Складність:** 🟡 СЕРЕДНЯ  
**Час:** ⏱️ 2 години

---

## Ключова ідея

⚠️ **БЕЗ запису аудіо!** Артикуляція — це фізичні вправи (як розтяжка).

**Механіка:**
1. Показати список 12 вправ
2. Клік на вправу → діалог з інструкцією + таймер
3. Користувач робить вправу
4. Позначає як виконано ✅
5. Прогрес зберігається в Room (WarmupCompletionDao)

---

## Задача Phase 2.2

Створити екран з **12 артикуляційними вправами**:

| # | Назва | Тривалість | Опис |
|---|-------|-----------|------|
| 1 | Усмішка-хоботок | 30 сек | Змінюй положення губ |
| 2 | Язик вліво-вправо | 20 сек | Рухи язиком в сторони |
| 3 | Язик вгору-вниз | 20 сек | Рухи язиком вверх-вниз |
| 4 | Коло язиком | 30 сек | Обертання навколо губ |
| 5 | Клацання язиком | 15 сек | Звук цокання |
| 6 | Масаж щік | 20 сек | Надувай і розслаблюй |
| 7 | Губи-трубочка | 20 сек | Витягни губи вперед |
| 8 | Широкий язик | 15 сек | Плоский язик на нижній губі |
| 9 | Гострий язик | 15 сек | Напружений вузький язик |
| 10 | Чашечка | 20 сек | Підняти боки язика |
| 11 | Гойдалка | 25 сек | Язик то вверх то вниз |
| 12 | Годинник | 30 сек | Рухи язиком як стрілки |

**Загальний час:** ~3 хвилини

---

## Структура файлів

```
ui/screens/warmup/
├── ArticulationScreen.kt
├── ArticulationViewModel.kt
├── ArticulationState.kt
├── ArticulationEvent.kt
└── components/
    ├── ArticulationExerciseItem.kt (картка вправи)
    └── ArticulationExerciseDialog.kt (діалог з таймером)

ui/components/timer/ (якщо ще немає з Phase 0.6)
└── CountdownTimer.kt (reusable timer component)
```

---

## UI Design

```
Step 1: Exercise List
┌──────────────────────────────────────────────┐
│  ← Артикуляційна гімнастика                  │
│  Виконано: 7/12                              │
├──────────────────────────────────────────────┤
│  ━━━━━━━━━━━━━━○○○○○○○  58%                  │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ✅ 1. Усмішка-хоботок        30 сек    │ │
│  └────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────┐ │
│  │ ✅ 2. Язик вліво-вправо      20 сек    │ │
│  └────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────┐ │
│  │ ⏸️ 3. Язик вгору-вниз        20 сек    │ │  ← Активна
│  └────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────┐ │
│  │ ○ 4. Коло язиком             30 сек    │ │
│  └────────────────────────────────────────┘ │
│  ...                                        │
│                                              │
│  [Завершити розминку]                       │
└──────────────────────────────────────────────┘

Step 2: Exercise Dialog
┌──────────────────────────────────────────────┐
│  3. Язик вгору-вниз                    [X]   │
├──────────────────────────────────────────────┤
│                                              │
│  📝 Інструкція:                              │
│  Рухай язиком вгору-вниз, торкаючись верхньої│
│  та нижньої губи. Виконуй повільно та       │
│  контрольовано.                              │
│                                              │
│  ⏱️ Тривалість: 20 секунд                    │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │                                        │ │
│  │              00:15                     │ │
│  │                                        │ │
│  │    ━━━━━━━━━━━━━━━○○○○○  75%          │ │
│  │                                        │ │
│  │         [⏸️ Пауза]                      │ │
│  │                                        │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  [Пропустити]          [Готово ✓]           │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Повний код

### 1. ArticulationState.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

data class ArticulationState(
    val exercises: List<ArticulationExercise> = getArticulationExercises(),
    val completedToday: Set<Int> = emptySet(), // Індекси виконаних вправ
    val selectedExercise: ArticulationExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val timerSeconds: Int = 0,
    val isTimerRunning: Boolean = false
)

data class ArticulationExercise(
    val id: Int,
    val title: String,
    val durationSeconds: Int,
    val instruction: String
)

private fun getArticulationExercises(): List<ArticulationExercise> {
    return listOf(
        ArticulationExercise(
            id = 1,
            title = "Усмішка-хоботок",
            durationSeconds = 30,
            instruction = "Широко посміхнись, показуючи зуби. Потім витягни губи вперед трубочкою. Чергуй ці положення."
        ),
        ArticulationExercise(
            id = 2,
            title = "Язик вліво-вправо",
            durationSeconds = 20,
            instruction = "Рухай язиком вліво-вправо, торкаючись куточків губ. Виконуй повільно та ритмічно."
        ),
        ArticulationExercise(
            id = 3,
            title = "Язик вгору-вниз",
            durationSeconds = 20,
            instruction = "Рухай язиком вгору-вниз, торкаючись верхньої та нижньої губи. Виконуй повільно та контрольовано."
        ),
        ArticulationExercise(
            id = 4,
            title = "Коло язиком",
            durationSeconds = 30,
            instruction = "Проводь кінчиком язика по зовнішньому боці зубів, роблячи коло. Спочатку за годинниковою, потім проти."
        ),
        ArticulationExercise(
            id = 5,
            title = "Клацання язиком",
            durationSeconds = 15,
            instruction = "Клацай язиком, як коник цокає копитами. Робіт чіткі, голосні звуки."
        ),
        ArticulationExercise(
            id = 6,
            title = "Масаж щік",
            durationSeconds = 20,
            instruction = "Надувай щоки, затримуй повітря на 2-3 секунди, потім розслабляй. Повтори кілька разів."
        ),
        ArticulationExercise(
            id = 7,
            title = "Губи-трубочка",
            durationSeconds = 20,
            instruction = "Витягни губи вперед трубочкою і затримай на кілька секунд. Розслабся і повтори."
        ),
        ArticulationExercise(
            id = 8,
            title = "Широкий язик",
            durationSeconds = 15,
            instruction = "Розслаб язик і поклади його плоско на нижню губу. Утримуй позицію."
        ),
        ArticulationExercise(
            id = 9,
            title = "Гострий язик",
            durationSeconds = 15,
            instruction = "Напружи язик і зроби його вузьким та гострим. Витягни вперед."
        ),
        ArticulationExercise(
            id = 10,
            title = "Чашечка",
            durationSeconds = 20,
            instruction = "Підніми боки язика вгору, формуючи чашечку. Утримуй позицію."
        ),
        ArticulationExercise(
            id = 11,
            title = "Гойдалка",
            durationSeconds = 25,
            instruction = "Рухай язиком вверх (до носа) і вниз (до підборіддя), як на гойдалці."
        ),
        ArticulationExercise(
            id = 12,
            title = "Годинник",
            durationSeconds = 30,
            instruction = "Рухай язиком по колу, ніби стрілки годинника. Спочатку повільно, потім трохи швидше."
        )
    )
}
```

### 2. ArticulationEvent.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

sealed class ArticulationEvent {
    data class ExerciseClicked(val exercise: ArticulationExercise) : ArticulationEvent()
    object ExerciseDialogDismissed : ArticulationEvent()
    object StartTimer : ArticulationEvent()
    object PauseTimer : ArticulationEvent()
    data class TimerTick(val secondsRemaining: Int) : ArticulationEvent()
    object MarkAsCompleted : ArticulationEvent()
    object SkipExercise : ArticulationEvent()
    object FinishWarmup : ArticulationEvent()
}
```

### 3. ArticulationViewModel.kt

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
class ArticulationViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(ArticulationState())
    val state: StateFlow<ArticulationState> = _state.asStateFlow()
    
    private var timerJob: Job? = null
    
    init {
        loadTodayProgress()
    }
    
    fun onEvent(event: ArticulationEvent) {
        when (event) {
            is ArticulationEvent.ExerciseClicked -> {
                _state.update {
                    it.copy(
                        selectedExercise = event.exercise,
                        isExerciseDialogOpen = true,
                        timerSeconds = event.exercise.durationSeconds,
                        isTimerRunning = false
                    )
                }
            }
            
            ArticulationEvent.ExerciseDialogDismissed -> {
                stopTimer()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false,
                        timerSeconds = 0,
                        isTimerRunning = false
                    )
                }
            }
            
            ArticulationEvent.StartTimer -> {
                startTimer()
            }
            
            ArticulationEvent.PauseTimer -> {
                stopTimer()
            }
            
            is ArticulationEvent.TimerTick -> {
                _state.update { it.copy(timerSeconds = event.secondsRemaining) }
                
                if (event.secondsRemaining <= 0) {
                    stopTimer()
                    // Auto-mark as completed
                    markCurrentAsCompleted()
                }
            }
            
            ArticulationEvent.MarkAsCompleted -> {
                markCurrentAsCompleted()
            }
            
            ArticulationEvent.SkipExercise -> {
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false
                    )
                }
            }
            
            ArticulationEvent.FinishWarmup -> {
                finishWarmup()
            }
        }
    }
    
    private fun loadTodayProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val completion = warmupCompletionDao.getCompletion(today, "articulation")
            
            if (completion != null) {
                // Парсимо які вправи виконано (можна зберігати як JSON або bitmap)
                // Поки що просто вважаємо exercisesCompleted = кількість
                val completed = (1..completion.exercisesCompleted).toSet()
                _state.update { it.copy(completedToday = completed) }
            }
        }
    }
    
    private fun startTimer() {
        stopTimer() // Зупиняємо попередній таймер
        
        _state.update { it.copy(isTimerRunning = true) }
        
        timerJob = viewModelScope.launch {
            while (_state.value.isTimerRunning && _state.value.timerSeconds > 0) {
                delay(1000)
                val newSeconds = _state.value.timerSeconds - 1
                onEvent(ArticulationEvent.TimerTick(newSeconds))
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isTimerRunning = false) }
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
                id = "${today}_articulation",
                date = today,
                category = "articulation",
                completedAt = System.currentTimeMillis(),
                exercisesCompleted = completedCount,
                totalExercises = totalExercises
            )
            
            warmupCompletionDao.insertOrUpdate(entity)
            
            // Оновлюємо todayMinutes в DataStore
            val estimatedMinutes = 3 // Артикуляція ~3 хв
            if (completedCount == totalExercises) {
                userPreferencesDataStore.updateSessionStats(
                    date = today,
                    minutes = estimatedMinutes,
                    exercises = 1
                )
            }
        }
    }
    
    private fun finishWarmup() {
        saveProgress()
        // Navigation handled in Screen
    }
    
    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
```

### 4. ArticulationScreen.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticulationScreen(
    viewModel: ArticulationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Артикуляційна гімнастика") },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Виконано: ${state.completedToday.size}/${state.exercises.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
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
                    ArticulationExerciseItem(
                        exercise = exercise,
                        isCompleted = state.completedToday.contains(exercise.id),
                        onClick = {
                            viewModel.onEvent(ArticulationEvent.ExerciseClicked(exercise))
                        }
                    )
                }
                
                // Finish button
                item {
                    if (state.completedToday.size == state.exercises.size) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                viewModel.onEvent(ArticulationEvent.FinishWarmup)
                                onNavigateBack()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Завершити розминку ✓")
                        }
                    }
                }
            }
        }
        
        // Exercise dialog
        if (state.isExerciseDialogOpen && state.selectedExercise != null) {
            ArticulationExerciseDialog(
                exercise = state.selectedExercise!!,
                timerSeconds = state.timerSeconds,
                isTimerRunning = state.isTimerRunning,
                onDismiss = {
                    viewModel.onEvent(ArticulationEvent.ExerciseDialogDismissed)
                },
                onStartTimer = {
                    viewModel.onEvent(ArticulationEvent.StartTimer)
                },
                onPauseTimer = {
                    viewModel.onEvent(ArticulationEvent.PauseTimer)
                },
                onMarkCompleted = {
                    viewModel.onEvent(ArticulationEvent.MarkAsCompleted)
                },
                onSkip = {
                    viewModel.onEvent(ArticulationEvent.SkipExercise)
                }
            )
        }
    }
}
```

### 5. components/ArticulationExerciseItem.kt

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
import com.aivoicepower.ui.screens.warmup.ArticulationExercise

@Composable
fun ArticulationExerciseItem(
    exercise: ArticulationExercise,
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
                
                Text(
                    text = "${exercise.id}. ${exercise.title}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Text(
                text = "${exercise.durationSeconds} сек",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### 6. components/ArticulationExerciseDialog.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aivoicepower.ui.screens.warmup.ArticulationExercise

@Composable
fun ArticulationExerciseDialog(
    exercise: ArticulationExercise,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    onDismiss: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onMarkCompleted: () -> Unit,
    onSkip: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${exercise.id}. ${exercise.title}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
                    }
                }
                
                Divider()
                
                // Instruction
                Text(
                    text = "📝 Інструкція:",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = exercise.instruction,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "⏱️ Тривалість: ${exercise.durationSeconds} секунд",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Timer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Time display
                        Text(
                            text = "%02d:%02d".format(timerSeconds / 60, timerSeconds % 60),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        // Progress
                        LinearProgressIndicator(
                            progress = { 1f - (timerSeconds.toFloat() / exercise.durationSeconds) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Timer button
                        Button(
                            onClick = if (isTimerRunning) onPauseTimer else onStartTimer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isTimerRunning) "⏸️ Пауза" else "▶️ Старт")
                        }
                    }
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
```

---

## Перевірка

### 1. Компіляція
```bash
./gradlew assembleDebug
```

### 2. Testing Flow

**Тест 1: Exercise List**
- [ ] 12 вправ відображаються
- [ ] Progress bar показує 0/12
- [ ] Completed exercises позначені ✅

**Тест 2: Exercise Dialog**
- [ ] Клік на вправу → діалог з інструкцією
- [ ] Таймер показує правильну тривалість
- [ ] "Старт" → таймер працює
- [ ] "Пауза" → таймер зупиняється
- [ ] "Готово" → вправа позначається ✅
- [ ] "Пропустити" → закриває діалог

**Тест 3: Progress Tracking**
- [ ] Progress bar оновлюється після кожної вправи
- [ ] Після 12/12 з'являється кнопка "Завершити"
- [ ] Дані зберігаються в Room

**Тест 4: Auto-complete**
- [ ] Коли таймер досягає 0 → auto-mark as completed

---

## Очікуваний результат

✅ ArticulationScreen з 12 вправами створено
✅ Exercise dialog з таймером працює
✅ Progress tracking з Room
✅ Checklist механіка
✅ Auto-complete по таймеру
✅ Завершення розминки оновлює DataStore

---

## Наступний крок

**Phase 2.3: Breathing Screen** — 8 вправ з Canvas animations та haptic feedback.

---

**Час на Phase 2.2:** ~2 години