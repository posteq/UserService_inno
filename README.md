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

## Run locally

Requirements: Java 21, running PostgreSQL and Redis .

```bash
./gradlew bootRun
```

The `local` profile is used by default.

## Configuration

Profiles: `local` (default) and `docker`. Settings are taken from environment variables:

| Variable | Description | Default (`local` only) |
|---|---|---|
| `DB_URL` | JDBC URL | `jdbc:postgresql://localhost:5555/postgres` |
| `DB_USERNAME` | DB user | `postgres` |
| `DB_PASSWORD` | DB password | `demo` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |

## Tests

Coverage report: `build/reports/jacoco/test/html/index.html`
