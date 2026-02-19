package com.aivoicepower.ui.screens.improvisation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun NegotiationScreen(
    viewModel: NegotiationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit = {}
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

    LaunchedEffect(state.isStarted) {
        if (state.isStarted && !focusDone) {
            showFocus = true
        }
    }

    BackHandler(enabled = state.isStarted && state.currentRound <= state.maxRounds) {
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
                exerciseTitle = "Перемовини",
                onDismiss = { viewModel.onEvent(NegotiationEvent.DismissAnalysis); onNavigateBack() }
            )
        }

        state.isAnalyzing -> {
            AnalyzingScreen()
        }

        !state.isStarted -> {
            NegotiationWelcomeScreen(
                onStart = { viewModel.onEvent(NegotiationEvent.StartSimulation) },
                onNavigateBack = onNavigateBack
            )
        }

        state.isStarted && showFocus -> {
            FocusCountdownOverlay(
                exerciseName = "Перемовини",
                onComplete = { showFocus = false; focusDone = true; viewModel.onEvent(NegotiationEvent.CountdownComplete) }
            )
        }

        state.currentRound <= state.maxRounds -> {
            VoiceExerciseScreen(
                title = "Перемовини",
                stepInfo = "Крок ${state.currentRound}/${state.maxRounds}",
                roleEmoji = "\uD83E\uDD1D",
                roleName = "Бiзнес-партнер",
                aiText = state.aiText,
                hint = state.hint,
                orbState = state.orbState,
                audioLevel = state.audioLevel,
                isRecording = state.isRecording,
                recordingSeconds = state.recordingSeconds,
                onRecordClick = { viewModel.onEvent(NegotiationEvent.StartRecording) },
                onStopClick = { viewModel.onEvent(NegotiationEvent.StopRecording) },
                onBackClick = onNavigateBack,
                errorMessage = state.error
            )
        }

        else -> {
            VoiceExerciseScreen(
                title = "Перемовини",
                stepInfo = "Завершено",
                roleEmoji = "\uD83E\uDD1D",
                roleName = "Бiзнес-партнер",
                aiText = state.aiText.ifBlank { "Перемовини завершенi. Дякуємо за продуктивну дискусiю!" },
                hint = null,
                orbState = state.orbState,
                onRecordClick = {},
                onBackClick = { viewModel.onEvent(NegotiationEvent.FinishNegotiation); onNavigateBack() },
                onAnalyzeClick = { viewModel.onEvent(NegotiationEvent.AnalyzeClicked) },
                onSkipClick = { viewModel.onEvent(NegotiationEvent.SkipClicked); onNavigateBack() },
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
private fun NegotiationWelcomeScreen(
    onStart: () -> Unit,
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
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Симуляція",
                    style = AppTypography.labelMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "\uD83E\uDD1D Перемовини",
                    style = AppTypography.displayLarge,
                    color = TextColors.onDarkPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.18f))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Готовий до переговорів?",
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "AI-партнер буде захищати свої інтереси, висувати контраргументи та шукати компроміси. Перемовини складаються з 3 раундів.",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "\uD83D\uDCA1 Стратегія:",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1. Вислухайте позицію партнера\n2. Висуньте зустрічну пропозицію\n3. Шукайте компроміс та закрийте угоду",
                            style = AppTypography.bodySmall,
                            color = TextColors.onLightSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "\uD83E\uDD1D Почати переговори",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
