Виправ помилки поетапно:

### КРОК 1: Domain models - додай відсутні параметри

Achievement.kt - додай параметри: icon, tier (з default values)
Course.kt - додай: estimatedMinutes, order (з default values)  
Lesson.kt - додай: completedLessons (з default value)

### КРОК 2: AudioPlayerUtil - створи stub

Створи utils/audio/AudioPlayerUtil.kt з базовими методами:
- play(filePath: String)
- pause()
- stop()
- release()
Поки що пусті реалізації.

### КРОК 3: DAO методи - додай відсутні

UserProgressDao - додай методи:
- getUserProgressOnce(): UserProgressEntity?
- insertOrUpdate(progress: UserProgressEntity)
- updateSkillLevels(...)

DiagnosticResultDao - додай:
- getInitialDiagnostic(): Flow<DiagnosticResultEntity?>

RecordingDao - додай:
- getAllRecordings(): Flow<List<RecordingEntity>>

### КРОК 4: UserPreferencesDataStore - додай методи

- hasCompletedOnboarding: Flow<Boolean>
- setDailyTrainingMinutes(minutes: Int)

### КРОК 5: Виправ exerciseId параметри

Подивись де викликається з exerciseId і виправ сигнатури.

Після кожного кроку перевіряй компіляцію.