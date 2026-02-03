package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.exercise.ExerciseContent

/**
 * Universal white card wrapper for all exercise content
 */
@Composable
private fun ExerciseContentCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 2.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(28.dp)
    ) {
        content()
    }
}

@Composable
fun ExerciseContentDisplay(
    content: ExerciseContent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (content) {
            is ExerciseContent.TongueTwister -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = content.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827),
                            letterSpacing = 0.2.sp
                        )
                        if (content.targetSounds.isNotEmpty()) {
                            Text(
                                text = "Цільові звуки: ${content.targetSounds.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            is ExerciseContent.ReadingText -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (content.emotion != null) {
                            Text(
                                text = "${content.emotion.getEmoji()} ${content.emotion.getDisplayName()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF667EEA),
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text = content.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827)
                        )
                    }
                }
            }

            is ExerciseContent.MinimalPairs -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Схожі слова",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )

                        content.pairs.forEach { pair ->
                            Text(
                                text = "${pair.first}  /  ${pair.second}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF111827)
                            )
                        }

                        if (content.targetSounds.isNotEmpty()) {
                            Text(
                                text = "Цільові звуки: ${content.targetSounds.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            is ExerciseContent.ContrastSounds -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Чергуй звуки",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )

                        Text(
                            text = content.sequence,
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827),
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "Повтори ${content.repetitions} разів",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )

                        if (content.targetSounds.isNotEmpty()) {
                            Text(
                                text = "Фокус: ${content.targetSounds.joinToString(" / ")}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            is ExerciseContent.TongueTwisterBattle -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "⚡ Батл: ${content.twisters.size} скоромовки поспіль",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEF4444),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        content.twisters.forEachIndexed { index, twister ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF667EEA)
                                )

                                Text(
                                    text = twister.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 24.sp,
                                    color = Color(0xFF111827),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Text(
                            text = "⚠️ Читай без пауз між скоромовками",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            is ExerciseContent.SlowMotion -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🐢 Повільна вимова",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )

                        Text(
                            text = content.text,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827)
                        )

                        Text(
                            text = "⏱ Мінімум ${content.minDurationSeconds} секунд",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            is ExerciseContent.BreathingExercise -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🫁 Дихальна вправа",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF667EEA)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${content.inhaleSeconds}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "Вдих",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${content.holdSeconds}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFBBF24)
                                )
                                Text(
                                    text = "Затримка",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${content.exhaleSeconds}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF667EEA)
                                )
                                Text(
                                    text = "Видих",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Text(
                            text = "🔁 Повтори ${content.cycles} циклів",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        Text(
                            text = content.instruction,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF6B7280),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            is ExerciseContent.FreeSpeechTopic -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💬 Тема для імпровізації",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )

                        Text(
                            text = content.topic,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 30.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827)
                        )

                        if (content.hints.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "💡 Підказки:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280)
                                )
                                content.hints.forEach { hint ->
                                    Text(
                                        text = "• $hint",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 15.sp,
                                        color = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is ExerciseContent.Retelling -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "📖 Прочитай і перекажи",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = content.sourceText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827)
                        )

                        if (content.keyPoints.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🔑 Ключові моменти:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280)
                                )
                                content.keyPoints.forEach { point ->
                                    Text(
                                        text = "• $point",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 15.sp,
                                        color = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is ExerciseContent.Dialogue -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🗣 Діалог",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        content.lines.forEach { line ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (line.isUserLine) {
                                            Color(0xFFF3F4F6)
                                        } else {
                                            Color(0xFFE0E7FF)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = line.speaker,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (line.isUserLine) {
                                        Color(0xFF667EEA)
                                    } else {
                                        Color(0xFF6B7280)
                                    }
                                )
                                Text(
                                    text = line.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111827)
                                )
                            }
                        }
                    }
                }
            }

            is ExerciseContent.Pitch -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "💼 Пітч",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = content.scenario,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827)
                        )

                        Text(
                            text = "🎯 Цільова аудиторія: ${content.targetAudience}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (content.keyMessages.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📌 Ключові меседжі:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280)
                                )
                                content.keyMessages.forEach { message ->
                                    Text(
                                        text = "• $message",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 15.sp,
                                        color = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is ExerciseContent.ArticulationExercise -> {
                ExerciseContentCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎭 Артикуляційна гімнастика",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )

                        Text(
                            text = "Виконай вправу за інструкцією",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF111827)
                        )
                    }
                }
            }
        }
    }
}
