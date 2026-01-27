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
    // Кольори для різних фаз
    val inhaleColor = Color(0xFF6366F1)     // Фіолетовий для вдиху
    val exhaleColor = Color(0xFF8B5CF6)     // Темно-фіолетовий для видиху
    val holdColor = Color(0xFFA78BFA)       // Світло-фіолетовий для затримки

    // Розрахунок масштабу на основі фази та прогресу
    val targetScale = when (phase) {
        BreathingPhase.INHALE -> {
            // При вдиху коло розширюється від 0.5 до 1.0
            0.5f + (progress * 0.5f)
        }
        BreathingPhase.INHALE_HOLD -> {
            // При затримці після вдиху - коло залишається великим (1.0)
            1.0f
        }
        BreathingPhase.EXHALE -> {
            // При видиху коло звужується від 1.0 до 0.5
            1.0f - (progress * 0.5f)
        }
        BreathingPhase.EXHALE_HOLD -> {
            // При затримці після видиху - коло залишається маленьким (0.5)
            0.5f
        }
    }

    // Вібрація для затримок
    val vibrationOffset = if (phase == BreathingPhase.INHALE_HOLD || phase == BreathingPhase.EXHALE_HOLD) {
        // Додаємо невелику вібрацію (±2%)
        val vibration = sin(progress * 40f) * 0.02f
        vibration
    } else {
        0f
    }

    val finalScale = targetScale + vibrationOffset

    // Колір залежно від фази
    val primaryColor = when (phase) {
        BreathingPhase.INHALE -> inhaleColor
        BreathingPhase.INHALE_HOLD -> holdColor
        BreathingPhase.EXHALE -> exhaleColor
        BreathingPhase.EXHALE_HOLD -> holdColor
    }

    val secondaryColor = primaryColor.copy(alpha = 0.3f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f * 0.9f // 90% розміру для відступу
        val currentRadius = maxRadius * finalScale

        // Внутрішнє градієнтне коло
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.4f),
                    secondaryColor.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = center,
                radius = currentRadius
            ),
            radius = currentRadius,
            center = center
        )

        // Зовнішнє кільце (основне)
        drawCircle(
            color = primaryColor.copy(alpha = 0.7f),
            radius = currentRadius,
            center = center,
            style = Stroke(width = 6f)
        )

        // Додаткове внутрішнє кільце для більш виразного ефекту
        drawCircle(
            color = primaryColor.copy(alpha = 0.3f),
            radius = currentRadius * 0.85f,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}
