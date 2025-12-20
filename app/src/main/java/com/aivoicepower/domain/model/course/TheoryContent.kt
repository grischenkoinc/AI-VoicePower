package com.aivoicepower.domain.model.course

data class TheoryContent(
    val text: String?,
    val videoUrl: String?,
    val tips: List<String>
) {
    val hasVideo: Boolean
        get() = videoUrl?.isNotBlank() == true

    val hasText: Boolean
        get() = text?.isNotBlank() == true
}
