package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.components.ReportAiContentDialog
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.utils.AnalyticsTracker
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AssistantMessageBubble(
    message: String,
    timestamp: Long,
    isSpeakingThis: Boolean = false,
    onSpeakClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportAiContentDialog(
            onDismiss = { showReportDialog = false },
            onReport = { reason ->
                AnalyticsTracker().logAiContentReported("coach_message", reason)
                showReportDialog = false
            }
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "speaker_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val bubbleShape = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 18.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .shadow(6.dp, CircleShape, spotColor = PrimaryColors.glow.copy(alpha = 0.3f))
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF8B5CF6))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "\uD83E\uDD16", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Bubble — always full text
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(bubbleShape)
                .background(Color.White.copy(alpha = 0.08f), bubbleShape)
                .border(1.dp, Color.White.copy(alpha = 0.10f), bubbleShape)
                .padding(12.dp)
        ) {
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.92f),
                style = TextStyle(fontSize = 15.sp, lineHeight = 22.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(timestamp),
                    style = TextStyle(fontSize = 10.sp),
                    color = TextColors.onDarkMuted
                )
                Spacer(modifier = Modifier.weight(1f))
                // Report button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .clickable { showReportDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Поскаржитись",
                        modifier = Modifier.size(13.dp),
                        tint = Color.White.copy(alpha = 0.25f)
                    )
                }
                if (onSpeakClick != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    SpeakerButton(
                        isSpeaking = isSpeakingThis,
                        glowAlpha = glowAlpha,
                        onClick = onSpeakClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeakerButton(
    isSpeaking: Boolean,
    glowAlpha: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
                if (isSpeaking) PrimaryColors.default.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.10f)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSpeaking)
                Icons.AutoMirrored.Filled.VolumeOff
            else
                Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = if (isSpeaking) "Зупинити" else "Озвучити",
            modifier = Modifier.size(15.dp),
            tint = if (isSpeaking)
                PrimaryColors.light.copy(alpha = glowAlpha)
            else
                Color.White.copy(alpha = 0.50f)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
