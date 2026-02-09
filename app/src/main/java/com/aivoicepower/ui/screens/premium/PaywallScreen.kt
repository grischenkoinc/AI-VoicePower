package com.aivoicepower.ui.screens.premium

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import com.aivoicepower.ui.theme.modifiers.glassBackground
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

    // Handle purchase success
    LaunchedEffect(state.isPremium) {
        if (state.isPremium) {
            onPurchaseSuccess()
        }
    }

    // Show error snackbar
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

    // Crown pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "paywall_anim")
    val crownScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crown_scale"
    )

    // Glow animation for selected card
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    GradientBackground(content = {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 180.dp) // Space for bottom bar
            ) {
                // Top bar with back button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Glass back button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                spotColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .glassBackground(
                                shape = CircleShape,
                                backgroundColor = Color.White.copy(alpha = 0.15f),
                                borderColor = Color.White.copy(alpha = 0.3f)
                            )
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

                Spacer(modifier = Modifier.height(8.dp))

                // Hero Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Crown hero icon with glow
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        // Glow circle behind
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFBBF24).copy(alpha = 0.3f),
                                            Color(0xFFFBBF24).copy(alpha = 0.0f)
                                        )
                                    ),
                                    CircleShape
                                )
                        )
                        Text(
                            text = "\uD83D\uDC51",
                            fontSize = 72.sp,
                            modifier = Modifier.scale(crownScale)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AI VoicePower PRO",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Розблокуй повний потенціал",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )

                    // Source-specific message
                    if (state.source != PaywallSource.UNKNOWN && state.source != PaywallSource.SETTINGS) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .glassBackground(
                                    shape = RoundedCornerShape(12.dp),
                                    backgroundColor = Color(0xFFFBBF24).copy(alpha = 0.15f),
                                    borderColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = getPaywallMessage(state.source),
                                fontSize = 13.sp,
                                color = Color(0xFFFBBF24),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

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
                        description = "Повний доступ до 6 курсів (126 уроків)",
                        iconBackgroundColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83C\uDFA4",
                        title = "Імпровізація без лімітів",
                        description = "Тренуйте спонтанне мовлення безмежно",
                        iconBackgroundColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83E\uDD16",
                        title = "AI Тренер",
                        description = "Необмежена кількість повідомлень з AI",
                        iconBackgroundColors = listOf(Color(0xFF10B981), Color(0xFF059669))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83D\uDCCA",
                        title = "Детальна аналітика",
                        description = "Повна статистика та порівняння до/після",
                        iconBackgroundColors = listOf(Color(0xFFA78BFA), Color(0xFF7C3AED))
                    )

                    PremiumBenefitItem(
                        emoji = "\uD83D\uDEAB",
                        title = "Без реклами",
                        description = "Ніяких відволікань під час тренувань",
                        iconBackgroundColors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Pricing Cards Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PricingPlan.entries.forEach { plan ->
                        PricingCard(
                            plan = plan,
                            isSelected = state.selectedPlan == plan,
                            glowAlpha = glowAlpha,
                            onSelect = { viewModel.onEvent(PaywallEvent.SelectPlan(plan)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Feature comparison
                FeatureComparisonCard()

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Frosted bottom bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF764BA2).copy(alpha = 0.0f),
                                Color(0xFF764BA2).copy(alpha = 0.85f),
                                Color(0xFF764BA2).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 8.dp)
            ) {
                // Purchase button
                val purchaseInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
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
                        Text(
                            text = "Отримати PRO  \u2022  ${state.selectedPlan.price}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Terms text
                Text(
                    text = "Підписка автоматично поновлюється. Скасуйте в будь-який момент у Google Play.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Restore + Terms row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Відновити покупки",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.onEvent(PaywallEvent.RestorePurchases) }
                    )

                    Text(
                        text = "  \u2022  ",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )

                    Text(
                        text = "Умови",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f),
                        textDecoration = TextDecoration.Underline
                    )

                    Text(
                        text = "  \u2022  ",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )

                    Text(
                        text = "Конфіденційність",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f),
                        textDecoration = TextDecoration.Underline
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
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
