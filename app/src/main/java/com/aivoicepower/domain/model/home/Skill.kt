package com.aivoicepower.domain.model.home

data class Skill(
    val id: String,
    val name: String,
    val emoji: String,
    val percentage: Int,
    val growth: String,
    val gradientColors: List<String>
)
