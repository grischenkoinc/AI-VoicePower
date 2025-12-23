package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.data.provider.DailyChallengeProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyChallengeCard(
    challenge: DailyChallengeProvider.DailyChallenge,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                Text(
                    text = today.format(formatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (isCompleted) {
                    Text(
                        text = "✓ Виконано",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider()

            // Challenge title
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Challenge description
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Divider()

            // Challenge metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Час: ${formatTimeLimit(challenge.timeLimit)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = getDifficultyBadge(challenge.difficulty),
                    style = MaterialTheme.typography.labelLarge,
                    color = getDifficultyColor(challenge.difficulty)
                )
            }
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

@Composable
private fun getDifficultyColor(difficulty: String): androidx.compose.ui.graphics.Color {
    return when (difficulty) {
        "BEGINNER" -> MaterialTheme.colorScheme.tertiary
        "INTERMEDIATE" -> MaterialTheme.colorScheme.secondary
        "ADVANCED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
}
