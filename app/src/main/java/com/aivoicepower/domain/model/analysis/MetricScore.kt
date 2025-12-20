package com.aivoicepower.domain.model.analysis

data class MetricScore(
    val value: Int,     // 0-100
    val label: String,  // "Добре", "Середньо", "Потребує покращення"
    val details: String?
) {
    companion object {
        fun fromValue(value: Int, details: String? = null): MetricScore {
            val label = when {
                value >= 80 -> "Відмінно"
                value >= 70 -> "Добре"
                value >= 60 -> "Задовільно"
                value >= 50 -> "Середньо"
                else -> "Потребує покращення"
            }
            return MetricScore(value, label, details)
        }
    }
}
