# Промпт для Claude Code — Phase 2.4: Voice Warmup Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1 — Warmup Main Screen
- ✅ Phase 2.2 — Articulation Screen
- ✅ Phase 2.3 — Breathing Screen

Зараз **Phase 2.4 — Voice Warmup Screen** — розминка голосу (6 вокальних вправ).

**Згідно з PHASE_STRUCTURE_GUIDE.md**: НИЗЬКА-СЕРЕДНЯ складність (UI + timer + optional audio).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.4 + 5.4 (Warmup Exercise, Voice).

**Складність:** 🟡 НИЗЬКА-СЕРЕДНЯ  
**Час:** ⏱️ 1.5 години

---

## Ключова ідея

⚠️ **БЕЗ запису аудіо!** Розминка голосу — це вокальні вправи (гумкання, розспівки).

**Механіка:**
1. Показати список 6 вправ
2. Клік на вправу → діалог з інструкцією + **аудіо-приклад (опціонально)**
3. Користувач слухає приклад (якщо є) та повторює
4. Таймер відраховує час вправи
5. Позначає як виконано ✅
6. Прогрес зберігається в Room

**Audio примітка:** Аудіо-приклади можуть бути placeholder (текстове пояснення замість реального аудіо).

---

## Задача Phase 2.4

Створити екран з **6 вправами для розминки голосу**:

| # | Назва | Тривалість | Опис |
|---|-------|-----------|------|
| 1 | Гумкання | 30 сек | "Ммм" на різних нотах |
| 2 | Сирена | 20 сек | Голос від низького до високого |
| 3 | Губні трелі | 20 сек | "Брррр" як мотор |
| 4 | Розспівка "Ма-ме-мі-мо-му" | 30 сек | Вокалізи на голосні |
| 5 | Співання на одній ноті | 25 сек | Утримання звуку |
| 6 | Глісандо | 20 сек | Плавний перехід між нотами |

**Загальний час:** ~2 хвилини

---

## Структура файлів

```
ui/screens/warmup/
├── VoiceWarmupScreen.kt
├── VoiceWarmupViewModel.kt
├── VoiceWarmupState.kt
├── VoiceWarmupEvent.kt
└── components/
    ├── VoiceExerciseItem.kt (картка вправи)
    └── VoiceExerciseDialog.kt (діалог з таймером + audio)

ui/components/audio/ (опціонально, якщо ще немає з Phase 0.6)
└── AudioPlayer.kt (placeholder для майбутнього)
```

---

## UI Design

```
Step 1: Exercise List (як в Phase 2.2)
┌──────────────────────────────────────────────┐
│  ← Розминка голосу                           │
│  Виконано: 2/6                               │
├──────────────────────────────────────────────┤
│  ━━━━━━━○○○○○○○○○○○○○○  33%                  │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ✅ 1. Гумкання               30 сек    │ │
│  └────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────┐ │
│  │ ○ 2. Сирена                  20 сек    │ │
│  └────────────────────────────────────────┘ │
│  ...                                        │
└──────────────────────────────────────────────┘

Step 2: Exercise Dialog
┌──────────────────────────────────────────────┐
│  2. Сирена                             [X]   │
├──────────────────────────────────────────────┤
│                                              │
│  📝 Інструкція:                              │
│  Ведіть голос від найнижчої ноти до          │
│  найвищої, як сирена. Плавно без стрибків.   │
│  Використовуйте звук "У-у-у".                │
│                                              │
│  🔊 Аудіо-приклад:                           │
│  [▶️ Послухати]  (placeholder)               │
│                                              │
│  ⏱️ Тривалість: 20 секунд                    │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │              00:12                     │ │
│  │    ━━━━━━━━━━━━━━━○○○○○  60%          │ │
│  │         [⏸️ Пауза]                      │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  [Пропустити]          [Готово ✓]           │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Повний код

### 1. VoiceWarmupState.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

data class VoiceWarmupState(
    val exercises: List<VoiceExercise> = getVoiceExercises(),
    val completedToday: Set<Int> = emptySet(),
    val selectedExercise: VoiceExercise? = null,
    val isExerciseDialogOpen: Boolean = false,
    val timerSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val isAudioPlaying: Boolean = false
)

data class VoiceExercise(
    val id: Int,
    val title: String,
    val durationSeconds: Int,
    val instruction: String,
    val audioExampleUrl: String? = null // Placeholder for future
)

private fun getVoiceExercises(): List<VoiceExercise> {
    return listOf(
        VoiceExercise(
            id = 1,
            title = "Гумкання",
            durationSeconds = 30,
            instruction = "Закрийте рот і гучно \"ммм\" на комфортній для вас ноті. Відчуйте вібрацію в носі та губах. Спробуйте на різних нотах.",
            audioExampleUrl = null // TODO: Add audio in Phase 8
        ),
        VoiceExercise(
            id = 2,
            title = "Сирена",
            durationSeconds = 20,
            instruction = "Ведіть голос від найнижчої ноти до найвищої, як сирена. Плавно без стрибків. Використовуйте звук \"У-у-у\".",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 3,
            title = "Губні трелі",
            durationSeconds = 20,
            instruction = "Робіть звук \"Брррр\" губами, як мотор. Спробуйте на різних висотах. Це розслабляє голосові зв'язки.",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 4,
            title = "Розспівка \"Ма-ме-мі-мо-му\"",
            durationSeconds = 30,
            instruction = "Співайте склади \"Ма-ме-мі-мо-му\" на одній ноті, потім підвищуйте. Чітко артикулюйте кожен склад.",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 5,
            title = "Співання на одній ноті",
            durationSeconds = 25,
            instruction = "Виберіть комфортну ноту і співайте \"А-а-а\" якомога довше. Тримайте звук рівним і стабільним.",
            audioExampleUrl = null
        ),
        VoiceExercise(
            id = 6,
            title = "Глісандо",
            durationSeconds = 20,
            instruction = "Плавно ведіть голос вгору і вниз, як ковзанка. Звук \"О-о-о\". Без різких переходів.",
            audioExampleUrl = null
        )
    )
}
```

### 2. VoiceWarmupEvent.kt

```kotlin
package com.aivoicepower.ui.screens.warmup

sealed class VoiceWarmupEvent {
    data class ExerciseClicked(val exercise: VoiceExercise) : VoiceWarmupEvent()
    object ExerciseDialogDismissed : VoiceWarmupEvent()
    object StartTimer : VoiceWarmupEvent()
    object PauseTimer : VoiceWarmupEvent()
    data class TimerTick(val secondsRemaining: Int) : VoiceWarmupEvent()
    object PlayAudioExample : VoiceWarmupEvent()
    object StopAudioExample : VoiceWarmupEvent()
    object MarkAsCompleted : VoiceWarmupEvent()
    object SkipExercise : VoiceWarmupEvent()
}
```

### 3. VoiceWarmupViewModel.kt

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
class VoiceWarmupViewModel @Inject constructor(
    private val warmupCompletionDao: WarmupCompletionDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(VoiceWarmupState())
    val state: StateFlow<VoiceWarmupState> = _state.asStateFlow()
    
    private var timerJob: Job? = null
    
    init {
        loadTodayProgress()
    }
    
    fun onEvent(event: VoiceWarmupEvent) {
        when (event) {
            is VoiceWarmupEvent.ExerciseClicked -> {
                _state.update {
                    it.copy(
                        selectedExercise = event.exercise,
                        isExerciseDialogOpen = true,
                        timerSeconds = event.exercise.durationSeconds,
                        isTimerRunning = false
                    )
                }
            }
            
            VoiceWarmupEvent.ExerciseDialogDismissed -> {
                stopTimer()
                stopAudio()
                _state.update {
                    it.copy(
                        selectedExercise = null,
                        isExerciseDialogOpen = false,
                        timerSeconds = 0,
                        isTimerRunning = false
                    )
                }
            }
            
            VoiceWarmupEvent.StartTimer -> {
                startTimer()
            }
            
            VoiceWarmupEvent.PauseTimer -> {
                stopTimer()
            }
            
            is VoiceWarmupEvent.TimerTick -> {
                _state.update { it.copy(timerSeconds = event.secondsRemaining) }
                
                if (event.secondsRemaining <= 0) {
                    stopTimer()
                    markCurrentAsCompleted()
                }
            }
            
            VoiceWarmupEvent.PlayAudioExample -> {
                playAudioExample()
            }
            
            VoiceWarmupEvent.StopAudioExample -> {
                stopAudio()
            }
            
            VoiceWarmupEvent.MarkAsCompleted -> {
                markCurrentAsCompleted()
            }
            
            VoiceWarmupEvent.SkipExercise -> {
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
            val completion = warmupCompletionDao.getCompletion(today, "voice")
            
            if (completion != null) {
                val completed = (1..completion.exercisesCompleted).toSet()
                _state.update { it.copy(completedToday = completed) }
            }
        }
    }
    
    private fun startTimer() {
        stopTimer()
        
        _state.update { it.copy(isTimerRunning = true) }
        
        timerJob = viewModelScope.launch {
            while (_state.value.isTimerRunning && _state.value.timerSeconds > 0) {
                delay(1000)
                val newSeconds = _state.value.timerSeconds - 1
                onEvent(VoiceWarmupEvent.TimerTick(newSeconds))
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isTimerRunning = false) }
    }
    
    private fun playAudioExample() {
        // TODO: Implement audio playback in Phase 8 (Content)
        // For now, just toggle the playing state
        _state.update { it.copy(isAudioPlaying = true) }
        
        // Auto-stop after 3 seconds (placeholder)
        viewModelScope.launch {
            delay(3000)
            stopAudio()
        }
    }
    
    private fun stopAudio() {
        _state.update { it.copy(isAudioPlaying = false) }
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
                id = "${today}_voice",
                date = today,
                category = "voice",
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

### 4. VoiceWarmupScreen.kt

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
fun VoiceWarmupScreen(
    viewModel: VoiceWarmupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Розминка голосу") },
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
                    VoiceExerciseItem(
                        exercise = exercise,
                        isCompleted = state.completedToday.contains(exercise.id),
                        onClick = {
                            viewModel.onEvent(VoiceWarmupEvent.ExerciseClicked(exercise))
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
        
        // Exercise dialog
        if (state.isExerciseDialogOpen && state.selectedExercise != null) {
            VoiceExerciseDialog(
                exercise = state.selectedExercise!!,
                timerSeconds = state.timerSeconds,
                isTimerRunning = state.isTimerRunning,
                isAudioPlaying = state.isAudioPlaying,
                onDismiss = {
                    viewModel.onEvent(VoiceWarmupEvent.ExerciseDialogDismissed)
                },
                onStartTimer = {
                    viewModel.onEvent(VoiceWarmupEvent.StartTimer)
                },
                onPauseTimer = {
                    viewModel.onEvent(VoiceWarmupEvent.PauseTimer)
                },
                onPlayAudio = {
                    viewModel.onEvent(VoiceWarmupEvent.PlayAudioExample)
                },
                onStopAudio = {
                    viewModel.onEvent(VoiceWarmupEvent.StopAudioExample)
                },
                onMarkCompleted = {
                    viewModel.onEvent(VoiceWarmupEvent.MarkAsCompleted)
                },
                onSkip = {
                    viewModel.onEvent(VoiceWarmupEvent.SkipExercise)
                }
            )
        }
    }
}
```

### 5. components/VoiceExerciseItem.kt

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
import com.aivoicepower.ui.screens.warmup.VoiceExercise

@Composable
fun VoiceExerciseItem(
    exercise: VoiceExercise,
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

### 6. components/VoiceExerciseDialog.kt

```kotlin
package com.aivoicepower.ui.screens.warmup.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aivoicepower.ui.screens.warmup.VoiceExercise

@Composable
fun VoiceExerciseDialog(
    exercise: VoiceExercise,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    isAudioPlaying: Boolean,
    onDismiss: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onPlayAudio: () -> Unit,
    onStopAudio: () -> Unit,
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
                
                // Audio example (if available)
                if (exercise.audioExampleUrl != null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🔊 Аудіо-приклад:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        
                        OutlinedButton(
                            onClick = if (isAudioPlaying) onStopAudio else onPlayAudio,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isAudioPlaying) "Зупинити" else "Послухати")
                        }
                    }
                } else {
                    // Placeholder
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "🔊 Аудіо-приклад буде доступний у наступних оновленнях",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
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
- [ ] 6 вправ відображаються
- [ ] Progress bar показує 0/6
- [ ] Completed exercises позначені ✅

**Тест 2: Exercise Dialog**
- [ ] Клік на вправу → діалог з інструкцією
- [ ] "Послухати" button показується (placeholder)
- [ ] Таймер показує правильну тривалість
- [ ] "Старт" → таймер працює
- [ ] "Пауза" → таймер зупиняється
- [ ] "Готово" → вправа позначається ✅
- [ ] "Пропустити" → закриває діалог

**Тест 3: Audio Placeholder**
- [ ] Якщо audioExampleUrl == null → показується placeholder текст
- [ ] Якщо audioExampleUrl != null → показується кнопка "Послухати"

**Тест 4: Progress Tracking**
- [ ] Progress bar оновлюється
- [ ] Після 6/6 з'являється кнопка "Завершити"
- [ ] Дані зберігаються в Room
- [ ] DataStore оновлюється (todayMinutes)

**Тест 5: Auto-complete**
- [ ] Коли таймер досягає 0 → auto-mark as completed

---

## Очікуваний результат

✅ VoiceWarmupScreen з 6 вправами створено
✅ Exercise dialog з таймером працює
✅ Audio player placeholder (ready for Phase 8)
✅ Progress tracking з Room
✅ Checklist механіка
✅ Auto-complete по таймеру
✅ DataStore integration

---

## Наступний крок

**Phase 2.5: Quick Warmup Screen** — швидка 5-хвилинна розминка (комбінація вправ з 2.2-2.4).

---

**Час на Phase 2.4:** ~1.5 години

**Примітка:** Аудіо-приклади будуть додані в Phase 8 (Content). Зараз використовуємо placeholder.