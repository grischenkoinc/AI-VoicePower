package com.aivoicepower.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                emoji = "🎤",
                title = "Розвивай голос",
                description = "Тренуй дикцію, артикуляцію та інтонацію з AI-наставником"
            ),
            OnboardingPage(
                emoji = "⚡",
                title = "21-денні курси",
                description = "Структуровані програми від основ до харизматичних виступів"
            ),
            OnboardingPage(
                emoji = "🎯",
                title = "Персональний коучинг",
                description = "AI аналізує твоє мовлення та дає поради для покращення"
            ),
            OnboardingPage(
                emoji = "🚀",
                title = "Почни зараз",
                description = "Перша діагностика — безкоштовно. Дізнайся свій рівень!"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    GradientBackground(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Pager з слайдами
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Page indicators
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { index ->
                    PageIndicator(
                        isActive = index == pagerState.currentPage
                    )
                }
            }

            // Navigation buttons
            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skip button
                    NavButton(
                        text = "Пропустити",
                        icon = "→",
                        isPrimary = false,
                        onClick = onComplete,
                        modifier = Modifier.weight(1f)
                    )

                    // Next button
                    NavButton(
                        text = "Далі",
                        icon = "→",
                        isPrimary = true,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Start button (остання сторінка)
                NavButton(
                    text = "Почати",
                    icon = "🚀",
                    isPrimary = true,
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )
            }
        }
    })
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Emoji illustration
        Text(
            text = page.emoji,
            fontSize = 120.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = page.title,
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            textAlign = TextAlign.Center,
            fontSize = 42.sp,
            lineHeight = 48.sp
        )

        // Description
        Text(
            text = page.description,
            style = AppTypography.bodyLarge,
            color = TextColors.onDarkSecondary,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun PageIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val width by animateDpAsState(
        targetValue = if (isActive) 32.dp else 8.dp,
        animationSpec = tween(300),
        label = "width"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(8.dp)
            .background(
                color = if (isActive)
                    androidx.compose.ui.graphics.Color.White
                else
                    androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
    )
}
