package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.aivoicepower.ui.screens.warmup.components.VoiceExerciseDialog
import com.aivoicepower.ui.screens.warmup.components.VoiceExerciseItem
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun VoiceWarmupScreen(
    viewModel: VoiceWarmupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                        text = "Вокальні вправи",
                        style = AppTypography.labelMedium,
                        color = TextColors.onDarkSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Голос",
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
                    text = "Виконано: ${state.completedToday.size}/${state.exercises.size}",
                    style = AppTypography.titleMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = { state.completedToday.size.toFloat() / state.exercises.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFE5E7EB)
                )
            }

            // Exercise list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(state.exercises) { _, exercise ->
                    VoiceExerciseItem(
                        exercise = exercise,
                        isCompleted = state.completedToday.contains(exercise.id),
                        onClick = {
                            viewModel.onEvent(VoiceWarmupEvent.ExerciseClicked(exercise))
                        }
                    )
                }

                // Finish button
                item {
                    if (state.completedToday.size == state.exercises.size) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color(0xFF10B981).copy(alpha = 0.4f)
                                )
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF10B981), Color(0xFF14B8A6))
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onNavigateBack() }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Завершити розминку ✓",
                                style = AppTypography.labelLarge,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Exercise dialog
        if (state.isExerciseDialogOpen && state.selectedExercise != null) {
            VoiceExerciseDialog(
                exercise = state.selectedExercise!!,
                timerSeconds = state.timerSeconds,
                isTimerRunning = state.isTimerRunning,
                isAudioPlaying = state.isAudioPlaying,
                onDismiss = {
                    viewModel.onEvent(VoiceWarmupEvent.ExerciseDialogDismissed)
                },
                onStartTimer = {
                    viewModel.onEvent(VoiceWarmupEvent.StartTimer)
                },
                onPauseTimer = {
                    viewModel.onEvent(VoiceWarmupEvent.PauseTimer)
                },
                onMarkCompleted = {
                    viewModel.onEvent(VoiceWarmupEvent.MarkAsCompleted)
                },
                onSkip = {
                    viewModel.onEvent(VoiceWarmupEvent.SkipExercise)
                },
                onPlayAudio = {
                    viewModel.onEvent(VoiceWarmupEvent.PlayAudioExample)
                },
                onStopAudio = {
                    viewModel.onEvent(VoiceWarmupEvent.StopAudioExample)
                }
            )
        }
    }
}
