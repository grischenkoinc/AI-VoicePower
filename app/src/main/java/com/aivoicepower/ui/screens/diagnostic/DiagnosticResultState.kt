package com.aivoicepower.ui.screens.diagnostic

data class DiagnosticResultState(
    val isLoading: Boolean = true,
    val result: DiagnosticResultDisplay? = null,
    val error: String? = null
)

data class DiagnosticResultDisplay(
    val overall: Int,
    val metrics: List<MetricDisplay>,
    val strengths: List<String>,
    val improvements: List<String>,
    val recommendations: List<RecommendationDisplay>
)

data class MetricDisplay(
    val name: String,
    val score: Int,          // 0-100
    val label: String,       // "Відмінно", "Добре", "Середньо", "Потребує покращення"
    val description: String
)

data class RecommendationDisplay(
    val icon: String,
    val title: String,
    val description: String,
    val actionRoute: String? = null
)
