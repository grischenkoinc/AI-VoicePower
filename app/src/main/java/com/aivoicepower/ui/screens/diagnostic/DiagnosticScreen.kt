package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import com.aivoicepower.ui.components.RecordButton
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.modifiers.glowShadow
import com.aivoicepower.ui.theme.modifiers.pulseAnimation

@Composable
fun DiagnosticScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTaskIndex by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val tasks = remember { getDefaultTasks() }
    val currentTaskData = tasks[currentTaskIndex]

    GradientBackground(
        content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 20.dp, top = 56.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            DiagnosticHeader(
                currentTask = currentTaskIndex + 1,
                totalTasks = tasks.size
            )

            // Progress
            DiagnosticProgress(
                current = currentTaskIndex + 1,
                total = tasks.size
            )

            // Task Card
            TaskCard(
                task = currentTaskData,
                taskNumber = currentTaskIndex + 1,
                isRecording = isRecording,
                onRecordClick = { isRecording = !isRecording }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation
            if (currentTaskIndex < tasks.size - 1) {
                PrimaryButton(
                    text = "Далі",
                    onClick = {
                        currentTaskIndex++
                        isRecording = false
                    }
                )
            } else {
                PrimaryButton(
                    text = "Завершити діагностику",
                    onClick = onComplete
                )
            }
        }
        },
        modifier = modifier
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
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.titleMedium,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun DiagnosticHeader(
    currentTask: Int,
    totalTasks: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Діагностика мовлення",
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1.2).sp
        )
        Text(
            text = "Виконайте 4 завдання для аналізу вашого мовлення",
            style = AppTypography.bodyMedium,
            color = TextColors.onDarkSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DiagnosticProgress(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Завдання $current з $total",
                style = AppTypography.labelMedium,
                color = TextColors.onDarkPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(current * 100 / total)}%",
                style = AppTypography.labelMedium,
                color = Color(0xFF22C55E),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(current.toFloat() / total)
                    .fillMaxHeight()
                    .glowShadow(
                        glowRadius = 16.dp,
                        glowColor = Color(0xFF22C55E),
                        intensity = 0.7f
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                        ),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: DiagnosticTask,
    taskNumber: Int,
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Анімація пульсуючої рамки під час запису
    val infiniteTransition = rememberInfiniteTransition(label = "border_pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Task Number Badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
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
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.emoji,
                    fontSize = 32.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF667EEA).copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ЗАВДАННЯ $taskNumber",
                            style = AppTypography.labelSmall,
                            color = Color(0xFF667EEA),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = task.title,
                    style = AppTypography.titleLarge,
                    color = TextColors.onLightPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // Instruction
        Text(
            text = task.instruction,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp
        )

        // Content Text Box (if available)
        task.contentText?.let { content ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF5F5F7),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = content,
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Recording Status
        if (isRecording) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFEF4444).copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
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
                    style = AppTypography.bodySmall,
                    color = Color(0xFFEF4444),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Record Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RecordButton(
                isRecording = isRecording,
                onClick = onRecordClick
            )
        }
    }
}
