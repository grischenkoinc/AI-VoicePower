package com.aivoicepower.domain.model.exercise

data class WarmupExercise(
    val id: String,
    val category: WarmupCategory,
    val title: String,
    val description: String,
    val durationSeconds: Int,
    val repetitions: Int?,
    val mediaType: WarmupMediaType,
    val mediaUrl: String?,
    val animationType: AnimationType?
) {
    /**
     * Чи потребує ця вправа медіа-контент?
     */
    val requiresMedia: Boolean
        get() = mediaUrl?.isNotBlank() == true || animationType != null
}
