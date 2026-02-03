package com.aivoicepower.ui.screens.improvisation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
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
fun NegotiationScreen(
    viewModel: NegotiationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var backPressedTime by remember { mutableStateOf(0L) }

    // Double-back to exit protection (NOT on welcome screen)
    BackHandler(enabled = state.isStarted) {
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
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            NegotiationHeader(onNavigateBack = onNavigateBack)

            when {
                !state.isStarted -> {
                    // Welcome screen
                    WelcomeCard()

                    PrimaryButton(
                        text = "🎯 Почати переговори",
                        onClick = { viewModel.onEvent(NegotiationEvent.StartSimulation) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                state.currentStepIndex < state.steps.size -> {
                    // Active step
                    val step = state.steps[state.currentStepIndex]

                    ProgressCard(
                        currentStep = state.currentStepIndex + 1,
                        totalSteps = state.steps.size
                    )

                    QuestionCard(
                        question = step.question,
                        hint = step.hint
                    )

                    if (!state.isRecording) {
                        PrimaryButton(
                            text = "🎤 Записати відповідь",
                            onClick = { viewModel.onEvent(NegotiationEvent.StartRecording) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        RecordingCard(
                            durationMs = state.recordingDurationMs,
                            onStop = { viewModel.onEvent(NegotiationEvent.StopRecording) }
                        )
                    }
                }

                else -> {
                    // Completed
                    CompletionCard(totalSteps = state.steps.size)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = "Назад",
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        )
                        PrimaryButton(
                            text = "Переглянути результати",
                            onClick = { onNavigateToResults("negotiation_${System.currentTimeMillis()}") },
                            modifier = Modifier.weight(1f)
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
private fun NegotiationHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
                text = "💼 Переговори",
                style = AppTypography.displayLarge,
                color = TextColors.onDarkPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
        }

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
}

@Composable
private fun WelcomeCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
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
            text = "Симуляція включає 4 кроки успішних переговорів: початкова позиція, аргументація, пошук компромісу та фінальна домовленість. Практикуй реальні переговори.",
            style = AppTypography.bodyMedium,
            color = TextColors.onLightSecondary,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFF3F4F6),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 Поради:",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Почни з високої anchor point\n• Аргументуй свою позицію фактами\n• Шукай win-win рішення для обох сторін",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0x73667EEA), Color(0x59764BA2))
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
                text = "Крок $currentStep з $totalSteps",
                style = AppTypography.titleMedium,
                color = TextColors.onDarkPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(Color(0xFFFBBF24), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${((currentStep.toFloat() / totalSteps) * 100).toInt()}%",
                    style = AppTypography.labelLarge,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: String,
    hint: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "❓ Питання:",
            style = AppTypography.labelMedium,
            color = Color(0xFF667EEA),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = question,
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFFFFBEB),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "💡 Підказка:",
                    style = AppTypography.labelSmall,
                    color = Color(0xFFD97706),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hint,
                    style = AppTypography.bodySmall,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun RecordingCard(
    durationMs: Long,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFFEF4444).copy(alpha = 0.3f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "🎙️ Запис...",
            style = AppTypography.titleLarge,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )

        val seconds = (durationMs / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60

        Text(
            text = String.format("%d:%02d", minutes, secs),
            style = AppTypography.displayLarge,
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Black
        )

        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⏹ Зупинити запис",
                style = AppTypography.bodyLarge,
                color = Color(0xFFEF4444),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CompletionCard(
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF10B981).copy(alpha = 0.3f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎉",
            fontSize = 64.sp
        )

        Text(
            text = "Вітаємо!",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ти пройшов усі $totalSteps питань переговорів. Відмінна робота!",
            style = AppTypography.bodyMedium,
            color = TextColors.onLightSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = text,
            style = AppTypography.bodyLarge,
            color = Color(0xFF667EEA),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
