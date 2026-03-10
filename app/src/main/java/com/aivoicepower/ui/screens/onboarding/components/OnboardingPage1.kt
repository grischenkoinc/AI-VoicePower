package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.modifiers.pulseAnimation

@Composable
fun OnboardingPage1(
    onNextClick: () -> Unit
) {
    // Trigger all animations
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // === MICROPHONE: drops from above with big bouncy spring ===
    val emojiScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessLow
        ),
        label = "emojiScale"
    )
    val emojiOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else -200f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "emojiDrop"
    )

    // === TITLE: scale-in from large with bounce ===
    val titleScale by animateFloatAsState(
        targetValue = if (visible) 1f else 1.8f,
        animationSpec = tween(700, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "titleScale"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )

    // === SUBTITLE: fade + slide up ===
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 650, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )
    val subtitleOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = tween(500, delayMillis = 650, easing = FastOutSlowInEasing),
        label = "subtitleOffset"
    )

    // === CARD: slides up + fades in ===
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 850, easing = FastOutSlowInEasing),
        label = "cardAlpha"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = tween(600, delayMillis = 850, easing = FastOutSlowInEasing),
        label = "cardOffset"
    )

    // === FEATURE ITEMS: each slides from left with stagger ===
    val feature1Alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1100, easing = FastOutSlowInEasing),
        label = "f1Alpha"
    )
    val feature1OffsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(400, delayMillis = 1100, easing = FastOutSlowInEasing),
        label = "f1Offset"
    )
    val feature2Alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1250, easing = FastOutSlowInEasing),
        label = "f2Alpha"
    )
    val feature2OffsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(400, delayMillis = 1250, easing = FastOutSlowInEasing),
        label = "f2Offset"
    )
    val feature3Alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1400, easing = FastOutSlowInEasing),
        label = "f3Alpha"
    )
    val feature3OffsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(400, delayMillis = 1400, easing = FastOutSlowInEasing),
        label = "f3Offset"
    )
    val feature4Alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1550, easing = FastOutSlowInEasing),
        label = "f4Alpha"
    )
    val feature4OffsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(400, delayMillis = 1550, easing = FastOutSlowInEasing),
        label = "f4Offset"
    )
    val feature5Alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1700, easing = FastOutSlowInEasing),
        label = "f5Alpha"
    )
    val feature5OffsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -60f,
        animationSpec = tween(400, delayMillis = 1700, easing = FastOutSlowInEasing),
        label = "f5Offset"
    )

    // === BUTTON: bouncy scale-in ===
    val buttonScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "buttonScale"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 1900, easing = FastOutSlowInEasing),
        label = "buttonAlpha"
    )

    // === BREATHING GLOW behind microphone ===
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    // Gentle float for microphone after landing
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(top = 32.dp, bottom = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with glowing orb + microphone
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Microphone with glow orb
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Breathing glow
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .graphicsLayer {
                                scaleX = glowScale
                                scaleY = glowScale
                                alpha = glowAlpha * emojiScale
                            }
                            .shadow(
                                elevation = 30.dp,
                                shape = CircleShape,
                                spotColor = Color(0xFF667EEA)
                            )
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF667EEA).copy(alpha = 0.4f),
                                        Color(0xFF764BA2).copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // Microphone emoji - drops in with bounce
                    Text(
                        text = "\uD83C\uDFA4",
                        fontSize = 72.sp,
                        modifier = Modifier.graphicsLayer {
                            scaleX = emojiScale
                            scaleY = emojiScale
                            translationY = emojiOffsetY - floatOffset
                        }
                    )
                }

                // Title - scale-in from large
                Text(
                    text = "Diqto",
                    style = AppTypography.displayLarge,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = titleAlpha
                        scaleX = titleScale
                        scaleY = titleScale
                    }
                )

                // Subtitle - fade + slide
                Text(
                    text = "Твій голос — твоя сила",
                    style = AppTypography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.graphicsLayer {
                        alpha = subtitleAlpha
                        translationY = subtitleOffsetY
                    }
                )
            }

            // Features Card — slides up
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = cardAlpha
                        translationY = cardOffsetY
                    }
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Покращ свою дикцію, інтонацію та впевненість у мовленні з AI-тренером",
                    style = AppTypography.bodyMedium,
                    color = TextColors.onLightPrimary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Each feature slides in from the left, one by one
                FeatureItem(
                    icon = Icons.Default.Assessment,
                    text = "Персоналізована діагностика",
                    modifier = Modifier.graphicsLayer {
                        alpha = feature1Alpha
                        translationX = feature1OffsetX
                    }
                )

                FeatureItem(
                    icon = Icons.Default.FitnessCenter,
                    text = "Щоденні розминки для голосу",
                    modifier = Modifier.graphicsLayer {
                        alpha = feature2Alpha
                        translationX = feature2OffsetX
                    }
                )

                FeatureItem(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    text = "Тематичні курси",
                    modifier = Modifier.graphicsLayer {
                        alpha = feature3Alpha
                        translationX = feature3OffsetX
                    }
                )

                FeatureItem(
                    icon = Icons.Default.Assistant,
                    text = "AI-тренер для персональних порад",
                    modifier = Modifier.graphicsLayer {
                        alpha = feature4Alpha
                        translationX = feature4OffsetX
                    }
                )

                FeatureItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    text = "Відстеження прогресу",
                    modifier = Modifier.graphicsLayer {
                        alpha = feature5Alpha
                        translationX = feature5OffsetX
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Button - bouncy scale-in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.graphicsLayer {
                    alpha = buttonAlpha
                    scaleX = buttonScale
                    scaleY = buttonScale
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .pulseAnimation(scaleFrom = 1f, scaleTo = 1.03f, duration = 2000)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF667EEA).copy(alpha = 0.4f))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onNextClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Почати \u2192",
                        style = AppTypography.titleMedium,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                PageIndicator(currentPage = 0, totalPages = 4)
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF667EEA),
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = TextColors.onLightPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
