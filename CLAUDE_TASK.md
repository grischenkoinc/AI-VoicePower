# Промпт для Claude Code — Phase 1.4: Diagnostic Results

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure
- ✅ Phase 1.1 — Splash Screen
- ✅ Phase 1.2 — Onboarding Flow
- ✅ Phase 1.3 — Diagnostic Flow (4 записи в Room)

Зараз **Phase 1.4 — Diagnostic Results** — візуалізація результатів та перехід до Home.

**Згідно з PHASE_STRUCTURE_GUIDE.md**, це ПРОСТА підфаза (візуалізація даних).

**Специфікація:** `SPECIFICATION.md`, секція 4.3.3 (DiagnosticResult Screen).

**Складність:** 🟡 НИЗЬКА-СЕРЕДНЯ (UI + fake scores)
**Час:** ⏱️ 1-1.5 години

---

## Задача Phase 1.4

Створити екран результатів діагностики, який показує:
1. **7 метрик** з оцінками 0-100 та radar chart
2. **Сильні сторони** (2-3 пункти)
3. **Зони покращення** (2-3 пункти)
4. **Персоналізовані рекомендації**
5. **Кнопка переходу до Home**

### Що створюємо

```
ui/screens/diagnostic/
├── DiagnosticResultScreen.kt
├── DiagnosticResultViewModel.kt
├── DiagnosticResultState.kt
└── components/
    ├── SkillRadarChart.kt (7-кутний radar)
    ├── SkillScoreCard.kt (окрема метрика з progress bar)
    ├── FeedbackSection.kt (сильні сторони / покращення)
    └── RecommendationCard.kt

domain/model/analysis/ (якщо ще немає з Phase 0.5)
└── DiagnosticResult.kt
```

---

## UI Design

```
┌──────────────────────────────────────────────┐
│  Результати діагностики                [X]   │
├──────────────────────────────────────────────┤
│  (scroll)                                    │
│                                              │
│  🎉 Діагностика завершена!                   │
│  Ось що ми дізналися про твоє мовлення:     │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │         RADAR CHART (7 metrics)        │ │
│  │              Дикція                    │ │
│  │         75                             │ │
│  │    Паразити       Темп                 │ │
│  │    50       •       70                 │ │
│  │                                        │ │
│  │  Впевн.                  Інтонація    │ │
│  │   55                      65          │ │
│  │       Структ.    Гучність             │ │
│  │         60         80                 │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                              │
│  📊 Детальні оцінки                          │
│                                              │
│  Дикція                          75 / 100   │
│  ━━━━━━━━━━━━━━━━━━○○○○○                    │
│  Добре! Чітке вимовляння звуків.            │
│                                              │
│  Темп мовлення                   70 / 100   │
│  ━━━━━━━━━━━━━━━━○○○○○○                     │
│  Гарний темп, невелика поспіх.              │
│                                              │
│  Інтонація                       65 / 100   │
│  ━━━━━━━━━━━━━━○○○○○○○○                     │
│  Можна додати більше виразності.            │
│                                              │
│  [показати всі 7 метрик]                    │
│                                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                              │
│  ✅ Твої сильні сторони:                     │
│  • Чітка дикція та вимова                   │
│  • Гарна гучність голосу                    │
│  • Структурована мова                       │
│                                              │
│  🎯 Зони для покращення:                     │
│  • Зменш кількість слів-паразитів           │
│  • Працюй над впевненістю                   │
│  • Додай більше емоційності                 │
│                                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                              │
│  💡 Персоналізовані рекомендації:            │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 📖 Курс: "Чисте мовлення"             │ │
│  │ Позбався слів-паразитів за 14 днів    │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 🎤 Щоденна розминка                    │ │
│  │ Почни з артикуляційної гімнастики     │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 🎭 Практика інтонації                  │ │
│  │ Емоційне читання 10 хв щодня          │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━    │
│                                              │
│  [Почати тренування →]                      │
│  (перехід до Home Screen)                   │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Повний код

### 1. DiagnosticResultState.kt

```kotlin
package com.aivoicepower.ui.screens.diagnostic

data class DiagnosticResultState(
    val isLoading: Boolean = true,
    val result: DiagnosticResultDisplay? = null,
    val error: String? = null
)

data class DiagnosticResultDisplay(
    val overall: Int,
    val metrics: List<MetricDisplay>,
    val strengths: List<String>,
    val improvements: List<String>,
    val recommendations: List<RecommendationDisplay>
)

data class MetricDisplay(
    val name: String,
    val score: Int,          // 0-100
    val label: String,       // "Відмінно", "Добре", "Середньо", "Потребує покращення"
    val description: String
)

data class RecommendationDisplay(
    val icon: String,
    val title: String,
    val description: String,
    val actionRoute: String? = null
)
```

### 2. DiagnosticResultViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticResultViewModel @Inject constructor(
    private val diagnosticResultDao: DiagnosticResultDao,
    private val userProgressDao: UserProgressDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(DiagnosticResultState())
    val state: StateFlow<DiagnosticResultState> = _state.asStateFlow()
    
    init {
        loadDiagnosticResult()
    }
    
    private fun loadDiagnosticResult() {
        viewModelScope.launch {
            try {
                // Завантажуємо останній результат діагностики
                diagnosticResultDao.getLatestDiagnostic().collect { entity ->
                    if (entity != null) {
                        // Конвертуємо Entity → Display model
                        val display = DiagnosticResultDisplay(
                            overall = calculateOverall(entity),
                            metrics = listOf(
                                MetricDisplay(
                                    name = "Дикція",
                                    score = entity.diction,
                                    label = getScoreLabel(entity.diction),
                                    description = getScoreDescription("diction", entity.diction)
                                ),
                                MetricDisplay(
                                    name = "Темп мовлення",
                                    score = entity.tempo,
                                    label = getScoreLabel(entity.tempo),
                                    description = getScoreDescription("tempo", entity.tempo)
                                ),
                                MetricDisplay(
                                    name = "Інтонація",
                                    score = entity.intonation,
                                    label = getScoreLabel(entity.intonation),
                                    description = getScoreDescription("intonation", entity.intonation)
                                ),
                                MetricDisplay(
                                    name = "Гучність",
                                    score = entity.volume,
                                    label = getScoreLabel(entity.volume),
                                    description = getScoreDescription("volume", entity.volume)
                                ),
                                MetricDisplay(
                                    name = "Структура",
                                    score = entity.structure,
                                    label = getScoreLabel(entity.structure),
                                    description = getScoreDescription("structure", entity.structure)
                                ),
                                MetricDisplay(
                                    name = "Впевненість",
                                    score = entity.confidence,
                                    label = getScoreLabel(entity.confidence),
                                    description = getScoreDescription("confidence", entity.confidence)
                                ),
                                MetricDisplay(
                                    name = "Без паразитів",
                                    score = entity.fillerWords,
                                    label = getScoreLabel(entity.fillerWords),
                                    description = getScoreDescription("fillerWords", entity.fillerWords)
                                )
                            ),
                            strengths = generateStrengths(entity),
                            improvements = generateImprovements(entity),
                            recommendations = generateRecommendations(entity)
                        )
                        
                        _state.update {
                            it.copy(
                                isLoading = false,
                                result = display
                            )
                        }
                        
                        // Зберігаємо рівні навичок в UserProgress
                        saveToUserProgress(entity)
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не вдалося завантажити результати"
                    )
                }
            }
        }
    }
    
    private fun calculateOverall(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): Int {
        return (entity.diction + entity.tempo + entity.intonation + 
                entity.volume + entity.structure + entity.confidence + 
                entity.fillerWords) / 7
    }
    
    private fun getScoreLabel(score: Int): String {
        return when {
            score >= 85 -> "Відмінно"
            score >= 70 -> "Добре"
            score >= 50 -> "Середньо"
            else -> "Потребує покращення"
        }
    }
    
    private fun getScoreDescription(metric: String, score: Int): String {
        // Fake descriptions based on score
        return when (metric) {
            "diction" -> when {
                score >= 70 -> "Чітке вимовляння звуків"
                else -> "Працюй над чіткістю вимови"
            }
            "tempo" -> when {
                score >= 70 -> "Гарний темп мовлення"
                score >= 50 -> "Невелика поспіх"
                else -> "Занадто швидко або повільно"
            }
            "intonation" -> when {
                score >= 70 -> "Виразна інтонація"
                else -> "Можна додати більше виразності"
            }
            "volume" -> when {
                score >= 70 -> "Гарна гучність голосу"
                else -> "Говори трохи голосніше"
            }
            "structure" -> when {
                score >= 70 -> "Структурована мова"
                else -> "Працюй над логікою викладу"
            }
            "confidence" -> when {
                score >= 70 -> "Впевнена манера мовлення"
                else -> "Додай більше впевненості"
            }
            "fillerWords" -> when {
                score >= 70 -> "Мало слів-паразитів"
                else -> "Зменш кількість слів-паразитів"
            }
            else -> "Гарний результат"
        }
    }
    
    private fun generateStrengths(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): List<String> {
        val strengths = mutableListOf<String>()
        
        if (entity.diction >= 70) strengths.add("Чітка дикція та вимова")
        if (entity.tempo >= 70) strengths.add("Гарний темп мовлення")
        if (entity.intonation >= 70) strengths.add("Виразна інтонація")
        if (entity.volume >= 70) strengths.add("Гарна гучність голосу")
        if (entity.structure >= 70) strengths.add("Структурована мова")
        if (entity.confidence >= 70) strengths.add("Впевнена манера мовлення")
        if (entity.fillerWords >= 70) strengths.add("Мало слів-паразитів")
        
        return if (strengths.size >= 2) {
            strengths.take(3)
        } else {
            listOf("Ти на правильному шляху!", "Є базові навички для розвитку")
        }
    }
    
    private fun generateImprovements(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): List<String> {
        val improvements = mutableListOf<String>()
        
        if (entity.diction < 70) improvements.add("Покращ чіткість дикції")
        if (entity.tempo < 70) improvements.add("Працюй над темпом мовлення")
        if (entity.intonation < 70) improvements.add("Додай більше емоційності")
        if (entity.volume < 70) improvements.add("Збільш гучність голосу")
        if (entity.structure < 70) improvements.add("Працюй над структурою думок")
        if (entity.confidence < 70) improvements.add("Розвивай впевненість")
        if (entity.fillerWords < 70) improvements.add("Зменш кількість слів-паразитів")
        
        return improvements.take(3)
    }
    
    private fun generateRecommendations(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity): List<RecommendationDisplay> {
        val recommendations = mutableListOf<RecommendationDisplay>()
        
        // Рекомендації на основі найслабших метрик
        if (entity.fillerWords < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "📖",
                    title = "Курс: \"Чисте мовлення\"",
                    description = "Позбався слів-паразитів за 14 днів",
                    actionRoute = "courses/clean_speech"
                )
            )
        }
        
        if (entity.diction < 60 || entity.tempo < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "🎤",
                    title = "Щоденна розминка",
                    description = "Почни з артикуляційної гімнастики",
                    actionRoute = "warmup/articulation"
                )
            )
        }
        
        if (entity.intonation < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "🎭",
                    title = "Практика інтонації",
                    description = "Емоційне читання 10 хв щодня",
                    actionRoute = "courses/intonation"
                )
            )
        }
        
        if (entity.confidence < 60) {
            recommendations.add(
                RecommendationDisplay(
                    icon = "💪",
                    title = "Тренуй впевненість",
                    description = "Імпровізуй на випадкові теми",
                    actionRoute = "improvisation/random"
                )
            )
        }
        
        // Завжди додаємо загальну рекомендацію
        recommendations.add(
            RecommendationDisplay(
                icon = "🏠",
                title = "Почни з головного",
                description = "Переглянь персоналізований план на сьогодні",
                actionRoute = "home"
            )
        )
        
        return recommendations.take(3)
    }
    
    private suspend fun saveToUserProgress(entity: com.aivoicepower.data.local.database.entity.DiagnosticResultEntity) {
        val existingProgress = userProgressDao.getUserProgressOnce()
        
        if (existingProgress == null) {
            // Створюємо новий прогрес
            userProgressDao.insertOrUpdate(
                UserProgressEntity(
                    id = "default_progress",
                    dictionLevel = entity.diction,
                    tempoLevel = entity.tempo,
                    intonationLevel = entity.intonation,
                    volumeLevel = entity.volume,
                    structureLevel = entity.structure,
                    confidenceLevel = entity.confidence,
                    fillerWordsLevel = entity.fillerWords
                )
            )
        } else {
            // Оновлюємо існуючий
            userProgressDao.updateSkillLevels(
                diction = entity.diction,
                tempo = entity.tempo,
                intonation = entity.intonation,
                volume = entity.volume,
                structure = entity.structure,
                confidence = entity.confidence,
                fillerWords = entity.fillerWords
            )
        }
    }
}
```

### 3. DiagnosticResultScreen.kt

```kotlin
package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.diagnostic.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticResultScreen(
    viewModel: DiagnosticResultViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результати діагностики") },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
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
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Аналізуємо твоє мовлення...")
                    }
                }
            }
            
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error ?: "Помилка")
                }
            }
            
            state.result != null -> {
                DiagnosticResultContent(
                    result = state.result!!,
                    onNavigateToHome = onNavigateToHome,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DiagnosticResultContent(
    result: DiagnosticResultDisplay,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎉 Діагностика завершена!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Ось що ми дізналися про твоє мовлення:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Radar Chart
        item {
            SkillRadarChart(
                metrics = result.metrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
        
        item {
            Divider()
        }
        
        // Detailed Scores
        item {
            Text(
                text = "📊 Детальні оцінки",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        items(result.metrics) { metric ->
            SkillScoreCard(metric = metric)
        }
        
        item {
            Divider()
        }
        
        // Strengths
        item {
            FeedbackSection(
                title = "✅ Твої сильні сторони:",
                items = result.strengths,
                isPositive = true
            )
        }
        
        // Improvements
        item {
            FeedbackSection(
                title = "🎯 Зони для покращення:",
                items = result.improvements,
                isPositive = false
            )
        }
        
        item {
            Divider()
        }
        
        // Recommendations
        item {
            Text(
                text = "💡 Персоналізовані рекомендації:",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        items(result.recommendations) { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onClick = { /* TODO: Navigate to recommendation.actionRoute */ }
            )
        }
        
        // Start button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Почати тренування →")
            }
        }
    }
}
```

### 4. components/SkillScoreCard.kt

```kotlin
package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.diagnostic.MetricDisplay

@Composable
fun SkillScoreCard(
    metric: MetricDisplay,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = metric.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "${metric.score} / 100",
                style = MaterialTheme.typography.titleMedium,
                color = getScoreColor(metric.score)
            )
        }
        
        // Progress bar
        LinearProgressIndicator(
            progress = { metric.score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = getScoreColor(metric.score)
        )
        
        // Description
        Text(
            text = metric.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun getScoreColor(score: Int) = when {
    score >= 85 -> MaterialTheme.colorScheme.primary
    score >= 70 -> MaterialTheme.colorScheme.tertiary
    score >= 50 -> MaterialTheme.colorScheme.secondary
    else -> MaterialTheme.colorScheme.error
}
```

### 5. components/SkillRadarChart.kt (simplified placeholder)

```kotlin
package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.diagnostic.MetricDisplay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SkillRadarChart(
    metrics: List<MetricDisplay>,
    modifier: Modifier = Modifier
) {
    // Simplified radar chart
    // TODO: Implement proper radar chart with Canvas or use library
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📊 Профіль навичок",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Simple Canvas placeholder
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f
            
            // Draw background circles
            for (i in 1..4) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    radius = radius * i / 4,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            
            // Draw metrics as points
            // TODO: Proper implementation
        }
    }
}
```

### 6. components/FeedbackSection.kt

```kotlin
package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedbackSection(
    title: String,
    items: List<String>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        )
        
        items.forEach { item ->
            Text(
                text = "• $item",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

### 7. components/RecommendationCard.kt

```kotlin
package com.aivoicepower.ui.screens.diagnostic.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.diagnostic.RecommendationDisplay

@Composable
fun RecommendationCard(
    recommendation: RecommendationDisplay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recommendation.icon,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

**Тест 1: Loading State**
- [ ] Показується "Аналізуємо твоє мовлення..." + spinner

**Тест 2: Results Display**
- [ ] Відображаються 7 метрик з оцінками
- [ ] Progress bars працюють
- [ ] Radar chart показується (навіть placeholder)
- [ ] Сильні сторони відображаються
- [ ] Зони покращення відображаються
- [ ] 3 рекомендації показуються

**Тест 3: Navigation**
- [ ] Кнопка "Почати тренування" → Home Screen
- [ ] Close button → Home Screen

### 3. Database Verification

```kotlin
// Перевірити що дані збережені в UserProgress
userProgressDao.getUserProgress().collect { progress ->
    println("Skill levels saved:")
    println("  Diction: ${progress?.dictionLevel}")
    println("  Tempo: ${progress?.tempoLevel}")
    // ...
}
```

---

## Очікуваний результат

✅ DiagnosticResultScreen показує результати
✅ 7 метрик з оцінками відображаються
✅ Сильні сторони та покращення генеруються
✅ Рекомендації персоналізовані
✅ Дані зберігаються в UserProgress
✅ Навігація до Home працює
✅ Phase 1 повністю завершена! 🎉

---

## Що далі?

**Phase 2: Warmup** — розминка мовленнєвого апарату (артикуляція, дихання, голос).

---

**Phase 1 завершена!** 🎊

Тепер у тебе є:
- ✅ Splash Screen (перевірка онбордингу)
- ✅ Onboarding (4 сторінки, збір даних)
- ✅ Diagnostic (4 завдання, fake recording)
- ✅ Results (візуалізація, рекомендації)

Користувач може пройти повний flow від першого запуску до персоналізованих рекомендацій!