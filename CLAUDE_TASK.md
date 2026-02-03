✅ ВИКОНАНО - Виправлення UX і кольорів

Зміни у Швидкому доступі:
- Скоромовки: змінено на сріблястий градієнт
- Швидка розминка: змінено на бірюзово-синій градієнт

Зміни на екрані Найслабша навичка:
- Кнопку Назад піднято вище (verticalAlignment = Top)
- Додано spacing між заголовком і кнопкою
- Зменшено padding кнопки для компактності
- Текст: "Рекомендації експертів" → "Рекомендації АІ тренера"
- Прибрано shadow рамку з інформаційного блоку

Виправлено перескакування вправ у Розминці:
- Додано `.clickable(enabled = false) {}` до основного Box контенту
- Додано блокування кліків на completion overlay
- Виправлено в ArticulationExerciseDialog.kt
- Виправлено в BreathingExerciseDialog.kt
- Виправлено в VoiceExerciseDialog.kt

Змінені файли:
- HomeScreen.kt (кольори Quick Access)
- WeakestSkillScreen.kt (кнопка Назад, текст, shadow)
- ArticulationExerciseDialog.kt (блокування кліків)
- BreathingExerciseDialog.kt (блокування кліків)
- VoiceExerciseDialog.kt (блокування кліків)
