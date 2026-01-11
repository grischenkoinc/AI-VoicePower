package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Top Status Components v2.0
 *
 * Джерело: Design_Example_react.md
 */

/**
 * Top Status Row
 * CSS: .top-status
 *
 * Верхній рядок з прогресом та lesson badge
 */
@Composable
fun TopStatusRow(
    currentStep: Int,
    totalSteps: Int,
    lessonNumber: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress circle (left)
        GlassCircle(
            text = "$currentStep/$totalSteps",
            size = 50
        )

        // Lesson badge (right)
        LessonBadge(lessonNumber = lessonNumber)
    }
}
