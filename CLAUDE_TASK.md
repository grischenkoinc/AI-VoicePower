Мінімальні виправлення HomeScreen. Замінити hardcoded QuickAccess на state.quickActions, додати padding для скролу. Код для HomeScreen.kt:
```kotlin
// Знайти рядок ~40 (Column з verticalScroll) і ДОДАТИ padding:
Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(bottom = 80.dp), // ДОДАТИ цей рядок для скролу
    verticalArrangement = Arrangement.spacedBy(32.dp)
) {
    // ... весь контент
}

// Знайти рядок ~139 QuickAccessSection і ЗАМІНИТИ:
// СТАРИЙ КОД (видалити):
QuickAccessSection(
    onWarmupClick = { onNavigate(Screen.Warmup.route) },
    onRecordClick = { /* TODO */ },
    onImprovisationClick = { onNavigate(Screen.Improvisation.route) },
    onProgressClick = { onNavigate(Screen.Progress.route) }
)

// НОВИЙ КОД (вставити):
QuickAccessSection(
    actions = state.quickActions, // Використати дані з ViewModel
    onActionClick = { action ->
        onNavigate(action.route)
    }
)

// Знайти QuickAccessSection composable (рядок ~600) і ЗАМІНИТИ сигнатуру:
// СТАРИЙ КОД:
@Composable
private fun QuickAccessSection(
    onWarmupClick: () -> Unit,
    onRecordClick: () -> Unit,
    onImprovisationClick: () -> Unit,
    onProgressClick: () -> Unit
) {
    // ... hardcoded карточки
}

// НОВИЙ КОД:
@Composable
private fun QuickAccessSection(
    actions: List<com.aivoicepower.domain.model.home.QuickAction>,
    onActionClick: (com.aivoicepower.domain.model.home.QuickAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Швидкий доступ",
            style = AppTypography.headlineMedium,
            color = Color.White
        )
        
        // Grid 2x2
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.take(2).forEach { action ->
                    QuickActionCard(
                        title = action.title,
                        icon = action.icon,
                        onClick = { onActionClick(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.drop(2).take(2).forEach { action ->
                    QuickActionCard(
                        title = action.title,
                        icon = action.icon,
                        onClick = { onActionClick(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// QuickActionCard залишити БЕЗ ЗМІН!
```

Додати import:
```kotlin
import com.aivoicepower.domain.model.home.QuickAction
```

Компіляція: ./gradlew assembleDebug && adb uninstall com.aivoicepower && ./gradlew installDebug. Що виправлено: QuickAccess тепер використовує state.quickActions з ViewModel (Швидка розминка, Випадкова тема, AI Тренер, Скоромовки), padding(bottom = 80.dp) для скролу до кінця. ВСЯ решта дизайну БЕЗ ЗМІН!