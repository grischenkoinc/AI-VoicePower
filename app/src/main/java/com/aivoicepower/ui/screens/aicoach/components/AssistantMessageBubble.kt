package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AssistantMessageBubble(
    message: String,
    timestamp: Long,
    isExpanded: Boolean = false,
    isSpeakingThis: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onSpeakClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Glow animation for speaker icon
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
        topEnd = 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI Avatar — gradient circle
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

        // Glass bubble
        Box(
            modifier = Modifier
                .weight(1f)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .clip(bubbleShape)
                .background(Color.White.copy(alpha = 0.10f), bubbleShape)
                .border(1.dp, Color.White.copy(alpha = 0.15f), bubbleShape)
                .padding(12.dp)
        ) {
            if (isExpanded) {
                // EXPANDED: full text + controls
                Column {
                    Text(
                        text = message,
                        color = Color.White.copy(alpha = 0.92f),
                        style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Footer: time + play + collapse
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatTimestamp(timestamp),
                            style = TextStyle(fontSize = 10.sp),
                            color = TextColors.onDarkMuted
                        )
                        if (onSpeakClick != null) {
                            SpeakerButton(
                                isSpeaking = isSpeakingThis,
                                glowAlpha = glowAlpha,
                                onClick = onSpeakClick
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.ExpandLess,
                            contentDescription = "Згорнути",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // COLLAPSED: 1 line + play
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(alpha = 0.8f),
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (onSpeakClick != null) {
                        SpeakerButton(
                            isSpeaking = isSpeakingThis,
                            glowAlpha = glowAlpha,
                            onClick = onSpeakClick
                        )
                    }
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = "Розгорнути",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
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
            .size(26.dp)
            .clip(CircleShape)
            .background(
                if (isSpeaking) PrimaryColors.default.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.06f)
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
            modifier = Modifier.size(14.dp),
            tint = if (isSpeaking)
                PrimaryColors.light.copy(alpha = glowAlpha)
            else
                Color.White.copy(alpha = 0.45f)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
