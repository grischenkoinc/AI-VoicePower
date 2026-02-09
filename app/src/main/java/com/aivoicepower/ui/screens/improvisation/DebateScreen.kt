package com.aivoicepower.ui.screens.improvisation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun DebateScreen(
    viewModel: DebateViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var backPressedTime by remember { mutableStateOf(0L) }

    // Double-back to exit protection (NOT on topic/position selection)
    BackHandler(enabled = state.phase != DebatePhase.TopicSelection && state.phase != DebatePhase.PositionSelection) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            onNavigateBack()
        } else {
            backPressedTime = currentTime
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Для виходу натисніть Назад ще раз",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Імпровізація",
                        style = AppTypography.labelMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "⚔️ Дебати з AI",
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                }

                // Back button
                Row(
                    modifier = Modifier
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        fontSize = 24.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Назад",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
            when (state.phase) {
                DebatePhase.TopicSelection -> {
                    TopicSelectionContent(
                        onTopicSelected = { viewModel.onEvent(DebateEvent.TopicSelected(it)) }
                    )
                }

                DebatePhase.PositionSelection -> {
                    PositionSelectionContent(
                        topic = state.selectedTopic!!,
                        onPositionSelected = { viewModel.onEvent(DebateEvent.PositionSelected(it)) }
                    )
                }

                DebatePhase.UserArgument -> {
                    UserArgumentContent(
                        topic = state.selectedTopic!!,
                        position = state.userPosition!!,
                        roundNumber = state.currentRound,
                        isRecording = state.isRecording,
                        secondsElapsed = state.recordingSeconds,
                        maxSeconds = state.maxRecordingSeconds,
                        onStartRecording = { viewModel.onEvent(DebateEvent.StartRecordingClicked) },
                        onStopRecording = { viewModel.onEvent(DebateEvent.StopRecordingClicked) }
                    )
                }

                DebatePhase.AiResponse -> {
                    AiResponseContent(
                        isThinking = state.isAiThinking,
                        rounds = state.rounds,
                        currentRound = state.currentRound,
                        maxRounds = state.maxRounds,
                        onNextRound = { viewModel.onEvent(DebateEvent.NextRoundClicked) },
                        onFinish = { viewModel.onEvent(DebateEvent.FinishDebateClicked) }
                    )
                }

                DebatePhase.DebateComplete -> {
                    DebateCompleteContent(
                        topic = state.selectedTopic!!,
                        rounds = state.rounds,
                        onFinish = onNavigateBack
                    )
                }
            }

                // Error message
                state.error?.let { error ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color(0xFFEF4444).copy(alpha = 0.2f)
                            )
                            .background(Color(0xFFFEF2F2), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = error,
                            style = AppTypography.bodyMedium,
                            color = Color(0xFFDC2626),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF667EEA),
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TopicSelectionContent(
    onTopicSelected: (com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic) -> Unit
) {
    val topics = remember { com.aivoicepower.data.content.DebateTopicsProvider().getAllTopics() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Обери тему для дебатів:",
                style = AppTypography.titleLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(topics) { topic ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color.Black.copy(alpha = 0.12f)
                    )
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clickable { onTopicSelected(topic) }
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = topic.topic,
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topic.description,
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Складність: ${topic.difficulty}",
                    style = AppTypography.labelMedium,
                    color = Color(0xFF6366F1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PositionSelectionContent(
    topic: com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic,
    onPositionSelected: (DebatePosition) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Тема:",
            style = AppTypography.titleMedium,
            color = TextColors.onDarkPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.12f)
                )
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = topic.topic,
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Обери свою позицію:",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0xFF10B981).copy(alpha = 0.2f)
                )
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable { onPositionSelected(DebatePosition.FOR) }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✅ ЗА",
                style = AppTypography.headlineMedium,
                color = Color(0xFF10B981),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Ти будеш підтримувати цю позицію",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0xFFEF4444).copy(alpha = 0.2f)
                )
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable { onPositionSelected(DebatePosition.AGAINST) }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❌ ПРОТИ",
                style = AppTypography.headlineMedium,
                color = Color(0xFFEF4444),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Ти будеш аргументувати проти",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun UserArgumentContent(
    topic: com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic,
    position: DebatePosition,
    roundNumber: Int,
    isRecording: Boolean,
    secondsElapsed: Int,
    maxSeconds: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.18f),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Раунд $roundNumber з 5",
                style = AppTypography.labelLarge,
                color = Color(0xFF667EEA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = topic.topic,
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Твоя позиція: ${if (position == DebatePosition.FOR) "ЗА" else "ПРОТИ"}",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isRecording) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0xFFEF4444).copy(alpha = 0.3f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🔴 Запис аргументу...",
                    style = AppTypography.headlineSmall,
                    color = Color(0xFFEF4444),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$secondsElapsed / $maxSeconds сек",
                    style = AppTypography.displayMedium,
                    color = Color(0xFF991B1B),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                )
                LinearProgressIndicator(
                    progress = { secondsElapsed.toFloat() / maxSeconds },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Color(0xFFEF4444),
                    trackColor = Color.White.copy(alpha = 0.5f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                PrimaryButton(
                    text = "■ Завершити запис",
                    onClick = onStopRecording,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.18f),
                        ambientColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Твій хід:",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Наведи свій аргумент. У тебе є до $maxSeconds секунд.",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "🎤 Почати запис аргументу",
                onClick = onStartRecording,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AiResponseContent(
    isThinking: Boolean,
    rounds: List<DebateRound>,
    currentRound: Int,
    maxRounds: Int,
    onNextRound: () -> Unit,
    onFinish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isThinking) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color.Black.copy(alpha = 0.18f),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        )
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = Color(0xFF667EEA))
                    Text(
                        text = "AI обдумує відповідь...",
                        style = AppTypography.titleMedium,
                        color = TextColors.onLightPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "Раунд $currentRound:",
                    style = AppTypography.titleLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            rounds.lastOrNull()?.let { lastRound ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color.Black.copy(alpha = 0.18f),
                                ambientColor = Color.Black.copy(alpha = 0.08f)
                            )
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Твій аргумент:",
                            style = AppTypography.labelLarge,
                            color = Color(0xFF667EEA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lastRound.userArgument,
                            style = AppTypography.bodyLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color.Black.copy(alpha = 0.18f),
                                ambientColor = Color.Black.copy(alpha = 0.08f)
                            )
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Відповідь AI:",
                            style = AppTypography.labelLarge,
                            color = Color(0xFF764BA2),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lastRound.aiResponse,
                            style = AppTypography.bodyLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                if (currentRound < maxRounds) {
                    PrimaryButton(
                        text = "Наступний раунд (${currentRound + 1}/$maxRounds)",
                        onClick = onNextRound,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PrimaryButton(
                        text = "Завершити дебати",
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DebateCompleteContent(
    topic: com.aivoicepower.data.content.DebateTopicsProvider.DebateTopic,
    rounds: List<DebateRound>,
    onFinish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.18f),
                        ambientColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✓ Дебати завершено!",
                    style = AppTypography.headlineMedium,
                    color = Color(0xFF667EEA),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Тема: ${topic.topic}",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Проведено ${rounds.size} раундів",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Text(
                text = "Історія дебатів:",
                style = AppTypography.titleLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        items(rounds) { round ->
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Раунд ${round.roundNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ти: ${round.userArgument}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HorizontalDivider()
                    Text(
                        text = "AI: ${round.aiResponse}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            PrimaryButton(
                text = "Готово",
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
