# TypeLess

Android-приложение для быстрой вставки сохранённых текстовых шаблонов в другие приложения.

[Страница приложения в RuStore](https://www.rustore.ru/catalog/app/com.klim.typeless)

## Возможности

- Создание и редактирование текстовых шаблонов.
- Автоматическая замена триггеров через Android Accessibility API.
- Организация шаблонов по папкам.
- Поддержка переменных в шаблонах.
- Импорт и экспорт пользовательских данных.
- Светлая и тёмная темы.
- Временная разблокировка дополнительных возможностей за rewarded-рекламу.

## Технологии

- Kotlin
- Jetpack Compose и Material 3
- MVVM и разделение на `data`, `domain`, `ui`
- Hilt
- Room
- DataStore
- Coroutines и Flow
- Kotlin Serialization
- Android Accessibility API
- Yandex Mobile Ads SDK

## Архитектура

```text
app/src/main/java/com/klim/typeless/
├── data/       # Room, DataStore и реализации репозиториев
├── di/         # Hilt-модули
├── domain/     # модели, контракты репозиториев и use cases
├── service/    # AccessibilityService
├── ui/         # Compose-экраны и ViewModel
└── util/       # вспомогательная логика
```

Главная функция реализована через `AccessibilityService`. Сервис отслеживает изменение текста, распознаёт триггер и заменяет его содержимым шаблона. При частых событиях предыдущая coroutine-задача отменяется, чтобы результат устаревшей обработки не применялся к новому состоянию поля.

Данные шаблонов хранятся в Room, пользовательские настройки — в DataStore. Зависимости предоставляются через Hilt.

## Требования

- Android Studio с поддержкой Kotlin 2.x
- JDK 11
- Android SDK 36
- Android 8.0 (API 26) и выше

## Запуск

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/KlimKlimKlimKlim/TypeLess.git
   cd TypeLess
   ```

2. Откройте проект в Android Studio и дождитесь Gradle Sync.
3. Запустите debug-сборку.
4. При первом запуске предоставьте приложению доступ к специальным возможностям Android.

Для debug-сборки signing-файлы не нужны. Конфигурация release-подписи хранится локально и не входит в репозиторий.

## Проверки

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

## Разрешения и приватность

Для автоматической подстановки TypeLess использует Android Accessibility API. Доступ включается пользователем вручную в системных настройках. Пользовательские шаблоны хранятся локально на устройстве.

## Автор

Клим Трофимов — [GitHub](https://github.com/KlimKlimKlimKlim)
