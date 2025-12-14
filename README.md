# SQQ Inscription

SQQ Inscription is a Quarkus-based application to manage online registrations and payments for SQQ. It exposes a public registration endpoint, integrates with Stripe Checkout for card payments, persists registrations in a PostgreSQL database, and provides a small Admin UI/API to view and process registrations. A daily reconciliation job verifies pending Stripe payments.

- Backend: Quarkus (RESTEasy Reactive, Hibernate ORM with Panache, Flyway, Scheduler, SmallRye OpenAPI)
- Frontend: Vue-based UI managed by Quarkus Quinoa under `src/main/webui`
- Payments: Stripe Checkout

Useful links when running locally:
- OpenAPI/Swagger UI: http://localhost:8080/q/swagger-ui
- Dev UI (dev mode only): http://localhost:8080/q/dev
- Admin UI (requires basic auth): http://localhost:8080/admin

## Architecture overview

- Public API
  - POST /api/v1/registrations — accepts form-encoded registration data, creates a Stripe Checkout Session and redirects the browser to Stripe.
  - POST /api/v1/registrations/success/{cooperateurId} — server-side confirmation endpoint invoked by the UI after a successful return from Stripe. Marks the registration as PAID if Stripe confirms the payment.
- Admin API (protected by Basic Auth; role: admin)
  - GET /api/v1/administration/cooperateurs — list all registrations
  - POST /api/v1/administration/cooperateurs/{id}/process — mark a paid registration as PROCESSED
- Scheduled job
  - PaymentReconciliationJob runs daily (cron: `0 0 1 * * ?`) to check pending payments against Stripe and update their status to PAID when appropriate.

## Run in development

Prerequisites:
- Java 21+
- PostgreSQL database

Configure via environment variables (see Configuration section below), then start the app in dev mode with live reload:

```bash
./mvnw quarkus:dev
```

Notes:
- The frontend is managed by Quinoa. In dev, Quarkus proxies the dev server (default port 3000). No separate frontend command is required.
- The database schema is managed by Flyway and will migrate automatically at startup.

## Build and run (packaged)

Build a JVM jar:

```bash
./mvnw package
```

Run it:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

Build an uber-jar:

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

Build a native executable (optional):

```bash
./mvnw package -Dnative
# or in container
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Configuration (environment variables only)

All configuration is done via environment variables. Quarkus maps environment variables to config keys by replacing dots and hyphens with underscores and uppercasing. The most relevant variables are listed below.

Database (PostgreSQL):
- QUARKUS_DATASOURCE_JDBC_URL — JDBC URL, e.g. jdbc:postgresql://localhost:5432/sqq
- QUARKUS_DATASOURCE_USERNAME — database user
- QUARKUS_DATASOURCE_PASSWORD — database password

Hibernate/Flyway:
- QUARKUS_FLYWAY_MIGRATE_AT_START=true (default in this project)
- QUARKUS_FLYWAY_BASELINE_ON_MIGRATE=true (default in this project)

HTTP and Auth:
- QUARKUS_HTTP_PORT — HTTP port (default 8080)
- QUARKUS_HTTP_AUTH_BASIC=true (enabled by default in this project)
- QUARKUS_SECURITY_USERS_EMBEDDED_ENABLED=true (enabled by default)
- QUARKUS_SECURITY_USERS_EMBEDDED_PLAIN_TEXT=true (enabled by default)
- QUARKUS_SECURITY_USERS_EMBEDDED_USERS_admin — admin user password, e.g. s3cret
- QUARKUS_SECURITY_USERS_EMBEDDED_ROLES_admin=admin — assign admin role to the admin user

Stripe integration:
- STRIPE_API_KEY — Your Stripe secret API key (required)
- STRIPE_PART_SOCIALE_PRICE_ID — Stripe Price ID used for the line item (required)
- STRIPE_REDIRECT_DOMAIN — Base URL used to redirect after payment, e.g. https://inscriptions.example.org (required)

Frontend (Quinoa) — typically leave defaults:
- QUARKUS_QUINOA_DEV_SERVER_PORT=3000
- QUARKUS_QUINOA_BUILD_DIR=dist
- QUARKUS_QUINOA_ENABLE_SPA_ROUTING=true

OpenAPI/Swagger:
- QUARKUS_SWAGGER_UI_ALWAYS_INCLUDE=true (enabled in this project)

Environment variables example (dev):

```bash
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/sqq
export QUARKUS_DATASOURCE_USERNAME=sqq
export QUARKUS_DATASOURCE_PASSWORD=devpass

export QUARKUS_SECURITY_USERS_EMBEDDED_USERS_admin=admin
export QUARKUS_SECURITY_USERS_EMBEDDED_ROLES_admin=admin

export STRIPE_API_KEY=sk_test_...
export STRIPE_PART_SOCIALE_PRICE_ID=price_...
export STRIPE_REDIRECT_DOMAIN=http://localhost:8080

./mvnw quarkus:dev
```

## Registration flow

1. A user submits the public form (served by the UI) which POSTs to /api/v1/registrations with form-encoded fields.
2. The backend creates a Stripe Checkout Session and redirects the browser to Stripe.
3. After payment, Stripe redirects the user back to the frontend using STRIPE_REDIRECT_DOMAIN. The UI then calls the backend confirmation endpoint POST /api/v1/registrations/success/{cooperateurId}.
4. The backend verifies the Checkout Session status with Stripe and marks the registration as PAID.
5. A nightly reconciliation job re-checks any remaining PAYMENT_PENDING records with Stripe and updates them to PAID when applicable.

## Admin

- Visit /admin and authenticate using the configured basic auth admin user.
- Use the Admin UI or call the Admin API to list registrations and mark PAID ones as PROCESSED.

## Testing

Run unit tests:

```bash
./mvnw test
```

Tests use their own application.properties under src/test/resources and do not hit real Stripe.

## Notes

- Database migrations live under src/main/resources/db/migration and execute automatically at startup (Flyway).
- OpenAPI schema is exported to src/main/webui/src/api at build time.
- The scheduler and Stripe features require valid configuration to operate correctly.
