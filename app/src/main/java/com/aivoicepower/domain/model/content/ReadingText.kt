package com.aivoicepower.domain.model.content

import com.aivoicepower.domain.model.exercise.Emotion

data class ReadingText(
    val id: String,
    val text: String,
    val emotion: Emotion?,
    val author: String?,
    val title: String?,
    val difficulty: Int        // 1-5
)
