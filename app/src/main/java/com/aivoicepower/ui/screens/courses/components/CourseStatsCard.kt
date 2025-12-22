package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.course.Course
import com.aivoicepower.domain.model.course.toDisplayString
import com.aivoicepower.domain.model.user.toDisplayString

@Composable
fun CourseStatsCard(
    course: Course,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⭐ ${course.difficulty.toDisplayString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "📚 ${course.totalLessons} уроків • ~${course.estimatedDays} днів",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "🎯 " + course.skills.joinToString(", ") { it.toDisplayString() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
