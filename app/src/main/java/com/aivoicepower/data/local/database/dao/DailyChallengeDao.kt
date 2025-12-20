package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    fun getChallengeForDate(date: String): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getChallengeForDateOnce(date: String): DailyChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(challenge: DailyChallengeEntity)

    @Query("UPDATE daily_challenges SET isCompleted = 1, completedAt = :timestamp, recordingId = :recordingId WHERE date = :date")
    suspend fun markCompleted(date: String, recordingId: String?, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE isCompleted = 1")
    fun getCompletedChallengesCount(): Flow<Int>
}
