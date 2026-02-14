package com.aivoicepower.domain.model.content

data class TongueTwister(
    val id: String,
    val text: String,
    val difficulty: Int,       // 1-5
    val targetSounds: List<String>,
    val category: String?,     // "Р", "Л", "Б-П-М-В-Ф", etc.
    val usedInLesson: String? = null  // e.g. "Чітке мовлення, Урок 2" — null = не задіяна в жодному уроці
)
