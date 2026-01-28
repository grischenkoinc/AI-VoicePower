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
import com.aivoicepower.domain.model.ImprovisationTopic
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun TopicDisplayCard(
    topic: ImprovisationTopic,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFFF59E0B).copy(alpha = 0.25f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🎲 Твоя тема:",
            style = AppTypography.titleSmall,
            color = TextColors.onLightSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = topic.title,
            style = AppTypography.headlineSmall,
            color = TextColors.onLightPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )

        if (topic.hints.isNotEmpty()) {
            Text(
                text = "💡 Підказки:",
                style = AppTypography.titleSmall,
                color = TextColors.onLightSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            topic.hints.forEach { hint ->
                Text(
                    text = "• $hint",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
