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
import com.aivoicepower.data.provider.DailyChallengeProvider
import com.aivoicepower.ui.theme.AppTypography

@Composable
fun DailyChallengeRecordingCard(
    challenge: DailyChallengeProvider.DailyChallenge,
    durationMs: Long,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeLimitMs = challenge.timeLimit * 1000L
    val progress = (durationMs.toFloat() / timeLimitMs).coerceIn(0f, 1f)

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
            text = challenge.title,
            style = AppTypography.titleLarge,
            color = Color(0xFF991B1B),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "🔴 Запис...",
            style = AppTypography.headlineSmall,
            color = Color(0xFFEF4444),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = Color(0xFFEF4444),
            trackColor = Color.White.copy(alpha = 0.5f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Text(
            text = formatDuration(durationMs),
            style = AppTypography.displayMedium,
            color = Color(0xFF991B1B),
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-2).sp
        )

        Text(
            text = "Ліміт: ${formatDuration(timeLimitMs)}",
            style = AppTypography.bodyMedium,
            color = Color(0xFF7C2D12),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = challenge.description,
                style = AppTypography.bodySmall,
                color = Color(0xFF7C2D12),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
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
