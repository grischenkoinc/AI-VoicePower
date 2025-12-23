package com.aivoicepower.domain.repository

import com.aivoicepower.domain.model.chat.ConversationContext
import com.aivoicepower.domain.model.chat.Message
import kotlinx.coroutines.flow.Flow

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
