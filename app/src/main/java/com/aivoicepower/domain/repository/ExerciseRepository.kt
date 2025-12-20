package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.exercise.Exercise
import com.aivoicepower.domain.model.exercise.WarmupExercise
import com.aivoicepower.domain.model.exercise.WarmupCategory
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun getWarmupExercises(category: WarmupCategory): List<WarmupExercise>
    suspend fun getAllWarmupExercises(): List<WarmupExercise>
}
