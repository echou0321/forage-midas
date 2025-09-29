# Midas Core (JPMC ASE Forage)

I built Midas Core as a small, resilient backend that simulates a real payment pipeline: consume transactions from Kafka, validate and apply them in a SQL database, enrich with an external incentives service, and expose a simple REST API for balances. I focused on correctness (money math), isolation of concerns, and an easy local developer experience.

## Why these choices
- SQL over NoSQL for financial data: stronger durability and transactional guarantees.
- BigDecimal for all monetary values: predictable arithmetic and no floating‑point surprises.
- Decoupled incentives via REST: the “contract” allows either side to evolve independently.
- One service hosting both ingestion and a tiny read API: simplest deployment now; easy to split later.

## What I implemented
- Kafka ingestion with Spring Kafka
  - `TransactionListener` subscribes to `midas-transactions` and forwards to `TransactionService`.
- Transaction processing
  - Validates sender/recipient existence and sufficient funds.
  - Calls the Incentive API (`POST /incentive`) and adds the returned incentive only to the recipient.
  - Persists updates and a `TransactionRecord` containing both amount and incentive.
- Persistence with Spring Data JPA + H2 (in‑memory)
  - `UserRecord` (name, balance BigDecimal)
  - `TransactionRecord` (sender, recipient, amount BigDecimal, incentive BigDecimal, timestamps)
- Balance REST API
  - `GET /balance?userId=<id>` → returns a `Balance` JSON; non‑existing users return 0.

## Tech stack
- Java 17, Spring Boot 3.2
- Spring Kafka, Spring Data JPA (H2)
- JUnit 5, Embedded Kafka for tests

## How to run locally
Prerequisite: Java 17 available on PATH.

1) Build
   - macOS/Linux: `./mvnw -DskipTests clean package`
   - Windows (PowerShell): `./mvnw.cmd -DskipTests clean package`

2) Start Incentive API (required for incentives & final tests)
   - `java -jar ./services/transaction-incentive-api.jar`
   - Endpoint: `POST http://localhost:8080/incentive`

3) Run Midas Core (optional standalone)
   - `./mvnw spring-boot:run`
   - Balance API: `GET http://localhost:33400/balance?userId=<id>`

## Configuration (application.yml)
- Kafka topic: `general.kafka-topic: midas-transactions`
- Incentive API URL: `incentive.api-url: http://localhost:8080/incentive`
- Server port: `server.port: 33400`

## Endpoints
- Balance (this service): `GET /balance?userId=<id>` → `{ "amount": <float> }`
- Incentive (external jar): `POST /incentive` with `Transaction { senderId, recipientId, amount }` → `{ "amount": <BigDecimal> }`

## How the pipeline works
1) Kafka delivers a `Transaction`.
2) Service validates users and balance.
3) Calls Incentive API; gets `Incentive.amount`.
4) Updates balances: `sender -= amount`, `recipient += amount + incentive`.
5) Saves a `TransactionRecord` with both `amount` and `incentive`.

## Running the exercises
- Task 3 (DB + validation): `./mvnw -Dtest=TaskThreeTests test`
- Task 4 (Incentive integration): start the Incentive API, then `./mvnw -Dtest=TaskFourTests test`
- Task 5 (Balance API): ensure Incentive API is running, then `./mvnw -Dtest=TaskFiveTests test` and submit the printed block (keep BEGIN/END tags).

## Developer notes
- Money is `BigDecimal` end‑to‑end. The public `Balance` DTO uses `float` to match the spec; conversion happens at the boundary.
- `entity.User` remains a plain POJO to avoid clashing with `UserRecord` JPA mapping.
- Helpful debug trick for tests: use `$env:MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"; ./mvnw.cmd -Dtest=TaskThreeTests test` and attach to `127.0.0.1:5005`.

## What I would improve next
- Add optimistic locking to `UserRecord` for concurrent updates.
- Index foreign keys on `TransactionRecord` for historical queries.
- Observability: structured logging and metrics around Kafka lag and incentive latency.
