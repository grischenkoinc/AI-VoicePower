package com.aivoicepower.domain.model.content

data class TongueTwister(
    val id: String,
    val text: String,
    val difficulty: Int,       // 1-5
    val targetSounds: List<String>,
    val category: String?      // "Р", "Л", "С-Ш", etc.
)
