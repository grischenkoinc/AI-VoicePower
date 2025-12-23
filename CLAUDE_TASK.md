# Промпт для Claude Code — Phase 2.5: Quick Warmup Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1 — Warmup Main Screen
- ✅ Phase 2.2 — Articulation Screen
- ✅ Phase 2.3 — Breathing Screen
- ✅ Phase 2.4 — Voice Warmup Screen

Зараз **Phase 2.5 — Quick Warmup Screen** — **фінальна підфаза Phase 2**.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: НИЗЬКА складність (sequential flow, reuse components).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.4 (Quick Warmup).

**Складність:** 🟢 НИЗЬКА  
**Час:** ⏱️ 1 година

---

## Ключова ідея

**Quick Warmup** — це швидка 5-хвилинна розминка, що включає **найважливіші вправи** з всіх 3 категорій:

| Категорія | Вправи | Час |
|-----------|--------|-----|
| Артикуляція | 2 вправи | ~1 хв |
| Дихання | 1 вправа | ~1 хв |
| Голос | 1 вправа | ~30 сек |
| **Всього** | **4 вправи** | **~2.5 хв** |

**Механіка:**
1. **Sequential flow** — вправи виконуються одна за одною
2. Користувач не може пропустити вправи (або можна, але не рекомендовано)
3. Progress bar показує загальний прогрес (1/4, 2/4, 3/4, 4/4)
4. Після завершення → збереження як окрема категорія "quick"

**Reuse components:**
- Використовуємо компоненти з Phase 2.2-2.4
- Не створюємо нові UI компоненти

---

## Задача Phase 2.5

Створити екран з **послідовним виконанням 4 вправ**:

| # | Вправа | Категорія | Тривалість |
|---|--------|-----------|-----------|
| 1 | Усмішка-хоботок | Артикуляція | 30 сек |
| 2 | Язик вліво-вправо | Артикуляція | 20 сек |
| 3 | Діафрагмальне дихання | Дихання | 60 сек |
| 4 | Гумкання | Голос | 30 сек |

**Загальний час:** ~2.5 хвилини (можна округлити до 3 хв)

---

## Структура файлів

```
ui/screens/warmup/
├── QuickWarmupScreen.kt
├── QuickWarmupViewModel.kt
├── QuickWarmupState.kt
└── QuickWarmupEvent.kt

// Reuse components з Phase 2.2-2.4:
// - ArticulationExerciseDialog.kt
// - BreathingExerciseDialog.kt
// - VoiceExerciseDialog.kt
```

---

## UI Design

```
Quick Warmup Flow (Sequential)
┌──────────────────────────────────────────────┐
│  ← Швидка розминка (5 хв)                    │
│  Вправа 2 з 4                                │
├──────────────────────────────────────────────┤
│  ━━━━━━━━━━━━○○○○○○○○○○  50%                │
│                                              │
│  ✅ 1. Усмішка-хоботок                       │
│  ▶️ 2. Язик вліво-вправо       (активна)     │
│  ○ 3. Діафрагмальне дихання                  │
│  ○ 4. Гумкання                               │
│                                              │
│  ──────────────────────────────────────────  │
│                                              │
│  [Показується діалог вправи 2]               │
│                                              │
└──────────────────────────────────────────────┘

Dialog (reuse з Phase 2.2-2.4):
- Артикуляція → ArticulationExerciseDialog
- Дихання → BreathingExerciseDialog
- Голос → VoiceExerciseDialog

Після завершення всіх 4 вправ:
┌──────────────────────────────────────────────┐
│  🎉 Розминка завершена!                      │
│                                              │
│  Ви виконали 4 вправи за 2 хв 34 сек        │
│                                              │
│  [Готово]                                    │
└──────────────────────────────────────────────┘
```

---

## Повний код

### 1. QuickWarmupState.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

data class QuickWarmupState(
    val exercises: List<QuickWarmupExercise> = getQuickWarmupExercises(),
    val currentExerciseIndex: Int = 0,
    val completedExercises: Set<Int> = emptySet(),
    val isExerciseDialogOpen: Boolean = false,
    val totalElapsedSeconds: Int = 0,
    val isCompleted: Boolean = false
)

data class QuickWarmupExercise(
    val id: Int,
    val title: String,
    val category: WarmupCategoryType,
    val durationSeconds: Int,
    val instruction: String,
    // Type-specific data
    val articulationExercise: ArticulationExercise? = null,
    val breathingExercise: BreathingExercise? = null,
    val voiceExercise: VoiceExercise? = null
)

enum class WarmupCategoryType {
    ARTICULATION, BREATHING, VOICE
}

private fun getQuickWarmupExercises(): List<QuickWarmupExercise> {
    return listOf(
        // 1. Артикуляція: Усмішка-хоботок
        QuickWarmupExercise(
            id = 1,
            title = "Усмішка-хоботок",
            category = WarmupCategoryType.ARTICULATION,
            durationSeconds = 30,
            instruction = "Широко посміхнись, показуючи зуби. Потім витягни губи вперед трубочкою. Чергуй ці положення.",
            articulationExercise = ArticulationExercise(
                id = 1,
                title = "Усмішка-хоботок",
                durationSeconds = 30,
                instruction = "Широко посміхнись, показуючи зуби. Потім витягни губи вперед трубочкою. Чергуй ці положення."
            )
        ),
        
        // 2. Артикуляція: Язик вліво-вправо
        QuickWarmupExercise(
            id = 2,
            title = "Язик вліво-вправо",
            category = WarmupCategoryType.ARTICULATION,
            durationSeconds = 20,
            instruction = "Рухай язиком вліво-вправо, торкаючись куточків губ. Виконуй повільно та ритмічно.",
            articulationExercise = ArticulationExercise(
                id = 2,
                title = "Язик вліво-вправо",
                durationSeconds = 20,
                instruction = "Рухай язиком вліво-вправо, торкаючись куточків губ. Виконуй повільно та ритмічно."
            )
        ),
        
        // 3. Дихання: Діафрагмальне
        QuickWarmupExercise(
            id = 3,
            title = "Діафрагмальне дихання",
            category = WarmupCategoryType.BREATHING,
            durationSeconds = 60,
            instruction = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху.",
            breathingExercise = BreathingExercise(
                id = 1,
                title = "Діафрагмальне дихання",
                durationSeconds = 60,
                pattern = BreathingPattern(
                    inhaleSeconds = 4,
                    exhaleSeconds = 4
                ),
                description = "Глибоке дихання животом. Покладіть руку на живіт і відчуйте як він піднімається на вдиху."
            )
        ),
        
        // 4. Голос: Гумкання
        QuickWarmupExercise(
            id = 4,
            title = "Гумкання",
            category = WarmupCategoryType.VOICE,
            durationSeconds = 30,
            instruction = "Закрийте рот і гучно \"ммм\" на комфортній для вас ноті. Відчуйте вібрацію в носі та губах.",
            voiceExercise = VoiceExercise(
                id = 1,
                title = "Гумкання",
                durationSeconds = 30,
                instruction = "Закрийте рот і гучно \"ммм\" на комфортній для вас ноті. Відчуйте вібрацію в носі та губах.",
                audioExampleUrl = null
            )
        )
    )
}
```

### 2. QuickWarmupEvent.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

sealed class QuickWarmupEvent {
    object StartQuickWarmup : QuickWarmupEvent()
    object CurrentExerciseCompleted : QuickWarmupEvent()
    data class UpdateElapsedTime(val seconds: Int) : QuickWarmupEvent()
    object FinishQuickWarmup : QuickWarmupEvent()
    object DismissCompletionDialog : QuickWarmupEvent()
}
```

### 3. QuickWarmupViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class QuickWarmupViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(QuickWarmupState())
    val state: StateFlow<QuickWarmupState> = _state.asStateFlow()
    
    fun onEvent(event: QuickWarmupEvent) {
        when (event) {
            QuickWarmupEvent.StartQuickWarmup -> {
                startQuickWarmup()
            }
            
            QuickWarmupEvent.CurrentExerciseCompleted -> {
                markCurrentExerciseCompleted()
            }
            
            is QuickWarmupEvent.UpdateElapsedTime -> {
                _state.update {
                    it.copy(totalElapsedSeconds = event.seconds)
                }
            }
            
            QuickWarmupEvent.FinishQuickWarmup -> {
                finishQuickWarmup()
            }
            
            QuickWarmupEvent.DismissCompletionDialog -> {
                _state.update {
                    it.copy(isCompleted = false)
                }
            }
        }
    }
    
    private fun startQuickWarmup() {
        _state.update {
            it.copy(
                currentExerciseIndex = 0,
                completedExercises = emptySet(),
                totalElapsedSeconds = 0,
                isExerciseDialogOpen = true
            )
        }
    }
    
    private fun markCurrentExerciseCompleted() {
        val currentIndex = _state.value.currentExerciseIndex
        val currentExerciseId = _state.value.exercises.getOrNull(currentIndex)?.id ?: return
        
        _state.update {
            it.copy(
                completedExercises = it.completedExercises + currentExerciseId
            )
        }
        
        // Переходимо до наступної вправи
        val nextIndex = currentIndex + 1
        
        if (nextIndex >= _state.value.exercises.size) {
            // Всі вправи виконано
            completeQuickWarmup()
        } else {
            _state.update {
                it.copy(currentExerciseIndex = nextIndex)
            }
        }
    }
    
    private fun completeQuickWarmup() {
        _state.update {
            it.copy(
                isExerciseDialogOpen = false,
                isCompleted = true
            )
        }
        
        saveProgress()
    }
    
    private fun finishQuickWarmup() {
        saveProgress()
        // Navigation handled in Screen
    }
    
    private fun saveProgress() {
        viewModelScope.launch {
            val today = getCurrentDateString()
            val totalExercises = _state.value.exercises.size
            
            val entity = WarmupCompletionEntity(
                id = "${today}_quick",
                date = today,
                category = "quick",
                completedAt = System.currentTimeMillis(),
                exercisesCompleted = totalExercises,
                totalExercises = totalExercises
            )
            
            warmupCompletionDao.insertOrUpdate(entity)
            
            // Оновлюємо DataStore
            val estimatedMinutes = (_state.value.totalElapsedSeconds / 60).coerceAtLeast(1)
            userPreferencesDataStore.updateSessionStats(
                date = today,
                minutes = estimatedMinutes,
                exercises = 1
            )
        }
    }
    
    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
```

### 4. QuickWarmupScreen.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
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
fun QuickWarmupScreen(
    viewModel: QuickWarmupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        // Auto-start first exercise
        viewModel.onEvent(QuickWarmupEvent.StartQuickWarmup)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Швидка розминка (5 хв)") },
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
                        text = "Вправа ${state.currentExerciseIndex + 1} з ${state.exercises.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    LinearProgressIndicator(
                        progress = { state.completedExercises.size.toFloat() / state.exercises.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }
            
            // Exercise list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.exercises.forEachIndexed { index, exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (index == state.currentExerciseIndex) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else if (state.completedExercises.contains(exercise.id)) {
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
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (state.completedExercises.contains(exercise.id)) {
                                        Icons.Filled.CheckCircle
                                    } else if (index == state.currentExerciseIndex) {
                                        Icons.Filled.CheckCircle // Or a play icon
                                    } else {
                                        Icons.Outlined.Circle
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        state.completedExercises.contains(exercise.id) -> 
                                            MaterialTheme.colorScheme.primary
                                        index == state.currentExerciseIndex -> 
                                            MaterialTheme.colorScheme.primary
                                        else -> 
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
            }
        }
        
        // Exercise dialogs (reuse from Phase 2.2-2.4)
        if (state.isExerciseDialogOpen) {
            val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)
            
            if (currentExercise != null) {
                when (currentExercise.category) {
                    WarmupCategoryType.ARTICULATION -> {
                        currentExercise.articulationExercise?.let { exercise ->
                            ArticulationExerciseDialog(
                                exercise = exercise,
                                timerSeconds = exercise.durationSeconds,
                                isTimerRunning = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = { /* Handle in local state */ },
                                onPauseTimer = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }
                    
                    WarmupCategoryType.BREATHING -> {
                        currentExercise.breathingExercise?.let { exercise ->
                            BreathingExerciseDialog(
                                exercise = exercise,
                                elapsedSeconds = 0,
                                totalSeconds = exercise.durationSeconds,
                                currentPhase = BreathingPhase.INHALE,
                                phaseProgress = 0f,
                                isRunning = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStart = { /* Handle in local state */ },
                                onPause = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }
                    
                    WarmupCategoryType.VOICE -> {
                        currentExercise.voiceExercise?.let { exercise ->
                            VoiceExerciseDialog(
                                exercise = exercise,
                                timerSeconds = exercise.durationSeconds,
                                isTimerRunning = false,
                                isAudioPlaying = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = { /* Handle in local state */ },
                                onPauseTimer = { /* Handle in local state */ },
                                onPlayAudio = { /* Handle in local state */ },
                                onStopAudio = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Completion dialog
        if (state.isCompleted) {
            CompletionDialog(
                totalExercises = state.exercises.size,
                elapsedSeconds = state.totalElapsedSeconds,
                onDismiss = {
                    viewModel.onEvent(QuickWarmupEvent.DismissCompletionDialog)
                    onNavigateBack()
                }
            )
        }
    }
}

@Composable
private fun CompletionDialog(
    totalExercises: Int,
    elapsedSeconds: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🎉", style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text("Розминка завершена!")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ви виконали $totalExercises вправи",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "за %d хв %02d сек".format(
                        elapsedSeconds / 60,
                        elapsedSeconds % 60
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Готово")
            }
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

**Тест 1: Auto-start**
- [ ] При відкритті екрану автоматично стартує перша вправа
- [ ] Progress bar показує 0/4

**Тест 2: Sequential Flow**
- [ ] Після завершення вправи 1 → автоматично відкривається вправа 2
- [ ] Progress bar оновлюється (1/4 → 2/4 → 3/4 → 4/4)
- [ ] Список вправ показує поточну (підсвічена)

**Тест 3: Exercise Dialogs**
- [ ] Вправи відкриваються в правильних діалогах (Articulation/Breathing/Voice)
- [ ] Таймери працюють
- [ ] "Готово" → наступна вправа
- [ ] "Пропустити" → наступна вправа

**Тест 4: Completion**
- [ ] Після 4/4 → діалог "Розминка завершена"
- [ ] Показується загальний час
- [ ] "Готово" → повернення назад
- [ ] Дані зберігаються в Room (category = "quick")

**Тест 5: Progress Tracking**
- [ ] Після завершення оновлюється WarmupCompletionDao
- [ ] DataStore оновлюється (todayMinutes)

---

## Очікуваний результат

✅ QuickWarmupScreen зі sequential flow створено
✅ 4 вправи виконуються одна за одною
✅ Reuse components з Phase 2.2-2.4
✅ Auto-start першої вправи
✅ Progress tracking (0/4 → 4/4)
✅ Completion dialog
✅ Room Database integration (category = "quick")
✅ DataStore integration

---

## ✨ Phase 2 Завершена!

**Phase 2 — Warmup** тепер повністю реалізована:
- ✅ 2.1 — Warmup Main Screen (hub)
- ✅ 2.2 — Articulation Screen (12 вправ)
- ✅ 2.3 — Breathing Screen (8 вправ + Canvas animations)
- ✅ 2.4 — Voice Warmup Screen (6 вправ)
- ✅ 2.5 — Quick Warmup Screen (4 вправи sequential)

**Загальний час розробки Phase 2:** ~8-10 годин

---

## Наступний крок

**Phase 3: Home Screen** — головний екран з персоналізованим планом.

Згідно з PHASE_STRUCTURE_GUIDE.md — Phase 3 буде **цільною фазою** (не розбивати на підфази).

---

**Час на Phase 2.5:** ~1 година