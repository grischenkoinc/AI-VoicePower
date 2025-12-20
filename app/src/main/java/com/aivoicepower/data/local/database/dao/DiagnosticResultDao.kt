package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.DiagnosticResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticResultDao {

    @Query("SELECT * FROM diagnostic_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<DiagnosticResultEntity>>

    @Query("SELECT * FROM diagnostic_results WHERE isInitial = 1 ORDER BY timestamp ASC LIMIT 1")
    fun getInitialDiagnostic(): Flow<DiagnosticResultEntity?>

    @Query("SELECT * FROM diagnostic_results ORDER BY timestamp DESC LIMIT 1")
    fun getLatestDiagnostic(): Flow<DiagnosticResultEntity?>

    @Query("SELECT * FROM diagnostic_results WHERE id = :id")
    suspend fun getById(id: String): DiagnosticResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: DiagnosticResultEntity)

    @Query("SELECT COUNT(*) FROM diagnostic_results")
    suspend fun getCount(): Int
}
