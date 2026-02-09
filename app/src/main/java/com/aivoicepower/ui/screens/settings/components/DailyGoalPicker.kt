package com.aivoicepower.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun DailyGoalPicker(
    currentGoalMinutes: Int,
    onGoalSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val goalOptions = listOf(15, 30, 45, 60)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Text(
                text = "Щоденна мета",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Виберіть скільки хвилин на день ви хочете тренуватися",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            goalOptions.forEach { minutes ->
                val isSelected = minutes == currentGoalMinutes

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Color(0xFF667EEA).copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                        .clickable { onGoalSelected(minutes) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$minutes хвилин",
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF667EEA) else Color(0xFF1A1A2E),
                        modifier = Modifier.weight(1f)
                    )

                    if (isSelected) {
                        Text(
                            text = "✓",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )
                    }
                }

                if (minutes != goalOptions.last()) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cancel button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6))
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Скасувати",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
