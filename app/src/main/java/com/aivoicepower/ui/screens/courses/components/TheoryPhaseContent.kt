package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.Gradients
import com.aivoicepower.ui.theme.TextColors
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
        // GradientBackground з контентом (все скролиться)
        GradientBackground(content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Spacer для висоти header (щоб контент не перекривався)
                Spacer(modifier = Modifier.height(88.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
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

                                BigTitle(text = lesson.title)
                            },
                            content = {
                                // Парсимо текст на блоки
                                val textParts = theory.text.split("\n\n")
                                var skipNext = 0

                                textParts.forEachIndexed { index, part ->
                                    if (skipNext > 0) {
                                        skipNext--
                                        return@forEachIndexed
                                    }

                                    val trimmed = part.trim()

                                    when {
                                        // Ключовий інсайт
                                        trimmed.contains("Ключовий інсайт:", ignoreCase = true) -> {
                                            val content = trimmed
                                                .substringAfter("Ключовий інсайт:", "")
                                                .trim()

                                            if (content.isNotBlank()) {
                                                FactBox(
                                                    title = "💡 Ключовий інсайт",
                                                    content = content
                                                )
                                            }
                                        }
                                        // Цікавий факт
                                        trimmed.contains("Цікавий факт:", ignoreCase = true) -> {
                                            val content = trimmed
                                                .substringAfter("Цікавий факт:", "")
                                                .trim()

                                            if (content.isNotBlank()) {
                                                HighlightBox(
                                                    title = "🎯 Цікавий факт",
                                                    content = content
                                                )
                                            }
                                        }
                                        // Заголовок секції (закінчується на ":")
                                        trimmed.endsWith(":") && trimmed.length < 100 && !trimmed.contains("Ключовий інсайт") && !trimmed.contains("Цікавий факт") -> {
                                            // Збираємо наступні пункти списку
                                            val listItems = mutableListOf<String>()
                                            var nextIndex = index + 1

                                            while (nextIndex < textParts.size) {
                                                val nextPart = textParts[nextIndex].trim()
                                                if (nextPart.matches(Regex("^\\d+\\..*"))) {
                                                    listItems.add(nextPart)
                                                    skipNext++
                                                    nextIndex++
                                                } else {
                                                    break
                                                }
                                            }

                                            // Показуємо заголовок жирним
                                            Text(
                                                text = trimmed,
                                                style = AppTypography.headlineMedium,
                                                color = TextColors.onLightPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                lineHeight = 26.sp
                                            )

                                            // Показуємо список без розривів
                                            if (listItems.isNotEmpty()) {
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    listItems.forEach { item ->
                                                        Text(
                                                            text = item,
                                                            style = AppTypography.bodyLarge,
                                                            color = TextColors.onLightSecondary,
                                                            fontSize = 15.sp,
                                                            lineHeight = 25.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        // Перше речення (назва) — виділити як title
                                        index == 0 && trimmed.contains("?") -> {
                                            val title = trimmed.substringBefore("\n")
                                            val rest = trimmed.substringAfter("\n", "")

                                            ContentText(
                                                title = title,
                                                text = rest.ifBlank { "" }
                                            )
                                        }
                                        // Звичайний текст
                                        trimmed.isNotBlank() -> {
                                            ContentText(text = trimmed)
                                        }
                                    }
                                }

                                // Важливо знати (tips без розривів)
                                if (theory.tips.isNotEmpty()) {
                                    NumberedTips(tips = theory.tips)
                                }
                            }
                        )
                    }

                    // Navigation (без смайликів, текст по центру)
                    BottomNavRow(
                        onPrevious = onNavigateBack,
                        onNext = onStartExercises
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        })

        // Fixed Header ПОВЕРХ (z-index вище через порядок у Box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Gradients.appBackground,
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
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
    }
}
