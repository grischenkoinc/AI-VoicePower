package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.exercise.ExerciseContent

@Composable
fun ExerciseContentDisplay(
    content: ExerciseContent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (content) {
                is ExerciseContent.TongueTwister -> {
                    Text(
                        text = "Текст:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = content.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (content.targetSounds.isNotEmpty()) {
                        Text(
                            text = "Цiльовi звуки: ${content.targetSounds.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is ExerciseContent.ReadingText -> {
                    if (content.emotion != null) {
                        Text(
                            text = "${content.emotion.getEmoji()} Емоцiя: ${content.emotion.getDisplayName()}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Text(
                        text = "Текст:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = content.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is ExerciseContent.FreeSpeechTopic -> {
                    Text(
                        text = "Тема:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = content.topic,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (content.hints.isNotEmpty()) {
                        Text(
                            text = "Пiдказки:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        content.hints.forEach { hint ->
                            Text(
                                text = "- $hint",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is ExerciseContent.Retelling -> {
                    Text(
                        text = "Прочитай i перекажи:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = content.sourceText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (content.keyPoints.isNotEmpty()) {
                        Text(
                            text = "Ключовi моменти:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        content.keyPoints.forEach { point ->
                            Text(
                                text = "- $point",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is ExerciseContent.Dialogue -> {
                    Text(
                        text = "Дiалог:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    content.lines.forEach { line ->
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = line.speaker,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (line.isUserLine) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Text(
                                text = line.text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is ExerciseContent.Pitch -> {
                    Text(
                        text = "Сценарiй:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = content.scenario,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Цiльова аудиторiя: ${content.targetAudience}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (content.keyMessages.isNotEmpty()) {
                        Text(
                            text = "Ключовi меседжi:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        content.keyMessages.forEach { message ->
                            Text(
                                text = "- $message",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                is ExerciseContent.ArticulationExercise -> {
                    Text(
                        text = "Виконай вправу за iнструкцiєю",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (content.hasTimer) {
                        Text(
                            text = "Час виконання: ${content.durationSeconds} сек",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
