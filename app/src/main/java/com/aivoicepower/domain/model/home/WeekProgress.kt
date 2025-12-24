package com.aivoicepower.domain.model.home

data class WeekProgress(
    val days: List<DayProgress>
)

data class DayProgress(
    val dayName: String,      // "Пн", "Вт", ...
    val date: String,         // "2024-12-15"
    val minutes: Int,
    val isCompleted: Boolean  // Чи була активність
)
