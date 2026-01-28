package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.background
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
fun StoryPromptCard(
    format: StoryFormat,
    prompt: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.2f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = getFormatIcon(format) + " " + getFormatTitle(format),
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

        Text(
            text = prompt,
            style = AppTypography.bodyLarge,
            color = TextColors.onLightSecondary,
            fontSize = 15.sp,
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
