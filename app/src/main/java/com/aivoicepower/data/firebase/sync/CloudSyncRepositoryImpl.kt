package com.aivoicepower.data.firebase.sync

import android.net.Uri
import com.aivoicepower.data.local.database.AppDatabase
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
    private val userPreferencesDataStore: UserPreferencesDataStore
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

    override suspend fun fullSync(): Result<Unit> {
        return try {
            _syncState.value = SyncState.Syncing(0f)
            syncUserProfile()
            _syncState.value = SyncState.Syncing(0.2f)
            syncUserProgress()
            _syncState.value = SyncState.Syncing(0.4f)
            syncCourseProgress()
            _syncState.value = SyncState.Syncing(0.6f)
            syncAchievements()
            _syncState.value = SyncState.Syncing(0.8f)
            syncRecordings()
            _syncState.value = SyncState.Success
            Result.success(Unit)
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Помилка синхронізації")
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
