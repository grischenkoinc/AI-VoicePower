package com.aivoicepower.ui.screens.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.ui.theme.AppTypography
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CelebrationOverlay(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showDescription by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(achievement) {
        delay(200)
        showContent = true
        delay(400)
        showTitle = true
        delay(300)
        showDescription = true
        delay(300)
        showButton = true
    }

    // Confetti particles
    val particles = remember {
        List(40) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                speedX = (Random.nextFloat() - 0.5f) * 2f,
                speedY = Random.nextFloat() * 3f + 1f,
                size = Random.nextFloat() * 8f + 4f,
                color = listOf(
                    Color(0xFFFFD700), Color(0xFF6366F1), Color(0xFF10B981),
                    Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF8B5CF6),
                    Color(0xFF3B82F6), Color(0xFFEF4444)
                ).random(),
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Icon pulse animation
    val iconScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* consume clicks */ },
        contentAlignment = Alignment.Center
    ) {
        // Confetti canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { particle ->
                val adjustedY = ((particle.y + particle.speedY * time) % 1.3f)
                val adjustedX = particle.x + particle.speedX * time * 0.1f

                drawCircle(
                    color = particle.color.copy(alpha = (1f - adjustedY).coerceIn(0f, 0.8f)),
                    radius = particle.size,
                    center = Offset(
                        x = adjustedX * size.width,
                        y = adjustedY * size.height
                    )
                )
            }
        }

        // Achievement card
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .shadow(
                    elevation = 32.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0xFF6366F1).copy(alpha = 0.4f)
                )
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Glow circle + icon
            Box(
                modifier = Modifier
                    .scale(iconScale)
                    .size(100.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFEF3C7),
                                Color(0xFFFDE68A)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 48.sp
                )
            }

            // "Нове досягнення!" label
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(400)) + scaleIn(tween(400))
            ) {
                Text(
                    text = "Нове досягнення!",
                    style = AppTypography.labelMedium,
                    color = Color(0xFF6366F1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            // Title
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(400))
            ) {
                Text(
                    text = achievement.title,
                    style = AppTypography.headlineMedium,
                    color = Color(0xFF1F2937),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }

            // Description
            AnimatedVisibility(
                visible = showDescription,
                enter = fadeIn(tween(400))
            ) {
                Text(
                    text = achievement.description,
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF6B7280),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Button
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(400)) + scaleIn(tween(400))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF6366F1).copy(alpha = 0.3f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onDismiss() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Чудово!",
                        style = AppTypography.titleMedium,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val size: Float,
    val color: Color,
    val rotation: Float
)
