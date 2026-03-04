# Copilot Instructions

## Build & Test

```bash
# Build and test
mvnd clean install

# Run a single test class
mvnd test -Dtest=SlettUsendteRinasakerTest
```

Requires JDK 25. Tests use Testcontainers (Docker must be running) for PostgreSQL and Kafka.

## Architecture

Kotlin/Spring Boot service that deletes RINA cases which were created but never received a SED within a configured time period. Deployed to NAIS (NAV's Kubernetes platform).

**Flow:** Kafka events → track case/document status in PostgreSQL → periodically delete eligible cases via REST call to `eux-rina-terminator-api`. The terminator is on Github: navikt/eux-rina-terminator-api. browse code when needed to see API usage.

**Layers:**
- `kafka.listener` — consumes events from `eux-rina-case-events-v1` and `eux-rina-document-events-v1` topics
- `service` — business logic: determines which cases can be deleted based on age and status
- `integration` — OAuth2-authenticated REST client calling `eux-rina-terminator-api` for deletion
- `persistence` — JPA repository for `RinasakStatus` entity with Flyway migrations
- `webapp` — REST API that triggers the deletion process

**State machine:** `RinasakStatus` tracks each case through: `NY_SAK` → `TIL_SLETTING` → `SLETTET` (or `DOKUMENT_SENT` if a document arrives).

## Conventions

- **Logging:** Use `kotlin-logging` (`KotlinLogging.logger {}`). Wrap contextual logs with the `mdc()` function to set `rinasakId`, `bucType`, and `sletteprosess` in MDC.
- **REST clients:** Configure via `RestClient.builder()` with an `OAuth2AccessTokenService` interceptor for bearer tokens. Registration names match `application.yml` client config.
- **Kafka models:** Separate data class hierarchies under `kafka.model.case` and `kafka.model.document` — use `@JsonIgnoreProperties(ignoreUnknown = true)` on all Kafka DTOs.
- **Entity updates:** Use `copy()` on data class entities rather than mutable setters.
- **Expression bodies:** Prefer expression body syntax (`fun f() = ...`) over block body (`fun f() { ... }`) for functions, including `try`/`catch` expressions.
- **Tests:** Integration tests extend `AbstractTest`, which provides shared Testcontainers (PostgreSQL + Kafka), `MockWebServer` for external APIs, and `MockOAuth2Server` for JWT. Use Awaitility for async assertions after Kafka events.
