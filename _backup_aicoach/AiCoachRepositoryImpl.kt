package com.aivoicepower.data.repository

import com.aivoicepower.data.local.database.dao.DiagnosticResultDao
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.dao.UserProfileDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.entity.MessageEntity
import com.aivoicepower.data.local.datastore.UserPreferencesDataStore
import com.aivoicepower.data.remote.GeminiApiClient
import com.aivoicepower.domain.model.chat.ConversationContext
import com.aivoicepower.domain.model.chat.Message
import com.aivoicepower.domain.model.chat.MessageRole
import com.aivoicepower.domain.model.user.DiagnosticResult
import com.aivoicepower.domain.model.user.SkillType
import com.aivoicepower.domain.model.user.UserGoal
import com.aivoicepower.domain.model.user.UserProfile
import com.aivoicepower.domain.model.user.UserProgress
import com.aivoicepower.domain.model.user.toDisplayString
import com.aivoicepower.domain.repository.AiCoachRepository
import com.aivoicepower.utils.constants.FreeTierLimits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiCoachRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val userProfileDao: UserProfileDao,
    private val userProgressDao: UserProgressDao,
    private val diagnosticResultDao: DiagnosticResultDao,
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
            // Check free tier limit
            if (!canSendMessage()) {
                return Result.failure(
                    FreeTierLimitException(
                        "Досягнуто ліміт повідомлень (${FreeTierLimits.FREE_MESSAGES_PER_DAY}/день). Оновіться до Premium."
                    )
                )
            }

            // Save user message
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(userMessage.toEntity())

            // Get conversation context
            val context = getUserContext()
            val history = messageDao.getRecentMessages(limit = 10)
                .reversed() // Oldest first
                .map { it.toMessage() }

            // Generate AI response
            val aiResponseResult = geminiApiClient.generateCoachResponse(
                userMessage = content,
                conversationHistory = history,
                userContext = context
            )

            aiResponseResult.fold(
                onSuccess = { responseText ->
                    // Save AI message
                    val aiMessage = Message(
                        id = UUID.randomUUID().toString(),
                        role = MessageRole.ASSISTANT,
                        content = responseText,
                        timestamp = System.currentTimeMillis()
                    )
                    messageDao.insertMessage(aiMessage.toEntity())

                    // Increment message count for free tier
                    val prefs = userPreferencesDataStore.userPreferencesFlow.first()
                    if (!prefs.isPremium) {
                        userPreferencesDataStore.incrementFreeMessages()
                    }

                    Result.success(aiMessage)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearConversation() {
        messageDao.clearConversation()
    }

    override suspend fun getUserContext(): ConversationContext {
        val profileEntity = userProfileDao.getUserProfileOnce()
        val progressEntity = userProgressDao.getUserProgressOnce()
        val diagnosticEntity = diagnosticResultDao.getLatestDiagnostic().firstOrNull()

        val profile = profileEntity?.toUserProfile()
        val progress = progressEntity?.toUserProgress()
        val diagnostic = diagnosticEntity?.toDiagnosticResult()

        // Find weakest skills
        val skillLevels = progress?.skillLevels ?: emptyMap()
        val weakestSkills = skillLevels.entries
            .sortedBy { it.value }
            .take(3)
            .map { (skill, level) -> skill.toDisplayString() to level }

        // Recent activity summary
        val recentActivity = buildString {
            progress?.let {
                if (it.currentStreak > 0) {
                    append("Активний ${it.currentStreak} днів поспіль. ")
                }
                if (it.totalExercises > 0) {
                    append("Виконано ${it.totalExercises} вправ. ")
                }
                if (it.totalMinutes > 0) {
                    append("Загалом ${it.totalMinutes} хвилин тренувань. ")
                }
            }
        }

        return ConversationContext(
            userProfile = profile,
            userProgress = progress,
            diagnosticResult = diagnostic,
            weakestSkills = weakestSkills,
            recentActivity = recentActivity.ifBlank { "Користувач щойно почав" }
        )
    }

    override suspend fun getQuickActions(): List<String> {
        val context = getUserContext()
        val result = geminiApiClient.generateQuickActions(context)
        return result.getOrElse {
            getDefaultQuickActions()
        }
    }

    override suspend fun canSendMessage(): Boolean {
        val prefs = userPreferencesDataStore.userPreferencesFlow.first()
        if (prefs.isPremium) return true

        val todayCount = getTodayMessagesCount()
        return todayCount < FreeTierLimits.FREE_MESSAGES_PER_DAY
    }

    override suspend fun getTodayMessagesCount(): Int {
        return messageDao.getTodayUserMessagesCount()
    }

    override suspend fun getRemainingFreeMessages(): Int {
        val prefs = userPreferencesDataStore.userPreferencesFlow.first()
        if (prefs.isPremium) return Int.MAX_VALUE

        val todayCount = getTodayMessagesCount()
        return (FreeTierLimits.FREE_MESSAGES_PER_DAY - todayCount).coerceAtLeast(0)
    }

    private fun getDefaultQuickActions(): List<String> = listOf(
        "Дай поради для покращення мовлення",
        "Як підготуватися до виступу?",
        "Які вправи мені підходять?",
        "Як позбутися нервозності?",
        "Підготуй мене до співбесіди"
    )

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

    private fun com.aivoicepower.data.local.database.entity.UserProfileEntity.toUserProfile(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            goal = try {
                UserGoal.valueOf(goal)
            } catch (e: Exception) {
                UserGoal.GENERAL
            },
            dailyMinutes = dailyMinutes,
            createdAt = createdAt,
            isPremium = isPremium
        )
    }

    private fun com.aivoicepower.data.local.database.entity.UserProgressEntity.toUserProgress(): UserProgress {
        return UserProgress(
            userId = id,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalMinutes = totalMinutes,
            totalExercises = totalExercises,
            totalRecordings = totalRecordings,
            lastActivityDate = lastActivityDate,
            skillLevels = mapOf(
                SkillType.DICTION to dictionLevel,
                SkillType.TEMPO to tempoLevel,
                SkillType.INTONATION to intonationLevel,
                SkillType.VOLUME to volumeLevel,
                SkillType.STRUCTURE to structureLevel,
                SkillType.CONFIDENCE to confidenceLevel,
                SkillType.FILLER_WORDS to fillerWordsLevel
            ),
            achievements = emptyList() // Loaded separately
        )
    }

    private fun com.aivoicepower.data.local.database.entity.DiagnosticResultEntity.toDiagnosticResult(): DiagnosticResult {
        return DiagnosticResult(
            id = id,
            userId = "default_user",
            timestamp = timestamp,
            diction = diction,
            tempo = tempo,
            intonation = intonation,
            volume = volume,
            structure = structure,
            confidence = confidence,
            fillerWords = fillerWords,
            recordingIds = emptyList(),
            recommendations = recommendations.split("|").filter { it.isNotBlank() },
            isInitial = isInitial
        )
    }
}

/**
 * Exception for free tier limits
 */
class FreeTierLimitException(message: String) : Exception(message)
