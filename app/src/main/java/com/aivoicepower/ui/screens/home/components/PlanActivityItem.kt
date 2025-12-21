package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.home.PlanActivity
import com.aivoicepower.domain.model.home.ActivityType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanActivityItem(
    activity: PlanActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = if (activity.isCompleted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = when (activity.type) {
                        ActivityType.WARMUP -> Icons.Default.FitnessCenter
                        ActivityType.LESSON -> Icons.Default.MenuBook
                        ActivityType.IMPROVISATION -> Icons.Default.Mic
                        ActivityType.AI_COACH -> Icons.Default.Assistant
                        ActivityType.DIAGNOSTIC -> Icons.Default.Assessment
                        ActivityType.DAILY_CHALLENGE -> Icons.Default.EmojiEvents
                    },
                    contentDescription = null,
                    tint = if (activity.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Column {
                    Text(
                        text = if (activity.isCompleted) "✅ ${activity.title}" else "▶️ ${activity.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    activity.subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "~${activity.estimatedMinutes} хв",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
