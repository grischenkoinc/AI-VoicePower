package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*

/**
 * AI VoicePower Tag & Badge Components v2.0
 *
 * Джерело: Design_Example_react.md
 */

/**
 * Section Tag (📖 Теорія, 🔥 Практика)
 * CSS: .section-tag
 *
 * Gradient bg, uppercase, с emoji
 */
@Composable
fun SectionTag(
    emoji: String,
    text: String,
    modifier: Modifier = Modifier,
    isPractice: Boolean = false
) {
    val gradient = if (isPractice) Gradients.tagSecondary else Gradients.tagPrimary
    val shadowColor = if (isPractice) Elevation.Tag.secondaryColor else Elevation.Tag.primaryColor

    Row(
        modifier = modifier
            .shadow(
                elevation = Elevation.Tag.elevation,
                shape = RoundedCornerShape(20.dp),
                spotColor = shadowColor
            )
            .background(gradient, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            fontSize = 14.sp
        )
        Text(
            text = text.uppercase(),
            style = AppTypography.labelSmall,
            color = TextColors.onPrimary,
            letterSpacing = 1.2.sp
        )
    }
}

/**
 * Level Pill (⚡ Рівень 3)
 * CSS: .level-pill
 *
 * Yellow-orange gradient, темний текст
 */
@Composable
fun LevelPill(
    emoji: String,
    level: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = Elevation.Tag.elevation,
                shape = RoundedCornerShape(25.dp),
                spotColor = Elevation.Tag.secondaryColor
            )
            .background(Gradients.levelPill, RoundedCornerShape(25.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            fontSize = 16.sp
        )
        Text(
            text = "Рівень $level",
            style = AppTypography.labelMedium,
            color = TextColors.onSecondary,
            fontSize = 13.sp
        )
    }
}

/**
 * Lesson Badge (Урок 1)
 * CSS: .lesson-badge
 *
 * Glass effect, white text
 */
@Composable
fun LessonBadge(
    lessonNumber: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(GlassColors.background, RoundedCornerShape(25.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "УРОК $lessonNumber",
            style = AppTypography.labelSmall,
            color = TextColors.onDarkPrimary,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Step Counter (Крок 1 з 4)
 * CSS: .step-counter
 *
 * Glass bg, bold white text
 */
@Composable
fun StepCounter(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(GlassColors.background, RoundedCornerShape(10.dp))
            .padding(horizontal = 13.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Крок $current з $total",
            style = AppTypography.labelMedium,
            color = TextColors.onDarkPrimary,
            fontSize = 13.sp,
            letterSpacing = 0.3.sp
        )
    }
}
