package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarmupCompletionDao {

    @Query("SELECT * FROM warmup_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<WarmupCompletionEntity>>

    @Query("SELECT * FROM warmup_completions WHERE date = :date AND category = :category LIMIT 1")
    suspend fun getCompletion(date: String, category: String): WarmupCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(completion: WarmupCompletionEntity)

    @Query("SELECT COUNT(DISTINCT date) FROM warmup_completions")
    fun getTotalWarmupDays(): Flow<Int>

    @Query("SELECT * FROM warmup_completions ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentCompletions(limit: Int = 30): Flow<List<WarmupCompletionEntity>>
}
