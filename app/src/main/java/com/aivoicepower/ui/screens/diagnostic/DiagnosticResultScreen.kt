package com.aivoicepower.ui.screens.diagnostic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivoicepower.ui.screens.diagnostic.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticResultScreen(
    viewModel: DiagnosticResultViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результати діагностики") },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити")
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Аналізуємо твоє мовлення...")
                    }
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

            state.result != null -> {
                DiagnosticResultContent(
                    result = state.result!!,
                    onNavigateToHome = onNavigateToHome,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DiagnosticResultContent(
    result: DiagnosticResultDisplay,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎉 Діагностика завершена!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Ось що ми дізналися про твоє мовлення:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Radar Chart
        item {
            SkillRadarChart(
                metrics = result.metrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        item {
            Divider()
        }

        // Detailed Scores
        item {
            Text(
                text = "📊 Детальні оцінки",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(result.metrics) { metric ->
            SkillScoreCard(metric = metric)
        }

        item {
            Divider()
        }

        // Strengths
        item {
            FeedbackSection(
                title = "✅ Твої сильні сторони:",
                items = result.strengths,
                isPositive = true
            )
        }

        // Improvements
        item {
            FeedbackSection(
                title = "🎯 Зони для покращення:",
                items = result.improvements,
                isPositive = false
            )
        }

        item {
            Divider()
        }

        // Recommendations
        item {
            Text(
                text = "💡 Персоналізовані рекомендації:",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(result.recommendations) { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onClick = { /* TODO: Navigate to recommendation.actionRoute */ }
            )
        }

        // Start button
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Почати тренування →")
            }
        }
    }
}
