package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors

@Composable
fun VoiceMicButton(
    isListening: Boolean,
    isSending: Boolean,
    audioLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_anim")

    // Pulse animation when listening
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Sound wave rings
    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1_alpha"
    )
    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1_scale"
    )

    val wave2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2_alpha"
    )
    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2_scale"
    )

    // Glow alpha for idle state
    val idleGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp) // Container for waves
        ) {
            // Sound wave rings (only when listening)
            if (isListening) {
                // Wave ring 1
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(wave1Scale + audioLevel * 0.3f)
                        .graphicsLayer { alpha = wave1Alpha }
                        .border(
                            width = 2.dp,
                            color = Color(0xFFEF4444),
                            shape = CircleShape
                        )
                )
                // Wave ring 2
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(wave2Scale + audioLevel * 0.2f)
                        .graphicsLayer { alpha = wave2Alpha }
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFFEF4444),
                            shape = CircleShape
                        )
                )
                // Audio level ring
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(1f + audioLevel * 0.4f)
                        .graphicsLayer { alpha = 0.2f + audioLevel * 0.3f }
                        .background(
                            color = Color(0xFFEF4444).copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
            }

            // Main button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .then(
                        if (isListening) {
                            Modifier
                                .scale(pulseScale)
                                .shadow(16.dp, CircleShape, spotColor = Color(0xFFEF4444).copy(alpha = 0.5f))
                        } else {
                            Modifier.shadow(12.dp, CircleShape, spotColor = PrimaryColors.glow.copy(alpha = idleGlow))
                        }
                    )
                    .clip(CircleShape)
                    .background(
                        brush = if (isListening) {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFFDC2626)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (isListening) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isSending,
                        onClick = onClick
                    )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Зупинити" else "Говорити",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hint text
        Text(
            text = when {
                isSending -> "Обробка..."
                isListening -> "Говоріть..."
                else -> "Натисніть, щоб говорити"
            },
            style = TextStyle(fontSize = 13.sp),
            color = if (isListening) Color(0xFFEF4444).copy(alpha = 0.8f) else TextColors.onDarkMuted,
            textAlign = TextAlign.Center
        )
    }
}
