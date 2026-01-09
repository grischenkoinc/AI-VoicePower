package com.aivoicepower.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.aivoicepower.ui.theme.*

/**
 * Metric data class
 */
data class Metric(
    val label: String,
    val score: Int
)

/**
 * AI Feedback Card - картка з результатами AI аналізу
 */
@Composable
fun AiFeedbackCard(
    overallScore: Int,
    metrics: List<Metric>,
    feedback: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val slideOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 20.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = AnimationEasing.decelerate
        ),
        label = "slideOffset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = slideOffset)
            .alpha(alpha)
            .glassEffect(GlassStrength.STRONG, RoundedCornerShape(CornerRadius.xl))
            .border(
                width = 1.dp,
                color = PrimaryColors.default.copy(alpha = 0.3f),
                shape = RoundedCornerShape(CornerRadius.xl)
            )
            .shadowPreset(ShadowPreset.CARD_ELEVATED, RoundedCornerShape(CornerRadius.xl)),
        shape = RoundedCornerShape(CornerRadius.xl),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    modifier = Modifier.size(24.dp),
                    tint = PrimaryColors.default
                )
                Text(
                    text = "AI Аналіз",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextColors.primary
                )
            }

            // Overall score
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgress(
                    progress = overallScore / 100f,
                    size = 80,
                    strokeWidth = 8
                )
            }

            // Metrics
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                metrics.forEachIndexed { index, metric ->
                    val delayMs = index * 100
                    MetricCard(
                        label = metric.label,
                        score = metric.score,
                        animationDelay = delayMs
                    )
                }
            }

            // Text feedback
            if (feedback.isNotEmpty()) {
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColors.secondary
                )
            }
        }
    }
}

/**
 * Metric Card - горизонтальний бар з оцінкою
 */
@Composable
fun MetricCard(
    label: String,
    score: Int,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    val progress by animateFloatAsState(
        targetValue = if (isVisible) score / 100f else 0f,
        animationSpec = tween(
            durationMillis = AnimationDuration.medium,
            easing = AnimationEasing.decelerate
        ),
        label = "metricProgress"
    )

    val scoreColor = when (score) {
        in 0..39 -> ErrorColors.default
        in 40..69 -> WarningColors.default
        else -> SuccessColors.default
    }

    val progressGradient = when (score) {
        in 0..39 -> Brush.horizontalGradient(listOf(ErrorColors.default, ErrorColors.light))
        in 40..69 -> Brush.horizontalGradient(listOf(WarningColors.default, WarningColors.light))
        else -> Gradients.progressFill
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(GlassColors.light, RoundedCornerShape(CornerRadius.md))
            .border(1.dp, BorderColors.light, RoundedCornerShape(CornerRadius.md))
            .padding(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Label + Score
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColors.secondary
                )
                Text(
                    text = "$score/100",
                    style = MaterialTheme.typography.titleLarge,
                    color = scoreColor
                )
            }

            // Right: Progress bar
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(8.dp)
                    .background(GlassColors.medium, RoundedCornerShape(CornerRadius.full))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(progressGradient, RoundedCornerShape(CornerRadius.full))
                )
            }
        }
    }
}

// Legacy support - old AiFeedbackCard signature
@Composable
fun AiFeedbackCard(
    overallScore: Int,
    strengths: List<String>,
    improvements: List<String>,
    tip: String,
    modifier: Modifier = Modifier
) {
    // Convert old format to new metrics format
    val metrics = listOf(
        Metric("Загальний результат", overallScore)
    )
    val feedback = buildString {
        if (strengths.isNotEmpty()) {
            append("Сильні сторони: ")
            append(strengths.joinToString(", "))
            append("\n")
        }
        if (improvements.isNotEmpty()) {
            append("Що покращити: ")
            append(improvements.joinToString(", "))
            append("\n")
        }
        append(tip)
    }

    AiFeedbackCard(
        overallScore = overallScore,
        metrics = metrics,
        feedback = feedback,
        modifier = modifier
    )
}
