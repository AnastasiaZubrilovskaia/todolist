# Todo list

Проект состоит из:
- веб-приложения (Spring Boot)
- базы данных (PostgreSQL)
- миграций (Flyway) 

Каждый компонент запускается в отдельном Docker-контейнере и взаимодействует внутри Docker-сети.


## 1. Функциональность

### 1) Авторизация
- Регистрация пользователя
- Логин пользователя
- Logout
- Авторизация по Bearer Token (через HTTP-заголовок `Authorization`)

Авторизация реализована упрощённо (без Spring Security).
### 2) Задачи 
- Создание задачи
- Получение списка задач пользователя
- Обновление задачи
- Удаление задачи
- Изменение задачи (в том числе и ее статус)
- Фильтрация задач по статусу

## 2. Архитектура
- Controller — обработка HTTP-запросов
- Service — бизнес-логика
- Repository — работа с базой данных
- Entity — JPA-сущности
- DTO — объекты передачи данных (request/response)

## 3.Технологии

- Java 21
- Spring Boot 
- PostgreSQL 15
- Flyway 
- Maven
- Docker + Docker Compose
- Lombok

## 4. База данных и миграции

- Используется база данных PostgreSQL
- Все SQL-миграции находятся в db/migration
- При запуске Docker стартует PostgreSQL, запускается контейнер Flyway, применяются миграции и затем стартует уже само приложение.

Отдельный контейнер для базы данных создаётся с использованием официального Docker-образа PostgreSQL.  


## 5. Переменные окружения

Файл `.env` (не хранится в репозитории):

```
POSTGRES_DB=todolist
POSTGRES_USER=<database_user>
POSTGRES_PASSWORD=<database_password>

SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/todolist
SPRING_DATASOURCE_USERNAME=<database_user>
SPRING_DATASOURCE_PASSWORD=<database_password>
```

## 6. Docker

В задании выполнены все требования:
- Доступ извне docker-сети возможно получить только к API
- У базы данных отсутствует port-forwarding
- Все пароли и настройки передаются через переменные окружения
- У базы данных настроен volume

### 1)Dockerfile
```
FROM maven:3.9.2-eclipse-temurin-21 AS build
WORKDIR /todolist
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre
WORKDIR /todolist
COPY --from=build /todolist/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```
где:
`WORKDIR` — рабочая директория в контейнере.  
`COPY --from=build` — копируем готовый jar из stage сборки.  
`ENTRYPOINT` — команда для запуска приложения.

### 2)docker-compose.yml
```
version: "3.9"

services:
  db:
    image: postgres:15
    container_name: todolist-db
    env_file:
      - .env
    volumes:
      - todolist-data:/var/lib/postgresql/data
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U $${POSTGRES_USER}" ]
      interval: 5s
      timeout: 5s
      retries: 5

  migration:
    image: flyway/flyway:10
    container_name: todolist-migration
    depends_on:
      db:
        condition: service_healthy
    env_file:
      - .env
    command: >
      -url=${SPRING_DATASOURCE_URL}
      -user=${SPRING_DATASOURCE_USERNAME}
      -password=${SPRING_DATASOURCE_PASSWORD}
      migrate
    volumes:
      - ./db/migration:/flyway/sql

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: todolist-app
    env_file:
      - .env
    depends_on:
      - db
    ports:
      - "8080:8080"

volumes:
  todolist-data:
```
где:  
`volumes` — подключает Docker volume для постоянного хранения данных базы.  
`env_file` — подключаем .env.  
`healthcheck` — проверяет готовность PostgreSQL принимать соединения.  
`depends_on` c `service_healthy` — миграции запускаются только после готовности базы данных.  
`command` — команда Flyway для применения миграций.  
`depends_on` (app) — приложение запускается после контейнера базы данных.  
`ports` — проброс наружу (8080 для доступа через браузер/Postman).

## 7. Запуск проекта 

Из корня проекта нужно выполнить команды:

1) Собираем и запускаем контейнеры:
```
docker compose up --build
```
2) Чтобы остановить и удалить контейнеры, сеть (данные БД не теряются):
```
docker-compose down
```
3) Полное удаление контейнеров(БД сбрасывается):
```
docker-compose down -v
```

4) Чтобы пересобрать полностью:
```
docker-compose down
docker-compose up --build
```
5) Чтобы просто остановить (не удаляя):
```
docker-compose stop
```
6) После обычной остановки запустить снова:
```
docker-compose start
```

После запуска API будет доступно по адресу:
http://localhost:8080


Работа с API роверяется через Postman.  
Примеры API:

- Регистрация:
```
POST http://localhost:8080/api/auth/register

{
  "name": "User",
  "login": "user",
  "password": "12345"
}
```

- Логин:
```
POST http://localhost:8080/api/auth/login

{
  "login": "user",
  "password": "12345"
}
```

- Выход:
`POST http://localhost:8080/api/auth/logout`
`Authorization: Bearer <token>`

- Создать задачу:
```
POST http://localhost:8080/api/tasks
Authorization: Bearer <token>

{
  "title": "task 1",
  "description": "4444444",
  "status": 1,
  "deadline": "2026-03-20"
}
```


- Удалить задачу:
`DELETE http://localhost:8080/api/tasks/{taskId}`  
`Authorization: Bearer <token>`

- Получение задач по статусу:
`GET http://localhost:8080/api/tasks/{statusId}`  
`Authorization: Bearer <token>`

- Получение всех задач:  
`GET http://localhost:8080/api/tasks`  
`Authorization: Bearer <token>`

- Обновить задачу:
```
PATCH http://localhost:8080/api/tasks/{taskId}
Authorization: Bearer <token>

{
  "status": 2
}
```
