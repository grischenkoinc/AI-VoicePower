package com.aivoicepower.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.VoiceMetrics
import com.aivoicepower.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    lessonId: String,
    exerciseId: String,
    onNavigateBack: () -> Unit,
    onNextExercise: (String) -> Unit
) {
    // Mock data for now - in production, fetch from ViewModel
    val mockMetrics = VoiceMetrics(
        clarity = 85f,
        pace = 145f,
        volume = 78f,
        pronunciation = 82f,
        pauseQuality = 75f,
        fillerWordsCount = 2
    )

    val mockRecommendations = listOf(
        "Працюйте над паузами між реченнями для кращої виразності",
        "Знизьте темп мовлення на 10-15%",
        "Додайте більше емоційності в інтонацію"
    )

    val mockStrengths = listOf(
        "Відмінна чіткість вимови",
        "Гарна гучність голосу"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результати аналізу") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                OverallScoreCard(score = 78f)
            }

            item {
                MetricsCard(metrics = mockMetrics)
            }

            item {
                SectionCard(
                    title = "Сильні сторони",
                    icon = Icons.Default.Star,
                    iconTint = Success,
                    items = mockStrengths
                )
            }

            item {
                SectionCard(
                    title = "Рекомендації",
                    icon = Icons.Default.Lightbulb,
                    iconTint = Warning,
                    items = mockRecommendations
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Завершити урок")
                }
            }
        }
    }
}

@Composable
private fun OverallScoreCard(score: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Загальна оцінка",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .border(10.dp, getScoreColor(score), CircleShape)
                    .clip(CircleShape)
                    .background(getScoreColor(score).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${score.toInt()}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = getScoreColor(score)
                    )
                    Text(
                        text = "балів",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = getScoreDescription(score),
                style = MaterialTheme.typography.bodyLarge,
                color = getScoreColor(score),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MetricsCard(metrics: VoiceMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Детальні метрики",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            MetricRow(
                label = "Чіткість",
                value = metrics.clarity,
                isPercentage = true
            )
            MetricRow(
                label = "Темп мовлення",
                value = metrics.pace,
                unit = "слів/хв",
                isPercentage = false
            )
            MetricRow(
                label = "Гучність",
                value = metrics.volume,
                isPercentage = true
            )
            MetricRow(
                label = "Вимова",
                value = metrics.pronunciation,
                isPercentage = true
            )
            MetricRow(
                label = "Якість пауз",
                value = metrics.pauseQuality,
                isPercentage = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Слова-паразити",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "${metrics.fillerWordsCount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (metrics.fillerWordsCount <= 2) Success else Warning
                )
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: Float,
    unit: String = "%",
    isPercentage: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "${value.toInt()}$unit",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isPercentage) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = value / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = getScoreColor(value),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(iconTint)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun getScoreColor(score: Float): Color {
    return when {
        score >= 80 -> Success
        score >= 60 -> Warning
        else -> Error
    }
}

private fun getScoreDescription(score: Float): String {
    return when {
        score >= 90 -> "Чудово!"
        score >= 80 -> "Відмінно!"
        score >= 70 -> "Добре!"
        score >= 60 -> "Непогано"
        else -> "Потрібна практика"
    }
}
