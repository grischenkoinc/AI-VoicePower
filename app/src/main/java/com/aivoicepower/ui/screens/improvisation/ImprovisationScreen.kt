package com.aivoicepower.ui.screens.improvisation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.data.ads.RewardedAdManager
import com.aivoicepower.ui.screens.improvisation.components.ImprovisationModeCard
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground
import kotlinx.coroutines.launch

@Composable
fun ImprovisationScreen(
    viewModel: ImprovisationViewModel = hiltViewModel(),
    rewardedAdManager: RewardedAdManager? = null,
    onNavigateToRandomTopic: () -> Unit,
    onNavigateToStorytelling: () -> Unit,
    onNavigateToDebate: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToJobInterview: () -> Unit,
    onNavigateToPresentation: () -> Unit,
    onNavigateToNegotiation: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Bottom sheet state
    var showProSheet by remember { mutableStateOf(false) }
    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }

    val isAdLoaded by rewardedAdManager?.isAdLoaded?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(false) }

    // Pro Feature Bottom Sheet
    if (showProSheet) {
        ProFeatureBottomSheet(
            isAdLoaded = isAdLoaded,
            onWatchAd = {
                showProSheet = false
                if (activity != null && rewardedAdManager != null) {
                    rewardedAdManager.showAd(
                        activity = activity,
                        onRewarded = {
                            pendingNavigation?.invoke()
                            pendingNavigation = null
                        },
                        onFailed = { error ->
                            scope.launch { snackbarHostState.showSnackbar(error) }
                        }
                    )
                }
            },
            onPremium = {
                showProSheet = false
                pendingNavigation = null
                onNavigateToPremium()
            },
            onDismiss = {
                showProSheet = false
                pendingNavigation = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        ImprovisationContent(
            state = state,
            viewModel = viewModel,
            onNavigateToRandomTopic = onNavigateToRandomTopic,
            onNavigateToStorytelling = onNavigateToStorytelling,
            onNavigateToDebate = onNavigateToDebate,
            onNavigateToSales = onNavigateToSales,
            onNavigateToChallenge = onNavigateToChallenge,
            onNavigateToJobInterview = onNavigateToJobInterview,
            onNavigateToPresentation = onNavigateToPresentation,
            onNavigateToNegotiation = onNavigateToNegotiation,
            onNavigateToPremium = onNavigateToPremium,
            onShowProSheet = { navigation ->
                pendingNavigation = navigation
                showProSheet = true
            },
            onNavigateBack = onNavigateBack
        )

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF667EEA),
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ImprovisationContent(
    state: ImprovisationState,
    viewModel: ImprovisationViewModel,
    onNavigateToRandomTopic: () -> Unit,
    onNavigateToStorytelling: () -> Unit,
    onNavigateToDebate: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToJobInterview: () -> Unit,
    onNavigateToPresentation: () -> Unit,
    onNavigateToNegotiation: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onShowProSheet: (navigation: () -> Unit) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 60.dp, end = 20.dp, bottom = 130.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        ImprovisationHeader(onNavigateBack = onNavigateBack)

        // Subtitle
        Text(
            text = "Тренуй спонтанне мовлення",
            style = AppTypography.bodyLarge,
            color = TextColors.onDarkSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        // Mode cards
        ImprovisationModeCard(
            emoji = "\uD83C\uDFB2",
            title = "Випадкова тема",
            description = "Готовий говорити про що завгодно?",
            isLocked = false,
            isComingSoon = false,
            onClick = {
                if (viewModel.canStartImprovisation()) {
                    viewModel.onEvent(ImprovisationEvent.RandomTopicClicked)
                    onNavigateToRandomTopic()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\uD83D\uDCD6",
            title = "Розкажи історію",
            description = "Створи захоплюючу розповідь",
            isLocked = false,
            isComingSoon = false,
            onClick = {
                if (viewModel.canStartImprovisation()) {
                    viewModel.onEvent(ImprovisationEvent.StorytellingClicked)
                    onNavigateToStorytelling()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\uD83C\uDFC6",
            title = "Щоденний челендж",
            description = "Унікальне завдання кожен день",
            isLocked = false,
            isComingSoon = false,
            onClick = {
                if (viewModel.canStartImprovisation()) {
                    viewModel.onEvent(ImprovisationEvent.DailyChallengeClicked)
                    onNavigateToChallenge()
                } else {
                    onNavigateToPremium()
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\u2694\uFE0F",
            title = "Дебати з AI",
            description = "Переконуй штучний інтелект",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.DebateClicked)
                    onNavigateToDebate()
                } else {
                    onShowProSheet { onNavigateToDebate() }
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\uD83D\uDCBC",
            title = "Продай товар",
            description = "Презентуй продукт AI-клієнту",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.SalesPitchClicked)
                    onNavigateToSales()
                } else {
                    onShowProSheet { onNavigateToSales() }
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\uD83D\uDCBC",
            title = "Співбесіда",
            description = "Практикуй відповіді на питання HR",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.JobInterviewClicked)
                    onNavigateToJobInterview()
                } else {
                    onShowProSheet { onNavigateToJobInterview() }
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\uD83D\uDCCA",
            title = "Презентація",
            description = "Структура виступу та робота з питаннями",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.PresentationClicked)
                    onNavigateToPresentation()
                } else {
                    onShowProSheet { onNavigateToPresentation() }
                }
            }
        )

        ImprovisationModeCard(
            emoji = "\uD83E\uDD1D",
            title = "Переговори",
            description = "Практикуй аргументацію та компроміси",
            isLocked = !state.isPremium,
            isComingSoon = false,
            onClick = {
                if (state.isPremium) {
                    viewModel.onEvent(ImprovisationEvent.NegotiationClicked)
                    onNavigateToNegotiation()
                } else {
                    onShowProSheet { onNavigateToNegotiation() }
                }
            }
        )

        // Premium prompt (if needed)
        if (!state.isPremium && state.completedToday >= state.dailyLimit) {
            PremiumPromptCard(onNavigateToPremium = onNavigateToPremium)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ImprovisationHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Спонтанне мовлення",
            style = AppTypography.labelMedium,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Імпровізація",
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.8).sp
        )
    }
}

@Composable
private fun PremiumPromptCard(
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFFFBBF24).copy(alpha = 0.3f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFBEB),
                        Color(0xFFFEF3C7)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable { onNavigateToPremium() }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⭐", fontSize = 32.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ліміт вичерпано",
                    style = AppTypography.titleMedium,
                    color = Color(0xFFD97706),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Отримай Premium для необмеженої практики",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProFeatureBottomSheet(
    isAdLoaded: Boolean,
    onWatchAd: () -> Unit,
    onPremium: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD1D5DB))
            )
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {
            // Inner content with blue border
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mic icon (like Paywall)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 12.dp,
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
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = "Розблокуй PRO вправу",
                    color = Color(0xFF1F2937),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle with value proposition
                Text(
                    text = "Ця вправа допоможе тобі вдосконалити\nнавички реального спілкування",
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Premium button — primary CTA (anchoring + urgency)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF667EEA).copy(alpha = 0.4f)
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onPremium() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Отримати PRO",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Все без обмежень \u2022 від \$9.99/міс",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Watch Ad — secondary CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFD1D5DB),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = isAdLoaded) { onWatchAd() }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "\u25B6\uFE0F", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAdLoaded) "Переглянути рекламу \u2022 1 раз" else "Реклама завантажується...",
                            color = if (isAdLoaded) Color(0xFF374151) else Color(0xFF9CA3AF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Social proof
                Text(
                    text = "\uD83D\uDD25 4,200+ користувачів вже обрали PRO",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
