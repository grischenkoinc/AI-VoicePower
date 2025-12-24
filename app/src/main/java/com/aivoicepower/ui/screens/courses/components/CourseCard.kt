package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.course.toDisplayString
import com.aivoicepower.ui.screens.courses.CourseWithProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCard(
    courseWithProgress: CourseWithProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title with emoji
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = courseWithProgress.course.iconEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = courseWithProgress.course.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = courseWithProgress.course.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = courseWithProgress.progressPercent / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${courseWithProgress.completedLessons}/${courseWithProgress.totalLessons}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${courseWithProgress.course.difficulty.toDisplayString()} • ${courseWithProgress.totalLessons} ${getLessonsText(courseWithProgress.totalLessons)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (courseWithProgress.isStarted) "Продовжити" else "Почати"
                    )
                }
            }
        }
    }
}

private fun getLessonsText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "урок"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "уроки"
        else -> "уроків"
    }
}
