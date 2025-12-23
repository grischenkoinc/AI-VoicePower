# Промпт для Claude Code — Phase 5.1: Improvisation Hub + Random Topic

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure  
- ✅ Phase 1.1-1.4 — Onboarding + Diagnostic
- ✅ Phase 2.1-2.5 — Warmup
- ✅ Phase 3 — Home Screen
- ✅ Phase 4.1-4.4 — Courses (повністю)

Зараз **Phase 5.1 — Improvisation Hub + Random Topic** — перша підфаза Phase 5.

**Згідно з PHASE_STRUCTURE_GUIDE.md**: Початок системи імпровізації.

**Специфікація:** `SPECIFICATION.md`, секція 4.3.7 (Improvisation Screen).

**Складність:** 🟡 СЕРЕДНЯ  
**Час:** ⏱️ 2-3 години

---

## Ключова ідея Phase 5

**Improvisation** — це тренування спонтанного мовлення через 5 режимів:

| Режим | Механіка | Phase |
|-------|----------|-------|
| 🎲 Random Topic | Тема → підготовка 15 сек → запис 1-3 хв → AI-аналіз | **5.1** |
| 📖 Storytelling | Елементи сюжету → розповідь → AI-аналіз | 5.2 |
| 🏆 Daily Challenge | Унікальне завдання щодня → запис → трекінг | 5.2 |
| ⚔️ Debate | Тема + позиція → раунди аргументів → AI-контраргументи | 5.3 🔴 |
| 💼 Sales Pitch | Товар + клієнт → pitch → AI грає клієнта з питаннями | 5.3 🔴 |

**Phase 5.1** фокусується на:
1. **Hub Screen** — головний екран вибору режиму
2. **Random Topic** — перший та найпростіший режим

---

## Задача Phase 5.1

### 1. Improvisation Hub Screen

```
┌────────────────────────────────────┐
│  🎭 Імпровізація                   │
├────────────────────────────────────┤
│                                    │
│  Тренуй спонтанне мовлення         │
│                                    │
│  📊 Сьогодні: 1/3 (Free tier)      │
│                                    │
│  ┌──────────────────────────────┐ │
│  │ 🎲 Випадкова тема            │ │
│  │ Готовий говорити про що     │ │
│  │ завгодно?                    │ │
│  │                              │ │
│  │ [Почати →]                   │ │
│  └──────────────────────────────┘ │
│                                    │
│  ┌──────────────────────────────┐ │
│  │ 📖 Розкажи історію      🔒   │ │
│  │ (Phase 5.2)                  │ │
│  └──────────────────────────────┘ │
│                                    │
│  ┌──────────────────────────────┐ │
│  │ 🏆 Щоденний челендж     🔒   │ │
│  │ (Phase 5.2)                  │ │
│  └──────────────────────────────┘ │
│                                    │
│  ┌──────────────────────────────┐ │
│  │ ⚔️ Дебати з AI          🔒   │ │
│  │ (Phase 5.3)                  │ │
│  └──────────────────────────────┘ │
│                                    │
│  ┌──────────────────────────────┐ │
│  │ 💼 Продай товар         🔒   │ │
│  │ (Phase 5.3)                  │ │
│  └──────────────────────────────┘ │
│                                    │
└────────────────────────────────────┘
```

### 2. Random Topic Flow

```
┌────────────────────────────────────┐
│  ← Випадкова тема                  │
├────────────────────────────────────┤
│                                    │
│  🎲 Твоя тема:                     │
│                                    │
│  "Чому подорожі змінюють людину"   │
│                                    │
│  💡 Підказки:                      │
│  • Власний досвід                  │
│  • Нові перспективи                │
│  • Культурний обмін                │
│                                    │
│  ⏱️ Час підготовки: 00:15          │
│                                    │
│  [🔄 Інша тема]  [✓ Готовий]      │
│                                    │
└────────────────────────────────────┘

↓ (після натискання "Готовий")

┌────────────────────────────────────┐
│  Говори 1-3 хвилини                │
│                                    │
│  🔴 Запис... 00:42                 │
│                                    │
│  "Чому подорожі змінюють людину"   │
│                                    │
│  [■ Завершити]                     │
│                                    │
└────────────────────────────────────┘

↓ (після завершення)

Navigate to Results Screen (Phase 4.4)
  з placeholder AI-аналізу
```

---

## Структура файлів

```
ui/screens/improvisation/
├── ImprovisationScreen.kt              # Hub
├── ImprovisationViewModel.kt
├── ImprovisationState.kt
├── ImprovisationEvent.kt
├── RandomTopicScreen.kt                # Random Topic
├── RandomTopicViewModel.kt
├── RandomTopicState.kt
├── RandomTopicEvent.kt
└── components/
    ├── ImprovisationModeCard.kt
    ├── TopicDisplayCard.kt
    ├── PreparationTimerCard.kt
    └── RandomTopicRecordingCard.kt

data/content/
└── ImprovisationTopicsProvider.kt      # 50+ topics
```

---

## Повний код

### 1. Domain Model (якщо ще не створено в Phase 0.5)

#### domain/model/content/ImprovisationTopic.kt

```kotlin
package com.aivoicepower.domain.model.content

import com.aivoicepower.domain.model.course.Difficulty

data class ImprovisationTopic(
    val id: String,
    val title: String,
    val difficulty: Difficulty,
    val hints: List<String> = emptyList()
)
```

---

### 2. ImprovisationState.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

data class ImprovisationState(
    val completedToday: Int = 0,
    val dailyLimit: Int = 3,
    val isPremium: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
```

### 3. ImprovisationEvent.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

sealed class ImprovisationEvent {
    object RandomTopicClicked : ImprovisationEvent()
    object StorytellingClicked : ImprovisationEvent()
    object DailyChallengeClicked : ImprovisationEvent()
    object DebateClicked : ImprovisationEvent()
    object SalesPitchClicked : ImprovisationEvent()
}
```

### 4. ImprovisationViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImprovisationViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(ImprovisationState())
    val state: StateFlow<ImprovisationState> = _state.asStateFlow()
    
    init {
        loadImprovisationStats()
    }
    
    fun onEvent(event: ImprovisationEvent) {
        when (event) {
            ImprovisationEvent.RandomTopicClicked -> {
                // Navigation handled in Screen
            }
            ImprovisationEvent.StorytellingClicked -> {
                // Phase 5.2
            }
            ImprovisationEvent.DailyChallengeClicked -> {
                // Phase 5.2
            }
            ImprovisationEvent.DebateClicked -> {
                // Phase 5.3
            }
            ImprovisationEvent.SalesPitchClicked -> {
                // Phase 5.3
            }
        }
    }
    
    private fun loadImprovisationStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                userPreferencesDataStore.userPreferencesFlow
                    .collect { prefs ->
                        _state.update {
                            it.copy(
                                completedToday = prefs.freeImprovisationsToday,
                                dailyLimit = 3, // FreeTierLimits.FREE_IMPROVISATIONS_PER_DAY
                                isPremium = prefs.isPremium,
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
    
    fun canStartImprovisation(): Boolean {
        val state = _state.value
        return state.isPremium || state.completedToday < state.dailyLimit
    }
}
```

### 5. ImprovisationScreen.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.improvisation.components.ImprovisationModeCard

@Composable
fun ImprovisationScreen(
    viewModel: ImprovisationViewModel = hiltViewModel(),
    onNavigateToRandomTopic: () -> Unit,
    onNavigateToStorytelling: () -> Unit,
    onNavigateToDebate: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "🎭 Імпровізація",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "Тренуй спонтанне мовлення",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Stats card (for free users)
        if (!state.isPremium) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📊 Сьогодні:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${state.completedToday}/${state.dailyLimit}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Mode cards
        ImprovisationModeCard(
            emoji = "🎲",
            title = "Випадкова тема",
            description = "Готовий говорити про що завгодно?",
            isLocked = false,
            isComingSoon = false,
            onClick = {
                if (viewModel.canStartImprovisation()) {
                    viewModel.onEvent(ImprovisationEvent.RandomTopicClicked)
                    onNavigateToRandomTopic()
                } else {
                    onNavigateToPremium()
                }
            }
        )
        
        ImprovisationModeCard(
            emoji = "📖",
            title = "Розкажи історію",
            description = "Створи захоплюючу розповідь",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.2",
            onClick = {
                // Phase 5.2
            }
        )
        
        ImprovisationModeCard(
            emoji = "🏆",
            title = "Щоденний челендж",
            description = "Унікальне завдання кожен день",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.2",
            onClick = {
                // Phase 5.2
            }
        )
        
        ImprovisationModeCard(
            emoji = "⚔️",
            title = "Дебати з AI",
            description = "Переконуй штучний інтелект",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.3",
            onClick = {
                // Phase 5.3
            }
        )
        
        ImprovisationModeCard(
            emoji = "💼",
            title = "Продай товар",
            description = "Презентуй продукт AI-клієнту",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.3",
            onClick = {
                // Phase 5.3
            }
        )
        
        // Premium prompt (if needed)
        if (!state.isPremium && state.completedToday >= state.dailyLimit) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⭐ Ліміт вичерпано",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Отримай Premium для необмеженої практики",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onNavigateToPremium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Дізнатись більше")
                    }
                }
            }
        }
    }
}
```

### 6. components/ImprovisationModeCard.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImprovisationModeCard(
    emoji: String,
    title: String,
    description: String,
    isLocked: Boolean,
    isComingSoon: Boolean = false,
    comingSoonText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        enabled = !isComingSoon,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji
            Text(
                text = emoji,
                style = MaterialTheme.typography.displaySmall
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Premium",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (isComingSoon && comingSoonText != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = comingSoonText,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Arrow
            if (!isComingSoon) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}
```

---

## Random Topic Screen

### 7. RandomTopicState.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import com.aivoicepower.domain.model.content.ImprovisationTopic

data class RandomTopicState(
    val currentTopic: ImprovisationTopic? = null,
    val preparationTimeLeft: Int = 15, // seconds
    val isPreparationPhase: Boolean = true,
    val isRecording: Boolean = false,
    val recordingDurationMs: Long = 0,
    val recordingPath: String? = null,
    val recordingId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### 8. RandomTopicEvent.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

sealed class RandomTopicEvent {
    object GenerateNewTopic : RandomTopicEvent()
    object StartPreparation : RandomTopicEvent()
    object StartRecording : RandomTopicEvent()
    object StopRecording : RandomTopicEvent()
    object CompleteTask : RandomTopicEvent()
}
```

### 9. RandomTopicViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.content.ImprovisationTopicsProvider
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.utils.audio.AudioRecorderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RandomTopicViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(RandomTopicState())
    val state: StateFlow<RandomTopicState> = _state.asStateFlow()
    
    private val audioRecorder = AudioRecorderUtil(context)
    
    init {
        generateNewTopic()
    }
    
    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
    }
    
    fun onEvent(event: RandomTopicEvent) {
        when (event) {
            RandomTopicEvent.GenerateNewTopic -> {
                generateNewTopic()
            }
            RandomTopicEvent.StartPreparation -> {
                startPreparationTimer()
            }
            RandomTopicEvent.StartRecording -> {
                startRecording()
            }
            RandomTopicEvent.StopRecording -> {
                stopRecording()
            }
            RandomTopicEvent.CompleteTask -> {
                completeTask()
            }
        }
    }
    
    private fun generateNewTopic() {
        val allTopics = ImprovisationTopicsProvider.getAllTopics()
        val randomTopic = allTopics.random()
        
        _state.update {
            it.copy(
                currentTopic = randomTopic,
                preparationTimeLeft = 15,
                isPreparationPhase = true,
                isRecording = false,
                recordingPath = null,
                recordingId = null
            )
        }
    }
    
    private fun startPreparationTimer() {
        viewModelScope.launch {
            for (i in 15 downTo 0) {
                _state.update { it.copy(preparationTimeLeft = i) }
                delay(1000)
            }
            
            // Timer finished, user can now start recording
            _state.update { it.copy(isPreparationPhase = false) }
        }
    }
    
    private fun startRecording() {
        viewModelScope.launch {
            try {
                val outputFile = context.filesDir.resolve("recordings/${UUID.randomUUID()}.m4a")
                outputFile.parentFile?.mkdirs()
                
                audioRecorder.startRecording(outputFile.absolutePath)
                
                _state.update {
                    it.copy(
                        isRecording = true,
                        recordingPath = outputFile.absolutePath,
                        isPreparationPhase = false
                    )
                }
                
                // Track recording duration
                val startTime = System.currentTimeMillis()
                while (_state.value.isRecording) {
                    val duration = System.currentTimeMillis() - startTime
                    _state.update { it.copy(recordingDurationMs = duration) }
                    delay(100)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка запису: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }
    
    private fun stopRecording() {
        viewModelScope.launch {
            try {
                val result = audioRecorder.stopRecording()
                
                _state.update {
                    it.copy(
                        isRecording = false,
                        recordingPath = result?.filePath,
                        recordingDurationMs = result?.durationMs ?: it.recordingDurationMs
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Помилка зупинки запису: ${e.message}",
                        isRecording = false
                    )
                }
            }
        }
    }
    
    private fun completeTask() {
        viewModelScope.launch {
            try {
                val recordingPath = _state.value.recordingPath
                val topic = _state.value.currentTopic
                
                if (recordingPath != null && topic != null) {
                    // Save recording to database
                    val recordingId = UUID.randomUUID().toString()
                    val recordingEntity = RecordingEntity(
                        id = recordingId,
                        filePath = recordingPath,
                        durationMs = _state.value.recordingDurationMs,
                        type = "improvisation",
                        contextId = "random_topic",
                        exerciseId = null,
                        isAnalyzed = false
                    )
                    recordingDao.insert(recordingEntity)
                    
                    // Increment free improvisation counter
                    userPreferencesDataStore.incrementFreeImprovisations()
                    
                    // Store recordingId for navigation
                    _state.update { it.copy(recordingId = recordingId) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Помилка збереження: ${e.message}")
                }
            }
        }
    }
}
```

### 10. RandomTopicScreen.kt

```kotlin
package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.improvisation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTopicScreen(
    viewModel: RandomTopicViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Auto-start preparation timer when topic is loaded
    LaunchedEffect(state.currentTopic) {
        if (state.currentTopic != null && state.isPreparationPhase && state.preparationTimeLeft == 15) {
            viewModel.onEvent(RandomTopicEvent.StartPreparation)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎲 Випадкова тема") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.isPreparationPhase -> {
                    // Preparation phase
                    state.currentTopic?.let { topic ->
                        TopicDisplayCard(topic = topic)
                        
                        PreparationTimerCard(
                            timeLeft = state.preparationTimeLeft,
                            onGenerateNew = {
                                viewModel.onEvent(RandomTopicEvent.GenerateNewTopic)
                            }
                        )
                    }
                    
                    if (state.preparationTimeLeft == 0) {
                        Button(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.StartRecording)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎤 Почати запис")
                        }
                    }
                }
                
                state.isRecording -> {
                    // Recording phase
                    state.currentTopic?.let { topic ->
                        RandomTopicRecordingCard(
                            topic = topic,
                            durationMs = state.recordingDurationMs,
                            onStop = {
                                viewModel.onEvent(RandomTopicEvent.StopRecording)
                            }
                        )
                    }
                }
                
                else -> {
                    // Recording completed
                    state.currentTopic?.let { topic ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "✓ Запис завершено",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Тривалість: ${formatDuration(state.recordingDurationMs)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        Button(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.CompleteTask)
                                // Wait for recordingId to be set
                                state.recordingId?.let { recordingId ->
                                    onNavigateToResults(recordingId)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.recordingId != null || state.recordingPath != null
                        ) {
                            Text("Переглянути результати")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.GenerateNewTopic)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔄 Нова тема")
                        }
                    }
                }
            }
            
            // Error message
            state.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
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
```

### 11-14. Components (TopicDisplayCard, PreparationTimerCard, RandomTopicRecordingCard)

[Components code same as in previous version - інклудив повністю]

---

### 15. Content Provider

#### data/content/ImprovisationTopicsProvider.kt

```kotlin
package com.aivoicepower.data.content

import com.aivoicepower.domain.model.content.ImprovisationTopic
import com.aivoicepower.domain.model.course.Difficulty

object ImprovisationTopicsProvider {
    
    fun getAllTopics(): List<ImprovisationTopic> {
        return listOf(
            // BEGINNER (15 topics)
            ImprovisationTopic(
                id = "topic_1",
                title = "Чому подорожі змінюють людину",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Власний досвід подорожей",
                    "Нові перспективи та світогляд",
                    "Культурний обмін"
                )
            ),
            ImprovisationTopic(
                id = "topic_2",
                title = "Як технології впливають на наше життя",
                difficulty = Difficulty.BEGINNER,
                hints = listOf(
                    "Позитивні зміни",
                    "Виклики та проблеми",
                    "Майбутнє технологій"
                )
            ),
            // ... more 20+ topics
        )
    }
    
    fun getTopicById(id: String): ImprovisationTopic? {
        return getAllTopics().find { it.id == id }
    }
}
```

---

## Оновити NavGraph.kt

```kotlin
// Improvisation hub
composable(NavRoutes.Improvisation.route) {
    ImprovisationScreen(
        onNavigateToRandomTopic = {
            navController.navigate(NavRoutes.RandomTopic.route)
        },
        onNavigateToStorytelling = {},
        onNavigateToDebate = {},
        onNavigateToSales = {},
        onNavigateToChallenge = {},
        onNavigateToPremium = {
            navController.navigate(NavRoutes.Premium.route)
        }
    )
}

// Random Topic
composable(NavRoutes.RandomTopic.route) {
    RandomTopicScreen(
        onNavigateBack = { navController.popBackStack() },
        onNavigateToResults = { recordingId ->
            navController.navigate(NavRoutes.Results.createRoute(recordingId))
        }
    )
}
```

---

## Перевірка

### Testing Flow
- [ ] Hub показує 5 mode cards
- [ ] Random Topic доступний
- [ ] Timer працює 15 → 0
- [ ] Recording працює
- [ ] Збереження в DB
- [ ] Free tier limits

---

## Очікуваний результат

✅ Improvisation Hub + Random Topic створено
✅ 20+ тем
✅ Free tier limits
✅ Navigation готова

**Час:** ~2-3 години