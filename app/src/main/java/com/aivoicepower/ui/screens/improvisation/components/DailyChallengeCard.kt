package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.data.provider.DailyChallengeProvider
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DailyChallengeCard(
    challenge: DailyChallengeProvider.DailyChallenge,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isCompleted) {
                    Color(0xFF10B981).copy(alpha = 0.2f)
                } else {
                    Color(0xFF6366F1).copy(alpha = 0.2f)
                }
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("uk", "UA"))
            Text(
                text = today.format(formatter),
                style = AppTypography.labelMedium,
                color = TextColors.onLightSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (isCompleted) {
                Text(
                    text = "✓ Виконано",
                    style = AppTypography.labelMedium,
                    color = Color(0xFF10B981),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

        // Challenge title
        Text(
            text = challenge.title,
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Challenge description
        Text(
            text = challenge.description,
            style = AppTypography.bodyLarge,
            color = TextColors.onLightSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

        // Challenge metadata
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Час: ${formatTimeLimit(challenge.timeLimit)}",
                style = AppTypography.labelLarge,
                color = TextColors.onLightPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getDifficultyBadge(challenge.difficulty),
                style = AppTypography.labelLarge,
                color = getDifficultyColor(challenge.difficulty),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatTimeLimit(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (remainingSeconds > 0) {
        "${minutes}:${String.format("%02d", remainingSeconds)}"
    } else {
        "${minutes} хв"
    }
}

private fun getDifficultyBadge(difficulty: String): String {
    return when (difficulty) {
        "BEGINNER" -> "🟢 Легко"
        "INTERMEDIATE" -> "🟡 Середньо"
        "ADVANCED" -> "🔴 Складно"
        else -> difficulty
    }
}

private fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty) {
        "BEGINNER" -> Color(0xFF10B981)  // Green
        "INTERMEDIATE" -> Color(0xFFF59E0B)  // Orange
        "ADVANCED" -> Color(0xFFEF4444)  // Red
        else -> TextColors.onLightSecondary
    }
}
