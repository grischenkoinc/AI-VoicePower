package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.exercise.StoryFormat
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun StoryFormatCard(
    format: StoryFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = getFormatIcon(format) + " " + getFormatTitle(format),
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = getFormatDescription(format),
            style = AppTypography.bodyMedium,
            color = TextColors.onLightSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getFormatIcon(format: StoryFormat): String {
    return when (format) {
        StoryFormat.WITH_PROMPTS -> "🎯"
        StoryFormat.FROM_IMAGE -> "🖼️"
        StoryFormat.CONTINUE -> "📝"
        StoryFormat.RANDOM_WORDS -> "🎲"
    }
}

private fun getFormatTitle(format: StoryFormat): String {
    return when (format) {
        StoryFormat.WITH_PROMPTS -> "З підказками"
        StoryFormat.FROM_IMAGE -> "За картинкою"
        StoryFormat.CONTINUE -> "Продовж історію"
        StoryFormat.RANDOM_WORDS -> "Випадкові слова"
    }
}

private fun getFormatDescription(format: StoryFormat): String {
    return when (format) {
        StoryFormat.WITH_PROMPTS -> "Створи історію про героя, місце, предмет і твіст"
        StoryFormat.FROM_IMAGE -> "Розкажи історію за згенерованою картинкою"
        StoryFormat.CONTINUE -> "Продовж початок історії своїми словами"
        StoryFormat.RANDOM_WORDS -> "Вплети 3 випадкові слова в свою історію"
    }
}
