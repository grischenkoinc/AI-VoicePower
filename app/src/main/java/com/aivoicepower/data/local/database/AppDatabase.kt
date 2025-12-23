package com.aivoicepower.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aivoicepower.data.local.database.dao.MessageDao
import com.aivoicepower.data.local.database.dao.UserProgressDao
import com.aivoicepower.data.local.database.dao.WarmupCompletionDao
import com.aivoicepower.data.local.database.entity.MessageEntity
import com.aivoicepower.data.local.database.entity.UserProgressEntity
import com.aivoicepower.data.local.database.entity.WarmupCompletionEntity

@Database(
    entities = [
        MessageEntity::class,
        UserProgressEntity::class,
        WarmupCompletionEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun warmupCompletionDao(): WarmupCompletionDao
}
