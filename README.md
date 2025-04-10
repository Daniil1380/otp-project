
---

# OTP Project

Это проект для работы с механикой OTP (одноразовых паролей), включающий регистрацию/аутентификацию пользователей, генерацию и валидацию OTP, а также возможности административного управления.

## Содержание

- [Функциональность](#функциональность)
- [Требования](#требования)
- [Установка и запуск](#установка-и-запуск)
- [Как пользоваться сервисом](#как-пользоваться-сервисом)
- [Поддерживаемые команды и эндпоинты](#поддерживаемые-команды-и-эндпоинты)
- [Тестирование](#тестирование)
- [Конфигурация](#конфигурация)

## Функциональность

- **Регистрация и аутентификация**  
  Пользователь может зарегистрироваться и выполнить вход. При аутентификации формируется JWT-токен.

- **OTP-сервис**  
  Генерация и валидация OTP для операций. OTP может доставляться разными способами:
    - **FILE** – сохранение OTP в файл.
    - **EMAIL** – отправка кода на электронную почту.
    - **SMS** – отправка кода через SMS.
    - **TELEGRAM** – отправка кода через Telegram.

- **Административные действия**  
  Возможность обновления конфигурации OTP, получения списка не-административных пользователей и удаления пользователей вместе с их OTP-кодами.

## Требования

- **Java 17**
- **Maven**
- **PostgreSQL**  
  Необходимо иметь запущенную базу данных PostgreSQL. По умолчанию проект настроен на подключение к базе:
    - URL: `jdbc:postgresql://localhost:5434/otp`
    - Пользователь: `postgres`
    - Пароль: `postgres`

- Дополнительно, для отправки письма, SMS и сообщений в Telegram. Необходимо настроить соответствующие сервисы в файле конфигурации [application.yaml](./src/main/resources/application.yaml).

## Установка и запуск

1. **Клонирование репозитория**

   ```bash
   git clone <URL_вашего_репозитория>
   cd otp-project
   ```

2. **Сборка и запуск проекта**

   Используйте Maven для сборки и запуска:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Проект будет запущен и доступен по умолчанию на порту 8080.

## Как пользоваться сервисом

После запуска сервиса можно обращаться к REST API с помощью таких инструментов, как Postman, cURL или любой другой HTTP-клиент.

### Примеры использования

1. **Регистрация пользователя**

    - **Метод:** `POST`
    - **Эндпоинт:** `/api/auth/register`
    - **Тело запроса (JSON):**

      ```json
      {
        "login": "username",
        "password": "your_password",
        "role": "USER",         // либо "ADMIN" — с ограничением: администратор может быть только один
        "email": "user@example.com",
        "phoneNumber": "+1234567890",
        "telegram": "telegram_handle"
      }
      ```

    - **Ответ:** JWT-токен в формате JSON

2. **Аутентификация (вход) пользователя**

    - **Метод:** `POST`
    - **Эндпоинт:** `/api/auth/login`
    - **Тело запроса (JSON):**

      ```json
      {
        "login": "username",
        "password": "your_password"
      }
      ```

    - **Ответ:** JWT-токен

3. **Генерация OTP**

   Необходимо предварительно аутентифицироваться (в заголовке запроса указывайте полученный JWT-токен).

    - **Метод:** `POST`
    - **Эндпоинт:** `/api/user/generate-otp`
    - **Тело запроса (JSON):**

      ```json
      {
        "operationId": "some_unique_operation_id",
        "deliveryType": "EMAIL"  // Значения: "FILE", "EMAIL", "SMS", "TELEGRAM"
      }
      ```

    - **Ответ:** Сгенерированный OTP-код (строка)

4. **Валидация OTP**

    - **Метод:** `POST`
    - **Эндпоинт:** `/api/user/validate-otp`
    - **Тело запроса (JSON):**

      ```json
      {
        "operationId": "some_unique_operation_id",
        "code": "введенный_код"
      }
      ```

    - **Ответ:** `true` если валидация успешна, иначе `false`.

5. **Административные команды**

   > **Важно:** Доступ к административным эндпоинтам должен осуществляться под учетной записью администратора.

    - **Обновление конфигурации OTP:**

        - **Метод:** `PUT`
        - **Эндпоинт:** `/api/admin/otp-config`
        - **Тело запроса (JSON):** пример объекта конфигурации (`OtpConfig`), например:

          ```json
          {
            "otpLength": 6,
            "otherConfigField": "value"  // остальные поля конфигурации, если имеются
          }
          ```

        - **Ответ:** Обновленный объект конфигурации.

    - **Получение списка пользователей (без админов):**

        - **Метод:** `GET`
        - **Эндпоинт:** `/api/admin/users`
        - **Ответ:** Список пользователей.

    - **Удаление пользователя и его OTP-кодов:**

        - **Метод:** `DELETE`
        - **Эндпоинт:** `/api/admin/user/{id}`
        - **Пример:** `DELETE /api/admin/user/1`

## Конфигурация

Основные параметры приложения находятся в файле [application.yaml](./src/main/resources/application.yaml):

- **Datasource:** параметры подключения к PostgreSQL.
- **JPA:** настройки Hibernate, такие как `ddl-auto` и диалект.
- **Mail, SMPP, Telegram:** настройки для отправки OTP через Email, SMS и Telegram. Перед запуском сервиса убедитесь, что данные параметры корректно заполнены (например, логин, пароль, токен и прочее).

