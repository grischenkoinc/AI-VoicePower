package com.aivoicepower.data.repository

import android.util.Log
import com.aivoicepower.data.local.database.dao.AchievementDao
import com.aivoicepower.domain.model.user.Achievement
import com.aivoicepower.domain.model.user.AchievementDefinitions
import com.aivoicepower.domain.repository.AchievementRepository
import com.aivoicepower.domain.service.AchievementChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao,
    private val achievementChecker: AchievementChecker
) : AchievementRepository {

    private val _pendingCelebrations = MutableSharedFlow<Achievement>(replay = 0, extraBufferCapacity = 10)
    override val pendingCelebrations: SharedFlow<Achievement> = _pendingCelebrations.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            achievementChecker.seedAchievements()
            Log.d("AchievementRepo", "Seeded ${AchievementDefinitions.all.size} achievements")
        }
    }

    override fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAll().map { entities ->
            entities.mapNotNull { entity ->
                val def = AchievementDefinitions.getById(entity.id) ?: return@mapNotNull null
                val progress = if (entity.unlockedAt == null) {
                    achievementChecker.getProgress(entity.id)
                } else null
                Achievement(
                    id = def.id,
                    category = def.category,
                    title = def.title,
                    description = def.description,
                    icon = def.icon,
                    unlockedAt = entity.unlockedAt,
                    progress = progress,
                    target = def.target
                )
            }
        }
    }

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlocked().map { entities ->
            entities.mapNotNull { entity ->
                val def = AchievementDefinitions.getById(entity.id) ?: return@mapNotNull null
                Achievement(
                    id = def.id,
                    category = def.category,
                    title = def.title,
                    description = def.description,
                    icon = def.icon,
                    unlockedAt = entity.unlockedAt,
                    progress = def.target,
                    target = def.target
                )
            }
        }
    }

    override suspend fun checkAndUnlock(): List<Achievement> {
        val newlyUnlocked = achievementChecker.checkAll()
        for (achievement in newlyUnlocked) {
            _pendingCelebrations.emit(achievement)
        }
        return newlyUnlocked
    }

    override suspend fun dismissCelebration() {
        // No-op — SharedFlow handles backpressure automatically
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AchievementRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository
}
