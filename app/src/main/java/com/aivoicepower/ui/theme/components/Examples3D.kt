package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.modifiers.*
import kotlinx.coroutines.launch

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

@Composable
fun GlassmorphismExamples(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Glassmorphism Examples",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Glass Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .glassBackground(
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Standard Glass",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary
            )
        }

        // Frost Glass
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .frostGlass()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Frost Glass",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary
            )
        }

        // Light Glass
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .lightGlass()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Light Glass",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary
            )
        }

        // Tinted Glass (yellow)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .tintedGlass(tintColor = Color(0xFFFBBF24))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tinted Glass (Yellow)",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary
            )
        }

        // Gradient Glass
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .gradientGlass(
                    gradient = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            Color(0x40FBBF24),
                            Color(0x26F59E0B)
                        )
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Gradient Glass",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary
            )
        }

        // Dark Glass
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .darkGlass()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Dark Glass",
                style = AppTypography.bodyMedium,
                color = TextColors.onDarkPrimary
            )
        }
    }
}

@Composable
fun AnimationExamples(
    modifier: Modifier = Modifier
) {
    var shimmerEnabled by remember { mutableStateOf(true) }
    var bounceEnabled by remember { mutableStateOf(false) }
    val shakeController = remember { ShakeController() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Animation Examples",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                .then(if (shimmerEnabled) Modifier.shimmerEffect() else Modifier)
                .clickable { shimmerEnabled = !shimmerEnabled }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Shimmer Effect (tap to toggle)",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightPrimary
            )
        }

        // Pulse
        Box(
            modifier = Modifier
                .size(80.dp)
                .pulseAnimation()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💫", fontSize = 32.sp)
        }

        // Shake
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shakeAnimation(shakeController)
                .background(Color(0xFFEF4444), RoundedCornerShape(12.dp))
                .clickable {
                    scope.launch {
                        shakeController.shake()
                    }
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Shake on Error (tap me!)",
                style = AppTypography.bodyMedium,
                color = Color.White
            )
        }

        // Scale on Press
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .scaleOnPress()
                .background(Color(0xFF10B981), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Scale on Press (press me!)",
                style = AppTypography.bodyMedium,
                color = Color.White
            )
        }

        // Bounce
        Box(
            modifier = Modifier
                .size(60.dp)
                .bounceAnimation(enabled = bounceEnabled)
                .background(Color(0xFFFBBF24), CircleShape)
                .clickable { bounceEnabled = !bounceEnabled },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚡", fontSize = 28.sp)
        }

        // Glow Pulse
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(12.dp)
                .glowPulse(color = Color(0xFF22C55E))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                    ),
                    RoundedCornerShape(6.dp)
                )
        )
    }
}
