# Інструкції з усунення помилки компіляції

## Проблема: Kotlin compile daemon

Якщо ви бачите помилку `Could not connect to Kotlin compile daemon`, виконайте наступні кроки:

## Рішення 1: Базове очищення (Рекомендовано спробувати спочатку)

1. **У Android Studio:**
   - `File` → `Invalidate Caches` → `Invalidate and Restart`

2. **Або через командний рядок:**
   ```bash
   # Запустіть скрипт очищення
   clean-build.bat

   # Потім спробуйте побудувати проект
   gradlew.bat assembleDebug
   ```

## Рішення 2: Глибоке очищення

Якщо перше рішення не допомогло:

1. Запустіть скрипт глибокого очищення:
   ```bash
   deep-clean.bat
   ```

2. Синхронізуйте Gradle:
   - У Android Studio: `File` → `Sync Project with Gradle Files`

3. Спробуйте побудувати:
   ```bash
   gradlew.bat assembleDebug
   ```

## Рішення 3: Ручне очищення кешу

1. Закрийте Android Studio
2. Видаліть наступні папки:
   - `[PROJECT]\.gradle`
   - `[PROJECT]\build`
   - `[PROJECT]\app\build`
   - `C:\Users\[USER]\.gradle\caches`
   - `C:\Users\[USER]\.kotlin`

3. Відкрийте Android Studio і синхронізуйте Gradle

## Внесені зміни для виправлення

### 1. Оновлено `gradle.properties`:
- Збільшено пам'ять для Gradle: `-Xmx4096m`
- Додано налаштування Kotlin daemon: `kotlin.daemon.jvmargs=-Xmx2048m`
- Вимкнено incremental compilation: `kotlin.incremental=false`
- Увімкнено fallback strategy: `kotlin.daemon.useFallbackStrategy=true`
- Тимчасово вимкнено caching: `org.gradle.caching=false`

### 2. Оновлено версії в `build.gradle.kts`:
- Android Gradle Plugin: `8.13.1` → `8.2.0` (стабільна версія)
- Kotlin: `1.9.24` → `1.9.21` (перевірена сумісність)
- Compose Compiler: `1.5.14` → `1.5.7` (відповідає Kotlin 1.9.21)

## Додаткові поради

1. **Переконайтеся, що Java встановлено:**
   - Перевірте: `java -version` в командному рядку
   - Android Studio зазвичай постачається з власним JDK

2. **Перевірте наявність достатньої пам'ті:**
   - Мінімум 8 GB RAM рекомендовано для Android розробки
   - Закрийте інші програми під час компіляції

3. **Використовуйте Android Studio:**
   - Компіляція через Android Studio часто більш стабільна
   - Android Studio автоматично налаштовує Java/JDK

4. **Якщо проблема залишається:**
   - Перевірте антивірус (може блокувати Gradle daemon)
   - Перевірте файрвол (може блокувати з'єднання)
   - Спробуйте компілювати з правами адміністратора

## Повернення налаштувань

Якщо хочете повернути caching після успішної компіляції:

У `gradle.properties` змініть:
```properties
org.gradle.caching=false  →  org.gradle.caching=true
kotlin.incremental=false  →  kotlin.incremental=true
```

Це прискорить наступні збірки.
