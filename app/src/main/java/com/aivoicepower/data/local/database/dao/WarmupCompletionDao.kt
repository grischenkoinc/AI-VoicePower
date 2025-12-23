package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarmupCompletionDao {

    @Query("SELECT * FROM warmup_completions WHERE date = :date AND category = :category LIMIT 1")
    suspend fun getCompletion(date: String, category: String): WarmupCompletionEntity?

    @Query("SELECT * FROM warmup_completions WHERE date = :date")
    suspend fun getCompletionsForDate(date: String): List<WarmupCompletionEntity>

    @Query("SELECT * FROM warmup_completions ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentCompletions(limit: Int): Flow<List<WarmupCompletionEntity>>

    @Query("SELECT COUNT(DISTINCT date) FROM warmup_completions")
    fun getTotalWarmupDays(): Flow<Int>

    @Query("SELECT SUM(exercisesCompleted) FROM warmup_completions")
    fun getTotalExercisesCompleted(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: WarmupCompletionEntity)

    @Update
    suspend fun updateCompletion(completion: WarmupCompletionEntity)

    @Query("DELETE FROM warmup_completions WHERE id = :id")
    suspend fun deleteCompletion(id: String)
}
