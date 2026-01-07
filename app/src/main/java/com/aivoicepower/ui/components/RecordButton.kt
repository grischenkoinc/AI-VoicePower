package com.aivoicepower.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Record Button - велика кругла кнопка для запису
 * Стани: Idle (готовий), Recording (записує), Processing (обробка)
 */
@Composable
fun RecordButton(
    isRecording: Boolean,
    isProcessing: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 72 // dp
) {
    // Pulse animation для idle стану
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Glow animation для recording стану
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .size(size.dp)
            .then(
                if (!isRecording && !isProcessing) {
                    Modifier.scale(scale)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer border
        Box(
            modifier = Modifier
                .size(size.dp)
                .border(
                    width = 4.dp,
                    color = BackgroundColors.surfaceElevated,
                    shape = CircleShape
                )
        )

        // Main button
        Surface(
            modifier = Modifier
                .size((size - 8).dp)
                .shadowPreset(
                    if (isRecording) ShadowPreset.BUTTON_CTA else ShadowPreset.BUTTON_PRIMARY,
                    CircleShape
                )
                .clickable(enabled = !isProcessing, onClick = onClick),
            shape = CircleShape,
            color = if (isRecording) SemanticColors.error else SecondaryColors.default
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = if (isRecording) {
                                listOf(
                                    SemanticColors.error.copy(alpha = glowAlpha),
                                    SemanticColors.errorLight.copy(alpha = glowAlpha)
                                )
                            } else {
                                listOf(
                                    SecondaryColors.default,
                                    SecondaryColors.light
                                )
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = TextColors.onSecondary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Зупинити запис" else "Почати запис",
                        modifier = Modifier.size(32.dp),
                        tint = TextColors.onSecondary
                    )
                }
            }
        }
    }
}

/**
 * Compact Record Button - компактна версія (56dp)
 */
@Composable
fun CompactRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RecordButton(
        isRecording = isRecording,
        onClick = onClick,
        modifier = modifier,
        size = 56
    )
}
