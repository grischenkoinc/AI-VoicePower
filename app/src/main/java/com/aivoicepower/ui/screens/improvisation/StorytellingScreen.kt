package com.aivoicepower.ui.screens.improvisation

import androidx.activity.compose.BackHandler
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
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.ui.screens.improvisation.components.StoryFormatCard
import com.aivoicepower.ui.screens.improvisation.components.StoryPromptCard
import com.aivoicepower.ui.screens.improvisation.components.StoryRecordingCard
import com.aivoicepower.ui.screens.improvisation.components.PreparationTimerCard
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.components.PrimaryButton
import com.aivoicepower.ui.theme.components.SecondaryButton
import kotlinx.coroutines.launch

@Composable
fun StorytellingScreen(
    viewModel: StorytellingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var backPressedTime by remember { mutableStateOf(0L) }

    // Double-back to exit protection (only when NOT on format selection)
    BackHandler(enabled = state.selectedFormat != null) {
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

    // Auto-start preparation timer when format is selected
    LaunchedEffect(state.selectedFormat, state.isPreparationPhase) {
        if (state.selectedFormat != null && state.isPreparationPhase && state.preparationTimeLeft == 30) {
            viewModel.onEvent(StorytellingEvent.StartPreparation)
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
                        text = "📖 Розкажи історію",
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
            when {
                state.selectedFormat == null -> {
                    // Format selection
                    Text(
                        text = "Обери формат історії:",
                        style = AppTypography.titleLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    StoryFormat.values().forEach { format ->
                        StoryFormatCard(
                            format = format,
                            onClick = {
                                viewModel.onEvent(StorytellingEvent.SelectFormat(format))
                            }
                        )
                    }
                }

                state.isPreparationPhase -> {
                    // Preparation phase
                    StoryPromptCard(
                        format = state.selectedFormat!!,
                        prompt = state.storyPrompt
                    )

                    PreparationTimerCard(
                        timeLeft = state.preparationTimeLeft,
                        onGenerateNew = {
                            viewModel.onEvent(StorytellingEvent.GenerateNewPrompt)
                        }
                    )

                    if (state.preparationTimeLeft == 0) {
                        PrimaryButton(
                            text = "🎤 Почати розповідь",
                            onClick = {
                                viewModel.onEvent(StorytellingEvent.StartRecording)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    SecondaryButton(
                        text = "← Змінити формат",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.ResetToFormatSelection)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                state.isRecording -> {
                    // Recording phase
                    StoryRecordingCard(
                        format = state.selectedFormat!!,
                        prompt = state.storyPrompt,
                        durationMs = state.recordingDurationMs,
                        onStop = {
                            viewModel.onEvent(StorytellingEvent.StopRecording)
                        }
                    )
                }

                else -> {
                    // Recording completed
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✓ Історія записана",
                            style = AppTypography.titleLarge,
                            color = TextColors.onLightPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Тривалість: ${formatDuration(state.recordingDurationMs)}",
                            style = AppTypography.bodyMedium,
                            color = TextColors.onLightSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    PrimaryButton(
                        text = "Завершити",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.CompleteTask)
                            state.recordingId?.let { recordingId ->
                                // For now, just go back since Results screen not yet ready for improvisation
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    SecondaryButton(
                        text = "🔄 Нова історія",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.GenerateNewPrompt)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    SecondaryButton(
                        text = "← Змінити формат",
                        onClick = {
                            viewModel.onEvent(StorytellingEvent.ResetToFormatSelection)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Error message
            state.error?.let { error ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
