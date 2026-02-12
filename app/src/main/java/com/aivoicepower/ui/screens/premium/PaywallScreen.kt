package com.aivoicepower.ui.screens.premium

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.premium.components.*
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
    source: PaywallSource = PaywallSource.UNKNOWN,
    onNavigateBack: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(source) {
        viewModel.setSource(source)
    }

    LaunchedEffect(state.isPremium) {
        if (state.isPremium) {
            onPurchaseSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            delay(100)
            viewModel.onEvent(PaywallEvent.ClearError)
        }
    }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "paywall_anim")

    val heroScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "ring_rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    GradientBackground(content = {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 180.dp)
            ) {
                // Top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                spotColor = Color.Black.copy(alpha = 0.06f)
                            )
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Section — Premium Mic Icon
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Upgraded microphone hero
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(140.dp)
                            .scale(heroScale)
                    ) {
                        // Outer glow ring (rotating gradient)
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .rotate(ringRotation)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            Color(0xFF667EEA).copy(alpha = glowAlpha),
                                            Color(0xFFA78BFA).copy(alpha = 0.05f),
                                            Color(0xFFFBBF24).copy(alpha = glowAlpha),
                                            Color(0xFF764BA2).copy(alpha = 0.05f),
                                            Color(0xFF667EEA).copy(alpha = glowAlpha)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        // Glow background
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF667EEA).copy(alpha = 0.2f),
                                            Color(0xFF764BA2).copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                        )

                        // Main gradient circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(
                                    elevation = 20.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                                )
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF667EEA),
                                            Color(0xFF764BA2),
                                            Color(0xFF9333EA)
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner mic icon
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // PRO badge — bottom right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-6).dp, y = (-6).dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(10.dp),
                                    spotColor = Color(0xFFFBBF24).copy(alpha = 0.4f)
                                )
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFBBF24),
                                            Color(0xFFF59E0B)
                                        )
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1F2937),
                                letterSpacing = 1.sp
                            )
                        }

                        // Sparkle — top left
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24).copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopStart)
                                .offset(x = 8.dp, y = 12.dp)
                        )

                        // Sparkle — top right
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFA78BFA).copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-12).dp, y = 18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "AI VoicePower PRO",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Розблокуй повний потенціал\nсвого голосу",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    // Source-specific message
                    if (state.source != PaywallSource.UNKNOWN && state.source != PaywallSource.SETTINGS) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = getPaywallMessage(state.source),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Pricing Plans
                PricingBlock(
                    selectedPlan = state.selectedPlan,
                    onSelectPlan = { viewModel.onEvent(PaywallEvent.SelectPlan(it)) }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Benefits Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumBenefitItem(
                        emoji = "\uD83C\uDFAF",
                        title = "Всі курси",
                        description = "Повний доступ до всіх уроків",
                        iconBackgroundColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83C\uDFA4",
                        title = "Імпровізація без лімітів",
                        description = "Тренуйте спонтанне мовлення безмежно",
                        iconBackgroundColors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE047))
                    )

                    PremiumBenefitItem(
                        emoji = "✨",
                        title = "AI Тренер",
                        description = "Необмежене спілкування з АІ Тренером",
                        iconBackgroundColors = listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83D\uDCCA",
                        title = "Детальна аналітика",
                        description = "Повна статистика та порівняння до/після",
                        iconBackgroundColors = listOf(Color(0xFFBBF7D0), Color(0xFF86EFAC))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83D\uDEAB",
                        title = "Без реклами",
                        description = "Ніяких відволікань під час тренувань",
                        iconBackgroundColors = listOf(Color.White, Color.White),
                        borderColor = Color(0xFF3B82F6)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Feature comparison
                FeatureComparisonCard()

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF764BA2).copy(alpha = 0.0f),
                                Color(0xFF764BA2).copy(alpha = 0.85f),
                                Color(0xFF764BA2).copy(alpha = 0.98f)
                            )
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 8.dp)
            ) {
                // Purchase button
                val purchaseInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.5f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = purchaseInteractionSource,
                            indication = null,
                            enabled = !state.isLoading
                        ) {
                            activity?.let { viewModel.launchPurchase(it) }
                                ?: viewModel.onEvent(PaywallEvent.PurchaseClicked)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(26.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Отримати PRO  \u2022  ${state.selectedPlan.price}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Підписка автоматично поновлюється. Скасуйте в будь-який момент у Google Play.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Snackbar host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 200.dp)
            )
        }
    })
}

private fun getPaywallMessage(source: PaywallSource): String {
    return when (source) {
        PaywallSource.COURSE_LOCKED ->
            "Перші 7 уроків безкоштовні. Оформіть Преміум для доступу до всіх уроків!"
        PaywallSource.IMPROV_LIMIT ->
            "Ви досягли денного ліміту (3 сесії). Преміум дає необмежений доступ!"
        PaywallSource.AI_COACH_LIMIT ->
            "Ви використали 10 повідомлень сьогодні. Преміум знімає всі обмеження!"
        PaywallSource.DIAGNOSTIC_LIMIT ->
            "Повторна діагностика доступна тільки у Преміум версії"
        PaywallSource.ANALYSIS_LIMIT ->
            "Ви використали всі безкоштовні аналізи на сьогодні. Преміум дає необмежений AI аналіз!"
        PaywallSource.IMPROV_ANALYSIS_LIMIT ->
            "Безкоштовний аналіз імпровізації вичерпано. Преміум знімає всі обмеження!"
        PaywallSource.SETTINGS ->
            "Отримайте повний доступ до всіх функцій"
        PaywallSource.ACHIEVEMENT ->
            "Розблокуйте всі досягнення з Преміум"
        PaywallSource.UNKNOWN ->
            "Отримайте повний доступ до всіх функцій"
    }
}

// Extension to find activity
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
