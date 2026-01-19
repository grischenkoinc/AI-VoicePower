package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*

@Composable
fun TheoryPhaseContent(
    lesson: Lesson,
    onStartExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    GradientBackground(
        content = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Theory Card з premium дизайном
                lesson.theory?.let { theory ->
                    MainCard(
                        header = {
                            SectionTag(
                                emoji = "📖",
                                text = "Теорія",
                                isPractice = false
                            )

                            // BigTitle inline
                            Text(
                                text = lesson.title,
                                style = AppTypography.displayLarge,
                                color = TextColors.onDarkPrimary,
                                fontSize = 36.sp,
                                lineHeight = 40.sp,
                                letterSpacing = (-1.5).sp
                            )

                            LevelPill(
                                emoji = "📚",
                                level = lesson.dayNumber
                            )
                        },
                        content = {
                            ContentText(
                                text = theory.text
                            )

                            // Поради як numbered tips
                            if (theory.tips.isNotEmpty()) {
                                HighlightBox(
                                    title = "💡 Поради",
                                    content = ""
                                )

                                NumberedTips(tips = theory.tips)
                            }
                        }
                    )
                }

                // Exercises Preview Card
                MainCard(
                    header = {
                        SectionTag(
                            emoji = "🔥",
                            text = "Вправи • ${lesson.exercises.size}",
                            isPractice = true
                        )

                        Text(
                            text = "План тренування",
                            style = AppTypography.displayLarge,
                            color = TextColors.onDarkPrimary,
                            fontSize = 36.sp,
                            lineHeight = 40.sp,
                            letterSpacing = (-1.5).sp
                        )
                    },
                    content = {
                        ContentText(
                            title = "Тривалість",
                            text = "~${lesson.estimatedMinutes} хвилин практики"
                        )

                        // Exercise list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            lesson.exercises.forEachIndexed { index, exercise ->
                                ContentText(
                                    text = "${index + 1}. ${exercise.title}"
                                )
                            }
                        }
                    }
                )

                // Start Button - використовуємо NavButton
                NavButton(
                    text = "Почати вправи",
                    icon = "🚀",
                    isPrimary = true,
                    onClick = onStartExercises,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
