package com.aivoicepower.ui.screens.premium.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun FeatureComparisonCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Порівняння версій",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Функція", modifier = Modifier.weight(1f))
                Text("Free", modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
                Text("Premium", modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Features
            ComparisonRow("Розминка", true, true)
            ComparisonRow("Діагностика", "1 раз", "Безліміт")
            ComparisonRow("Уроки курсів", "7/курс", "Всі")
            ComparisonRow("Імпровізація", "3/день", "Безліміт")
            ComparisonRow("AI-тренер", "10 msg/день", "Безліміт")
            ComparisonRow("Прогрес", "Базовий", "Повний")
            ComparisonRow("Офлайн режим", false, true)
        }
    }
}

@Composable
private fun ComparisonRow(
    feature: String,
    free: Any,
    premium: Any
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier.width(60.dp),
            contentAlignment = Alignment.Center
        ) {
            when (free) {
                is Boolean -> {
                    Icon(
                        imageVector = if (free) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (free) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                is String -> {
                    Text(
                        text = free,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Box(
            modifier = Modifier.width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            when (premium) {
                is Boolean -> {
                    Icon(
                        imageVector = if (premium) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (premium) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                is String -> {
                    Text(
                        text = premium,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
