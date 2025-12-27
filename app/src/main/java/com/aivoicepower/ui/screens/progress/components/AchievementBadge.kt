package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.user.Achievement

@Composable
fun AchievementBadge(
    achievement: Achievement,
    isLarge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = if (achievement.isUnlocked) {
            CardDefaults.cardColors()
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLarge) 16.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon placeholder - using type title as fallback
            Text(
                text = getAchievementIcon(achievement),
                style = if (isLarge) {
                    MaterialTheme.typography.displayMedium
                } else {
                    MaterialTheme.typography.headlineMedium
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.title,
                style = if (isLarge) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleSmall
                },
                maxLines = 2
            )

            if (!achievement.isUnlocked && achievement.progress != null && achievement.target != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${achievement.progress}/${achievement.target}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LinearProgressIndicator(
                        progress = achievement.progress.toFloat() / achievement.target,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(4.dp)
                    )
                }
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
