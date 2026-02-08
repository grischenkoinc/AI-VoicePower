package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors

@Composable
fun AchievementBadge(
    achievement: Achievement,
    isLarge: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isUnlocked = achievement.isUnlocked

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isUnlocked) 16.dp else 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isUnlocked) Color(0xFF6366F1).copy(alpha = 0.25f)
                else Color.Black.copy(alpha = 0.06f)
            )
            .background(
                color = if (isUnlocked) Color.White else Color(0xFFF9FAFB),
                shape = RoundedCornerShape(20.dp)
            )
            .then(if (!isUnlocked) Modifier.alpha(0.7f) else Modifier)
            .padding(if (isLarge) 20.dp else 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Icon with glow
        Box(
            modifier = Modifier
                .size(if (isLarge) 64.dp else 48.dp)
                .then(
                    if (isUnlocked) Modifier.shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFFFFD700).copy(alpha = 0.3f)
                    ) else Modifier
                )
                .background(
                    if (isUnlocked) Brush.radialGradient(
                        colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                    ) else Brush.radialGradient(
                        colors = listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement.icon,
                fontSize = if (isLarge) 32.sp else 24.sp
            )
        }

        // Title
        Text(
            text = achievement.title,
            style = if (isLarge) AppTypography.titleMedium else AppTypography.bodyMedium,
            color = if (isUnlocked) TextColors.onLightPrimary else TextColors.onLightMuted,
            fontSize = if (isLarge) 15.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Description for large
        if (isLarge) {
            Text(
                text = achievement.description,
                style = AppTypography.bodySmall,
                color = TextColors.onLightSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }

        // Progress bar for locked achievements with progress
        if (!isUnlocked && achievement.progress != null && achievement.target != null && achievement.target > 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "${achievement.progress}/${achievement.target}",
                    style = AppTypography.labelSmall,
                    color = TextColors.onLightMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LinearProgressIndicator(
                    progress = { (achievement.progress.toFloat() / achievement.target).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFE5E7EB)
                )
            }
        }

        // Unlocked badge
        if (isUnlocked && isLarge) {
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF10B981).copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Відкрито ✓",
                    style = AppTypography.labelSmall,
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
