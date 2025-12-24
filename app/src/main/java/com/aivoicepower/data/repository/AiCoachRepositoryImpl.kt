package com.aivoicepower.data.repository

import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import com.aivoicepower.data.chat.MessageRole
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.entity.MessageEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
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
 * TODO Phase 6: Implement full AI Coach functionality with Gemini API
 *
 * This is a stub implementation to satisfy dependency injection.
 * Full implementation pending Gemini API integration.
 */
@Singleton
class AiCoachRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : AiCoachRepository {

    override fun getMessagesFlow(): Flow<List<Message>> {
        return messageDao.getMessagesFlow().map { entities ->
            entities.map { it.toMessage() }
        }
    }

    override suspend fun sendMessage(content: String): Result<Message> {
        return try {
            // TODO: Implement Gemini API call
            // For now, just save the user message and return a stub AI response

            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(userMessage.toEntity())

            val aiMessage = Message(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                content = "AI Coach response will be implemented in Phase 6.",
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(aiMessage.toEntity())

            Result.success(aiMessage)
        } catch (e: Exception) {
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
        return listOf(
            "Дай поради для покращення мовлення",
            "Як підготуватися до виступу?",
            "Які вправи мені підходять?"
        )
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
