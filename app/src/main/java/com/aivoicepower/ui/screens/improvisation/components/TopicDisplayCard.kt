package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.ImprovisationTopic

@Composable
fun TopicDisplayCard(
    topic: ImprovisationTopic,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎲 Твоя тема:",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = topic.title,
                style = MaterialTheme.typography.headlineSmall
            )

            if (topic.hints.isNotEmpty()) {
                Text(
                    text = "💡 Підказки:",
                    style = MaterialTheme.typography.titleSmall
                )

                topic.hints.forEach { hint ->
                    Text(
                        text = "• $hint",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
