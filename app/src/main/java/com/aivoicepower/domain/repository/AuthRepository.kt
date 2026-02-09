package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.user.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    val isAuthenticated: Flow<Boolean>

    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<AuthUser>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
    fun getCurrentUser(): AuthUser?
}
