# Промпт для Claude Code — Phase 1.2: Onboarding Flow

## Контекст

Продовжую розробку AI VoicePower. Завершені фази:
- ✅ Phase 0.1-0.6 — Infrastructure (Database, Navigation, Domain, UI Components)
- ✅ Phase 1.1 — Splash Screen (з Phase 0.4)

Зараз **Phase 1.2 — Onboarding Flow** — перше знайомство користувача з застосунком.

**Згідно з PHASE_STRUCTURE_GUIDE.md**, Phase 1 розбита на 3 підфази:
- ✅ Phase 1.1 — Splash Screen (готово)
- **Phase 1.2** — Onboarding Flow (ЦЕ)
- Phase 1.3 — Diagnostic Flow (наступна)
- Phase 1.4 — Diagnostic Results (наступна)

**Специфікація:** `SPECIFICATION.md`, секція 4.3.1 (Onboarding Screen).

---

## Задача Phase 1.2

Створити **4-сторінковий Onboarding** для:
1. Знайомства з можливостями застосунку
2. Вибору головної цілі користувача
3. Вказання часу для щоденних тренувань
4. Переходу до діагностики

### Структура файлів

```
ui/screens/onboarding/
├── OnboardingScreen.kt (HorizontalPager з 4 сторінками)
├── OnboardingViewModel.kt
├── OnboardingState.kt
├── OnboardingEvent.kt
└── components/
    ├── OnboardingPage1.kt (Вітання + можливості)
    ├── OnboardingPage2.kt (Вибір цілі)
    ├── OnboardingPage3.kt (Час тренувань)
    ├── OnboardingPage4.kt (Готовність до діагностики)
    └── PageIndicator.kt
```

---

## Вимоги до UI

### Page 1: Вітання

```
┌─────────────────────────────────────────┐
│            🎤                           │
│        AI VoicePower                    │
│                                         │
│  Твій голос — твоя сила                │
│                                         │
│  ════════════════════════════════════   │
│                                         │
│  Покращ свою дикцію, інтонацію та       │
│  впевненість у мовленні з AI-тренером  │
│                                         │
│  ✓ Персоналізована діагностика          │
│  ✓ Щоденні розминки для голосу         │
│  ✓ Тематичні курси                      │
│  ✓ AI-тренер для персональних порад     │
│  ✓ Відстеження прогресу                 │
│                                         │
│  ┌────────────────────────────────┐    │
│  │         Почати →               │    │
│  └────────────────────────────────┘    │
│                                         │
│          • • • ○  (page indicator)      │
└─────────────────────────────────────────┘
```

### Page 2: Вибір цілі

```
┌─────────────────────────────────────────┐
│  Яка твоя головна ціль?                 │
│  ════════════════════════════════════   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🗣️  Говорити чіткіше            │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎤  Впевнені публічні виступи   │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🎵  Покращити голос             │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 💼  Навчитись переконувати      │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 🤝  Підготовка до співбесіди    │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 📚  Загальний розвиток          │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ← Назад              [Далі →]          │
│          ○ • • ○  (page indicator)      │
└─────────────────────────────────────────┘
```

### Page 3: Час для тренувань

```
┌─────────────────────────────────────────┐
│  Скільки часу готовий приділяти?        │
│  ════════════════════════════════════   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │     5 хвилин на день            │   │
│  │  Швидкі вправи між справами     │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ ✓  15 хвилин на день            │   │
│  │  Оптимально для результату      │   │
│  └─────────────────────────────────┘   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │    30 хвилин на день            │   │
│  │  Прискорений прогрес            │   │
│  └─────────────────────────────────┘   │
│                                         │
│  Ти завжди зможеш змінити це в          │
│  налаштуваннях                          │
│                                         │
│  ← Назад              [Далі →]          │
│          ○ ○ • ○  (page indicator)      │
└─────────────────────────────────────────┘
```

### Page 4: Готовність до діагностики

```
┌─────────────────────────────────────────┐
│  Почнемо з діагностики! 🎯              │
│  ════════════════════════════════════   │
│                                         │
│  Ми проведемо швидкий тест (5 хвилин)  │
│  щоб визначити твій поточний рівень та │
│  створити персоналізований план         │
│                                         │
│  Що будемо оцінювати:                   │
│                                         │
│  📊 Чіткість дикції                     │
│  ⏱️  Темп мовлення                      │
│  🎵 Інтонація та виразність             │
│  🔊 Гучність голосу                     │
│  📝 Структура думок                     │
│  💪 Впевненість                         │
│  🚫 Слова-паразити                      │
│                                         │
│  Знадобиться:                           │
│  • 5 хвилин часу                        │
│  • Тихе місце                           │
│  • Дозвіл на мікрофон                   │
│                                         │
│  ┌────────────────────────────────┐    │
│  │  Почати діагностику →          │    │
│  └────────────────────────────────┘    │
│                                         │
│  ← Назад                                │
│          ○ ○ ○ •  (page indicator)      │
└─────────────────────────────────────────┘
```

---

## Повний код

### 1. OnboardingState.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding

import com.aivoicepower.domain.model.user.UserGoal

data class OnboardingState(
    val currentPage: Int = 0,
    val selectedGoal: UserGoal = UserGoal.GENERAL,
    val dailyMinutes: Int = 15,
    val isNavigating: Boolean = false
)
```

### 2. OnboardingEvent.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding

import com.aivoicepower.domain.model.user.UserGoal

sealed class OnboardingEvent {
    data class PageChanged(val page: Int) : OnboardingEvent()
    data class GoalSelected(val goal: UserGoal) : OnboardingEvent()
    data class MinutesSelected(val minutes: Int) : OnboardingEvent()
    object NextClicked : OnboardingEvent()
    object BackClicked : OnboardingEvent()
    object StartDiagnosticClicked : OnboardingEvent()
}
```

### 3. OnboardingViewModel.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.model.user.UserGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()
    
    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> {
                _state.update { it.copy(currentPage = event.page) }
            }
            
            is OnboardingEvent.GoalSelected -> {
                _state.update { it.copy(selectedGoal = event.goal) }
            }
            
            is OnboardingEvent.MinutesSelected -> {
                _state.update { it.copy(dailyMinutes = event.minutes) }
            }
            
            OnboardingEvent.NextClicked -> {
                val currentPage = _state.value.currentPage
                if (currentPage < 3) {
                    _state.update { it.copy(currentPage = currentPage + 1) }
                }
            }
            
            OnboardingEvent.BackClicked -> {
                val currentPage = _state.value.currentPage
                if (currentPage > 0) {
                    _state.update { it.copy(currentPage = currentPage - 1) }
                }
            }
            
            OnboardingEvent.StartDiagnosticClicked -> {
                saveOnboardingDataAndNavigate()
            }
        }
    }
    
    private fun saveOnboardingDataAndNavigate() {
        viewModelScope.launch {
            val currentState = _state.value
            
            // Зберігаємо вибір користувача в DataStore
            userPreferencesDataStore.setUserGoal(currentState.selectedGoal.name)
            userPreferencesDataStore.setDailyTrainingMinutes(currentState.dailyMinutes)
            userPreferencesDataStore.setOnboardingCompleted(true)
            
            // Позначаємо що навігація в процесі
            _state.update { it.copy(isNavigating = true) }
        }
    }
}
```

### 4. OnboardingScreen.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.onboarding.components.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onNavigateToDiagnostic: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    
    // Синхронізація pagerState з state.currentPage
    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }
    
    // Синхронізація state.currentPage з pagerState
    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onEvent(OnboardingEvent.PageChanged(page))
        }
    }
    
    // Навігація після завершення onboarding
    LaunchedEffect(state.isNavigating) {
        if (state.isNavigating) {
            onNavigateToDiagnostic()
        }
    }
    
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> OnboardingPage1(
                onNextClick = {
                    scope.launch {
                        viewModel.onEvent(OnboardingEvent.NextClicked)
                    }
                }
            )
            
            1 -> OnboardingPage2(
                selectedGoal = state.selectedGoal,
                onGoalSelected = { goal ->
                    viewModel.onEvent(OnboardingEvent.GoalSelected(goal))
                },
                onNextClick = {
                    scope.launch {
                        viewModel.onEvent(OnboardingEvent.NextClicked)
                    }
                },
                onBackClick = {
                    scope.launch {
                        viewModel.onEvent(OnboardingEvent.BackClicked)
                    }
                }
            )
            
            2 -> OnboardingPage3(
                selectedMinutes = state.dailyMinutes,
                onMinutesSelected = { minutes ->
                    viewModel.onEvent(OnboardingEvent.MinutesSelected(minutes))
                },
                onNextClick = {
                    scope.launch {
                        viewModel.onEvent(OnboardingEvent.NextClicked)
                    }
                },
                onBackClick = {
                    scope.launch {
                        viewModel.onEvent(OnboardingEvent.BackClicked)
                    }
                }
            )
            
            3 -> OnboardingPage4(
                onStartDiagnostic = {
                    viewModel.onEvent(OnboardingEvent.StartDiagnosticClicked)
                },
                onBackClick = {
                    scope.launch {
                        viewModel.onEvent(OnboardingEvent.BackClicked)
                    }
                }
            )
        }
    }
}
```

### 5. components/OnboardingPage1.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingPage1(
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎤",
                style = MaterialTheme.typography.displayLarge,
                fontSize = MaterialTheme.typography.displayLarge.fontSize * 1.5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "AI VoicePower",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Твій голос — твоя сила",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Features List
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Покращ свою дикцію, інтонацію та\nвпевненість у мовленні з AI-тренером",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            FeatureItem(
                icon = Icons.Default.Assessment,
                text = "Персоналізована діагностика"
            )
            
            FeatureItem(
                icon = Icons.Default.FitnessCenter,
                text = "Щоденні розминки для голосу"
            )
            
            FeatureItem(
                icon = Icons.Default.MenuBook,
                text = "Тематичні курси"
            )
            
            FeatureItem(
                icon = Icons.Default.Assistant,
                text = "AI-тренер для персональних порад"
            )
            
            FeatureItem(
                icon = Icons.Default.TrendingUp,
                text = "Відстеження прогресу"
            )
        }
        
        // Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Почати →")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PageIndicator(currentPage = 0, totalPages = 4)
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
```

### 6. components/OnboardingPage2.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.user.UserGoal

@Composable
fun OnboardingPage2(
    selectedGoal: UserGoal,
    onGoalSelected: (UserGoal) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Яка твоя головна ціль?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalOption(
                    emoji = "🗣️",
                    title = "Говорити чіткіше",
                    goal = UserGoal.CLEAR_SPEECH,
                    isSelected = selectedGoal == UserGoal.CLEAR_SPEECH,
                    onSelect = { onGoalSelected(UserGoal.CLEAR_SPEECH) }
                )
                
                GoalOption(
                    emoji = "🎤",
                    title = "Впевнені публічні виступи",
                    goal = UserGoal.PUBLIC_SPEAKING,
                    isSelected = selectedGoal == UserGoal.PUBLIC_SPEAKING,
                    onSelect = { onGoalSelected(UserGoal.PUBLIC_SPEAKING) }
                )
                
                GoalOption(
                    emoji = "🎵",
                    title = "Покращити голос",
                    goal = UserGoal.BETTER_VOICE,
                    isSelected = selectedGoal == UserGoal.BETTER_VOICE,
                    onSelect = { onGoalSelected(UserGoal.BETTER_VOICE) }
                )
                
                GoalOption(
                    emoji = "💼",
                    title = "Навчитись переконувати",
                    goal = UserGoal.PERSUASION,
                    isSelected = selectedGoal == UserGoal.PERSUASION,
                    onSelect = { onGoalSelected(UserGoal.PERSUASION) }
                )
                
                GoalOption(
                    emoji = "🤝",
                    title = "Підготовка до співбесіди",
                    goal = UserGoal.INTERVIEW_PREP,
                    isSelected = selectedGoal == UserGoal.INTERVIEW_PREP,
                    onSelect = { onGoalSelected(UserGoal.INTERVIEW_PREP) }
                )
                
                GoalOption(
                    emoji = "📚",
                    title = "Загальний розвиток",
                    goal = UserGoal.GENERAL,
                    isSelected = selectedGoal == UserGoal.GENERAL,
                    onSelect = { onGoalSelected(UserGoal.GENERAL) }
                )
            }
        }
        
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Назад")
                }
                
                Button(
                    onClick = onNextClick,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Далі →")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PageIndicator(currentPage = 1, totalPages = 4)
        }
    }
}

@Composable
private fun GoalOption(
    emoji: String,
    title: String,
    goal: UserGoal,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
```

### 7. components/OnboardingPage3.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingPage3(
    selectedMinutes: Int,
    onMinutesSelected: (Int) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Скільки часу готовий приділяти?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeOption(
                    minutes = 5,
                    title = "5 хвилин на день",
                    subtitle = "Швидкі вправи між справами",
                    isSelected = selectedMinutes == 5,
                    onSelect = { onMinutesSelected(5) }
                )
                
                TimeOption(
                    minutes = 15,
                    title = "15 хвилин на день",
                    subtitle = "Оптимально для результату",
                    isSelected = selectedMinutes == 15,
                    isRecommended = true,
                    onSelect = { onMinutesSelected(15) }
                )
                
                TimeOption(
                    minutes = 30,
                    title = "30 хвилин на день",
                    subtitle = "Прискорений прогрес",
                    isSelected = selectedMinutes == 30,
                    onSelect = { onMinutesSelected(30) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Ти завжди зможеш змінити це в налаштуваннях",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Назад")
                }
                
                Button(
                    onClick = onNextClick,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Далі →")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PageIndicator(currentPage = 2, totalPages = 4)
        }
    }
}

@Composable
private fun TimeOption(
    minutes: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isRecommended: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                if (isRecommended && !isSelected) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "✓ Рекомендовано",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )
        }
    }
}
```

### 8. components/OnboardingPage4.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingPage4(
    onStartDiagnostic: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Почнемо з діагностики! 🎯",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Ми проведемо швидкий тест (5 хвилин) щоб визначити твій поточний рівень та створити персоналізований план",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Metrics
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Що будемо оцінювати:",
                    style = MaterialTheme.typography.titleMedium
                )
                
                MetricItem(
                    icon = Icons.Default.GraphicEq,
                    text = "Чіткість дикції"
                )
                
                MetricItem(
                    icon = Icons.Default.Speed,
                    text = "Темп мовлення"
                )
                
                MetricItem(
                    icon = Icons.Default.MusicNote,
                    text = "Інтонація та виразність"
                )
                
                MetricItem(
                    icon = Icons.Default.VolumeUp,
                    text = "Гучність голосу"
                )
                
                MetricItem(
                    icon = Icons.Default.ListAlt,
                    text = "Структура думок"
                )
                
                MetricItem(
                    icon = Icons.Default.CheckCircle,
                    text = "Впевненість"
                )
                
                MetricItem(
                    icon = Icons.Default.Block,
                    text = "Слова-паразити"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Requirements
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Знадобиться:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    RequirementItem("• 5 хвилин часу")
                    RequirementItem("• Тихе місце")
                    RequirementItem("• Дозвіл на мікрофон")
                }
            }
        }
        
        Column {
            Button(
                onClick = onStartDiagnostic,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Почати діагностику →")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onBackClick) {
                Text("← Назад")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PageIndicator(currentPage = 3, totalPages = 4)
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RequirementItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}
```

### 9. components/PageIndicator.kt

```kotlin
package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}
```

---

## Перевірка

### Після виконання:

1. **Компіляція:**
```bash
./gradlew assembleDebug
```

2. **Тест flow:**
   - Запустити застосунок
   - Splash повинен показатися → Onboarding
   - Пройти всі 4 сторінки
   - Натиснути "Почати діагностику"
   - DataStore має зберегти: goal, dailyMinutes, hasCompletedOnboarding=true
   - Має перейти до DiagnosticScreen (поки placeholder)

3. **Перевірка DataStore:**
```kotlin
// В будь-якому ViewModel
viewModelScope.launch {
    userPreferencesDataStore.userPreferencesFlow.collect { prefs ->
        println("Goal: ${prefs.userGoal}")
        println("Minutes: ${prefs.dailyTrainingMinutes}")
        println("Onboarding: ${prefs.hasCompletedOnboarding}")
    }
}
```

---

## Очікуваний результат

✅ Onboarding з 4 сторінками створено
✅ Вибір цілі працює
✅ Вибір часу працює
✅ Дані зберігаються в DataStore
✅ Навігація до Diagnostic працює
✅ Page indicators працюють
✅ Swipe між сторінками працює
✅ Кнопки "Назад/Далі" працюють

---

## Що НЕ робити

- НЕ створювати DiagnosticScreen (це Phase 1.3)
- НЕ інтегрувати з Room Database (поки що тільки DataStore)
- НЕ додавати анімації (базовий flow спочатку)
- НЕ створювати UserProfile в Room (поки що)

---

## Наступний крок

**Phase 1.3: Diagnostic Flow** — 4 завдання з записом аудіо та placeholder AI-аналізом.