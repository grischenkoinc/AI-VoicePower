package com.aivoicepower.ui.screens.aicoach.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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

    // Pulse animation for mic button
    val infiniteTransition = rememberInfiniteTransition(label = "mic_anim")
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.08f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse"
    )
    val micGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_glow"
    )

    val containerShape = RoundedCornerShape(28.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(containerShape)
            .background(bgColor, containerShape)
            .border(1.dp, borderColor, containerShape)
            .padding(start = 6.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                    text = "Або напишіть...",
                    color = Color.White.copy(alpha = 0.30f),
                    style = TextStyle(fontSize = 14.sp)
                )
            }
            if (isListening && text.isEmpty()) {
                Text(
                    text = "Говоріть...",
                    color = Color(0xFFEF4444).copy(alpha = 0.60f),
                    style = TextStyle(fontSize = 14.sp)
                )
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                enabled = enabled && !isListening,
                maxLines = 4,
                textStyle = TextStyle(
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(PrimaryColors.light),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Listening waveform indicator
        if (isListening) {
            MiniWaveform(
                barCount = 3,
                barWidth = 2.dp,
                maxBarHeight = 14.dp,
                minBarHeight = 3.dp,
                startColor = Color(0xFFEF4444).copy(alpha = 0.8f),
                endColor = Color(0xFFEF4444).copy(alpha = 0.5f),
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        // ===== VOICE-FIRST ACTION BUTTON (48dp, prominent) =====
        if (hasText && !isListening) {
            // Send button (smaller, 40dp — text is secondary)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                        )
                    )
                    .clickable(
                        enabled = enabled && !isProcessing,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onSendClick()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Надіслати",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            // Mic / Stop button (48dp — voice is PRIMARY)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(if (!isProcessing) micPulse else 1f)
                    .shadow(
                        if (isListening) 12.dp else 8.dp,
                        CircleShape,
                        spotColor = if (isListening) Color(0xFFEF4444).copy(alpha = 0.4f)
                        else PrimaryColors.glow.copy(alpha = micGlow)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = if (isListening) {
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                            )
                        }
                    )
                    .border(
                        1.5.dp,
                        if (isListening) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f),
                        CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isProcessing,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onVoiceClick()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Зупинити" else "Говорити",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
