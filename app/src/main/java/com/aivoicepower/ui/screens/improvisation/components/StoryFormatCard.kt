package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.exercise.StoryFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryFormatCard(
    format: StoryFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = getFormatIcon(format) + " " + getFormatTitle(format),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = getFormatDescription(format),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getFormatIcon(format: StoryFormat): String {
    return when (format) {
        StoryFormat.WITH_PROMPTS -> "🎯"
        StoryFormat.FROM_IMAGE -> "🖼️"
        StoryFormat.CONTINUE -> "📝"
        StoryFormat.RANDOM_WORDS -> "🎲"
    }
}

private fun getFormatTitle(format: StoryFormat): String {
    return when (format) {
        StoryFormat.WITH_PROMPTS -> "З підказками"
        StoryFormat.FROM_IMAGE -> "За картинкою"
        StoryFormat.CONTINUE -> "Продовж історію"
        StoryFormat.RANDOM_WORDS -> "Випадкові слова"
    }
}

private fun getFormatDescription(format: StoryFormat): String {
    return when (format) {
        StoryFormat.WITH_PROMPTS -> "Створи історію про героя, місце, предмет і твіст"
        StoryFormat.FROM_IMAGE -> "Розкажи історію за згенерованою картинкою"
        StoryFormat.CONTINUE -> "Продовж початок історії своїми словами"
        StoryFormat.RANDOM_WORDS -> "Вплети 3 випадкові слова в свою історію"
    }
}
