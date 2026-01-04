В артикуляційних вправах немає кнопки "Повернутись". Потрібно додати.

1. Знайди UI для артикуляційних вправ в LessonScreen.kt:
grep -B10 -A30 "Виконано\|ARTICULATION\|ArticulationExercise" app/src/main/java/com/aivoicepower/ui/screens/lesson/LessonScreen.kt

2. Знайди кнопку "Виконано" і заміни її на Row з двома кнопками:
```kotlin
// Замість однієї кнопки "Виконано":
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    // Кнопка "Повернутись" - показувати якщо не перша вправа
    if (currentExerciseIndex > 0) {
        OutlinedButton(
            onClick = { viewModel.onPreviousExercise() },
            modifier = Modifier.weight(1f)
        ) {
            Text("Повернутись")
        }
    }
    
    // Кнопка "Виконано"
    Button(
        onClick = { viewModel.onExerciseCompleted() },  // або onNextExercise()
        modifier = Modifier.weight(1f)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Виконано")
    }
}
```

3. Переконайся що currentExerciseIndex доступний в цьому composable. Якщо ні - передай його як параметр.

4. Компіляція:
./gradlew assembleDebug