# External Integrations

**Analysis Date:** 2026-03-23

## APIs & External Services

**Database Infrastructure:**
- PostgreSQL - primary application datastore configured in `src/main/resources/application.yml`
  - SDK/Client: `org.postgresql:postgresql` with Spring JDBC and MyBatis-Plus from `build.gradle`
  - Auth: `POSTGRES_USERNAME`, `POSTGRES_PASSWORD`

**Identity and Security:**
- Custom JWT authentication - token issuance and validation implemented in `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`
  - SDK/Client: `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`
  - Auth: bearer token via `Authorization` header, enforced by `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`

**Data Generation:**
- `common-random` library - generates person, bank, and company-like values in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
  - SDK/Client: `io.github.yindz:common-random`
  - Auth: Not applicable

**OCR Sidecar Invocation:**
- Local Python OCR script - external process invoked by `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
  - SDK/Client: `Runtime.getRuntime().exec(...)` and `ProcessBuilder`
  - Auth: Not applicable

## Data Storage

**Databases:**
- PostgreSQL
  - Connection: JDBC URL plus `POSTGRES_USERNAME` and `POSTGRES_PASSWORD` in `src/main/resources/application.yml`
  - Client: Spring JDBC plus MyBatis-Plus mapper/service layer in `src/main/java/nl/hukss/randomdata/randomdata/mapper/*`
- H2 in-memory database
  - Connection: `jdbc:h2:mem:testdb...` in `src/test/resources/application-unit-test.yaml`
  - Client: Spring datasource for tests

**File Storage:**
- Local filesystem only
- Static assets are served from `src/main/resources/static/`

**Caching:**
- None active in application code
- `src/test/resources/application-unit-test.yaml` contains Redis-related test config text, but no Redis dependency or runtime integration is wired from `build.gradle` or `src/main/java/nl/hukss/randomdata/randomdata/*`

## Authentication & Identity

**Auth Provider:**
- Custom username/password plus JWT
  - Implementation: `/user/login` in `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` authenticates through `AuthenticationManager`; `JwtAuthenticationTokenFilter` restores authenticated users from bearer tokens; Spring Security rules are defined in `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`

## Monitoring & Observability

**Error Tracking:**
- None detected

**Logs:**
- Ad hoc console output via `System.out.println` in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`

## CI/CD & Deployment

**Hosting:**
- Not explicitly defined
- Current runtime config suggests a self-managed Spring Boot deployment using PostgreSQL from `src/main/resources/application.yml`

**CI Pipeline:**
- None detected in repository files

## Environment Configuration

**Required env vars:**
- `POSTGRES_USERNAME`
- `POSTGRES_PASSWORD`
- `DOCKER_HOST` for test execution override, configured in `build.gradle`

**Secrets location:**
- Credentials are expected from environment variables for database auth in `src/main/resources/application.yml`
- JWT signing material is hardcoded in `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`

## Webhooks & Callbacks

**Incoming:**
- None

**Outgoing:**
- None over HTTP
- Outgoing local process execution to `python.exe` and `main.py` path from `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`

---

*Integration audit: 2026-03-23*
