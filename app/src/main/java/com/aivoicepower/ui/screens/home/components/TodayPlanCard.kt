package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aivoicepower.domain.model.home.TodayPlan
import com.aivoicepower.domain.model.home.PlanActivity

@Composable
fun TodayPlanCard(
    plan: TodayPlan,
    onActivityClick: (PlanActivity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📋 Сьогоднішній план",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = plan.recommendedFocus,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            plan.activities.forEach { activity ->
                PlanActivityItem(
                    activity = activity,
                    onClick = { onActivityClick(activity) }
                )
            }
        }
    }
}
