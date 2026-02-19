package com.aivoicepower.ui.screens.improvisation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.content.DebateTopicsProvider
import com.aivoicepower.ui.components.FocusCountdownOverlay
import com.aivoicepower.ui.screens.improvisation.components.AnalyzingScreen
import com.aivoicepower.ui.screens.improvisation.components.ImprovisationAnalysisScreen
import com.aivoicepower.ui.screens.improvisation.components.OrbState
import com.aivoicepower.ui.screens.improvisation.components.VoiceExerciseScreen
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
    var showFocus by remember { mutableStateOf(false) }
    var focusDone by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.ttsManager.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.phase) {
        if (state.phase == DebatePhase.Conversation && !focusDone) {
            showFocus = true
        }
    }

    BackHandler(enabled = state.phase == DebatePhase.Conversation) {
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

    when {
        state.analysisResult != null -> {
            ImprovisationAnalysisScreen(
                result = state.analysisResult!!,
                exerciseTitle = "Дебати з AI",
                onDismiss = { viewModel.onEvent(DebateEvent.DismissAnalysis); onNavigateBack() }
            )
        }

        state.isAnalyzing -> {
            AnalyzingScreen()
        }

        state.phase == DebatePhase.TopicSelection -> {
            DebateTopicSelectionScreen(
                onTopicSelected = { viewModel.onEvent(DebateEvent.TopicSelected(it)) },
                onNavigateBack = onNavigateBack
            )
        }

        state.phase == DebatePhase.PositionSelection -> {
            DebatePositionScreen(
                topic = state.selectedTopic!!,
                onPositionSelected = { viewModel.onEvent(DebateEvent.PositionSelected(it)) },
                onNavigateBack = onNavigateBack
            )
        }

        state.phase == DebatePhase.Conversation && showFocus -> {
            FocusCountdownOverlay(
                exerciseName = "Дебати з AI",
                topic = state.selectedTopic?.topic ?: "",
                onComplete = { showFocus = false; focusDone = true; viewModel.onEvent(DebateEvent.CountdownComplete) }
            )
        }

        state.phase == DebatePhase.Conversation -> {
            val positionText = if (state.userPosition == DebatePosition.FOR) "ЗА" else "ПРОТИ"
            VoiceExerciseScreen(
                title = "Дебати з AI",
                stepInfo = "Раунд ${state.currentRound}/${state.maxRounds}",
                roleEmoji = "\u2694\uFE0F",
                roleName = "Опонент ($positionText)",
                aiText = state.aiText,
                hint = state.hint,
                orbState = state.orbState,
                audioLevel = state.audioLevel,
                isRecording = state.isRecording,
                recordingSeconds = state.recordingSeconds,
                onRecordClick = { viewModel.onEvent(DebateEvent.StartRecordingClicked) },
                onStopClick = { viewModel.onEvent(DebateEvent.StopRecordingClicked) },
                onBackClick = onNavigateBack,
                errorMessage = state.error
            )
        }

        state.phase == DebatePhase.Complete -> {
            VoiceExerciseScreen(
                title = "Дебати з AI",
                stepInfo = "Завершено",
                roleEmoji = "\u2694\uFE0F",
                roleName = "Опонент",
                aiText = state.aiText.ifBlank { "Дебати завершено! Гарний виступ." },
                hint = null,
                orbState = state.orbState,
                onRecordClick = {},
                onBackClick = { viewModel.onEvent(DebateEvent.FinishDebateClicked); onNavigateBack() },
                onAnalyzeClick = { viewModel.onEvent(DebateEvent.AnalyzeClicked) },
                onSkipClick = { viewModel.onEvent(DebateEvent.SkipClicked); onNavigateBack() },
                errorMessage = state.error
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                contentColor = Color.White
            )
        }
    }
}

@Composable
private fun DebateTopicSelectionScreen(
    onTopicSelected: (DebateTopicsProvider.DebateTopic) -> Unit,
    onNavigateBack: () -> Unit
) {
    val topics = remember { DebateTopicsProvider().getAllTopics() }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp)
        ) {
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
                        text = "\u2694\uFE0F Дебати з AI",
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                }

                Row(
                    modifier = Modifier
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\u2190", fontSize = 24.sp, color = Color(0xFF667EEA), fontWeight = FontWeight.Bold)
                    Text(text = "Назад", style = AppTypography.bodyMedium, color = TextColors.onLightPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.12f))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .clickable { onTopicSelected(topic) }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = topic.topic, style = AppTypography.titleMedium, color = TextColors.onLightPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = topic.description, style = AppTypography.bodyMedium, color = TextColors.onLightSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Складність: ${topic.difficulty}", style = AppTypography.labelMedium, color = Color(0xFF6366F1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DebatePositionScreen(
    topic: DebateTopicsProvider.DebateTopic,
    onPositionSelected: (DebatePosition) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Тема:", style = AppTypography.titleMedium, color = TextColors.onDarkPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.12f))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(text = topic.topic, style = AppTypography.titleLarge, color = TextColors.onLightPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Обери свою позицію:", style = AppTypography.titleLarge, color = TextColors.onDarkPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF10B981).copy(alpha = 0.2f))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clickable { onPositionSelected(DebatePosition.FOR) }
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "\u2705 ЗА", style = AppTypography.headlineMedium, color = Color(0xFF10B981), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Ти будеш підтримувати цю позицію", style = AppTypography.bodyMedium, color = TextColors.onLightSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFEF4444).copy(alpha = 0.2f))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clickable { onPositionSelected(DebatePosition.AGAINST) }
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "\u274C ПРОТИ", style = AppTypography.headlineMedium, color = Color(0xFFEF4444), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Ти будеш аргументувати проти", style = AppTypography.bodyMedium, color = TextColors.onLightSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
