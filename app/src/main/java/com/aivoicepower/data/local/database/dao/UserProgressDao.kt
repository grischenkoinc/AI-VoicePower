package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = :id LIMIT 1")
    fun getProgressFlow(id: String = "default"): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = :id LIMIT 1")
    suspend fun getProgress(id: String = "default"): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgressEntity)

    @Update
    suspend fun updateProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET currentStreak = :streak, lastActivityDate = :date, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStreak(id: String = "default", streak: Int, date: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET totalMinutes = totalMinutes + :minutes, updatedAt = :updatedAt WHERE id = :id")
    suspend fun addMinutes(id: String = "default", minutes: Int, updatedAt: Long = System.currentTimeMillis())

    // Additional methods for v2 ViewModels
    @Query("SELECT * FROM user_progress WHERE id = :id LIMIT 1")
    suspend fun getUserProgressOnce(id: String = "default"): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgressEntity)

    @Query("""
        UPDATE user_progress
        SET dictionLevel = :diction,
            tempoLevel = :tempo,
            intonationLevel = :intonation,
            volumeLevel = :volume,
            structureLevel = :structure,
            confidenceLevel = :confidence,
            fillerWordsLevel = :fillerWords,
            updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateSkillLevels(
        id: String = "default",
        diction: Int,
        tempo: Int,
        intonation: Int,
        volume: Int,
        structure: Int,
        confidence: Int,
        fillerWords: Int,
        updatedAt: Long = System.currentTimeMillis()
    )
}
