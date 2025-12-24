package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.course.Course

@Composable
fun CourseHeaderCard(
    course: Course,
    completedLessons: Int,
    totalLessons: Int,
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = course.iconEmoji,
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Divider()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Прогрес: $completedLessons/$totalLessons ($progressPercent%)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                LinearProgressIndicator(
                    progress = progressPercent / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
