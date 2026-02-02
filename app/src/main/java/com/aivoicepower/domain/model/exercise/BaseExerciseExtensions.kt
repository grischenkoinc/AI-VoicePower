package com.aivoicepower.domain.model.exercise

import com.aivoicepower.domain.model.user.SkillType

/**
 * Helper functions for working with BaseExercise system
 *
 * These utilities help with analytics, searching, and filtering exercises
 * across all exercise types (Lesson, Warmup, Improvisation).
 */

/**
 * Get all exercises that target a specific skill
 *
 * Usage: getAllExercises().filterBySkill(SkillType.DICTION)
 */
fun List<BaseExercise>.filterBySkill(skillType: SkillType): List<BaseExercise> {
    return filter { exercise ->
        exercise.targetMetrics.contains(skillType)
    }
}

/**
 * Get all exercises that require recording
 *
 * Useful for showing only exercises with AI analysis
 */
fun List<BaseExercise>.onlyRecordable(): List<BaseExercise> {
    return filter { it.requiresRecording }
}

/**
 * Get all exercises that don't require recording
 *
 * Useful for warmup-style exercises without analysis
 */
fun List<BaseExercise>.onlyNonRecordable(): List<BaseExercise> {
    return filter { !it.requiresRecording }
}

/**
 * Group exercises by their type (lesson, warmup, improvisation)
 */
fun List<BaseExercise>.groupByType(): Map<String, List<BaseExercise>> {
    return groupBy { it.getExerciseType() }
}

/**
 * Filter exercises by type
 */
fun List<BaseExercise>.filterByType(type: String): List<BaseExercise> {
    return filter { it.getExerciseType() == type }
}

/**
 * Get total duration of all exercises in seconds
 */
fun List<BaseExercise>.totalDuration(): Int {
    return sumOf { it.durationSeconds }
}

/**
 * Check if an exercise improves a specific skill
 */
fun BaseExercise.improvesSkill(skillType: SkillType): Boolean {
    return targetMetrics.contains(skillType)
}

/**
 * Get all skills that this exercise improves
 */
fun BaseExercise.getTargetSkills(): List<SkillType> {
    return targetMetrics
}

/**
 * Check if exercise is a lesson exercise
 */
fun BaseExercise.isLessonExercise(): Boolean {
    return this is LessonExercise
}

/**
 * Check if exercise is a warmup exercise
 */
fun BaseExercise.isWarmupExercise(): Boolean {
    return this is WarmupExercise
}

/**
 * Check if exercise is an improvisation exercise
 */
fun BaseExercise.isImprovisationExercise(): Boolean {
    return this is ImprovisationExercise
}

/**
 * Get exercise difficulty as a string
 *
 * Useful for display in UI
 */
fun BaseExercise.getDifficultyString(): String {
    return when (this) {
        is LessonExercise -> difficulty.toString()
        is ImprovisationExercise -> difficulty
        is WarmupExercise -> "BEGINNER" // Warmup is always beginner-friendly
        else -> "UNKNOWN"
    }
}

/**
 * Format duration for display (e.g., "1:30" for 90 seconds)
 */
fun BaseExercise.getFormattedDuration(): String {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    return if (minutes > 0) {
        "$minutes:${seconds.toString().padStart(2, '0')}"
    } else {
        "${seconds}s"
    }
}

/**
 * Get a short type label for UI (e.g., "Урок", "Розминка", "Імпровізація")
 */
fun BaseExercise.getTypeLabel(): String {
    return when (getExerciseType()) {
        "lesson" -> "Урок"
        "warmup" -> "Розминка"
        "improvisation" -> "Імпровізація"
        "diagnostic" -> "Діагностика"
        else -> "Вправа"
    }
}

/**
 * Get color for exercise type (for UI consistency)
 */
fun BaseExercise.getTypeColor(): String {
    return when (getExerciseType()) {
        "lesson" -> "#6366F1"       // Indigo
        "warmup" -> "#10B981"       // Green
        "improvisation" -> "#F59E0B" // Amber
        "diagnostic" -> "#EC4899"   // Pink
        else -> "#6B7280"           // Gray
    }
}

/**
 * Check if exercise is suitable for beginners
 */
fun BaseExercise.isForBeginners(): Boolean {
    return when (this) {
        is LessonExercise -> difficulty.name == "BEGINNER"
        is WarmupExercise -> true // Warmup is always beginner-friendly
        is ImprovisationExercise -> difficulty == "beginner"
        else -> false
    }
}

/**
 * Get all skills improved by a list of exercises
 */
fun List<BaseExercise>.getAllImprovedSkills(): List<SkillType> {
    return flatMap { it.targetMetrics }.distinct()
}

/**
 * Find exercises by ID across all types
 */
fun List<BaseExercise>.findById(id: String): BaseExercise? {
    return find { it.id == id }
}
