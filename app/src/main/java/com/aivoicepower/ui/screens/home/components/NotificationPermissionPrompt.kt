package com.aivoicepower.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.AppTypography
import com.aivoicepower.ui.theme.TextColors
import com.aivoicepower.ui.theme.components.PrimaryButton
import com.aivoicepower.ui.theme.components.SecondaryButton

/**
 * Одноразовий м'який запит дозволу на сповіщення — показується на Home
 * (перший раз для нових і вже існуючих юзерів, поки дозвіл не надано).
 */
@Composable
fun NotificationPermissionPrompt(
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🔔", fontSize = 48.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Не пропускай нові уроки й нагадування",
                style = AppTypography.titleLarge,
                color = TextColors.onLightPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Увімкни сповіщення, щоб отримувати нагадування про тренування та дізнаватись про нові уроки й курси.",
                style = AppTypography.bodyMedium,
                color = TextColors.onLightSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Дозволити",
                onClick = onAllow,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            SecondaryButton(
                text = "Не зараз",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
