package com.aivoicepower.ui.screens.warmup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.warmup.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmupScreen(
    viewModel: WarmupViewModel = hiltViewModel(),
    onNavigateToArticulation: () -> Unit,
    onNavigateToBreathing: () -> Unit,
    onNavigateToVoice: () -> Unit,
    onNavigateToQuick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Розминка") },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Налаштування")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error ?: "Помилка")
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
                    modifier = Modifier.padding(paddingValues)
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Card
        item {
            if (state.stats != null) {
                WarmupStatsCard(stats = state.stats)
            }
        }

        // Quick Warmup
        item {
            Text(
                text = "\uD83D\uDCA8 Швидка розминка (5 хв)", // 💨
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            QuickWarmupCard(onClick = onQuickWarmupClick)
        }

        item {
            Divider()
        }

        // Categories
        item {
            Text(
                text = "\uD83D\uDCDA Категорії розминки", // 📚
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(state.categories) { category ->
            WarmupCategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }

        // Tip
        item {
            Divider()
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "\uD83D\uDCA1", // 💡
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = "Підказка:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Виконуй розминку щодня перед основними вправами для кращих результатів!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
