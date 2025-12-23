package com.aivoicepower.ui.screens.warmup

sealed class WarmupEvent {
    data class CategoryClicked(val categoryId: String) : WarmupEvent()
    object QuickWarmupClicked : WarmupEvent()
    object Refresh : WarmupEvent()
}
