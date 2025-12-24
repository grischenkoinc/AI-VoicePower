package com.aivoicepower.domain.model.course

data class LessonProgress(
    val lessonId: String,
    val courseId: String,
    val userId: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val exerciseResults: Map<String, ExerciseResult>,
    val bestScore: Int?
) {
    /**
     * Обчислює прогрес уроку (0-100)
     */
    fun calculateProgress(): Int {
        if (exerciseResults.isEmpty()) return 0
        val completedExercises = exerciseResults.count { it.value.isCompleted }
        return ((completedExercises.toFloat() / exerciseResults.size) * 100).toInt()
    }
}
