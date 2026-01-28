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
import com.aivoicepower.ui.screens.improvisation.components.*
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.GradientBackground

@Composable
fun RandomTopicScreen(
    viewModel: RandomTopicViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResults: (recordingId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Auto-start preparation timer when topic is loaded
    LaunchedEffect(state.currentTopic) {
        if (state.currentTopic != null && state.isPreparationPhase && state.preparationTimeLeft == 15) {
            viewModel.onEvent(RandomTopicEvent.StartPreparation)
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
                        text = "🎲 Випадкова тема",
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
                                colors = listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                            ),
                            CircleShape
                        )
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", fontSize = 20.sp, color = Color.White)
                }
            }
            when {
                state.isPreparationPhase -> {
                    // Preparation phase
                    state.currentTopic?.let { topic ->
                        TopicDisplayCard(topic = topic)

                        PreparationTimerCard(
                            timeLeft = state.preparationTimeLeft,
                            onGenerateNew = {
                                viewModel.onEvent(RandomTopicEvent.GenerateNewTopic)
                            }
                        )
                    }

                    if (state.preparationTimeLeft == 0) {
                        Button(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.StartRecording)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎤 Почати запис")
                        }
                    }
                }

                state.isRecording -> {
                    // Recording phase
                    state.currentTopic?.let { topic ->
                        RandomTopicRecordingCard(
                            topic = topic,
                            durationMs = state.recordingDurationMs,
                            onStop = {
                                viewModel.onEvent(RandomTopicEvent.StopRecording)
                            }
                        )
                    }
                }

                else -> {
                    // Recording completed
                    state.currentTopic?.let { topic ->
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
                                text = "✓ Запис завершено",
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

                        Button(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.CompleteTask)
                                // Navigate to placeholder results (TODO: implement actual results screen)
                                state.recordingId?.let { recordingId ->
                                    // For now, just go back since Results screen not yet ready for improvisation
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.recordingId != null
                        ) {
                            Text("Завершити")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.onEvent(RandomTopicEvent.GenerateNewTopic)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔄 Нова тема")
                        }
                    }
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
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
