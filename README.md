# Banking Management System

A backend-first Banking Management System built with **Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA/Hibernate, and PostgreSQL**. It follows Controller–Service–Repository layering and exposes REST APIs.

> This is a learning/demo project, not production banking software. Do not use real customer data or real money. Before production use, add professional security review, audit controls, KYC/AML, rate limiting, secrets management, monitoring, backups, and regulatory compliance.

## Features
- Customer registration and login with BCrypt password hashing and JWT authentication.
- Customer and admin roles.
- Open accounts and view only your own accounts.
- Deposit, withdraw, and account-to-account transfer.
- Transaction history with pagination.
- Admin endpoints to list users, enable/disable users, and block/unblock accounts.
- Bean Validation, centralized API error responses, transactional money operations, pessimistic row locking for balance changes, and PostgreSQL persistence.

## Requirements
- JDK 17+
- Maven 3.9+
- Docker Desktop (recommended for PostgreSQL), or a local PostgreSQL 14+ instance.

## Run locally (Mac)
1. Unzip the project and open its folder in Terminal.
2. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

3. Start the Spring Boot API:

   ```bash
   mvn spring-boot:run
   ```

4. API base URL: `http://localhost:8080`

To stop PostgreSQL: `docker compose down`. To also delete its database volume (irreversible): `docker compose down -v`.

## Create a local admin
The app does **not** create a default admin unless explicitly enabled. For local testing, start it with:

```bash
DEV_ADMIN_ENABLED=true DEV_ADMIN_EMAIL=admin@bank.local DEV_ADMIN_PASSWORD='ChangeMe123!' mvn spring-boot:run
```

Change the password immediately and never use this development password outside a disposable local environment. Set `BANKING_JWT_SECRET` to a long random Base64-encoded secret in any shared environment.

## API quick start

All request/response bodies are JSON. Register and login are public. All `/api/accounts/**` endpoints require `Authorization: Bearer <token>`. `/api/admin/**` requires the ADMIN role.

### 1. Register customer

`POST /api/auth/register`

```json
{
  "fullName": "Asha Kumar",
  "email": "asha@example.com",
  "password": "StrongPass123"
}
```

### 2. Login

`POST /api/auth/login`

```json
{
  "email": "asha@example.com",
  "password": "StrongPass123"
}
```

Copy the `token` from the response and send it as a Bearer token.

### 3. Open an account

`POST /api/accounts`

No body required.

### 4. List your accounts

`GET /api/accounts`

### 5. Deposit

`POST /api/accounts/{accountNumber}/deposit`

```json
{"amount": 5000.00, "description": "Initial deposit"}
```

### 6. Withdraw

`POST /api/accounts/{accountNumber}/withdraw`

```json
{"amount": 250.00, "description": "ATM withdrawal"}
```

### 7. Transfer

`POST /api/accounts/transfer`

```json
{
  "fromAccount": "KRW...",
  "toAccount": "KRW...",
  "amount": 100.00,
  "description": "Rent share"
}
```

The authenticated user must own the source account. The destination account must be active.

### 8. Transaction history

`GET /api/accounts/{accountNumber}/transactions?page=0&size=20`

### 9. Admin APIs

- `GET /api/admin/users`
- `PATCH /api/admin/users/{id}/enable`
- `PATCH /api/admin/users/{id}/disable`
- `PATCH /api/admin/accounts/{accountNumber}/block`
- `PATCH /api/admin/accounts/{accountNumber}/unblock`

## Postman testing
Import or manually create requests using the paths above. For protected routes, add:
`Authorization: Bearer <token>`

Suggested test flow:
1. Register two customers and log in as each.
2. Open one account for each customer.
3. Deposit into the first account.
4. Transfer part of the balance to the second account.
5. Verify balances and transaction history.
6. Try withdrawing more than the available balance (expect HTTP 400).
7. Try accessing another customer's account (expect HTTP 403).
8. Enable the local admin and test the admin routes.

## Project structure

```text
src/main/java/com/sudhakar/banking/
  config/        Security configuration and optional local admin initializer
  controller/    REST controllers
  dto/           Request and response DTOs
  entity/        JPA entities and enums
  exception/     API exception handling
  repository/    Spring Data repositories
  security/      JWT filter, service, and user details service
  service/       Business logic and transaction boundaries
```

## GitHub
After verifying the app locally:

```bash
git init
git add .
git commit -m "Build banking management system with Spring Boot and PostgreSQL"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/banking-management-system.git
git push -u origin main
```

If the repository already exists and contains commits, clone it first or configure the remote carefully; do not overwrite existing work blindly.

## Important implementation notes
- Account balances use `BigDecimal` with two decimal places.
- Deposit, withdrawal, and transfer methods are transactional.
- Transfer locks both account rows in deterministic account-number order to reduce race conditions and deadlocks.
- Passwords are stored as BCrypt hashes; plaintext passwords are never persisted.
- JWT signing secret and database credentials can be overridden with environment variables.
- Hibernate `ddl-auto=update` is convenient for development. Use versioned database migrations (Flyway/Liquibase) for production.
