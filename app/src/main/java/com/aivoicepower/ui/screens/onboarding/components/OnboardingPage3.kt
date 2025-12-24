package com.aivoicepower.ui.screens.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingPage3(
    selectedMinutes: Int,
    onMinutesSelected: (Int) -> Unit,
    onNextClick: () -> Unit,
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
                text = "Скільки часу готовий приділяти?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeOption(
                    minutes = 5,
                    title = "5 хвилин на день",
                    subtitle = "Швидкі вправи між справами",
                    isSelected = selectedMinutes == 5,
                    onSelect = { onMinutesSelected(5) }
                )

                TimeOption(
                    minutes = 15,
                    title = "15 хвилин на день",
                    subtitle = "Оптимально для результату",
                    isSelected = selectedMinutes == 15,
                    isRecommended = true,
                    onSelect = { onMinutesSelected(15) }
                )

                TimeOption(
                    minutes = 30,
                    title = "30 хвилин на день",
                    subtitle = "Прискорений прогрес",
                    isSelected = selectedMinutes == 30,
                    onSelect = { onMinutesSelected(30) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ти завжди зможеш змінити це в налаштуваннях",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Назад")
                }

                Button(
                    onClick = onNextClick,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Далі →")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PageIndicator(currentPage = 2, totalPages = 4)
        }
    }
}

@Composable
private fun TimeOption(
    minutes: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isRecommended: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                if (isRecommended && !isSelected) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "✓ Рекомендовано",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )
        }
    }
}
