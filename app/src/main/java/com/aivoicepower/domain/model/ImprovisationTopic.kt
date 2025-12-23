package com.aivoicepower.domain.model

data class ImprovisationTopic(
    val id: String,
    val title: String,
    val difficulty: Difficulty,
    val hints: List<String> = emptyList()
)
