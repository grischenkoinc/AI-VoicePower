package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.courses.LessonWithProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListItem(
    lessonWithProgress: LessonWithProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = !lessonWithProgress.isLocked,
        colors = if (lessonWithProgress.isCompleted) {
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
                    imageVector = when {
                        lessonWithProgress.isLocked -> Icons.Default.Lock
                        lessonWithProgress.isCompleted -> Icons.Filled.CheckCircle
                        else -> Icons.Outlined.Circle
                    },
                    contentDescription = null,
                    tint = when {
                        lessonWithProgress.isLocked -> MaterialTheme.colorScheme.onSurfaceVariant
                        lessonWithProgress.isCompleted -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                Column {
                    Text(
                        text = "День ${lessonWithProgress.lesson.dayNumber}: ${lessonWithProgress.lesson.title}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${lessonWithProgress.lesson.exercises.size} вправи • ${lessonWithProgress.lesson.estimatedMinutes} хв",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
