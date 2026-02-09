package com.aivoicepower.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SettingsTrailingType {
    CHEVRON, SWITCH, VALUE, NONE
}

@Composable
fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingType: SettingsTrailingType = SettingsTrailingType.CHEVRON,
    isChecked: Boolean = false,
    valueText: String? = null,
    iconTint: Color = Color(0xFF8B5CF6),
    titleColor: Color = Color.White,
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient circle background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        iconTint.copy(alpha = 0.15f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            // Trailing
            when (trailingType) {
                SettingsTrailingType.CHEVRON -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(22.dp)
                    )
                }
                SettingsTrailingType.SWITCH -> {
                    Switch(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF8B5CF6),
                            checkedBorderColor = Color(0xFFA78BFA).copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.12f),
                            uncheckedBorderColor = Color.White.copy(alpha = 0.25f)
                        )
                    )
                }
                SettingsTrailingType.VALUE -> {
                    if (valueText != null) {
                        Text(
                            text = valueText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFA78BFA)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                SettingsTrailingType.NONE -> { }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.07f),
                modifier = Modifier.padding(start = 66.dp, end = 16.dp)
            )
        }
    }
}
