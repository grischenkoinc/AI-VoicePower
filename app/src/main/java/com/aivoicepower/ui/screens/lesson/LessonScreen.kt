package com.aivoicepower.ui.screens.lesson

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivoicepower.ui.theme.AIVoicePowerTheme
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.*

@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    onNavigateBack: () -> Unit,
    onNavigateToResults: (String) -> Unit,
    viewModel: LessonViewModel = viewModel()
) {
    var isRecording by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    GradientBackground(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Content (scrollable)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 140.dp, bottom = 80.dp) // Space for fixed header + footer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(start = 24.dp, end = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Theory Card
                        MainCard(
                            header = {
                                SectionTag(
                                    emoji = "📖",
                                    text = "Теорія",
                                    isPractice = false
                                )

                                BigTitle(text = "Основи артикуляції")

                                LevelPill(
                                    emoji = "⚡",
                                    level = 3
                                )
                            },
                            content = {
                                ContentText(
                                    title = "Що таке артикуляція?",
                                    text = "Артикуляція — це робота органів мовлення (губ, язика, щелеп) під час вимови звуків. Це основа чіткого мовлення."
                                )

                                HighlightBox(
                                    title = "💡 Ключовий інсайт",
                                    content = "Чітка дикція = впевненість у спілкуванні"
                                )

                                ContentText(
                                    text = "Люди з гарною артикуляцією справляють враження компетентних професіоналів. Регулярні тренування приносять відчутний результат."
                                )

                                NumberedTips(
                                    tips = listOf(
                                        "Розтягни губи широко — зуби мають бути видно",
                                        "Витягни губи вперед трубочкою максимально",
                                        "Виконуй без пауз між повтореннями"
                                    )
                                )
                            }
                        )

                        // Practice Card
                        PracticeCard(
                            header = {
                                SectionTag(
                                    emoji = "🔥",
                                    text = "Практика • 1/5",
                                    isPractice = true
                                )

                                BigTitle(text = "Посмішка → Трубочка")
                            },
                            content = {
                                ExerciseVisual(
                                    content = {
                                        VisualRow(
                                            items = listOf(
                                                VisualItem(
                                                    emoji = "😄",
                                                    label = "Широка посмішка",
                                                    time = "2 сек"
                                                ),
                                                VisualItem(
                                                    emoji = "😗",
                                                    label = "Губи трубочкою",
                                                    time = "2 сек"
                                                )
                                            )
                                        )

                                        VisualDivider()

                                        RepeatRow(repetitions = 10)
                                    }
                                )

                                // Record Section
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        RecordButton(
                                            isRecording = isRecording,
                                            onClick = { isRecording = !isRecording }
                                        )

                                        Text(
                                            text = if (isRecording) "Йде запис..." else "Натисни для запису",
                                            style = AppTypography.bodyMedium,
                                            color = TextColors.muted,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Fixed Progress Header (top)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProgressBar3D(
                        progress = 0.25f,
                        currentStep = 1,
                        totalSteps = 4,
                        stepLabel = "Теорія"
                    )
                }

                // Fixed Bottom Navigation
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    BottomNavRow(
                        onPrevious = onNavigateBack,
                        onNext = { /* Navigate to next step */ }
                    )
                }
            }
        }
    )
}

@Composable
private fun BigTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AppTypography.displayLarge,
        color = TextColors.onDarkPrimary,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1.5).sp,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun LessonScreenPreview() {
    AIVoicePowerTheme {
        LessonScreen(
            courseId = "course_1",
            lessonId = "lesson_1",
            onNavigateBack = {},
            onNavigateToResults = {}
        )
    }
}
