# Промпт для Claude Code — Phase 4.4: Results Screen

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1-2.5 — Warmup
- ✅ Phase 3 — Home Screen
- ✅ Phase 4.1 — Courses Infrastructure
- ✅ Phase 4.2 — Courses List + Detail
- ✅ Phase 4.3 — Lesson Screen (universal)

Зараз **Phase 4.4 — Results Screen** — остання підфаза Phase 4.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: UI екран для показу результатів після вправи.

**Специфікація:** `SPECIFICATION.md`, секції 4.3.9 (Results Screen) + 5.6 (VoiceAnalysis).

**Складність:** 🟡 СЕРЕДНЯ  
**Час:** ⏱️ 1.5-2 години

---

## Ключова ідея

**Phase 4.4** — це **екран результатів після вправи**:

1. **Recording info** — показати збережений запис
2. **Playback** — можливість прослухати
3. **AI Feedback placeholder** — поки без реального аналізу (буде в Phase 6)
4. **Actions** — "Спробувати знову" або "Далі"
5. **Navigation** — до наступного уроку або до курсу

**Phase 6** (AI Coach) додасть:
- Реальний AI-аналіз через Gemini API
- Метрики (diction, tempo, intonation)
- Персональні поради

---

## Задача Phase 4.4

### Results Screen Flow

```
┌────────────────────────────────────┐
│  ← Результати                      │
├────────────────────────────────────┤
│                                    │
│  🎤 Твій запис                     │
│  ┌──────────────────────────────┐ │
│  │ 📊 Вправа: Скоромовка        │ │
│  │ ⏱️ Тривалість: 00:42          │ │
│  │ 📅 12 груд. 2024, 14:30      │ │
│  │                              │ │
│  │ 🎧 [▶️ Прослухати]            │ │
│  └──────────────────────────────┘ │
│                                    │
│  📊 Аналіз (placeholder)           │
│  ┌──────────────────────────────┐ │
│  │ Аналіз буде доступний після  │ │
│  │ інтеграції AI в Phase 6      │ │
│  │                              │ │
│  │ 💡 Поради:                   │ │
│  │ • Продовжуй тренуватися      │ │
│  │ • Стеж за диханням           │ │
│  └──────────────────────────────┘ │
│                                    │
│  [🔄 Спробувати знову]             │
│  [→ Наступна вправа]               │
│                                    │
└────────────────────────────────────┘
```

---

## Структура файлів

```
ui/screens/results/
├── ResultsScreen.kt
├── ResultsViewModel.kt
├── ResultsState.kt
├── ResultsEvent.kt
└── components/
    ├── RecordingInfoCard.kt
    ├── AnalysisPlaceholderCard.kt
    └── ResultsActionsCard.kt
```

---

## Повний код

### 1. ResultsState.kt

```kotlin
package com.aivoicepower.ui.screens.results

import com.aivoicepower.domain.model.course.Exercise

data class ResultsState(
    val recordingId: String = "",
    val recording: RecordingInfo? = null,
    val exercise: Exercise? = null,
    val analysis: AnalysisResult? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class RecordingInfo(
    val id: String,
    val filePath: String,
    val durationMs: Long,
    val createdAt: Long,
    val exerciseTitle: String,
    val exerciseType: String
)

data class AnalysisResult(
    val isAnalyzed: Boolean,
    val overallScore: Int?,
    val feedback: FeedbackData?
)

data class FeedbackData(
    val summary: String,
    val strengths: List<String>,
    val improvements: List<String>,
    val tip: String
)
```

### 2. ResultsEvent.kt

```kotlin
package com.aivoicepower.ui.screens.results

sealed class ResultsEvent {
    object PlayRecordingClicked : ResultsEvent()
    object StopPlaybackClicked : ResultsEvent()
    object RetryExerciseClicked : ResultsEvent()
    object NextExerciseClicked : ResultsEvent()
    object BackToCourseClicked : ResultsEvent()
}
```

### 3. ResultsViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.results

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.domain.repository.CourseRepository
import com.aivoicepower.utils.audio.AudioPlayerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val recordingDao: RecordingDao,
    private val courseRepository: CourseRepository
) : ViewModel() {
    
    private val recordingId: String = checkNotNull(savedStateHandle["recordingId"])
    
    private val _state = MutableStateFlow(ResultsState(recordingId = recordingId))
    val state: StateFlow<ResultsState> = _state.asStateFlow()
    
    private val audioPlayer = AudioPlayerUtil(context)
    
    init {
        loadResults()
    }
    
    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
    
    fun onEvent(event: ResultsEvent) {
        when (event) {
            ResultsEvent.PlayRecordingClicked -> {
                playRecording()
            }
            ResultsEvent.StopPlaybackClicked -> {
                stopPlayback()
            }
            ResultsEvent.RetryExerciseClicked -> {
                // Navigation handled in Screen
            }
            ResultsEvent.NextExerciseClicked -> {
                // Navigation handled in Screen
            }
            ResultsEvent.BackToCourseClicked -> {
                // Navigation handled in Screen
            }
        }
    }
    
    private fun loadResults() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                recordingDao.getByIdFlow(recordingId)
                    .collect { recordingEntity ->
                        if (recordingEntity == null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Запис не знайдено"
                                )
                            }
                            return@collect
                        }
                        
                        // Load exercise info
                        val exercise = if (recordingEntity.exerciseId != null && 
                                          recordingEntity.contextId != null) {
                            val parts = recordingEntity.contextId.split("_")
                            if (parts.size == 2) {
                                val (courseId, lessonId) = parts
                                courseRepository.getLessonById(courseId, lessonId)
                                    .first()?.exercises?.find { it.id == recordingEntity.exerciseId }
                            } else null
                        } else null
                        
                        val recordingInfo = RecordingInfo(
                            id = recordingEntity.id,
                            filePath = recordingEntity.filePath,
                            durationMs = recordingEntity.durationMs,
                            createdAt = recordingEntity.createdAt,
                            exerciseTitle = exercise?.title ?: "Вправа",
                            exerciseType = exercise?.type?.name ?: "Unknown"
                        )
                        
                        val analysis = if (recordingEntity.isAnalyzed) {
                            // TODO Phase 6: Parse analysisJson
                            AnalysisResult(
                                isAnalyzed = true,
                                overallScore = recordingEntity.overallScore,
                                feedback = null // Will be parsed from analysisJson in Phase 6
                            )
                        } else {
                            AnalysisResult(
                                isAnalyzed = false,
                                overallScore = null,
                                feedback = null
                            )
                        }
                        
                        _state.update {
                            it.copy(
                                recording = recordingInfo,
                                exercise = exercise,
                                analysis = analysis,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалось завантажити результати"
                    )
                }
            }
        }
    }
    
    private fun playRecording() {
        val filePath = _state.value.recording?.filePath ?: return
        
        viewModelScope.launch {
            try {
                audioPlayer.play(filePath)
                _state.update { it.copy(isPlaying = true) }
                
                // Auto-stop after duration
                kotlinx.coroutines.delay(_state.value.recording?.durationMs ?: 0)
                _state.update { it.copy(isPlaying = false) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        error = "Помилка відтворення: ${e.message}",
                        isPlaying = false
                    )
                }
            }
        }
    }
    
    private fun stopPlayback() {
        audioPlayer.stop()
        _state.update { it.copy(isPlaying = false) }
    }
}
```

### 4. ResultsScreen.kt

```kotlin
package com.aivoicepower.ui.screens.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.results.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    recordingId: String,
    viewModel: ResultsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результати") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(state.error!!)
                        Button(onClick = onNavigateBack) {
                            Text("Повернутися")
                        }
                    }
                }
            }
            
            state.recording != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Recording info
                    RecordingInfoCard(
                        recording = state.recording!!,
                        isPlaying = state.isPlaying,
                        onPlayClicked = {
                            if (state.isPlaying) {
                                viewModel.onEvent(ResultsEvent.StopPlaybackClicked)
                            } else {
                                viewModel.onEvent(ResultsEvent.PlayRecordingClicked)
                            }
                        }
                    )
                    
                    // Analysis section
                    if (state.analysis?.isAnalyzed == true) {
                        // TODO Phase 6: Real analysis card
                        AnalysisPlaceholderCard(
                            message = "AI-аналіз буде додано в Phase 6",
                            score = state.analysis?.overallScore
                        )
                    } else {
                        AnalysisPlaceholderCard(
                            message = "Аналіз буде доступний після інтеграції AI в Phase 6"
                        )
                    }
                    
                    // Actions
                    ResultsActionsCard(
                        onRetry = {
                            viewModel.onEvent(ResultsEvent.RetryExerciseClicked)
                            onNavigateBack() // For now, just go back
                        },
                        onNext = {
                            viewModel.onEvent(ResultsEvent.NextExerciseClicked)
                            onNavigateBack() // For now, just go back
                        }
                    )
                }
            }
        }
    }
}
```

### 5. Components

#### components/RecordingInfoCard.kt

```kotlin
package com.aivoicepower.ui.screens.results.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.results.RecordingInfo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecordingInfoCard(
    recording: RecordingInfo,
    isPlaying: Boolean,
    onPlayClicked: () -> Unit,
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
                text = "🎤 Твій запис",
                style = MaterialTheme.typography.titleLarge
            )
            
            HorizontalDivider()
            
            // Exercise info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📊 ${recording.exerciseTitle}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = "⏱️ ${formatDuration(recording.durationMs)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "📅 ${formatDate(recording.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Play button
            Button(
                onClick = onPlayClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isPlaying) "Зупинити" else "Прослухати")
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("uk"))
    return sdf.format(Date(timestamp))
}
```

#### components/AnalysisPlaceholderCard.kt

```kotlin
package com.aivoicepower.ui.screens.results.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnalysisPlaceholderCard(
    message: String,
    score: Int? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📊 Аналіз",
                style = MaterialTheme.typography.titleLarge
            )
            
            HorizontalDivider()
            
            if (score != null) {
                // Show score if available
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$score/100",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            // Generic tips (placeholder)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💡 Загальні поради:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    val genericTips = listOf(
                        "Продовжуй регулярно практикуватися",
                        "Стеж за диханням під час мовлення",
                        "Записуй себе для самоконтролю",
                        "Тренуйся перед дзеркалом"
                    )
                    
                    genericTips.forEach { tip ->
                        Text(
                            text = "• $tip",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Phase 6 note
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ℹ️")
                    Text(
                        text = "Детальний AI-аналіз (дикція, темп, інтонація) буде доступний після інтеграції Gemini API в Phase 6.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}
```

#### components/ResultsActionsCard.kt

```kotlin
package com.aivoicepower.ui.screens.results.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsActionsCard(
    onRetry: () -> Unit,
    onNext: () -> Unit,
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
                text = "Що далі?",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Retry button
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Спробувати знову")
            }
            
            // Next button
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Повернутися до курсу")
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

**Тест 1: Loading**
- [ ] Показує loading indicator
- [ ] Завантажує recording з DB
- [ ] Показує exercise info (якщо є)

**Тест 2: Recording Info**
- [ ] Відображає правильний exerciseTitle
- [ ] Показує тривалість у форматі MM:SS
- [ ] Показує дату/час створення

**Тест 3: Playback**
- [ ] Кнопка "Прослухати" → відтворення
- [ ] Кнопка змінюється на "Зупинити"
- [ ] Аудіо відтворюється
- [ ] Auto-stop після завершення

**Тест 4: Analysis Placeholder**
- [ ] Показує placeholder message
- [ ] Показує generic tips
- [ ] Показує Phase 6 note

**Тест 5: Actions**
- [ ] "Спробувати знову" → navigation back
- [ ] "Повернутися до курсу" → navigation back
- [ ] Back button працює

**Тест 6: Error Handling**
- [ ] Показує error якщо recording не знайдено
- [ ] "Повернутися" button працює

---

## Очікуваний результат

✅ ResultsScreen створено
✅ Recording info відображається
✅ Audio playback працює
✅ Placeholder для AI analysis
✅ Actions (Retry, Next)
✅ Navigation готова
✅ Phase 6 integration точки підготовлені

---

## 🎉 Phase 4 ЗАВЕРШЕНО!

```
✅ Phase 4.1 — Courses Infrastructure
✅ Phase 4.2 — Courses List + Detail
✅ Phase 4.3 — Lesson Screen (universal)
✅ Phase 4.4 — Results Screen
```

**Загальний час Phase 4:** ~8-10 годин (як і планувалося)

---

## 🚀 Наступний крок: Phase 5

**Phase 5: Improvisation** — розбити на 3 підфази:
- **Phase 5.1**: Improvisation Hub + Random Topic (2-3 год) 🟡
- **Phase 5.2**: Storytelling + Daily Challenge (2 год) 🟡
- **Phase 5.3**: Debate + Sales Pitch (AI-interactive) (4-5 год) 🔴 складна

**Загальний час Phase 5:** ~7-9 годин

---

**Час на Phase 4.4:** ~1.5-2 години

**Примітка:** 
- AI-аналіз буде додано в Phase 6 (Gemini API)
- Зараз Results показує тільки recording info + placeholder
- VoiceAnalysis domain model вже готовий з Phase 0.5