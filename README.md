# UserService

Spring Boot microservice that manages user profiles and their payment cards.
Educational task by Innowise.

## Stack

Java 21, Spring Boot 4.1.1, PostgreSQL, Liquibase, Redis (cache),Docker.

## Entities

- **User**: id, firstname, surname, birthday, email, active
- **PaymentCard**: id, user_id, number, holder, expiration date, active

## Run with Docker

```bash
git clone <repo-url>
cd <repo-folder>
docker compose up --build
```

The stack starts PostgreSQL, Redis and the service (profile `docker`).

- Service: http://localhost:8081
- PostgreSQL: `jdbc:postgresql://localhost:5555/userservice`
  (user `postgres`, password `demo`)


```bash
./gradlew bootRun
```

## Configuration

Profiles: `local` (default) and `docker`. Settings are taken from environment variables:

## Tests

Coverage report: `build/reports/jacoco/test/html/index.html`
