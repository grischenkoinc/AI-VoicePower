package com.aivoicepower.data.repository

import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementType
import com.aivoicepower.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor() : AchievementRepository {

    private val achievementsFlow = MutableStateFlow(createDefaultAchievements())

    override fun getAllAchievements(): Flow<List<Achievement>> = achievementsFlow

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementsFlow.map { achievements ->
            achievements.filter { it.isUnlocked }
        }
    }

    override suspend fun unlockAchievement(achievementId: String) {
        val updated = achievementsFlow.value.map { achievement ->
            if (achievement.id == achievementId) {
                achievement.copy(unlockedAt = System.currentTimeMillis())
            } else {
                achievement
            }
        }
        achievementsFlow.value = updated
    }

    override suspend fun updateProgress(achievementId: String, progress: Int) {
        val updated = achievementsFlow.value.map { achievement ->
            if (achievement.id == achievementId) {
                achievement.copy(progress = progress)
            } else {
                achievement
            }
        }
        achievementsFlow.value = updated
    }

    private fun createDefaultAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "streak_7",
                type = AchievementType.STREAK_7,
                title = "Тиждень практики",
                description = "Займайся 7 днiв поспiль",
                iconRes = 0,
                unlockedAt = null,
                progress = 0,
                target = 7
            ),
            Achievement(
                id = "streak_30",
                type = AchievementType.STREAK_30,
                title = "Мiсяць постiйностi",
                description = "Займайся 30 днiв поспiль",
                iconRes = 0,
                unlockedAt = null,
                progress = 0,
                target = 30
            ),
            Achievement(
                id = "first_course",
                type = AchievementType.FIRST_COURSE,
                title = "Перший крок",
                description = "Заверши перший курс",
                iconRes = 0,
                unlockedAt = null,
                progress = 0,
                target = 1
            ),
            Achievement(
                id = "diction_90",
                type = AchievementType.DICTION_90,
                title = "Iдеальна дикцiя",
                description = "Досягни 90 балiв у дикцiї",
                iconRes = 0,
                unlockedAt = null,
                progress = 0,
                target = 90
            ),
            Achievement(
                id = "improviser_50",
                type = AchievementType.IMPROVISER_50,
                title = "Iмпровiзатор",
                description = "Виконай 50 iмпровiзацiйних вправ",
                iconRes = 0,
                unlockedAt = null,
                progress = 0,
                target = 50
            ),
            Achievement(
                id = "zero_fillers",
                type = AchievementType.ZERO_FILLERS,
                title = "Чисте мовлення",
                description = "Зроби запис без слiв-паразитiв",
                iconRes = 0,
                unlockedAt = null,
                progress = null,
                target = null
            )
        )
    }
}
