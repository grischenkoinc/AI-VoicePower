package com.aivoicepower.domain.model.course

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    fun getDisplayName(): String {
        return when (this) {
            BEGINNER -> "Початковий"
            INTERMEDIATE -> "Середній"
            ADVANCED -> "Просунутий"
        }
    }
}
