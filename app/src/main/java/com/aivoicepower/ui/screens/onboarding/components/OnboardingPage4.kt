package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingPage4(
    onStartDiagnostic: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Почнемо з діагностики! 🎯",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ми проведемо швидкий тест (5 хвилин) щоб визначити твій поточний рівень та створити персоналізований план",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Metrics
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Що будемо оцінювати:",
                    style = MaterialTheme.typography.titleMedium
                )

                MetricItem(
                    icon = Icons.Default.GraphicEq,
                    text = "Чіткість дикції"
                )

                MetricItem(
                    icon = Icons.Default.Speed,
                    text = "Темп мовлення"
                )

                MetricItem(
                    icon = Icons.Default.MusicNote,
                    text = "Інтонація та виразність"
                )

                MetricItem(
                    icon = Icons.Default.VolumeUp,
                    text = "Гучність голосу"
                )

                MetricItem(
                    icon = Icons.Default.ListAlt,
                    text = "Структура думок"
                )

                MetricItem(
                    icon = Icons.Default.CheckCircle,
                    text = "Впевненість"
                )

                MetricItem(
                    icon = Icons.Default.Block,
                    text = "Слова-паразити"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Requirements
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Знадобиться:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    RequirementItem("• 5 хвилин часу")
                    RequirementItem("• Тихе місце")
                    RequirementItem("• Дозвіл на мікрофон")
                }
            }
        }

        Column {
            Button(
                onClick = onStartDiagnostic,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Почати діагностику →")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackClick) {
                Text("← Назад")
            }

            Spacer(modifier = Modifier.height(8.dp))

            PageIndicator(currentPage = 3, totalPages = 4)
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RequirementItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}
