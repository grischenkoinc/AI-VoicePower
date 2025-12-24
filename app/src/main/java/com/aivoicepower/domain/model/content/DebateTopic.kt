package com.aivoicepower.domain.model.content

data class DebateTopic(
    val id: String,
    val topic: String,
    val description: String,
    val difficulty: Int,       // 1-5
    val category: String       // "Суспільство", "Технології", "Етика", etc.
)
