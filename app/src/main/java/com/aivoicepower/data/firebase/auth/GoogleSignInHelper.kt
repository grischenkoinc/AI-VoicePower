package com.aivoicepower.data.firebase.auth

import android.content.Context
import android.content.Intent
import com.aivoicepower.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun getIdTokenFromIntent(data: Intent?): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
                ?: return Result.failure(Exception("Не вдалося отримати токен Google"))
            Result.success(idToken)
        } catch (e: ApiException) {
            Result.failure(Exception("Помилка входу через Google: ${e.statusCode}"))
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}
