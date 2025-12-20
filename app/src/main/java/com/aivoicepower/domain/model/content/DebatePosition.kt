package com.aivoicepower.domain.model.content

enum class DebatePosition {
    FOR,
    AGAINST;

    fun getDisplayName(): String {
        return when (this) {
            FOR -> "За"
            AGAINST -> "Проти"
        }
    }

    fun opposite(): DebatePosition {
        return if (this == FOR) AGAINST else FOR
    }
}
