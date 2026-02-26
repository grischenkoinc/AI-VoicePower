package com.aivoicepower.ui.screens.aicoach.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aivoicepower.ui.theme.PrimaryColors
import com.aivoicepower.ui.theme.modifiers.scaleOnPress

@Composable
fun QuickActionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chipShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .clip(chipShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, PrimaryColors.light.copy(alpha = 0.20f), chipShape)
            .scaleOnPress(pressedScale = 0.93f)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.85f),
            style = TextStyle(fontSize = 13.sp)
        )
    }
}
