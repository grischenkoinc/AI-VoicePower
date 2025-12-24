package com.aivoicepower.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aivoicepower.data.local.database.entity.RecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recording: RecordingEntity)

    @Update
    suspend fun update(recording: RecordingEntity)

    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getById(id: String): RecordingEntity?

    @Query("SELECT * FROM recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE type = :type ORDER BY createdAt DESC")
    fun getByType(type: String): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE contextId = :contextId ORDER BY createdAt DESC")
    suspend fun getByContextId(contextId: String): List<RecordingEntity>

    @Query("SELECT * FROM recordings WHERE isAnalyzed = 0 LIMIT 10")
    suspend fun getUnanalyzedRecordings(): List<RecordingEntity>

    @Query("SELECT COUNT(*) FROM recordings WHERE type = :type")
    suspend fun getCountByType(type: String): Int

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM recordings WHERE createdAt < :beforeTimestamp")
    suspend fun deleteOldRecordings(beforeTimestamp: Long)
}
