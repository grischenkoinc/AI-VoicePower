package com.aivoicepower.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * Record Button - велика кругла кнопка для запису (115dp)
 * Стани: Idle (з pulse rings), Recording (синій pulsating border)
 */
@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = AnimationDuration.short,
            easing = AnimationEasing.standard
        ),
        label = "recordButtonScale"
    )

    // Border pulsation for recording state
    val borderAlpha by rememberInfiniteTransition(label = "border_pulse").animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = AnimationEasing.smooth),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_alpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Pulse rings (тільки коли НЕ recording)
        if (!isRecording) {
            repeat(3) { index ->
                val delay = index * 300
                val targetScale = 1.0f + (index + 1) * 0.3f
                val alpha = 0.3f - index * 0.1f

                val pulseScale by rememberInfiniteTransition(label = "pulse_$index").animateFloat(
                    initialValue = 1f,
                    targetValue = targetScale,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 2000,
                            delayMillis = delay,
                            easing = AnimationEasing.smooth
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "pulse_scale_$index"
                )

                val pulseAlpha by rememberInfiniteTransition(label = "pulse_alpha_$index").animateFloat(
                    initialValue = alpha,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 2000,
                            delayMillis = delay,
                            easing = AnimationEasing.smooth
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "pulse_alpha_$index"
                )

                Box(
                    modifier = Modifier
                        .size(115.dp)
                        .scale(pulseScale)
                        .border(
                            width = 2.dp,
                            color = PrimaryColors.default.copy(alpha = pulseAlpha),
                            shape = CircleShape
                        )
                )
            }
        }

        // Main button
        Box(
            modifier = Modifier
                .size(115.dp)
                .scale(scale)
                .then(
                    if (isRecording) {
                        Modifier.border(
                            width = 4.dp,
                            color = PrimaryColors.default.copy(alpha = borderAlpha),
                            shape = CircleShape
                        )
                    } else Modifier
                )
                .gradientBackground(Gradients.recordButton, CircleShape)
                .shadowPreset(
                    if (isRecording) ShadowPreset.RECORD_BUTTON_ACTIVE else ShadowPreset.RECORD_BUTTON,
                    CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.Mic,
                contentDescription = if (isRecording) "Pause recording" else "Start recording",
                modifier = Modifier.size(48.dp),
                tint = TextColors.primary
            )
        }
    }
}

/**
 * Compact Record Button - компактна версія (56dp)
 * Без pulse rings, але з border pulsation при recording
 */
@Composable
fun CompactRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = AnimationDuration.short,
            easing = AnimationEasing.standard
        ),
        label = "compactRecordButtonScale"
    )

    // Border pulsation for recording state
    val borderAlpha by rememberInfiniteTransition(label = "compact_border_pulse").animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = AnimationEasing.smooth),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compact_border_alpha"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .then(
                if (isRecording) {
                    Modifier.border(
                        width = 3.dp,
                        color = PrimaryColors.default.copy(alpha = borderAlpha),
                        shape = CircleShape
                    )
                } else Modifier
            )
            .gradientBackground(Gradients.recordButton, CircleShape)
            .shadowPreset(
                if (isRecording) ShadowPreset.RECORD_BUTTON_ACTIVE else ShadowPreset.RECORD_BUTTON,
                CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.Mic,
            contentDescription = if (isRecording) "Pause recording" else "Start recording",
            modifier = Modifier.size(24.dp),
            tint = TextColors.primary
        )
    }
}
