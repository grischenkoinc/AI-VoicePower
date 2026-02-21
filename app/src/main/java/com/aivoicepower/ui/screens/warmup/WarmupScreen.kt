package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.*
import com.aivoicepower.ui.theme.*
import com.aivoicepower.ui.theme.components.GradientBackground
import com.aivoicepower.ui.theme.modifiers.staggeredEntry

@Composable
fun WarmupScreen(
    viewModel: WarmupViewModel = hiltViewModel(),
    onNavigateToArticulation: () -> Unit,
    onNavigateToBreathing: () -> Unit,
    onNavigateToVoice: () -> Unit,
    onNavigateToQuick: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(content = {})

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.error ?: "Помилка",
                        color = TextColors.onDarkPrimary,
                        style = AppTypography.bodyLarge
                    )
                }
            }

            else -> {
                WarmupContent(
                    state = state,
                    onCategoryClick = { categoryId ->
                        when (categoryId) {
                            "articulation" -> onNavigateToArticulation()
                            "breathing" -> onNavigateToBreathing()
                            "voice" -> onNavigateToVoice()
                        }
                    },
                    onQuickWarmupClick = onNavigateToQuick,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun WarmupContent(
    state: WarmupState,
    onCategoryClick: (String) -> Unit,
    onQuickWarmupClick: () -> Unit,
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
        WarmupHeader(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.staggeredEntry(index = 0)
        )

        // Quick Warmup Section
        Text(
            text = "Швидка розминка",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.staggeredEntry(index = 1)
        )

        QuickWarmupCard(
            onClick = onQuickWarmupClick,
            modifier = Modifier.staggeredEntry(index = 2)
        )

        // Tip Card
        TipCard(modifier = Modifier.staggeredEntry(index = 3))

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Section
        Text(
            text = "Категорії розминки",
            style = AppTypography.titleLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.staggeredEntry(index = 4)
        )

        state.categories.forEachIndexed { index, category ->
            WarmupCategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) },
                modifier = Modifier.staggeredEntry(index = 5 + index)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WarmupHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Розігрів голосу",
            style = AppTypography.labelMedium,
            color = TextColors.onDarkSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Розминка",
            style = AppTypography.displayLarge,
            color = TextColors.onDarkPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.8).sp
        )
    }
}

@Composable
private fun TipCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFBEB), // Світло-жовтий
                        Color(0xFFFEF3C7)  // Золотистий
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                        ),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💡", fontSize = 32.sp)
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Підказка",
                    style = AppTypography.labelMedium,
                    color = Color(0xFFD97706), // Темно-помаранчевий
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Виконуй розминку щодня перед основними вправами для кращих результатів!",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF92400E), // Темно-коричневий
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
