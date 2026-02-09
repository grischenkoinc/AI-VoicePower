package com.aivoicepower.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Запис аудіо з вправи (BaseExercise)
 *
 * Підтримує всі типи вправ:
 * - LessonExercise (уроки з курсів)
 * - WarmupExercise (розминка)
 * - ImprovisationExercise (імпровізація)
 * - Diagnostic (діагностика)
 */
@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey
    val id: String,

    /** Шлях до аудіо файлу в локальному сховищі */
    val filePath: String,

    /** Тривалість запису в мілісекундах */
    val durationMs: Long,

    /**
     * Тип вправи (відповідає BaseExercise.getExerciseType())
     * Можливі значення: "lesson", "warmup", "improvisation", "diagnostic"
     */
    val type: String,

    /**
     * Додатковий контекст для вправи:
     * - Для lesson: courseId
     * - Для improvisation: scenarioId або topicId
     * - Для diagnostic: "initial" або "progress_check"
     * - Для warmup: категорія (articulation, breathing, voice)
     */
    val contextId: String,

    /** Транскрипція тексту (якщо доступна) */
    val transcription: String? = null,

    /** Чи проаналізований запис AI (метрики обчислені) */
    val isAnalyzed: Boolean = false,

    /** Timestamp створення запису */
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * ID вправи (BaseExercise.id)
     * Дозволяє знайти, яка саме вправа була виконана
     * Використовується для аналітики: "які вправи покращили навичку?"
     */
    val exerciseId: String? = null,

    /** Firebase Storage URL (після синхронізації) */
    val cloudUrl: String? = null,

    /** Чи синхронізований з хмарою */
    val isSynced: Boolean = false,

    /** Час останньої синхронізації */
    val lastSyncedAt: Long? = null
)
