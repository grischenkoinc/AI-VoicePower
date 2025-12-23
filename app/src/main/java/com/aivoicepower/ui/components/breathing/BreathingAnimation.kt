package com.aivoicepower.ui.components.breathing

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aivoicepower.ui.screens.warmup.BreathingPhase

@Composable
fun BreathingAnimation(
    phase: BreathingPhase,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Calculate scale based on phase
    val targetScale = when (phase) {
        BreathingPhase.INHALE -> 0.5f + (progress * 0.5f) // 0.5 → 1.0
        BreathingPhase.INHALE_HOLD -> 1.0f
        BreathingPhase.EXHALE -> 1.0f - (progress * 0.5f) // 1.0 → 0.5
        BreathingPhase.EXHALE_HOLD -> 0.5f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "breathing_scale"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f
        val currentRadius = maxRadius * animatedScale

        // Gradient circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.3f),
                    secondaryColor.copy(alpha = 0.1f)
                ),
                center = center,
                radius = currentRadius
            ),
            radius = currentRadius,
            center = center
        )

        // Outer ring
        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            radius = currentRadius,
            center = center,
            style = Stroke(width = 4f)
        )
    }
}
