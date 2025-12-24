package com.aivoicepower.domain.model.analysis

data class AnalysisMetrics(
    val diction: MetricScore,
    val tempo: MetricScore,
    val intonation: MetricScore,
    val volume: MetricScore,
    val structure: MetricScore?,
    val confidence: MetricScore?,
    val overall: Int    // 0-100
) {
    /**
     * Повертає всі метрики як список
     */
    fun getAllMetrics(): List<MetricScore> {
        return listOfNotNull(diction, tempo, intonation, volume, structure, confidence)
    }

    /**
     * Повертає метрики, що потребують покращення (< 70)
     */
    fun getWeakMetrics(): List<MetricScore> {
        return getAllMetrics().filter { it.value < 70 }
    }

    /**
     * Повертає сильні метрики (>= 80)
     */
    fun getStrongMetrics(): List<MetricScore> {
        return getAllMetrics().filter { it.value >= 80 }
    }
}
