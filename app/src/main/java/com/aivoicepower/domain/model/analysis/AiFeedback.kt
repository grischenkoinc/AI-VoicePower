package com.aivoicepower.domain.model.analysis

data class AiFeedback(
    val summary: String,
    val strengths: List<String>,
    val improvements: List<String>,
    val tip: String
) {
    /**
     * Чи є фідбек позитивним?
     */
    val isPositive: Boolean
        get() = strengths.size >= improvements.size
}
