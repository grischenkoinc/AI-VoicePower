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

            // Overall progress bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Загальний прогрес",
                    style = AppTypography.labelMedium,
                    color = TextColors.onDarkSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LinearProgressIndicator(
                    progress = { state.completedExercises.size.toFloat() / state.exercises.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Color(0xFF10B981),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
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
                                timerSeconds = state.timerSeconds,
                                isTimerRunning = state.isTimerRunning,
                                showCompletionOverlay = state.showCompletionOverlay,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = {
                                    viewModel.onEvent(QuickWarmupEvent.StartTimer)
                                },
                                onPauseTimer = {
                                    viewModel.onEvent(QuickWarmupEvent.PauseTimer)
                                },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.SkipExercise)
                                },
                                currentExerciseNumber = state.currentExerciseIndex + 1,
                                totalExercises = state.exercises.size,
                                completedExercises = state.completedExercises.size
                            )
                        }
                    }

                    WarmupCategoryType.BREATHING -> {
                        currentExercise.breathingExercise?.let { exercise ->
                            BreathingExerciseDialog(
                                exercise = exercise,
                                elapsedSeconds = state.breathingElapsedSeconds,
                                totalSeconds = exercise.durationSeconds,
                                currentPhase = state.breathingCurrentPhase,
                                phaseProgress = state.breathingPhaseProgress,
                                isRunning = state.breathingIsRunning,
                                showInstructions = false,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStart = {
                                    viewModel.onEvent(QuickWarmupEvent.StartBreathing)
                                },
                                onPause = {
                                    viewModel.onEvent(QuickWarmupEvent.PauseBreathing)
                                },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.SkipExercise)
                                },
                                onHideInstructions = { /* No instructions in quick warmup */ },
                                currentExerciseNumber = state.currentExerciseIndex + 1,
                                totalExercises = state.exercises.size,
                                completedExercises = state.completedExercises.size
                            )
                        }
                    }

                    WarmupCategoryType.VOICE -> {
                        currentExercise.voiceExercise?.let { exercise ->
                            VoiceExerciseDialog(
                                exercise = exercise,
                                timerSeconds = state.timerSeconds,
                                isTimerRunning = state.isTimerRunning,
                                isAudioPlaying = false,
                                showCompletionOverlay = state.showCompletionOverlay,
                                onDismiss = { /* Не дозволяємо закривати */ },
                                onStartTimer = {
                                    viewModel.onEvent(QuickWarmupEvent.StartTimer)
                                },
                                onPauseTimer = {
                                    viewModel.onEvent(QuickWarmupEvent.PauseTimer)
                                },
                                onPlayAudio = { /* Handle in local state */ },
                                onStopAudio = { /* Handle in local state */ },
                                onMarkCompleted = {
                                    viewModel.onEvent(QuickWarmupEvent.CurrentExerciseCompleted)
                                },
                                onSkip = {
                                    viewModel.onEvent(QuickWarmupEvent.SkipExercise)
                                },
                                currentExerciseNumber = state.currentExerciseIndex + 1,
                                totalExercises = state.exercises.size,
                                completedExercises = state.completedExercises.size
                            )
                        }
                    }

                    WarmupCategoryType.QUICK -> {
                        // Quick warmup не використовується всередині себе
                    }
                }
            }
        }

        // Final Completion overlay (після всіх вправ)
        if (state.isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .background(Color.White, RoundedCornerShape(32.dp))
                        .padding(40.dp)
                        .clickable {
                            viewModel.onEvent(QuickWarmupEvent.DismissCompletionDialog)
                            onNavigateBack()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Success icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF10B981), Color(0xFF14B8A6))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Success message
                    Text(
                        text = "Чудова робота!",
                        style = AppTypography.displayLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.6).sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Text(
                        text = "Розминку завершено",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
