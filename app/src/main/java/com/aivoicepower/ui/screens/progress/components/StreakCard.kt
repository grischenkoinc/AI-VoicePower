package com.aivoicepower.ui.screens.progress.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "[fire]",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "$currentStreak",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "днiв поспiль",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "[trophy]",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "$longestStreak",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "рекорд",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
