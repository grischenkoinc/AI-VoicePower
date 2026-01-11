package com.aivoicepower.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * AI VoicePower Navigation Components v2.0
 *
 * Джерело: Design_Example_react.md
 */

/**
 * Bottom Navigation Row
 * CSS: .bottom-nav
 *
 * Контейнер для навігаційних кнопок (Prev/Next)
 */
@Composable
fun BottomNavRow(
    onPrevious: (() -> Unit)? = null,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    previousText: String = "Назад",
    nextText: String = "Далі"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Previous button (якщо є)
        if (onPrevious != null) {
            NavButton(
                text = previousText,
                icon = "←",
                isPrimary = false,
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            )
        }

        // Next button
        NavButton(
            text = nextText,
            icon = "→",
            isPrimary = true,
            onClick = onNext,
            modifier = Modifier.weight(1f)
        )
    }
}
