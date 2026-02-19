package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserMessageBubble(
    message: String,
    timestamp: Long,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val collapsedShape = RoundedCornerShape(20.dp)
    val expandedShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 4.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            if (isExpanded) {
                // EXPANDED: full text
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .shadow(
                            8.dp,
                            expandedShape,
                            spotColor = PrimaryColors.glow.copy(alpha = 0.2f)
                        )
                        .clip(expandedShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.12f), expandedShape)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatTimestamp(timestamp),
                            style = TextStyle(fontSize = 10.sp),
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Icon(
                            Icons.Default.ExpandLess,
                            contentDescription = "Згорнути",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                // COLLAPSED: mini waveform + time
                Row(
                    modifier = Modifier
                        .clip(collapsedShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    PrimaryColors.default.copy(alpha = 0.2f),
                                    PrimaryColors.darker.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(1.dp, PrimaryColors.light.copy(alpha = 0.2f), collapsedShape)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniWaveform(
                        barCount = 5,
                        barWidth = 3.dp,
                        maxBarHeight = 16.dp,
                        minBarHeight = 4.dp
                    )
                    Text(
                        text = formatTimestamp(timestamp),
                        style = TextStyle(fontSize = 11.sp),
                        color = TextColors.onDarkMuted
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
