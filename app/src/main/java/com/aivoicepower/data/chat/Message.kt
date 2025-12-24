package com.aivoicepower.data.chat

/**
 * Domain model for chat message
 */
data class Message(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long,
    val metadata: MessageMetadata? = null
)

/**
 * Role of the message sender
 */
enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

/**
 * Additional metadata for messages
 */
data class MessageMetadata(
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val tokensUsed: Int? = null,
    val processingTimeMs: Long? = null
)
