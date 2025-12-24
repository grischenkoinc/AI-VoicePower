package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.ui.screens.aicoach.SimulationScenario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioDialog(
    scenarios: List<SimulationScenario>,
    onScenarioSelected: (SimulationScenario) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Оберіть симуляцію",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(scenarios) { scenario ->
                    Card(
                        onClick = { onScenarioSelected(scenario) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = getScenarioEmoji(scenario.id) + " " + scenario.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scenario.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AssistChip(
                                    onClick = { },
                                    label = { Text("${scenario.steps.size} кроків") }
                                )
                                AssistChip(
                                    onClick = { },
                                    label = { Text(getScenarioDuration(scenario.steps.size)) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрити")
            }
        }
    )
}

private fun getScenarioEmoji(scenarioId: String): String {
    return when (scenarioId) {
        "job_interview" -> "\uD83D\uDCBC"
        "sales_pitch" -> "\uD83D\uDCB0"
        "presentation" -> "\uD83D\uDCCA"
        "negotiation" -> "\uD83E\uDD1D"
        else -> "\uD83C\uDFAD"
    }
}

private fun getScenarioDuration(steps: Int): String {
    val minutes = steps * 2
    return "~$minutes хв"
}
