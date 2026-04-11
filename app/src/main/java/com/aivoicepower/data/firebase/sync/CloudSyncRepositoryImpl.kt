package com.aivoicepower.data.firebase.sync

import android.net.Uri
import com.aivoicepower.data.local.database.AppDatabase
import android.content.Context
import com.aivoicepower.data.local.database.entity.AchievementEntity
import com.aivoicepower.data.local.database.entity.CourseProgressEntity
import com.aivoicepower.data.local.database.entity.DiagnosticResultEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.domain.repository.CloudSyncRepository
import com.aivoicepower.domain.repository.SyncState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val database: AppDatabase,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    @ApplicationContext private val context: Context
) : CloudSyncRepository {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    override val syncState: Flow<SyncState> = _syncState.asStateFlow()

    private val uid: String?
        get() = firebaseAuth.currentUser?.uid

    override suspend fun syncUserProfile(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            // Read current preferences and upload to Firestore
            val profileRef = firestore.collection("users").document(userId)
            val profileData = hashMapOf(
                "updatedAt" to System.currentTimeMillis()
            )
            profileRef.set(profileData, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Save isPremium = true to Firestore user document so it survives reinstall */
    override suspend fun savePremiumToCloud(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            firestore.collection("users").document(userId)
                .set(hashMapOf("isPremium" to true, "updatedAt" to System.currentTimeMillis()), SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncUserProgress(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            val progress = database.userProgressDao().getProgress("default")
                ?: return Result.success(Unit) // No progress yet
            val progressRef = firestore.collection("users").document(userId)
                .collection("data").document("progress")
            val data = hashMapOf(
                "currentStreak" to progress.currentStreak,
                "longestStreak" to progress.longestStreak,
                "lastActivityDate" to progress.lastActivityDate,
                "totalMinutes" to progress.totalMinutes,
                "totalExercises" to progress.totalExercises,
                "totalRecordings" to progress.totalRecordings,
                "dictionLevel" to progress.dictionLevel,
                "tempoLevel" to progress.tempoLevel,
                "intonationLevel" to progress.intonationLevel,
                "volumeLevel" to progress.volumeLevel,
                "structureLevel" to progress.structureLevel,
                "confidenceLevel" to progress.confidenceLevel,
                "fillerWordsLevel" to progress.fillerWordsLevel,
                "lastDictionLevel" to progress.lastDictionLevel,
                "lastTempoLevel" to progress.lastTempoLevel,
                "lastIntonationLevel" to progress.lastIntonationLevel,
                "lastVolumeLevel" to progress.lastVolumeLevel,
                "lastStructureLevel" to progress.lastStructureLevel,
                "lastConfidenceLevel" to progress.lastConfidenceLevel,
                "lastFillerWordsLevel" to progress.lastFillerWordsLevel,
                "peakDictionLevel" to progress.peakDictionLevel,
                "peakTempoLevel" to progress.peakTempoLevel,
                "peakIntonationLevel" to progress.peakIntonationLevel,
                "peakVolumeLevel" to progress.peakVolumeLevel,
                "peakStructureLevel" to progress.peakStructureLevel,
                "peakConfidenceLevel" to progress.peakConfidenceLevel,
                "peakFillerWordsLevel" to progress.peakFillerWordsLevel,
                "updatedAt" to System.currentTimeMillis()
            )
            progressRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncRecordings(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            // Get unsynced recordings
            val recordings = database.recordingDao().getAllRecordingsOnce()
                .filter { !it.isSynced }

            recordings.forEachIndexed { index, recording ->
                _syncState.value = SyncState.Syncing(index.toFloat() / recordings.size)

                // Upload audio file to Firebase Storage
                val file = File(recording.filePath)
                if (file.exists()) {
                    val storageRef = storage.reference
                        .child("recordings/$userId/${recording.id}.m4a")
                    storageRef.putFile(Uri.fromFile(file)).await()
                    val cloudUrl = storageRef.downloadUrl.await().toString()

                    // Save recording metadata to Firestore
                    val recordingRef = firestore.collection("users").document(userId)
                        .collection("recordings").document(recording.id)
                    val data = hashMapOf(
                        "durationMs" to recording.durationMs,
                        "type" to recording.type,
                        "contextId" to recording.contextId,
                        "transcription" to recording.transcription,
                        "isAnalyzed" to recording.isAnalyzed,
                        "createdAt" to recording.createdAt,
                        "exerciseId" to recording.exerciseId,
                        "cloudUrl" to cloudUrl
                    )
                    recordingRef.set(data).await()

                    // Mark as synced locally
                    database.recordingDao().update(
                        recording.copy(
                            cloudUrl = cloudUrl,
                            isSynced = true,
                            lastSyncedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncCourseProgress(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            val startedCourseIds = database.courseProgressDao().getStartedCourseIdsOnce()
            startedCourseIds.forEach { courseId ->
                val lessons = database.courseProgressDao().getCourseProgressOnce(courseId)
                val courseRef = firestore.collection("users").document(userId)
                    .collection("course_progress").document(courseId)
                val data = hashMapOf(
                    "lessons" to lessons.associate { it.lessonId to it.isCompleted },
                    "updatedAt" to System.currentTimeMillis()
                )
                courseRef.set(data, SetOptions.merge()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncAchievements(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            val achievements = database.achievementDao().getUnlockedOnce()
            val achievementsRef = firestore.collection("users").document(userId)
                .collection("data").document("achievements")
            val data = hashMapOf(
                "unlocked" to achievements.associate { it.id to it.unlockedAt },
                "updatedAt" to System.currentTimeMillis()
            )
            achievementsRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Upload diagnostic results to Firestore */
    suspend fun syncDiagnostics(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            val results = database.diagnosticResultDao().getAllResultsOnce()
            if (results.isEmpty()) return Result.success(Unit)

            results.forEach { result ->
                val docRef = firestore.collection("users").document(userId)
                    .collection("diagnostics").document(result.id)
                val data = hashMapOf(
                    "timestamp" to result.timestamp,
                    "diction" to result.diction,
                    "tempo" to result.tempo,
                    "intonation" to result.intonation,
                    "volume" to result.volume,
                    "structure" to result.structure,
                    "confidence" to result.confidence,
                    "fillerWords" to result.fillerWords,
                    "persuasiveness" to result.persuasiveness,
                    "recommendations" to result.recommendations,
                    "isInitial" to result.isInitial
                )
                docRef.set(data, SetOptions.merge()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Check if user has premium status (Firestore or hardcoded test accounts) */
    suspend fun checkPremiumStatus(): Boolean {
        val user = firebaseAuth.currentUser ?: return false
        val email = user.email

        // Hardcoded test/admin accounts
        val premiumEmails = setOf(
            "euheniy.loren@gmail.com"
        )
        if (email != null && email.lowercase() in premiumEmails) {
            userPreferencesDataStore.updatePremiumStatus(true)
            return true
        }

        // Check Firestore: users/{userId} → isPremium field
        return try {
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            val isPremium = userDoc.getBoolean("isPremium") ?: false
            if (isPremium) {
                userPreferencesDataStore.updatePremiumStatus(isPremium)
            }
            isPremium
        } catch (_: Exception) { false }
    }

    /** Save onboarding/diagnostic flags to Firestore */
    suspend fun saveUserFlags() {
        val userId = uid ?: return
        try {
            val flagsRef = firestore.collection("users").document(userId)
                .collection("data").document("flags")
            val data = hashMapOf(
                "hasCompletedOnboarding" to true,
                "hasCompletedDiagnostic" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            flagsRef.set(data, SetOptions.merge()).await()
        } catch (_: Exception) { }
    }

    /** Restore onboarding/diagnostic flags from Firestore after login */
    suspend fun restoreUserFlags(): Boolean {
        val userId = uid ?: return false
        return try {
            val flagsDoc = firestore.collection("users").document(userId)
                .collection("data").document("flags").get().await()
            if (flagsDoc.exists()) {
                val completedDiagnostic = flagsDoc.getBoolean("hasCompletedDiagnostic") ?: false
                val completedOnboarding = flagsDoc.getBoolean("hasCompletedOnboarding") ?: false
                if (completedOnboarding) {
                    userPreferencesDataStore.setOnboardingCompleted(true)
                }
                if (completedDiagnostic) {
                    userPreferencesDataStore.setDiagnosticCompleted(true)
                }
                completedDiagnostic
            } else false
        } catch (_: Exception) { false }
    }

    /** Restore all user data from Firestore to local DB after login */
    suspend fun restoreAllData(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            // 1. Restore user progress (skills, streak, stats)
            restoreUserProgress(userId)
            // 2. Restore course progress
            restoreCourseProgress(userId)
            // 3. Restore achievements
            restoreAchievements(userId)
            // 4. Restore diagnostic results
            restoreDiagnostics(userId)
            // 5. Restore recording metadata + download audio files
            restoreRecordings(userId)
            // 6. Restore user name
            restoreUserName(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun restoreUserProgress(userId: String) {
        val doc = firestore.collection("users").document(userId)
            .collection("data").document("progress").get().await()
        if (!doc.exists()) return

        val progress = UserProgressEntity(
            id = "default",
            currentStreak = (doc.getLong("currentStreak") ?: 0).toInt(),
            longestStreak = (doc.getLong("longestStreak") ?: 0).toInt(),
            lastActivityDate = doc.getLong("lastActivityDate"),
            totalMinutes = (doc.getLong("totalMinutes") ?: 0).toInt(),
            totalExercises = (doc.getLong("totalExercises") ?: 0).toInt(),
            totalRecordings = (doc.getLong("totalRecordings") ?: 0).toInt(),
            dictionLevel = (doc.getDouble("dictionLevel") ?: 1.0).toFloat(),
            tempoLevel = (doc.getDouble("tempoLevel") ?: 1.0).toFloat(),
            intonationLevel = (doc.getDouble("intonationLevel") ?: 1.0).toFloat(),
            volumeLevel = (doc.getDouble("volumeLevel") ?: 1.0).toFloat(),
            structureLevel = (doc.getDouble("structureLevel") ?: 1.0).toFloat(),
            confidenceLevel = (doc.getDouble("confidenceLevel") ?: 1.0).toFloat(),
            fillerWordsLevel = (doc.getDouble("fillerWordsLevel") ?: 1.0).toFloat(),
            lastDictionLevel = doc.getDouble("lastDictionLevel")?.toFloat(),
            lastTempoLevel = doc.getDouble("lastTempoLevel")?.toFloat(),
            lastIntonationLevel = doc.getDouble("lastIntonationLevel")?.toFloat(),
            lastVolumeLevel = doc.getDouble("lastVolumeLevel")?.toFloat(),
            lastStructureLevel = doc.getDouble("lastStructureLevel")?.toFloat(),
            lastConfidenceLevel = doc.getDouble("lastConfidenceLevel")?.toFloat(),
            lastFillerWordsLevel = doc.getDouble("lastFillerWordsLevel")?.toFloat(),
            peakDictionLevel = (doc.getDouble("peakDictionLevel") ?: 1.0).toFloat(),
            peakTempoLevel = (doc.getDouble("peakTempoLevel") ?: 1.0).toFloat(),
            peakIntonationLevel = (doc.getDouble("peakIntonationLevel") ?: 1.0).toFloat(),
            peakVolumeLevel = (doc.getDouble("peakVolumeLevel") ?: 1.0).toFloat(),
            peakStructureLevel = (doc.getDouble("peakStructureLevel") ?: 1.0).toFloat(),
            peakConfidenceLevel = (doc.getDouble("peakConfidenceLevel") ?: 1.0).toFloat(),
            peakFillerWordsLevel = (doc.getDouble("peakFillerWordsLevel") ?: 1.0).toFloat(),
            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
        )
        database.userProgressDao().insertOrUpdate(progress)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun restoreCourseProgress(userId: String) {
        val courseDocs = firestore.collection("users").document(userId)
            .collection("course_progress").get().await()

        for (courseDoc in courseDocs.documents) {
            val courseId = courseDoc.id
            val lessons = courseDoc.get("lessons") as? Map<String, Boolean> ?: continue
            for ((lessonId, isCompleted) in lessons) {
                val entity = CourseProgressEntity(
                    id = "${courseId}_${lessonId}",
                    courseId = courseId,
                    lessonId = lessonId,
                    isCompleted = isCompleted,
                    completedAt = if (isCompleted) courseDoc.getLong("updatedAt") else null,
                    attemptsCount = if (isCompleted) 1 else 0
                )
                database.courseProgressDao().insertOrUpdate(entity)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun restoreAchievements(userId: String) {
        val doc = firestore.collection("users").document(userId)
            .collection("data").document("achievements").get().await()
        if (!doc.exists()) return

        val unlocked = doc.get("unlocked") as? Map<String, Long> ?: return
        for ((id, timestamp) in unlocked) {
            // Insert achievement if not exists, then unlock it
            database.achievementDao().insertAll(listOf(AchievementEntity(id = id)))
            database.achievementDao().unlock(id, timestamp)
        }
    }

    private suspend fun restoreDiagnostics(userId: String) {
        val docs = firestore.collection("users").document(userId)
            .collection("diagnostics").get().await()

        for (doc in docs.documents) {
            val entity = DiagnosticResultEntity(
                id = doc.id,
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                diction = (doc.getLong("diction") ?: 50).toInt(),
                tempo = (doc.getLong("tempo") ?: 50).toInt(),
                intonation = (doc.getLong("intonation") ?: 50).toInt(),
                volume = (doc.getLong("volume") ?: 50).toInt(),
                structure = (doc.getLong("structure") ?: 50).toInt(),
                confidence = (doc.getLong("confidence") ?: 50).toInt(),
                fillerWords = (doc.getLong("fillerWords") ?: 50).toInt(),
                persuasiveness = (doc.getLong("persuasiveness") ?: 50).toInt(),
                recommendations = doc.getString("recommendations") ?: "[]",
                isInitial = doc.getBoolean("isInitial") ?: true
            )
            database.diagnosticResultDao().insert(entity)
        }
    }

    private suspend fun restoreRecordings(userId: String) {
        val docs = firestore.collection("users").document(userId)
            .collection("recordings").get().await()

        val outputDir = context.getExternalFilesDir(null)

        for (doc in docs.documents) {
            val cloudUrl = doc.getString("cloudUrl") ?: continue
            val recordingId = doc.id
            val type = doc.getString("type") ?: "lesson"

            // Create local file path
            val localFile = File(outputDir, "${recordingId}.m4a")

            // Download audio file from Firebase Storage
            try {
                if (!localFile.exists()) {
                    val storageRef = storage.getReferenceFromUrl(cloudUrl)
                    storageRef.getFile(localFile).await()
                }
            } catch (_: Exception) {
                // If download fails, still save metadata with cloud URL
            }

            val entity = RecordingEntity(
                id = recordingId,
                filePath = localFile.absolutePath,
                durationMs = doc.getLong("durationMs") ?: 0,
                type = type,
                contextId = doc.getString("contextId") ?: "",
                transcription = doc.getString("transcription"),
                isAnalyzed = doc.getBoolean("isAnalyzed") ?: false,
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                exerciseId = doc.getString("exerciseId"),
                cloudUrl = cloudUrl,
                isSynced = true,
                lastSyncedAt = System.currentTimeMillis()
            )
            database.recordingDao().insert(entity)
        }
    }

    private suspend fun restoreUserName(@Suppress("UNUSED_PARAMETER") userId: String) {
        val displayName = firebaseAuth.currentUser?.displayName
        if (!displayName.isNullOrBlank()) {
            userPreferencesDataStore.setUserName(displayName)
        }
    }

    override suspend fun fullSync(): Result<Unit> {
        return try {
            _syncState.value = SyncState.Syncing(0f)
            syncUserProfile()
            _syncState.value = SyncState.Syncing(0.15f)
            syncUserProgress()
            _syncState.value = SyncState.Syncing(0.3f)
            syncCourseProgress()
            _syncState.value = SyncState.Syncing(0.45f)
            syncAchievements()
            _syncState.value = SyncState.Syncing(0.6f)
            syncDiagnostics()
            _syncState.value = SyncState.Syncing(0.75f)
            syncRecordings()
            _syncState.value = SyncState.Success
            Result.success(Unit)
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Помилка синхронізації")
            Result.failure(e)
        }
    }

    /** Delete ALL user data from Firestore and Firebase Storage */
    suspend fun deleteAllCloudData(): Result<Unit> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            // Delete subcollections
            val collections = listOf("data", "course_progress", "diagnostics", "recordings")
            for (collName in collections) {
                val docs = firestore.collection("users").document(userId)
                    .collection(collName).get().await()
                for (doc in docs.documents) {
                    doc.reference.delete().await()
                }
            }
            // Delete user document
            firestore.collection("users").document(userId).delete().await()
            // Delete recordings from Storage
            try {
                val storageRef = storage.reference.child("recordings/$userId")
                val items = storageRef.listAll().await()
                for (item in items.items) {
                    item.delete().await()
                }
            } catch (_: Exception) { /* Storage folder may not exist */ }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadRecording(localPath: String, recordingId: String): Result<String> {
        val userId = uid ?: return Result.failure(Exception("Не авторизовано"))
        return try {
            val file = File(localPath)
            val storageRef = storage.reference
                .child("recordings/$userId/$recordingId.m4a")
            storageRef.putFile(Uri.fromFile(file)).await()
            val url = storageRef.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadRecording(cloudUrl: String, localPath: String): Result<Unit> {
        return try {
            val storageRef = storage.getReferenceFromUrl(cloudUrl)
            val localFile = File(localPath)
            storageRef.getFile(localFile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
