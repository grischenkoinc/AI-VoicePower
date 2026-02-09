package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*

/**
 * AI VoicePower Exercise Visual Components v2.0
 *
 * Джерело: Design_Example_react.md
 * Візуалізація вправ (emoji + інструкції)
 */

/**
 * Exercise Visual Container
 * CSS: .exercise-visual
 *
 * Контейнер для візуалізації вправи
 */
@Composable
fun ExerciseVisual(
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

/**
 * Visual Row (emoji → emoji)
 * CSS: .visual-row
 *
 * Візуалізація переходу між станами
 */
@Composable
fun VisualRow(
    items: List<VisualItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            VisualItemComponent(
                emoji = item.emoji,
                label = item.label,
                time = item.time
            )

            // Arrow між елементами (окрім останнього)
            if (index < items.size - 1) {
                VisualArrow()
            }
        }
    }
}

data class VisualItem(
    val emoji: String,
    val label: String,
    val time: String
)

/**
 * Visual Item (окремий елемент)
 * CSS: .visual-item
 */
@Composable
private fun VisualItemComponent(
    emoji: String,
    label: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Emoji
        Text(
            text = emoji,
            fontSize = 48.sp
        )

        // Label
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        // Time badge
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = time,
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Visual Arrow
 * CSS: .visual-arrow
 */
@Composable
private fun VisualArrow(
    modifier: Modifier = Modifier
) {
    Text(
        text = "→",
        fontSize = 24.sp,
        color = TextColors.muted,
        modifier = modifier.padding(horizontal = 8.dp)
    )
}

/**
 * Visual Divider
 * CSS: .visual-divider
 */
@Composable
fun VisualDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = Color(0xFFE5E7EB)
    )
}

/**
 * Repeat Row (🔄 Повтори X разів)
 * CSS: .repeat-row
 */
@Composable
fun RepeatRow(
    repetitions: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF3F4F6),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔄",
                fontSize = 20.sp
            )
        }

        // Text
        Column {
            Text(
                text = "Повтори",
                style = AppTypography.bodySmall,
                color = TextColors.muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$repetitions разів",
                style = AppTypography.titleMedium,
                color = TextColors.onLightPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
