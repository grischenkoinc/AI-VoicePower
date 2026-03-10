package com.aivoicepower.data.firebase.auth

import com.aivoicepower.domain.model.user.AuthUser
import com.aivoicepower.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toAuthUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val isAuthenticated: Flow<Boolean> = currentUser.map { it != null }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("Не вдалося увійти через Google"))
            val isNew = result.additionalUserInfo?.isNewUser ?: false
            Result.success(user.toAuthUser().copy(isNewUser = isNew))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Не вдалося увійти"))
            Result.success(user.toAuthUser().copy(isNewUser = false))
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<AuthUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Не вдалося створити акаунт"))

            // Update display name
            val profileUpdates = userProfileChangeRequest {
                this.displayName = displayName
            }
            user.updateProfile(profileUpdates).await()

            Result.success(user.toAuthUser().copy(displayName = displayName, isNewUser = true))
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
                ?: return Result.failure(Exception("Користувач не знайдений"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override fun getCurrentUser(): AuthUser? {
        return firebaseAuth.currentUser?.toAuthUser()
    }

    private fun FirebaseUser.toAuthUser(): AuthUser {
        return AuthUser(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString(),
            isEmailVerified = isEmailVerified,
            providerId = providerData.firstOrNull { it.providerId != "firebase" }?.providerId ?: "password"
        )
    }

    private fun mapFirebaseException(e: Exception): Exception {
        val msg = e.message?.uppercase() ?: ""
        val message = when {
            "INVALID_LOGIN_CREDENTIALS" in msg ||
            "INVALID_EMAIL" in msg ||
            "WRONG_PASSWORD" in msg ||
            "INVALID_CREDENTIAL" in msg ->
                "Невірна електронна пошта або пароль"
            "USER_NOT_FOUND" in msg ->
                "Користувача з такою поштою не знайдено"
            "EMAIL_ALREADY_IN_USE" in msg ||
            "EMAIL_EXISTS" in msg ||
            "ALREADY_EXISTS" in msg ->
                "Акаунт з такою поштою вже існує"
            "WEAK_PASSWORD" in msg ->
                "Пароль занадто слабкий (мінімум 6 символів)"
            "TOO_MANY_ATTEMPTS" in msg ||
            "TOO_MANY_REQUESTS" in msg ->
                "Забагато спроб. Спробуйте пізніше"
            "NETWORK" in msg ->
                "Помилка мережі. Перевірте підключення"
            "REQUIRES_RECENT_LOGIN" in msg ->
                "Потрібно увійти знову для цієї дії"
            "USER_DISABLED" in msg ->
                "Цей акаунт заблоковано"
            "OPERATION_NOT_ALLOWED" in msg ->
                "Цей метод входу не підтримується"
            "CREDENTIAL_ALREADY_IN_USE" in msg ->
                "Ці облікові дані вже використовуються іншим акаунтом"
            "MISSING_PASSWORD" in msg ||
            "MISSING_EMAIL" in msg ->
                "Будь ласка, заповніть усі поля"
            else -> "Невірні дані. Перевірте пошту та пароль"
        }
        return Exception(message)
    }
}
