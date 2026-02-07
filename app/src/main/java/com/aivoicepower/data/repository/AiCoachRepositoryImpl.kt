package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.entity.MessageEntity
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
    private val geminiApiClient: GeminiApiClient
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
        return ConversationContext.empty()
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
