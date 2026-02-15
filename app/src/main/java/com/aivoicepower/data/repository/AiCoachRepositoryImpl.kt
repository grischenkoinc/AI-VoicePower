package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.data.local.database.dao.CourseProgressDao
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.dao.RecordingDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.MessageEntity
import com.aivoicepower.data.local.database.entity.RecordingEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.AiPrompts
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.domain.repository.AiCoachRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Coach Repository with Gemini API integration
 */
@Singleton
class AiCoachRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val geminiApiClient: GeminiApiClient,
    private val userProgressDao: UserProgressDao,
    private val recordingDao: RecordingDao,
    private val courseProgressDao: CourseProgressDao
) : AiCoachRepository {

    override fun getMessagesFlow(): Flow<List<Message>> {
        return messageDao.getMessagesFlow().map { entities ->
            entities.map { it.toMessage() }
        }
    }

    override suspend fun sendMessage(content: String): Result<Message> {
        return try {
            Log.d("AiCoach", "Sending message: $content")

            // Save user message
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(userMessage.toEntity())

            // Get conversation history
            val conversationHistory = messageDao.getMessagesFlow().first().map { it.toMessage() }

            // Get user context
            val userContext = getUserContext()

            Log.d("AiCoach", "Calling Gemini API with ${conversationHistory.size} messages in history")

            // Call Gemini API
            val geminiResult = geminiApiClient.generateCoachResponse(
                userMessage = content,
                conversationHistory = conversationHistory,
                userContext = userContext
            )

            if (geminiResult.isFailure) {
                Log.e("AiCoach", "Gemini API error", geminiResult.exceptionOrNull())
                throw geminiResult.exceptionOrNull() ?: Exception("Unknown Gemini error")
            }

            val aiResponseContent = geminiResult.getOrThrow()
            Log.d("AiCoach", "Gemini response received: ${aiResponseContent.take(100)}...")

            // Save AI response
            val aiMessage = Message(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                content = aiResponseContent,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(aiMessage.toEntity())

            Result.success(aiMessage)
        } catch (e: Exception) {
            Log.e("AiCoach", "Error in sendMessage", e)
            Result.failure(e)
        }
    }

    override suspend fun clearConversation() {
        messageDao.clearConversation()
    }

    override suspend fun getUserContext(): ConversationContext {
        return try {
            val prefs = userPreferencesDataStore.userPreferencesFlow.first()

            val progress = userProgressDao.getProgress()
            val skillLevels = if (progress != null) mapOf(
                "Дикція" to progress.dictionLevel.toInt(),
                "Темп" to progress.tempoLevel.toInt(),
                "Інтонація" to progress.intonationLevel.toInt(),
                "Гучність" to progress.volumeLevel.toInt(),
                "Структура" to progress.structureLevel.toInt(),
                "Впевненість" to progress.confidenceLevel.toInt(),
                "Без слів-паразитів" to progress.fillerWordsLevel.toInt()
            ) else emptyMap()

            val weakestSkills = skillLevels.entries
                .sortedBy { it.value }
                .take(3)
                .map { Pair(it.key, it.value) }

            val currentStreak = progress?.currentStreak ?: 0
            val totalExercises = progress?.totalExercises ?: 0

            val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
            val recentRecordings = recordingDao.getRecordingsSince(sevenDaysAgo)
            val recentActivity = buildRecentActivity(recentRecordings, totalExercises)

            ConversationContext(
                userName = prefs.userName,
                userGoal = prefs.userGoal,
                skillLevels = skillLevels,
                weakestSkills = weakestSkills,
                currentStreak = currentStreak,
                totalExercises = totalExercises,
                recentActivity = recentActivity
            )
        } catch (e: Exception) {
            Log.e("AiCoach", "Error building user context", e)
            ConversationContext.empty()
        }
    }

    override suspend fun getQuickActions(): List<String> {
        return try {
            Log.d("AiCoach", "Generating quick actions via Gemini")

            val userContext = getUserContext()
            val result = geminiApiClient.generateQuickActions(userContext)

            if (result.isSuccess) {
                val actions = result.getOrThrow()
                Log.d("AiCoach", "Generated ${actions.size} quick actions")
                actions
            } else {
                Log.w("AiCoach", "Failed to generate quick actions, using defaults")
                AiPrompts.DEFAULT_QUICK_ACTIONS
            }
        } catch (e: Exception) {
            Log.e("AiCoach", "Error generating quick actions", e)
            AiPrompts.DEFAULT_QUICK_ACTIONS
        }
    }

    override suspend fun canSendMessage(): Boolean {
        val prefs = userPreferencesDataStore.userPreferencesFlow.first()
        if (prefs.isPremium) return true

        val todayCount = getTodayMessagesCount()
        return todayCount < 10 // Free tier limit
    }

    override suspend fun getTodayMessagesCount(): Int {
        return messageDao.getTodayUserMessagesCount()
    }

    override suspend fun getRemainingFreeMessages(): Int {
        val prefs = userPreferencesDataStore.userPreferencesFlow.first()
        if (prefs.isPremium) return Int.MAX_VALUE

        val todayCount = getTodayMessagesCount()
        return (10 - todayCount).coerceAtLeast(0)
    }

    private fun buildRecentActivity(
        recentRecordings: List<RecordingEntity>,
        totalExercises: Int
    ): String {
        if (recentRecordings.isEmpty()) {
            return if (totalExercises == 0) "Користувач щойно почав"
            else "Не тренувався останні 7 днів"
        }
        val count = recentRecordings.size
        val minutes = (recentRecordings.sumOf { it.durationMs } / 60000).toInt()
        val types = recentRecordings.map { it.type }.toSet()
        val typesText = buildList {
            if ("lesson" in types) add("уроки")
            if ("improvisation" in types) add("імпровізації")
            if ("warmup" in types) add("розминки")
        }.joinToString(", ").ifEmpty { "вправи" }
        return "$count записів ($minutes хв) за 7 днів: $typesText"
    }

    // ===== MAPPERS =====

    private fun MessageEntity.toMessage(): Message {
        return Message(
            id = id,
            role = when (role) {
                "user" -> MessageRole.USER
                "assistant" -> MessageRole.ASSISTANT
                "system" -> MessageRole.SYSTEM
                else -> MessageRole.USER
            },
            content = content,
            timestamp = timestamp
        )
    }

    private fun Message.toEntity(): MessageEntity {
        return MessageEntity(
            id = id,
            role = when (role) {
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "assistant"
                MessageRole.SYSTEM -> "system"
            },
            content = content,
            timestamp = timestamp
        )
    }
}

/**
 * Hilt module for AiCoachRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiCoachRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAiCoachRepository(
        impl: AiCoachRepositoryImpl
    ): AiCoachRepository
}
