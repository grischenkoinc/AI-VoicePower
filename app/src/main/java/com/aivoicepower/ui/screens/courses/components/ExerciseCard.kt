package com.aivoicepower.ui.screens.courses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.screens.courses.ExerciseState
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun ExerciseCard(
    exerciseState: ExerciseState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Instruction Box
        if (exerciseState.exercise.instruction.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                    )
                    .background(Color(0xFFFFFBEB), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "💡", fontSize = 20.sp)
                    Text(
                        text = "Інструкція",
                        style = AppTypography.titleSmall,
                        color = Color(0xFF92400E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = exerciseState.exercise.instruction,
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Exercise Content
        ExerciseContentDisplay(
            content = exerciseState.exercise.content
        )

        // Duration
        if (exerciseState.exercise.durationSeconds > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⏱️", fontSize = 18.sp)
                Text(
                    text = "Тривалість: ${exerciseState.exercise.durationSeconds} сек",
                    style = AppTypography.bodySmall,
                    color = TextColors.onLightSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
