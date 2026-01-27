package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun AchievementBadge(
    achievement: Achievement,
    isLarge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isLarge) 20.dp else 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = if (achievement.isUnlocked) 0.18f else 0.08f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = if (achievement.isUnlocked) Color.White else Color(0xFFF3F4F6),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(if (isLarge) 16.dp else 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon placeholder - using type title as fallback
        Text(
            text = getAchievementIcon(achievement),
            fontSize = if (isLarge) 48.sp else 36.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = achievement.title,
            style = if (isLarge) AppTypography.titleMedium else AppTypography.titleSmall,
            color = if (achievement.isUnlocked) TextColors.onLightPrimary else TextColors.onLightMuted,
            fontSize = if (isLarge) 16.sp else 14.sp,
            fontWeight = if (isLarge) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 2
        )

        if (!achievement.isUnlocked && achievement.progress != null && achievement.target != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${achievement.progress}/${achievement.target}",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                LinearProgressIndicator(
                    progress = { achievement.progress.toFloat() / achievement.target },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(4.dp),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFE5E7EB)
                )
            }
        }
    }
}

private fun getAchievementIcon(achievement: Achievement): String {
    return when {
        achievement.type.name.contains("STREAK") -> "🔥"
        achievement.type.name.contains("COURSE") -> "📚"
        achievement.type.name.contains("DICTION") -> "🗣️"
        achievement.type.name.contains("TEMPO") -> "⏱️"
        achievement.type.name.contains("EMOTION") -> "⭐"
        achievement.type.name.contains("FILLER") -> "✅"
        achievement.type.name.contains("IMPROV") -> "🎤"
        achievement.type.name.contains("DEBAT") -> "💬"
        achievement.type.name.contains("SALES") -> "💰"
        achievement.type.name.contains("STORY") -> "📚"
        achievement.type.name.contains("EARLY") -> "☀️"
        achievement.type.name.contains("NIGHT") -> "🌙"
        achievement.type.name.contains("BREAK") -> "🚀"
        else -> "🏆"
    }
}
