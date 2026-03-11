package com.aivoicepower.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ReportAiContentDialog(
    onDismiss: () -> Unit,
    onReport: (reason: String) -> Unit
) {
    val context = LocalContext.current
    var selectedReason by remember { mutableStateOf<String?>(null) }

    val reasons = listOf(
        "Образливий контент",
        "Неточна інформація",
        "Невідповідний контент",
        "Інше"
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E2E), RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Поскаржитись на AI-контент",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Оберіть причину скарги:",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            reasons.forEach { reason ->
                val isSelected = selectedReason == reason
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Color(0xFF667EEA).copy(alpha = 0.2f)
                            else Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF667EEA)
                            else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedReason = reason }
                        .padding(14.dp)
                ) {
                    Text(
                        text = reason,
                        color = if (isSelected) Color(0xFF667EEA) else Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Скасувати",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Submit
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedReason != null)
                                Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))
                            else
                                Brush.linearGradient(listOf(Color(0xFF4B5563), Color(0xFF4B5563))),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = selectedReason != null) {
                            selectedReason?.let {
                                onReport(it)
                                Toast
                                    .makeText(context, "Дякуємо за скаргу", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Надіслати",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
