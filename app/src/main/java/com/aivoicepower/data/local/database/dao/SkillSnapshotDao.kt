package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.SkillSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: SkillSnapshotEntity)

    @Query("SELECT * FROM skill_snapshots WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getByDateRange(startDate: String, endDate: String): Flow<List<SkillSnapshotEntity>>

    @Query("SELECT * FROM skill_snapshots ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): SkillSnapshotEntity?

    @Query("SELECT * FROM skill_snapshots WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SkillSnapshotEntity?

    @Query("SELECT * FROM skill_snapshots ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<SkillSnapshotEntity>>
}
