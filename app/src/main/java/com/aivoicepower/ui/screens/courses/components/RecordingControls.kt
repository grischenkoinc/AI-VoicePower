package com.aivoicepower.ui.screens.courses.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.courses.ExerciseState
import com.aivoicepower.ui.screens.courses.ExerciseStatus
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.PrimaryButton
import com.aivoicepower.ui.theme.components.SecondaryButton

@Composable
fun RecordingControls(
    exerciseState: ExerciseState,
    isPlaying: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onStopPlayback: () -> Unit,
    onReRecord: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording = exerciseState.status == ExerciseStatus.Recording

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        when (exerciseState.status) {
            ExerciseStatus.NotStarted, ExerciseStatus.Recording -> {
                // Record Button з хвилями
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Wave rings коли записує
                    if (isRecording) {
                        repeat(3) { index ->
                            WaveRing(
                                delay = index * 600,
                                color = Color(0xFF667EEA)
                            )
                        }
                    }

                    // Main button
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                                ),
                                CircleShape
                            )
                            .clickable(onClick = if (isRecording) onStopRecording else onStartRecording),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isRecording) "⏹" else "🎤",
                            fontSize = 48.sp
                        )
                    }
                }

                Text(
                    text = if (isRecording) "Йде запис..." else "Натисни для запису",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            ExerciseStatus.Recorded -> {
                Text(
                    text = "Запис збережено",
                    style = AppTypography.titleMedium,
                    color = Color(0xFF22C55E),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = if (isPlaying) "Стоп" else "Слухати",
                        onClick = if (isPlaying) onStopPlayback else onPlayRecording,
                        modifier = Modifier.weight(1f)
                    )

                    SecondaryButton(
                        text = "Перезаписати",
                        onClick = onReRecord,
                        modifier = Modifier.weight(1f)
                    )
                }

                PrimaryButton(
                    text = "Завершити вправу",
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ExerciseStatus.Completed -> {
                Text(
                    text = "✓ Вправа виконана",
                    style = AppTypography.titleMedium,
                    color = Color(0xFF22C55E),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WaveRing(
    delay: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = color.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}
