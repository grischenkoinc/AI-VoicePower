package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserMessageBubble(
    message: String,
    timestamp: Long,
    modifier: Modifier = Modifier
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 4.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .shadow(
                    4.dp,
                    bubbleShape,
                    spotColor = PrimaryColors.glow.copy(alpha = 0.15f)
                )
                .clip(bubbleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.10f), bubbleShape)
                .padding(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = message,
                color = Color.White,
                style = TextStyle(fontSize = 15.sp, lineHeight = 22.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(timestamp),
                style = TextStyle(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.45f)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
