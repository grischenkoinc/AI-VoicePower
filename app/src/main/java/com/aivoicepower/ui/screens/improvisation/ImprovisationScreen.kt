package com.aivoicepower.ui.screens.improvisation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.improvisation.components.ImprovisationModeCard
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun ImprovisationScreen(
    viewModel: ImprovisationViewModel = hiltViewModel(),
    onNavigateToRandomTopic: () -> Unit,
    onNavigateToStorytelling: () -> Unit,
    onNavigateToDebate: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        ImprovisationContent(
            state = state,
            viewModel = viewModel,
            onNavigateToRandomTopic = onNavigateToRandomTopic,
            onNavigateToStorytelling = onNavigateToStorytelling,
            onNavigateToDebate = onNavigateToDebate,
            onNavigateToSales = onNavigateToSales,
            onNavigateToChallenge = onNavigateToChallenge,
            onNavigateToPremium = onNavigateToPremium,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
private fun ImprovisationContent(
    state: ImprovisationState,
    viewModel: ImprovisationViewModel,
    onNavigateToRandomTopic: () -> Unit,
    onNavigateToStorytelling: () -> Unit,
    onNavigateToDebate: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        ImprovisationHeader(onNavigateBack = onNavigateBack)

        // Subtitle
        Text(
            text = "Тренуй спонтанне мовлення",
            style = AppTypography.bodyLarge,
            color = TextColors.onDarkSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        // Stats card (for free users)
        if (!state.isPremium) {
            StatsCard(
                completedToday = state.completedToday,
                dailyLimit = state.dailyLimit
            )
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
            isLocked = false,
            isComingSoon = false,
            onClick = {
                if (viewModel.canStartImprovisation()) {
                    viewModel.onEvent(ImprovisationEvent.StorytellingClicked)
                    onNavigateToStorytelling()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        ImprovisationModeCard(
            emoji = "🏆",
            title = "Щоденний челендж",
            description = "Унікальне завдання кожен день",
            isLocked = false,
            isComingSoon = false,
            onClick = {
                if (viewModel.canStartImprovisation()) {
                    viewModel.onEvent(ImprovisationEvent.DailyChallengeClicked)
                    onNavigateToChallenge()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        ImprovisationModeCard(
            emoji = "⚔️",
            title = "Дебати з AI",
            description = "Переконуй штучний інтелект",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.DebateClicked)
                    onNavigateToDebate()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        ImprovisationModeCard(
            emoji = "💼",
            title = "Продай товар",
            description = "Презентуй продукт AI-клієнту",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.SalesPitchClicked)
                    onNavigateToSales()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        // Premium prompt (if needed)
        if (!state.isPremium && state.completedToday >= state.dailyLimit) {
            PremiumPromptCard(onNavigateToPremium = onNavigateToPremium)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ImprovisationHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Спонтанне мовлення",
            style = AppTypography.labelMedium,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Імпровізація",
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.8).sp
        )
    }
}

@Composable
private fun StatsCard(
    completedToday: Int,
    dailyLimit: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.3f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x73667EEA), // 45% opacity
                        Color(0x59764BA2)  // 35% opacity
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 Сьогодні:",
                style = AppTypography.titleMedium,
                color = TextColors.onDarkPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$completedToday/$dailyLimit",
                style = AppTypography.displayMedium,
                color = Color(0xFFFBBF24),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun PremiumPromptCard(
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFBEB),
                        Color(0xFFFEF3C7)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable { onNavigateToPremium() }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⭐", fontSize = 32.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ліміт вичерпано",
                    style = AppTypography.titleMedium,
                    color = Color(0xFFD97706),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Отримай Premium для необмеженої практики",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
