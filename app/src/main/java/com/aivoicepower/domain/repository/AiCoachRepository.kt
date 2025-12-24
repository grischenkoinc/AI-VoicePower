package com.aivoicepower.domain.repository

import com.aivoicepower.data.chat.ConversationContext
import com.aivoicepower.data.chat.Message
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for AI Coach functionality
 *
 * TODO Phase 6: Implement this interface when domain models are ready
 */
interface AiCoachRepository {
    fun getMessagesFlow(): Flow<List<Message>>
    suspend fun sendMessage(content: String): Result<Message>
    suspend fun clearConversation()
    suspend fun getUserContext(): ConversationContext
    suspend fun getQuickActions(): List<String>
    suspend fun canSendMessage(): Boolean
    suspend fun getTodayMessagesCount(): Int
    suspend fun getRemainingFreeMessages(): Int
}
