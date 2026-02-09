package com.aivoicepower.ui.screens.premium.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.modifiers.glassBackground

/**
 * Premium benefit row item with glass morphism style.
 *
 * Each benefit shows a gradient icon container (with emoji),
 * title + description, and a green checkmark.
 */
@Composable
fun PremiumBenefitItem(
    emoji: String,
    title: String,
    description: String,
    iconBackgroundColors: List<Color>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .glassBackground(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.12f),
                borderColor = Color.White.copy(alpha = 0.2f)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gradient icon container
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = iconBackgroundColors.first().copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(colors = iconBackgroundColors),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
        }

        // Title and description
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                lineHeight = 16.sp
            )
        }

        // Green checkmark
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    Color(0xFF10B981).copy(alpha = 0.2f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
