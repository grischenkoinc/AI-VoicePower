package com.aivoicepower.data.firebase.sync

import android.util.Log
import com.aivoicepower.utils.constants.FreeTierLimits
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Server-side daily analysis limits via Firestore.
 * Prevents date manipulation — counters use server timestamps.
 *
 * Document: daily_limits/{userId}
 * Fields:
 *   exerciseCount, adExerciseCount, improvCount, adImprovCount: Int
 *   dateUtc: String ("yyyy-MM-dd" in UTC, based on server timestamp)
 *   updatedAt: Timestamp (FieldValue.serverTimestamp())
 */
@Singleton
class ServerLimitService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val TAG = "ServerLimitService"
        private const val COLLECTION = "daily_limits"
    }

    private val userId: String?
        get() = firebaseAuth.currentUser?.uid

    /**
     * Check if free user can analyze (server-side).
     * Returns true if within limits or if check fails (fallback to local).
     */
    suspend fun canAnalyze(isImprov: Boolean, isAdBased: Boolean = false): Boolean {
        val uid = userId ?: return true // Not authenticated → trust local limits

        return try {
            val docRef = firestore.collection(COLLECTION).document(uid)
            val doc = docRef.get().await()

            if (!doc.exists()) return true // First time → allowed

            val serverTimestamp = doc.getTimestamp("updatedAt")
            val storedDateUtc = doc.getString("dateUtc") ?: ""

            // Determine if stored data is from today (UTC) using server timestamp
            val serverDateUtc = serverTimestamp?.toDate()?.toInstant()
                ?.atZone(ZoneOffset.UTC)
                ?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""

            // If stored date doesn't match server timestamp date → new day, counters = 0
            val isNewDay = storedDateUtc != serverDateUtc || storedDateUtc.isEmpty()
            if (isNewDay) return true // New day → allowed

            val fieldName = getCounterField(isImprov, isAdBased)
            val currentCount = doc.getLong(fieldName)?.toInt() ?: 0
            val limit = getLimit(isImprov, isAdBased)

            currentCount < limit
        } catch (e: Exception) {
            Log.e(TAG, "Server limit check failed: ${e.message}")
            true // Fallback: allow, rely on local limits
        }
    }

    /**
     * Increment server counter after successful analysis.
     * Uses transaction with server timestamp for date tracking.
     */
    suspend fun incrementAnalysis(isImprov: Boolean, isAdBased: Boolean = false) {
        val uid = userId ?: return

        try {
            val docRef = firestore.collection(COLLECTION).document(uid)

            firestore.runTransaction { transaction ->
                val doc = transaction.get(docRef)

                val serverTimestamp = doc.getTimestamp("updatedAt")
                val storedDateUtc = doc.getString("dateUtc") ?: ""

                val serverDateUtc = serverTimestamp?.toDate()?.toInstant()
                    ?.atZone(ZoneOffset.UTC)
                    ?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""

                val isNewDay = !doc.exists() || storedDateUtc != serverDateUtc || storedDateUtc.isEmpty()

                val fieldName = getCounterField(isImprov, isAdBased)

                if (isNewDay) {
                    // Reset all counters, set the new one to 1
                    val data = hashMapOf<String, Any>(
                        "exerciseCount" to 0L,
                        "adExerciseCount" to 0L,
                        "improvCount" to 0L,
                        "adImprovCount" to 0L,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    data[fieldName] = 1L
                    // dateUtc will be set by the next read based on updatedAt
                    // For now, use UTC date string from client as initial value
                    data["dateUtc"] = Instant.now().atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)
                    transaction.set(docRef, data)
                } else {
                    val currentCount = doc.getLong(fieldName)?.toInt() ?: 0
                    transaction.update(
                        docRef,
                        mapOf(
                            fieldName to (currentCount + 1L),
                            "updatedAt" to FieldValue.serverTimestamp(),
                            "dateUtc" to serverDateUtc // Keep server date
                        )
                    )
                }
            }.await()

            Log.d(TAG, "Server counter incremented: ${getCounterField(isImprov, isAdBased)}")
        } catch (e: Exception) {
            Log.e(TAG, "Server increment failed: ${e.message}")
            // Non-critical: local limits still work as fallback
        }
    }

    /**
     * Get server-side daily limits for display.
     * Returns null if unavailable.
     */
    suspend fun getServerCounts(): ServerDailyCounts? {
        val uid = userId ?: return null

        return try {
            val doc = firestore.collection(COLLECTION).document(uid).get().await()
            if (!doc.exists()) return ServerDailyCounts()

            val serverTimestamp = doc.getTimestamp("updatedAt")
            val storedDateUtc = doc.getString("dateUtc") ?: ""
            val serverDateUtc = serverTimestamp?.toDate()?.toInstant()
                ?.atZone(ZoneOffset.UTC)
                ?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""

            val isNewDay = storedDateUtc != serverDateUtc || storedDateUtc.isEmpty()
            if (isNewDay) return ServerDailyCounts() // New day, all zeros

            ServerDailyCounts(
                exerciseCount = doc.getLong("exerciseCount")?.toInt() ?: 0,
                adExerciseCount = doc.getLong("adExerciseCount")?.toInt() ?: 0,
                improvCount = doc.getLong("improvCount")?.toInt() ?: 0,
                adImprovCount = doc.getLong("adImprovCount")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get server counts: ${e.message}")
            null
        }
    }

    private fun getCounterField(isImprov: Boolean, isAdBased: Boolean): String = when {
        isImprov && isAdBased -> "adImprovCount"
        isImprov -> "improvCount"
        isAdBased -> "adExerciseCount"
        else -> "exerciseCount"
    }

    private fun getLimit(isImprov: Boolean, isAdBased: Boolean): Int = when {
        isImprov && isAdBased -> FreeTierLimits.FREE_AD_IMPROV_ANALYSES_PER_DAY
        isImprov -> FreeTierLimits.FREE_IMPROV_ANALYSES_PER_DAY
        isAdBased -> FreeTierLimits.FREE_AD_ANALYSES_PER_DAY
        else -> FreeTierLimits.FREE_ANALYSES_PER_DAY
    }
}

data class ServerDailyCounts(
    val exerciseCount: Int = 0,
    val adExerciseCount: Int = 0,
    val improvCount: Int = 0,
    val adImprovCount: Int = 0
)
