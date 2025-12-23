package com.aivoicepower.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aivoicepower.data.local.database.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: DailyChallengeEntity)

    @Update
    suspend fun updateChallenge(challenge: DailyChallengeEntity)

    @Query("SELECT * FROM daily_challenges WHERE challengeId = :challengeId")
    suspend fun getChallengeById(challengeId: String): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getChallengeByDate(date: String): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    fun getChallengeByDateFlow(date: String): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_challenges WHERE completed = 1 ORDER BY completedAt DESC")
    fun getAllCompletedChallenges(): Flow<List<DailyChallengeEntity>>

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE completed = 1")
    suspend fun getCompletedChallengesCount(): Int

    @Query("SELECT * FROM daily_challenges WHERE completed = 1 ORDER BY completedAt DESC LIMIT 7")
    suspend fun getLastSevenCompletedChallenges(): List<DailyChallengeEntity>

    @Query("DELETE FROM daily_challenges WHERE date < :beforeDate AND completed = 0")
    suspend fun deleteOldIncompleteChallenges(beforeDate: String)
}
