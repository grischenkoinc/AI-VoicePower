package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.screens.aicoach.SimulationScenario

@Composable
fun ScenarioDialog(
    scenarios: List<SimulationScenario>,
    onScenarioSelected: (SimulationScenario) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val dialogShape = RoundedCornerShape(24.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(dialogShape)
                .background(Color(0xF00A0A0F)) // 94% dark
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = dialogShape
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Оберіть симуляцію",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(scenarios) { scenario ->
                    ScenarioCard(
                        scenario = scenario,
                        onClick = { onScenarioSelected(scenario) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Закрити",
                        color = PrimaryColors.light
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenarioCard(
    scenario: SimulationScenario,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.04f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = cardShape
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = getScenarioEmoji(scenario.id) + " " + scenario.title,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = scenario.description,
            style = TextStyle(fontSize = 13.sp),
            color = TextColors.onDarkMuted
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip("${scenario.steps.size} кроків")
            InfoChip(getScenarioDuration(scenario.steps.size))
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    val chipShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .clip(chipShape)
            .background(PrimaryColors.default.copy(alpha = 0.15f))
            .border(1.dp, PrimaryColors.default.copy(alpha = 0.25f), chipShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 11.sp),
            color = PrimaryColors.light
        )
    }
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
