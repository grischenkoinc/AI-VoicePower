package com.aivoicepower.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aivoicepower.data.local.database.entity.AnalysisResultEntity

@Dao
interface AnalysisResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: AnalysisResultEntity)

    @Query("SELECT * FROM analysis_results WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByExerciseId(exerciseId: String, limit: Int = 3): List<AnalysisResultEntity>

    @Query("SELECT * FROM analysis_results WHERE exerciseType = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getByExerciseType(type: String, limit: Int = 3): List<AnalysisResultEntity>

    @Query("DELETE FROM analysis_results WHERE timestamp < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)
}
