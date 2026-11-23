# Claims API

A REST API for managing insurance policies and claims, built with Java 21, Spring Boot, PostgreSQL and Docker.

Claims move through a lifecycle (`SUBMITTED → UNDER_REVIEW → APPROVED/DENIED → PAID`), and the service enforces business rules:

- A claim can't exceed its policy's coverage limit.
- The incident date must fall within the policy's coverage period.
- Status changes must follow the allowed lifecycle (no skipping review, no paying a denied claim).

## Tech stack

Java 21 · Spring Boot 3 (Web, Data JPA, Validation) · PostgreSQL 16 · Docker Compose · JUnit 5 · Mockito

## Running locally

Prerequisites: JDK 21, Maven, Docker Desktop.

```bash
docker compose up -d        # start PostgreSQL
mvn spring-boot:run         # start the API on http://localhost:8080
mvn test                    # run the unit tests
```

On first startup the app seeds one sample policy (id `1`). Example requests are in `requests.http`.

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/claims` | File a claim |
| GET | `/api/claims` | List claims (optional `?status=`) |
| GET | `/api/claims/{id}` | Get one claim |
| POST | `/api/claims/{id}/review` | Move to UNDER_REVIEW |
| POST | `/api/claims/{id}/approve` | Approve |
| POST | `/api/claims/{id}/deny` | Deny |
| POST | `/api/claims/{id}/pay` | Mark as paid |

Errors are returned as RFC 7807 problem details: 400 for validation errors, 404 for missing records, 422 for broken business rules.

## Project layout

```
src/main/java/com/priceerwin/claims/
├── claim/          Claim entity, status lifecycle, service, controller, DTOs
├── policy/         Policy entity and repository
├── policyholder/   Policyholder entity and repository
└── common/         Exceptions, global error handler, sample-data seeder
```

## Roadmap

- [ ] Policyholder endpoints (create, get, list)
- [ ] Policy endpoints (create with validation that end date is after start date, get, list by policyholder)
- [ ] Payout amount on approval, which may be lower than the amount requested
- [ ] Controller tests with `@WebMvcTest` and MockMvc
- [ ] Dockerfile for the app, so `docker compose up` starts the API and the database
- [ ] GitHub Actions workflow that runs `mvn test` on every push
