Перевір чи є контент провайдери в v2-complete-redesign:

=== КРОК 1: Що є в v2 ===

echo "=== Content providers в V2 ==="
git ls-tree -r v2-complete-redesign --name-only | grep -i "content\|provider\|tonguetwister\|reading"

echo "=== Детальніше ==="
git ls-tree -r v2-complete-redesign --name-only | grep "data/content"

=== КРОК 2: Що є в main ===

echo "=== Content providers в MAIN ==="
ls app/src/main/java/com/aivoicepower/data/content/*.kt 2>/dev/null

=== КРОК 3: Порівняй кількість файлів ===

echo "=== Файлів в v2 data/content ==="
git ls-tree -r v2-complete-redesign --name-only | grep "data/content" | wc -l

echo "=== Файлів в main data/content ==="
ls app/src/main/java/com/aivoicepower/data/content/*.kt 2>/dev/null | wc -l

=== КРОК 4: Якщо в v2 більше — перенеси ВСЕ ===

git checkout v2-complete-redesign -- app/src/main/java/com/aivoicepower/data/content/

=== КРОК 5: Також перевір Settings та Premium ===

echo "=== Settings в v2 ==="
git ls-tree -r v2-complete-redesign --name-only | grep -i "settings"

echo "=== Premium в v2 ==="
git ls-tree -r v2-complete-redesign --name-only | grep -i "premium\|paywall\|billing"

=== КРОК 6: Перенеси якщо є ===

# Якщо є settings
git checkout v2-complete-redesign -- app/src/main/java/com/aivoicepower/ui/screens/settings/ 2>/dev/null

# Якщо є premium
git checkout v2-complete-redesign -- app/src/main/java/com/aivoicepower/ui/screens/premium/ 2>/dev/null

=== КРОК 7: Компіляція ===

./gradlew assembleDebug 2>&1 | grep -E "error:" | head -30

=== РЕЗУЛЬТАТ ===

Покажи:
1. Скільки файлів контенту було в v2
2. Скільки було в main
3. Що перенесено
4. Результат компіляції