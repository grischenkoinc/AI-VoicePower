package com.aivoicepower.ui.screens.improvisation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreparationTimerCard(
    timeLeft: Int,
    onGenerateNew: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⏱️ Час підготовки:",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "00:%02d".format(timeLeft),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            onGenerateNew?.let { callback ->
                OutlinedButton(
                    onClick = callback,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔄 Інша тема")
                }
            }
        }
    }
}
