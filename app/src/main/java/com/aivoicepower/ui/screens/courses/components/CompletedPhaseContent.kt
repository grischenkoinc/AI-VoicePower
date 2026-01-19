package com.aivoicepower.ui.screens.courses.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.course.Lesson
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*

@Composable
fun CompletedPhaseContent(
    lesson: Lesson,
    nextLesson: Lesson? = null,
    onFinish: () -> Unit,
    onNextLesson: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Celebration animation
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    GradientBackground(
        content = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFFFBBF24).copy(alpha = 0.5f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎉", fontSize = 64.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "Урок пройдено!",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-1.5).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "День ${lesson.dayNumber}: ${lesson.title}",
                    style = AppTypography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Stats Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                        .background(
                            Color.White.copy(alpha = 0.95f),
                            RoundedCornerShape(32.dp)
                        )
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Чудова робота!",
                        style = AppTypography.titleLarge,
                        color = TextColors.onLightPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Ти виконав ${lesson.exercises.size} вправи. Прогрес збережено.",
                        style = AppTypography.bodyMedium,
                        color = TextColors.onLightSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (nextLesson != null && onNextLesson != null) {
                        NavButton(
                            text = "До наступного: ${nextLesson.title}",
                            icon = "→",
                            isPrimary = true,
                            onClick = onNextLesson,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    NavButton(
                        text = "Повернутися до курсу",
                        icon = "←",
                        isPrimary = false,
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
