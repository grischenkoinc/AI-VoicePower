package com.aivoicepower.ui.screens.improvisation

data class ImprovisationState(
    val completedToday: Int = 0,
    val dailyLimit: Int = 3,
    val isPremium: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
