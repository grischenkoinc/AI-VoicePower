package com.aivoicepower.domain.model.user

data class DiagnosticResult(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val diction: Int,           // 0-100
    val tempo: Int,             // 0-100
    val intonation: Int,        // 0-100
    val volume: Int,            // 0-100
    val structure: Int,         // 0-100
    val confidence: Int,        // 0-100
    val fillerWords: Int,       // 0-100 (100 = немає паразитів)
    val recordingIds: List<String>,
    val recommendations: List<String>,
    val isInitial: Boolean = true // true = перша діагностика
) {
    /**
     * Повертає всі навички як Map
     */
    fun toSkillLevelsMap(): Map<SkillType, Int> {
        return mapOf(
            SkillType.DICTION to diction,
            SkillType.TEMPO to tempo,
            SkillType.INTONATION to intonation,
            SkillType.VOLUME to volume,
            SkillType.STRUCTURE to structure,
            SkillType.CONFIDENCE to confidence,
            SkillType.FILLER_WORDS to fillerWords
        )
    }

    /**
     * Обчислює загальний рівень (0-100)
     */
    fun calculateOverallLevel(): Int {
        return listOf(diction, tempo, intonation, volume, structure, confidence, fillerWords)
            .average()
            .toInt()
    }

    /**
     * Повертає найсильніші навички
     */
    fun getStrengths(): List<SkillType> {
        return toSkillLevelsMap().entries
            .filter { it.value >= 70 }
            .sortedByDescending { it.value }
            .map { it.key }
    }

    /**
     * Повертає навички для покращення
     */
    fun getAreasForImprovement(): List<SkillType> {
        return toSkillLevelsMap().entries
            .filter { it.value < 70 }
            .sortedBy { it.value }
            .map { it.key }
    }
}
