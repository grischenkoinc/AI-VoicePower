package com.aivoicepower.ui.screens.aicoach.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors

@Composable
fun UnifiedInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onAttachClick: () -> Unit,
    isListening: Boolean,
    isSending: Boolean,
    isUploadingAudio: Boolean,
    @Suppress("UNUSED_PARAMETER") audioLevel: Float,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isProcessing = isSending || isUploadingAudio
    val hasText = text.isNotBlank()

    // Listening state animations
    val bgColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFFEF4444).copy(alpha = 0.06f)
        else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(300),
        label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFFEF4444).copy(alpha = 0.30f)
        else Color.White.copy(alpha = 0.12f),
        animationSpec = tween(300),
        label = "border"
    )

    // Subtle pulse when listening
    val infiniteTransition = rememberInfiniteTransition(label = "listen_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val containerShape = RoundedCornerShape(28.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(containerShape)
            .background(bgColor, containerShape)
            .border(1.dp, borderColor, containerShape)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Attach button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable(
                    enabled = enabled && !isListening && !isProcessing,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onAttachClick()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Завантажити аудіо",
                tint = Color.White.copy(alpha = if (enabled && !isListening) 0.50f else 0.25f),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text field
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (text.isEmpty() && !isListening) {
                Text(
                    text = "Повідомлення...",
                    color = Color.White.copy(alpha = 0.35f),
                    style = TextStyle(fontSize = 15.sp)
                )
            }
            if (isListening && text.isEmpty()) {
                Text(
                    text = "Говоріть...",
                    color = Color(0xFFEF4444).copy(alpha = 0.60f),
                    style = TextStyle(fontSize = 15.sp)
                )
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                enabled = enabled && !isListening,
                maxLines = 4,
                textStyle = TextStyle(
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(PrimaryColors.light),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Listening waveform indicator (small, next to stop button)
        if (isListening) {
            MiniWaveform(
                barCount = 3,
                barWidth = 2.dp,
                maxBarHeight = 12.dp,
                minBarHeight = 3.dp,
                startColor = Color(0xFFEF4444).copy(alpha = 0.8f),
                endColor = Color(0xFFEF4444).copy(alpha = 0.5f),
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        // Action button: Mic / Send / Stop / Loading
        ActionButton(
            hasText = hasText,
            isListening = isListening,
            isProcessing = isProcessing,
            pulseScale = pulseScale,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                when {
                    isListening -> onVoiceClick() // Stop listening
                    hasText -> onSendClick()
                    else -> onVoiceClick() // Start listening
                }
            }
        )
    }
}

@Composable
private fun ActionButton(
    hasText: Boolean,
    isListening: Boolean,
    isProcessing: Boolean,
    pulseScale: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .then(
                if (isListening) Modifier.scale(pulseScale)
                else Modifier
            )
            .clip(CircleShape)
            .background(
                when {
                    isListening -> Brush.radialGradient(
                        colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                    )
                    else -> Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                    )
                }
            )
            .clickable(
                enabled = !isProcessing,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = when {
                isProcessing -> "loading"
                isListening -> "stop"
                hasText -> "send"
                else -> "mic"
            },
            transitionSpec = {
                (fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.8f))
                    .togetherWith(fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.8f))
            },
            label = "action_icon"
        ) { state ->
            when (state) {
                "loading" -> CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                "stop" -> Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Зупинити",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                "send" -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Надіслати",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                else -> Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Говорити",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
