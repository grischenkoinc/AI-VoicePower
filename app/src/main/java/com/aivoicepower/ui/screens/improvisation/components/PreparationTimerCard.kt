package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun PreparationTimerCard(
    timeLeft: Int,
    onGenerateNew: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⏱️ Час підготовки:",
            style = AppTypography.titleMedium,
            color = TextColors.onLightPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "00:%02d".format(timeLeft),
            style = AppTypography.displayLarge,
            color = Color(0xFF6366F1),
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )

        onGenerateNew?.let { callback ->
            OutlinedButton(
                onClick = callback,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6366F1)
                )
            ) {
                Text(
                    text = "🔄 Інша тема",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
