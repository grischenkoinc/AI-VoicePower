package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = 'default_progress' LIMIT 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 'default_progress' LIMIT 1")
    suspend fun getUserProgressOnce(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET currentStreak = :streak, longestStreak = MAX(longestStreak, :streak), lastActivityDate = :date, updatedAt = :timestamp WHERE id = 'default_progress'")
    suspend fun updateStreak(streak: Int, date: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET totalMinutes = totalMinutes + :minutes, totalExercises = totalExercises + :exercises, updatedAt = :timestamp WHERE id = 'default_progress'")
    suspend fun addActivity(minutes: Int, exercises: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_progress SET totalRecordings = totalRecordings + 1, updatedAt = :timestamp WHERE id = 'default_progress'")
    suspend fun incrementRecordings(timestamp: Long = System.currentTimeMillis())

    @Query("""
        UPDATE user_progress SET
            dictionLevel = :diction,
            tempoLevel = :tempo,
            intonationLevel = :intonation,
            volumeLevel = :volume,
            structureLevel = :structure,
            confidenceLevel = :confidence,
            fillerWordsLevel = :fillerWords,
            updatedAt = :timestamp
        WHERE id = 'default_progress'
    """)
    suspend fun updateSkillLevels(
        diction: Int,
        tempo: Int,
        intonation: Int,
        volume: Int,
        structure: Int,
        confidence: Int,
        fillerWords: Int,
        timestamp: Long = System.currentTimeMillis()
    )
}
