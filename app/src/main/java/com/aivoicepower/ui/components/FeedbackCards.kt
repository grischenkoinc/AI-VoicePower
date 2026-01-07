package com.aivoicepower.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.theme.*

/**
 * AI Feedback Card - картка з результатами AI аналізу
 */
@Composable
fun AiFeedbackCard(
    overallScore: Int, // 0-100
    strengths: List<String>,
    improvements: List<String>,
    tip: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .glassEffect(GlassStrength.STRONG, RoundedCornerShape(CornerRadius.xl))
            .shadowPreset(ShadowPreset.ELEVATED, RoundedCornerShape(CornerRadius.xl)),
        colors = CardDefaults.cardColors(
            containerColor = GlassEffect.backgroundStrong
        ),
        shape = RoundedCornerShape(CornerRadius.xl)
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.lg)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = PrimaryColors.default,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "AI Аналіз",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextColors.primary
                )
            }

            // Overall score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgress(
                    progress = overallScore / 100f,
                    size = 64,
                    strokeWidth = 6,
                    showPercentage = true
                )

                Column {
                    Text(
                        text = "Загальний результат",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColors.secondary
                    )
                    Text(
                        text = getScoreLabel(overallScore),
                        style = MaterialTheme.typography.titleMedium,
                        color = getScoreColor(overallScore)
                    )
                }
            }

            Divider(color = BorderColors.subtle)

            // Strengths
            if (strengths.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = "Сильні сторони",
                        style = MaterialTheme.typography.titleMedium,
                        color = SemanticColors.success
                    )
                    strengths.forEach { strength ->
                        Text(
                            text = "• $strength",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextColors.primary
                        )
                    }
                }
            }

            // Improvements
            if (improvements.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = "Що покращити",
                        style = MaterialTheme.typography.titleMedium,
                        color = SemanticColors.warning
                    )
                    improvements.forEach { improvement ->
                        Text(
                            text = "• $improvement",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextColors.primary
                        )
                    }
                }
            }

            // Tip
            Surface(
                color = PrimaryColors.default.copy(alpha = 0.1f),
                shape = RoundedCornerShape(CornerRadius.md)
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColors.primary
                    )
                }
            }
        }
    }
}

/**
 * Metric Card - картка з метрикою (дикція, темп, і т.д.)
 */
@Composable
fun MetricCard(
    metricName: String,
    score: Int, // 0-100
    details: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColors.surface
        ),
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metricName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextColors.primary
                )
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.titleLarge,
                    color = getScoreColor(score)
                )
            }

            LinearProgressIndicator(
                progress = score / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = getScoreColor(score),
                trackColor = BackgroundColors.primary.copy(alpha = 0.3f)
            )

            if (details != null) {
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextColors.secondary
                )
            }
        }
    }
}

// Helper functions
private fun getScoreLabel(score: Int): String = when {
    score >= 90 -> "Відмінно"
    score >= 75 -> "Добре"
    score >= 60 -> "Задовільно"
    else -> "Потребує покращення"
}

private fun getScoreColor(score: Int): androidx.compose.ui.graphics.Color = when {
    score >= 75 -> SemanticColors.success
    score >= 50 -> SemanticColors.warning
    else -> SemanticColors.error
}
