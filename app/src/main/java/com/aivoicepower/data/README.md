# Data Layer

Шар даних (Data Layer) відповідає за роботу з джерелами даних.

## Структура:

- **local/** - Локальне збереження даних
  - **database/** - Room Database (DAO, Entity)
  - **datastore/** - DataStore для налаштувань

- **remote/** - Віддалені джерела даних (API)
  - Gemini API клієнт

- **repository/** - Імплементації репозиторіїв
  - Реалізація інтерфейсів з domain layer

- **model/** - Data моделі (DTO, Entity)
