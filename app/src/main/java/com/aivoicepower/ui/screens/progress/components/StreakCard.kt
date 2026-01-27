package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🔥",
                fontSize = 32.sp
            )
            Text(
                text = "$currentStreak",
                style = AppTypography.headlineMedium,
                color = Color(0xFF6366F1),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "днiв поспiль",
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .height(60.dp)
                .width(1.dp)
                .background(Color(0xFFE5E7EB))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🏆",
                fontSize = 32.sp
            )
            Text(
                text = "$longestStreak",
                style = AppTypography.headlineMedium,
                color = Color(0xFFFBBF24),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "рекорд",
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
