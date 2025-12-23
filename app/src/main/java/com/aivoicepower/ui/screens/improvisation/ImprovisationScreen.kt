package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
            onClick = { /* Phase 5.2 */ }
        )

        ImprovisationModeCard(
            emoji = "🏆",
            title = "Щоденний челендж",
            description = "Унікальне завдання кожен день",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.2",
            onClick = { /* Phase 5.2 */ }
        )

        ImprovisationModeCard(
            emoji = "⚔️",
            title = "Дебати з AI",
            description = "Переконуй штучний інтелект",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.3",
            onClick = { /* Phase 5.3 */ }
        )

        ImprovisationModeCard(
            emoji = "💼",
            title = "Продай товар",
            description = "Презентуй продукт AI-клієнту",
            isLocked = !state.isPremium,
            isComingSoon = true,
            comingSoonText = "Phase 5.3",
            onClick = { /* Phase 5.3 */ }
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
