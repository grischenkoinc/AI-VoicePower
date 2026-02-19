package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.SemanticColors

@Composable
fun AnimatedOrb(
    state: OrbState,
    emoji: String,
    audioLevel: Float = 0f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    // === IDLE: slow breathing pulse ===
    val idleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_scale"
    )
    val idleGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_glow"
    )

    // === SPEAKING: wave ripples ===
    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1_scale"
    )
    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1_alpha"
    )
    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2_scale"
    )
    val wave2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2_alpha"
    )

    // === THINKING: rotation shimmer ===
    val thinkRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "think_rot"
    )

    // === LISTENING: shrink + audio bars ===
    val listenScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "listen_scale"
    )

    // === COMPLETE: glow ===
    val completeGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "complete_glow"
    )

    // Colors
    val orbGradient = when (state) {
        OrbState.IDLE -> listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
        OrbState.SPEAKING -> listOf(Color(0xFF667EEA), Color(0xFF764BA2))
        OrbState.LISTENING -> listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))
        OrbState.THINKING -> listOf(Color(0xFF8B5CF6), Color(0xFF667EEA))
        OrbState.COMPLETE -> listOf(Color(0xFF22C55E), Color(0xFF16A34A))
    }

    val ringColor by animateColorAsState(
        targetValue = when (state) {
            OrbState.IDLE -> PrimaryColors.light.copy(alpha = 0.3f)
            OrbState.SPEAKING -> Color(0xFF764BA2).copy(alpha = 0.4f)
            OrbState.LISTENING -> Color(0xFF4ECDC4).copy(alpha = 0.4f)
            OrbState.THINKING -> PrimaryColors.default.copy(alpha = 0.3f)
            OrbState.COMPLETE -> SemanticColors.success.copy(alpha = 0.4f)
        },
        label = "ring_color"
    )

    val glowColor = when (state) {
        OrbState.IDLE -> PrimaryColors.glow.copy(alpha = idleGlow)
        OrbState.SPEAKING -> Color(0xFF764BA2).copy(alpha = 0.5f)
        OrbState.LISTENING -> Color(0xFF4ECDC4).copy(alpha = 0.4f)
        OrbState.THINKING -> PrimaryColors.glow.copy(alpha = 0.4f)
        OrbState.COMPLETE -> SemanticColors.success.copy(alpha = completeGlow)
    }

    val currentScale = when (state) {
        OrbState.IDLE -> idleScale
        OrbState.SPEAKING -> 1.02f
        OrbState.LISTENING -> listenScale
        OrbState.THINKING -> 1f
        OrbState.COMPLETE -> 1.05f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(160.dp)
    ) {
        // Wave rings (SPEAKING)
        if (state == OrbState.SPEAKING) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(wave1Scale)
                    .graphicsLayer { alpha = wave1Alpha }
                    .border(2.dp, Color(0xFF764BA2), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(wave2Scale)
                    .graphicsLayer { alpha = wave2Alpha }
                    .border(1.5.dp, Color(0xFF764BA2), CircleShape)
            )
        }

        // Audio level rings (LISTENING)
        if (state == OrbState.LISTENING) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(1f + audioLevel * 0.5f)
                    .graphicsLayer { alpha = 0.15f + audioLevel * 0.2f }
                    .background(Color(0xFF4ECDC4).copy(alpha = 0.1f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(1.15f + audioLevel * 0.3f)
                    .graphicsLayer { alpha = 0.1f + audioLevel * 0.15f }
                    .border(1.5.dp, Color(0xFF4ECDC4).copy(alpha = 0.3f), CircleShape)
            )
        }

        // Main orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .scale(currentScale)
                .shadow(16.dp, CircleShape, spotColor = glowColor)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(orbGradient)
                )
                .border(2.dp, ringColor, CircleShape)
                .then(
                    if (state == OrbState.THINKING) {
                        Modifier.graphicsLayer { rotationZ = thinkRotation }
                    } else Modifier
                )
        ) {
            if (state == OrbState.COMPLETE) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(
                    text = emoji,
                    fontSize = if (state == OrbState.THINKING) 24.sp else 28.sp
                )
            }
        }
    }
}
