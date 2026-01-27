package com.aivoicepower.ui.components.breathing

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aivoicepower.ui.screens.warmup.BreathingPhase
import kotlin.math.sin

@Composable
fun BreathingAnimation(
    phase: BreathingPhase,
    progress: Float,
    modifier: Modifier = Modifier
) {
    // Кольори для градієнту (точно як у кнопки мікрофона - appBackground)
    val gradientColors = listOf(
        Color(0xFF4ECDC4),  // Бірюзовий
        Color(0xFF667EEA),  // Світло-фіолетовий
        Color(0xFF764BA2)   // Темно-фіолетовий
    )

    // Розрахунок масштабу на основі фази та прогресу (БЕЗ згладжування - пряма синхронізація)
    val currentScale = when (phase) {
        BreathingPhase.INHALE -> {
            // При вдиху коло розширюється від 0.5 до 1.0
            0.5f + (progress * 0.5f)
        }
        BreathingPhase.INHALE_HOLD -> {
            // При затримці після вдиху - коло залишається великим (1.0)
            // Додаємо невелику вібрацію (±2%)
            1.0f + (sin(progress * 40f) * 0.02f)
        }
        BreathingPhase.EXHALE -> {
            // При видиху коло звужується від 1.0 до 0.5
            1.0f - (progress * 0.5f)
        }
        BreathingPhase.EXHALE_HOLD -> {
            // При затримці після видиху - коло залишається маленьким (0.5)
            // Додаємо невелику вібрацію (±2%)
            0.5f + (sin(progress * 40f) * 0.02f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f * 0.9f // 90% розміру для відступу
        val currentRadius = maxRadius * currentScale

        // Заповнене градієнтне коло (точно як у мікрофона - linearGradient 135°)
        drawCircle(
            brush = Brush.linearGradient(
                colors = gradientColors,
                start = Offset(center.x - currentRadius, center.y - currentRadius),
                end = Offset(center.x + currentRadius, center.y + currentRadius) // 135° діагональ
            ),
            radius = currentRadius,
            center = center
        )

        // Зовнішнє кільце для чіткості
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = currentRadius,
            center = center,
            style = Stroke(width = 3f)
        )

        // Внутрішнє світіння
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = center,
                radius = currentRadius * 0.6f
            ),
            radius = currentRadius * 0.6f,
            center = center
        )
    }
}
