package com.aivoicepower.domain.model.course

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

fun Difficulty.toDisplayString(): String {
    return when (this) {
        Difficulty.BEGINNER -> "Початковий"
        Difficulty.INTERMEDIATE -> "Середній"
        Difficulty.ADVANCED -> "Просунутий"
    }
}
