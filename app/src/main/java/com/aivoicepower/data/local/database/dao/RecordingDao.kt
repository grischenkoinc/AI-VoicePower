package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.RecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Query("SELECT * FROM recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE type = :type ORDER BY createdAt DESC")
    fun getRecordingsByType(type: String): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): RecordingEntity?

    @Query("SELECT * FROM recordings WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: String): Flow<RecordingEntity?>

    @Query("SELECT * FROM recordings WHERE contextId = :contextId ORDER BY createdAt DESC")
    fun getByContext(contextId: String): Flow<List<RecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recording: RecordingEntity)

    @Update
    suspend fun update(recording: RecordingEntity)

    @Query("""
        UPDATE recordings SET
            isAnalyzed = 1,
            transcription = :transcription,
            analysisJson = :analysisJson,
            overallScore = :overallScore,
            dictionScore = :dictionScore,
            tempoScore = :tempoScore,
            intonationScore = :intonationScore
        WHERE id = :id
    """)
    suspend fun updateAnalysis(
        id: String,
        transcription: String?,
        analysisJson: String,
        overallScore: Int,
        dictionScore: Int?,
        tempoScore: Int?,
        intonationScore: Int?
    )

    @Delete
    suspend fun delete(recording: RecordingEntity)

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM recordings WHERE type = 'diagnostic' ORDER BY createdAt ASC")
    fun getDiagnosticRecordings(): Flow<List<RecordingEntity>>
}
