package com.aivoicepower.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section title - slightly larger with refined letter spacing
        Text(
            text = title.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.55f),
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(start = 6.dp, bottom = 10.dp)
        )

        // Section card - glass morphism with border
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0xFF764BA2).copy(alpha = 0.15f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Color.White.copy(alpha = 0.12f),
                    RoundedCornerShape(20.dp)
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            content()
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
