package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import com.aivoicepower.ui.theme.modifiers.*

private data class TaskItem(
    val number: Int,
    val title: String,
    val subtitle: String,
    val icon: String,
    val prompt: String,
    val tip: String,
    val isScrollable: Boolean = false,
    val hasEmotions: Boolean = false
)

@Composable
fun DiagnosticScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTask by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val tasks = remember {
        listOf(
            TaskItem(
                number = 1,
                title = "Читання",
                subtitle = "Прочитайте текст природно",
                icon = "📖",
                prompt = "Мистецтво публічного виступу полягає не лише в тому, що ви говорите, але й у тому, як ви це робите. Впевненість, чіткість та емоційність — ключові складові успішної комунікації.",
                tip = "Читайте спокійно, ніби розмовляєте з другом",
                isScrollable = true
            ),
            TaskItem(
                number = 2,
                title = "Дикція",
                subtitle = "Чітко вимовте скоромовку",
                icon = "🎯",
                prompt = "Коси коса, поки роса. Роса долі — коси додому.",
                tip = "Не поспішайте, головне — чіткість кожного звуку"
            ),
            TaskItem(
                number = 3,
                title = "Емоції",
                subtitle = "Читайте з відповідними емоціями",
                icon = "🎭",
                prompt = "emotions",
                tip = "Уявіть що переживаєте ці емоції насправді",
                hasEmotions = true
            ),
            TaskItem(
                number = 4,
                title = "Вільна мова",
                subtitle = "Розкажіть про себе",
                icon = "💬",
                prompt = "Чому ви хочете покращити мовлення? Що ви сподіваєтесь досягти?",
                tip = "Будьте природними та щирими"
            )
        )
    }

    val currentTaskData = tasks[currentTask]

    Box(modifier = modifier.fillMaxSize()) {
        GradientBackground(content = {})

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress dots
            ProgressDots(
                current = currentTask,
                total = tasks.size
            )

            // Title
            Text(
                text = "Діагностика",
                style = AppTypography.displayLarge,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            // Task card
            TaskCardNew(
                task = currentTaskData,
                isRecording = isRecording,
                onRecordClick = { isRecording = !isRecording }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentTask > 0) {
                    SecondaryButton(
                        text = "Назад",
                        onClick = {
                            currentTask--
                            isRecording = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                PrimaryButton(
                    text = when {
                        currentTask < tasks.size - 1 -> "Далі"
                        else -> "Завершити"
                    },
                    onClick = {
                        if (currentTask < tasks.size - 1) {
                            currentTask++
                            isRecording = false
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.weight(if (currentTask > 0) 1.5f else 1f)
                )
            }
        }
    }
}

@Composable
private fun ProgressDots(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index <= current)
                            Color.White
                        else
                            Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun TaskCardNew(
    task: TaskItem,
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Border pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .then(
                if (isRecording) {
                    Modifier.border(
                        width = 3.dp,
                        color = Color(0xFF667EEA).copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(24.dp)
                    )
                } else Modifier
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0xFF667EEA).copy(alpha = 0.3f)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = task.icon, fontSize = 28.sp)
        }

        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Завдання ${task.number}/4",
                style = AppTypography.labelMedium,
                color = Color(0xFF667EEA),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = task.title,
                style = AppTypography.displayLarge,
                color = TextColors.onLightPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = task.subtitle,
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        // Prompt
        if (task.hasEmotions) {
            EmotionalTextPrompt()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF8F9FA),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = task.prompt,
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFFFFBEB),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "💡", fontSize = 20.sp)
            Text(
                text = task.tip,
                style = AppTypography.bodySmall,
                color = Color(0xFF92400E),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Record Button with wave rings
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Wave rings when recording
            if (isRecording) {
                repeat(3) { index ->
                    WaveRing(
                        delay = index * 600,
                        color = Color(0xFF667EEA)
                    )
                }
            }

            // Main button (КРУГЛИЙ!)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(
                        elevation = if (isRecording) 16.dp else 12.dp,
                        shape = CircleShape,
                        spotColor = if (isRecording)
                            Color(0xFFEF4444).copy(alpha = 0.5f)
                        else
                            Color(0xFF667EEA).copy(alpha = 0.4f)
                    )
                    .background(
                        if (isRecording)
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                            )
                        else
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onRecordClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRecording) "⏹" else "🎤",
                    fontSize = 40.sp
                )
            }
        }

        // Status
        if (isRecording) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .pulseAnimation()
                        .background(Color(0xFFEF4444), CircleShape)
                )
                Text(
                    text = "Запис...",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmotionalTextPrompt(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF8F9FA),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Радісно
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "😊", fontSize = 16.sp)
                Text(
                    text = "Радісно:",
                    style = AppTypography.labelMedium,
                    color = Color(0xFF10B981),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "Сьогодні чудовий день! Я радий бути тут!",
                style = AppTypography.bodySmall,
                color = TextColors.onLightPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        // Сумно
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "😢", fontSize = 16.sp)
                Text(
                    text = "Сумно:",
                    style = AppTypography.labelMedium,
                    color = Color(0xFF6366F1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "Іноді буває важко, але завтра буде новий день.",
                style = AppTypography.bodySmall,
                color = TextColors.onLightPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        // Впевнено
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💪", fontSize = 16.sp)
                Text(
                    text = "Впевнено:",
                    style = AppTypography.labelMedium,
                    color = Color(0xFFF59E0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = "Я впевнений у собі та досягну своїх цілей!",
                style = AppTypography.bodySmall,
                color = TextColors.onLightPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun WaveRing(
    delay: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .border(
                width = 3.dp,
                color = color.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                ),
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
