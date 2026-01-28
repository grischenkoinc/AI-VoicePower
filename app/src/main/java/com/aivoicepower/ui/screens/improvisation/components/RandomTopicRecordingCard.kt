package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.ImprovisationTopic
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun RandomTopicRecordingCard(
    topic: ImprovisationTopic,
    durationMs: Long,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFFEF4444).copy(alpha = 0.3f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Говори 1-3 хвилини",
            style = AppTypography.titleLarge,
            color = Color(0xFF991B1B),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "🔴 Запис...",
            style = AppTypography.headlineSmall,
            color = Color(0xFFEF4444),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = formatDuration(durationMs),
            style = AppTypography.displayMedium,
            color = Color(0xFF991B1B),
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-2).sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topic.title,
                style = AppTypography.titleMedium,
                color = Color(0xFF991B1B),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onStop,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF4444)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "■ Завершити",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000).toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
