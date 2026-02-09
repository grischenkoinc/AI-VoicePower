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
    titleColor: Color = Color(0xFF1A1A2E),
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with soft colored background
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        iconTint.copy(alpha = 0.1f),
                        RoundedCornerShape(11.dp)
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
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            when (trailingType) {
                SettingsTrailingType.CHEVRON -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFD1D5DB),
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
                            checkedBorderColor = Color(0xFF8B5CF6),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE5E7EB),
                            uncheckedBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                }
                SettingsTrailingType.VALUE -> {
                    if (valueText != null) {
                        Text(
                            text = valueText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8B5CF6)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFD1D5DB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                SettingsTrailingType.NONE -> { }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color(0xFFF3F4F6),
                modifier = Modifier.padding(start = 68.dp, end = 16.dp)
            )
        }
    }
}
