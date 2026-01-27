package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
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
import com.aivoicepower.ui.screens.warmup.components.*
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun QuickWarmupScreen(
    viewModel: QuickWarmupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        // Auto-start first exercise
        viewModel.onEvent(QuickWarmupEvent.StartQuickWarmup)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        text = "5 хвилин",
                        style = AppTypography.labelMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Швидка розминка",
                        style = AppTypography.displayLarge,
                        color = TextColors.onDarkPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                }

                // Back button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            ),
                            CircleShape
                        )
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", fontSize = 20.sp, color = Color.White)
                }
            }

            // Progress card
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Вправа ${state.currentExerciseIndex + 1} з ${state.exercises.size}",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = { state.completedExercises.size.toFloat() / state.exercises.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFE5E7EB)
                )
            }

            // Exercise list
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.exercises.forEachIndexed { index, exercise ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (index == state.currentExerciseIndex) {
                                    Color(0xFF6366F1).copy(alpha = 0.2f)
                                } else {
                                    Color.Black.copy(alpha = 0.12f)
                                }
                            )
                            .background(
                                color = when {
                                    state.completedExercises.contains(exercise.id) -> Color(0xFFF0FDF4)
                                    index == state.currentExerciseIndex -> Color(0xFFEEF2FF)
                                    else -> Color.White
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (state.completedExercises.contains(exercise.id)) {
                                    Icons.Filled.CheckCircle
                                } else if (index == state.currentExerciseIndex) {
                                    Icons.Filled.CheckCircle
                                } else {
                                    Icons.Outlined.Circle
                                },
                                contentDescription = null,
                                tint = when {
                                    state.completedExercises.contains(exercise.id) -> Color(0xFF10B981)
                                    index == state.currentExerciseIndex -> Color(0xFF6366F1)
                                    else -> Color(0xFF9CA3AF)
                                }
                            )

                            Text(
                                text = "${exercise.id}. ${exercise.title}",
                                style = AppTypography.bodyLarge,
                                color = TextColors.onLightPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "${exercise.durationSeconds} сек",
                            style = AppTypography.bodySmall,
                            color = TextColors.onLightMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Exercise dialogs (reuse from Phase 2.2-2.4)
        if (state.isExerciseDialogOpen) {
            val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)

            if (currentExercise != null) {
                when (currentExercise.category) {
                    WarmupCategoryType.ARTICULATION -> {
                        currentExercise.articulationExercise?.let { exercise ->
                            ArticulationExerciseDialog(
                                exercise = exercise,
                                timerSeconds = exercise.durationSeconds,
                                isTimerRunning = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = { /* Handle in local state */ },
                                onPauseTimer = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }

                    WarmupCategoryType.BREATHING -> {
                        currentExercise.breathingExercise?.let { exercise ->
                            BreathingExerciseDialog(
                                exercise = exercise,
                                elapsedSeconds = 0,
                                totalSeconds = exercise.durationSeconds,
                                currentPhase = BreathingPhase.INHALE,
                                phaseProgress = 0f,
                                isRunning = false,
                                showInstructions = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStart = { /* Handle in local state */ },
                                onPause = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onHideInstructions = { /* No instructions in quick warmup */ }
                            )
                        }
                    }

                    WarmupCategoryType.VOICE -> {
                        currentExercise.voiceExercise?.let { exercise ->
                            VoiceExerciseDialog(
                                exercise = exercise,
                                timerSeconds = exercise.durationSeconds,
                                isTimerRunning = false,
                                isAudioPlaying = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = { /* Handle in local state */ },
                                onPauseTimer = { /* Handle in local state */ },
                                onPlayAudio = { /* Handle in local state */ },
                                onStopAudio = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                }
                            )
                        }
                    }

                    WarmupCategoryType.QUICK -> {
                        // Quick warmup не використовується всередині себе
                    }
                }
            }
        }

        // Completion dialog
        if (state.isCompleted) {
            CompletionDialog(
                totalExercises = state.exercises.size,
                elapsedSeconds = state.totalElapsedSeconds,
                onDismiss = {
                    viewModel.onEvent(QuickWarmupEvent.DismissCompletionDialog)
                    onNavigateBack()
                }
            )
        }
    }
}

@Composable
private fun CompletionDialog(
    totalExercises: Int,
    elapsedSeconds: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🎉", fontSize = 48.sp)
        },
        title = {
            Text(
                text = "Розминка завершена!",
                style = AppTypography.titleLarge,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ви виконали $totalExercises вправи",
                    style = AppTypography.bodyLarge,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "за %d хв %02d сек".format(
                        elapsedSeconds / 60,
                        elapsedSeconds % 60
                    ),
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                )
            ) {
                Text("Готово", fontWeight = FontWeight.Bold)
            }
        }
    )
}
