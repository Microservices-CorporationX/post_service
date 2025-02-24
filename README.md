# Post Service

Управление социальными активностями

# Использованные технологии

* [Spring Boot](https://spring.io/projects/spring-boot) – как основной фрэймворк
* [PostgreSQL](https://www.postgresql.org/) – как основная реляционная база данных
* [Redis](https://redis.io/) – как кэш и очередь сообщений через pub/sub
* [testcontainers](https://testcontainers.com/) – для изолированного тестирования с базой данных
* [Liquibase](https://www.liquibase.org/) – для ведения миграций схемы БД
* [Gradle](https://gradle.org/) – как система сборки приложения
* [Lombok](https://projectlombok.org/) – для удобной работы с POJO классами
* [MapStruct](https://mapstruct.org/) – для удобного маппинга между POJO классами

# База данных

* База поднимается в отдельном сервисе [infra](https://github.com/Microservices-CorporationX/infra)
* Redis поднимается в единственном инстансе тоже в [infra](https://github.com/Microservices-CorporationX/infra)
* Liquibase сам накатывает нужные миграции на голый PostgreSql при старте приложения

# Как начать разработку начиная с шаблона?

1. Сначала нужно склонировать этот репозиторий

```shell
git clone <link_to_repo>
```

2. Далее удаляем служебную директорию для git

```shell
# Переходим в корневую директорию проекта
cd Service
rm -rf .git
```

3. Далее нужно создать совершенно пустой репозиторий в github/gitlab

4. Создаём новый репозиторий локально и коммитим изменения

```shell
git init
git remote add origin <link_to_repo>
git add .
git commit -m "Initial commit"
```

Готово, можно начинать работу!

# Как запустить локально?

Сначала нужно развернуть базу данных из директории [infra](https://github.com/Microservices-CorporationX/infra)

Далее собрать gradle проект

```shell
# Нужно запустить из корневой директории, где лежит build.gradle.kts
gradle build
```

Запустить jar'ник

```shell
java -jar build/libs/Service-1.0.jar
```

Но легче всё это делать через IDE

# Код

RESTful приложение

* Обычная трёхслойная
  архитектура – [Controller](src/main/java/ru/corporationx/postservice/controller), [Service](src/main/java/ru/corporationx/postservice/service), [Repository](src/main/java/ru/corporationx/postservice/repository)
* Слой Repository реализован на JPA (Hibernate)
* Реализован простой Messaging через [Redis pub/sub](https://redis.io/docs/manual/pubsub/)
* [Конфигурация](src/main/java/ru/corporationx/postservice/config/redis/RedisConfig.java) –
    сетапится [RedisTemplate](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisTemplate.html) –
    класс, для удобной работы с Redis силами Spring
* [Публикация](src/main/java/ru/corporationx/postservice/publisher) – публикация ивентов в топики брокера
* [Обработчик ошибок](src/main/java/ru/corporationx/postservice/exception/handler) – глобальный обработчик ошибок

# Тесты

* SpringBootTest
* MockMvc
* Testcontainers
* AssertJ
* JUnit5

# TODO

* Dockerfile, который подключается к сети запущенной postgres в docker-compose
