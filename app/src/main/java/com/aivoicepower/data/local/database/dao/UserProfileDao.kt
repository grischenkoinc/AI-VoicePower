package com.aivoicepower.data.local.database.dao

import androidx.room.*
import com.aivoicepower.data.local.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET hasCompletedOnboarding = 1, updatedAt = :timestamp WHERE id = 'default_user'")
    suspend fun markOnboardingCompleted(timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET hasCompletedDiagnostic = 1, updatedAt = :timestamp WHERE id = 'default_user'")
    suspend fun markDiagnosticCompleted(timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET isPremium = :isPremium, updatedAt = :timestamp WHERE id = 'default_user'")
    suspend fun updatePremiumStatus(isPremium: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET name = :name, updatedAt = :timestamp WHERE id = 'default_user'")
    suspend fun updateName(name: String, timestamp: Long = System.currentTimeMillis())
}
