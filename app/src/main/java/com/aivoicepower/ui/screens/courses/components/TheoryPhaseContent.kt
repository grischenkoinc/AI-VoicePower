package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.components.*

@Composable
fun TheoryPhaseContent(
    lesson: Lesson,
    onStartExercises: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                // Fixed Header "Урок X: Title"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Урок ${lesson.dayNumber}: ${lesson.title}",
                        style = AppTypography.displayLarge,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Theory Card
                    lesson.theory?.let { theory ->
                        MainCard(
                            header = {
                                SectionTag(
                                    emoji = "📖",
                                    text = "Теорія",
                                    isPractice = false
                                )
                            },
                            content = {
                                // Main theory text
                                ContentText(
                                    text = theory.text
                                )

                                // Ключовий інсайт (використати HighlightBox)
                                if (theory.text.contains("💡 Ключовий інсайт:")) {
                                    val insightText = theory.text
                                        .substringAfter("💡 Ключовий інсайт:")
                                        .substringBefore("🎯", "")
                                        .substringBefore("💡 Важливо", "")
                                        .trim()

                                    if (insightText.isNotBlank()) {
                                        HighlightBox(
                                            title = "💡 Ключовий інсайт",
                                            content = insightText
                                        )
                                    }
                                }

                                // Цікавий факт (жовтий фон як NumberedTips)
                                if (theory.text.contains("🎯 Цікавий факт:")) {
                                    val factText = theory.text
                                        .substringAfter("🎯 Цікавий факт:")
                                        .substringBefore("💡 Важливо", "")
                                        .trim()

                                    if (factText.isNotBlank()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(
                                                    elevation = 8.dp,
                                                    shape = RoundedCornerShape(16.dp),
                                                    spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                                                )
                                                .background(Color(0xFFFFFBEB), RoundedCornerShape(16.dp))
                                                .padding(20.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "🎯 Цікавий факт",
                                                style = AppTypography.titleMedium,
                                                color = Color(0xFF92400E),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )

                                            Text(
                                                text = factText,
                                                style = AppTypography.bodyMedium,
                                                color = Color(0xFF92400E),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 24.sp
                                            )
                                        }
                                    }
                                }

                                // Важливо знати (використати NumberedTips)
                                if (theory.tips.isNotEmpty()) {
                                    NumberedTips(tips = theory.tips)
                                }
                            }
                        )
                    }

                    // Navigation (як в зразку)
                    BottomNavRow(
                        onPrevious = onNavigateBack,
                        onNext = onStartExercises
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        )
    }
}
