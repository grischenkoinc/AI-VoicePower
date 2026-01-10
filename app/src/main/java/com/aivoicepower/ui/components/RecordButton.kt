package com.aivoicepower.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*

@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(115.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulse rings - ONLY during recording
        if (isRecording) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")

            val scale1 by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ring1"
            )

            val alpha1 by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "alpha1"
            )

            val scale2 by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing, delayMillis = 300),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ring2"
            )

            val alpha2 by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing, delayMillis = 300),
                    repeatMode = RepeatMode.Restart
                ),
                label = "alpha2"
            )

            val scale3 by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing, delayMillis = 600),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ring3"
            )

            val alpha3 by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing, delayMillis = 600),
                    repeatMode = RepeatMode.Restart
                ),
                label = "alpha3"
            )

            Box(
                modifier = Modifier
                    .size(115.dp * scale1)
                    .border(3.dp, Color(0xFF60A5FA).copy(alpha = alpha1), CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(115.dp * scale2)
                    .border(3.dp, Color(0xFF60A5FA).copy(alpha = alpha2), CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(115.dp * scale3)
                    .border(3.dp, Color(0xFF60A5FA).copy(alpha = alpha3), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(115.dp)
                .gradientBackground(
                    Gradients.recordButton,
                    CircleShape
                )
                .shadowPreset(ShadowPreset.RECORD_BUTTON, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRecording) "⏸️" else "🎤",
                fontSize = 44.sp
            )
        }
    }
}

@Composable
fun CompactRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .gradientBackground(Gradients.recordButton, CircleShape)
            .shadowPreset(ShadowPreset.RECORD_BUTTON, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isRecording) "⏸️" else "🎤",
            fontSize = 24.sp
        )
    }
}
