package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.modifiers.*

/**
 * Приклади використання 3D Shadow System
 * Для тестування та демонстрації
 */

@Composable
fun Shadow3DExamples(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "3D Shadow Examples",
            style = AppTypography.titleLarge,
            color = TextColors.onLightPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Multi-layer shadow card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .multiLayerShadow(
                    elevation = 12.dp,
                    spotColor = Color.Black
                )
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Multi-Layer Shadow Card",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary
            )
        }

        // Glow progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(12.dp)
                .glowShadow(
                    glowRadius = 16.dp,
                    glowColor = Color(0xFF22C55E),
                    intensity = 0.7f
                )
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                    ),
                    RoundedCornerShape(6.dp)
                )
        )

        // Inset shadow (depressed)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .insetShadow(
                    depth = 4.dp,
                    color = Color.Black.copy(alpha = 0.15f)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Inset Shadow (Depressed)",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary
            )
        }

        // Elevated FAB
        Box(
            modifier = Modifier
                .size(64.dp)
                .elevatedShadow(
                    elevation = 24.dp,
                    color = Color(0xFF667EEA).copy(alpha = 0.4f)
                )
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🚀", fontSize = 28.sp)
        }
    }
}
