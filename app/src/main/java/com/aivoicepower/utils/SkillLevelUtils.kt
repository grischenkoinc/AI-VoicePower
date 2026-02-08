package com.aivoicepower.utils

import androidx.compose.ui.graphics.Color

object SkillLevelUtils {

    fun getSkillLabel(level: Int): String = when {
        level <= 20 -> "Новачок"
        level <= 40 -> "Учень"
        level <= 60 -> "Практик"
        level <= 80 -> "Впевнений"
        level <= 90 -> "Майстер"
        else -> "Експерт"
    }

    fun getSkillLabelColor(level: Int): Color = when {
        level <= 20 -> Color(0xFF9E9E9E)  // Gray
        level <= 40 -> Color(0xFF4CAF50)  // Green
        level <= 60 -> Color(0xFF2196F3)  // Blue
        level <= 80 -> Color(0xFF9C27B0)  // Purple
        level <= 90 -> Color(0xFFFF9800)  // Orange
        else -> Color(0xFFFFD700)         // Gold
    }
}
