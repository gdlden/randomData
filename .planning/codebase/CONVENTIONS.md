# Coding Conventions

**Analysis Date:** 2026-03-23

## Naming Patterns

**Files:**
- Use `PascalCase.java` for classes and interfaces such as `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java` and `src/main/java/nl/hukss/randomdata/randomdata/service/UserService.java`.
- Keep packages lowercase and layer-oriented under `src/main/java/nl/hukss/randomdata/randomdata/`.
- Name test files with the `*Tests.java` suffix, for example `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java`.

**Functions:**
- Use `camelCase` for production methods such as `getOne`, `getImageCode`, `register`, and `login`.
- Use behavior-style test names such as `shouldGenerateAndPersistDataWhenGetOneCalled` and `shouldCreateAndParseJwtWithSubject`.

**Variables:**
- Use `camelCase` for fields and locals such as `personalInfoService`, `companyService`, `authenticationToken`, and `persistedUserId`.
- Use `UPPER_SNAKE_CASE` for constants such as `JWT_TTL` and `JWT_KEY` in `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`.

**Types:**
- Use `PascalCase` for domain and framework types such as `User`, `Company`, `SecurityConfig`, `ResultData`, and `JwtAuthenticationTokenFilter`.
- Use suffixes to communicate layer role: `*Controller`, `*Service`, `*ServiceImpl`, `*Mapper`, `*Filter`, and `*Vo`.

## Code Style

**Formatting:**
- Follow standard Java formatting with 4-space indentation, matching files such as `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`.
- Keep classes compact when they are simple wrappers, as seen in `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`.

**Linting:**
- No Checkstyle, SpotBugs, PMD, or formatter plugin is configured in `build.gradle`.
- The enforceable style source is the repository guidance in `AGENTS.md` plus the existing code layout.

## Import Organization

**Order:**
1. Third-party and framework imports such as Spring, Jakarta, MyBatis-Plus, and JJWT
2. Project imports under `nl.hukss.randomdata.randomdata.*`
3. JDK imports such as `java.io.*`, `java.nio.*`, and `java.util.*`

**Path Aliases:**
- Not applicable. Use Java package imports directly, for example `nl.hukss.randomdata.randomdata.service.UserService`.

## Error Handling

**Patterns:**
- Structured API results use `ResultData.success(...)` and `ResultData.fail(...)` from `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java`.
- Authentication endpoints currently return raw `String` values from `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` and `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`.
- Exceptional control flow often throws `RuntimeException` directly, as in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

## Logging

**Framework:** `System.out`

**Patterns:**
- No SLF4J or Logback logger usage is detected in the main source tree.
- Debug output uses `System.out.println(...)` in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

## Comments

**When to Comment:**
- Keep comments sparse. `AGENTS.md` explicitly says not to add verbose comments.
- Existing comments are mostly ad hoc and explanatory, including commented-out code in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

**JSDoc/TSDoc:**
- Not applicable.
- JavaDoc is not part of the current convention in `src/main/java/nl/hukss/randomdata/randomdata/`.

## Function Design

**Size:** Keep controllers and simple service implementations thin when they only delegate or adapt, as seen in `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` and `src/main/java/nl/hukss/randomdata/randomdata/service/impl/PersonalInfoServiceImpl.java`.

**Parameters:** Accept request payloads as entities or maps in controllers, for example `@RequestBody User user` in `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` and `@RequestBody Map<String,String> reqMap` in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

**Return Values:** Prefer `ResultData<T>` for structured endpoint responses and return plain `String` only where the current auth contract already does so.

## Module Design

**Exports:** Keep one top-level class per file within the package tree rooted at `src/main/java/nl/hukss/randomdata/randomdata/`.

**Barrel Files:** Not used. Spring component scanning from `src/main/java/nl/hukss/randomdata/randomdata/RandomDataApplication.java` wires the application.

## Layering Patterns

- Put HTTP endpoints in `src/main/java/nl/hukss/randomdata/randomdata/controller/`.
- Put service interfaces in `src/main/java/nl/hukss/randomdata/randomdata/service/` and implementations in `src/main/java/nl/hukss/randomdata/randomdata/service/impl/`.
- Put persistence access in `src/main/java/nl/hukss/randomdata/randomdata/mapper/`.
- Put ORM entities in `src/main/java/nl/hukss/randomdata/randomdata/entity/`.
- Put security filters and configuration in `src/main/java/nl/hukss/randomdata/randomdata/filter/` and `src/main/java/nl/hukss/randomdata/randomdata/config/`.
- Put reusable payload wrappers and value objects in `src/main/java/nl/hukss/randomdata/randomdata/vo/`.

## Response Patterns

- Use `ResultData<T>` from `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java` for endpoints returning domain payloads, as in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.
- Reuse `ReturnCode` values when building successful responses, as exercised by `src/test/java/nl/hukss/randomdata/randomdata/vo/ResultDataTests.java`.
- The auth endpoints in `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` currently expose raw strings, so new auth-adjacent code should preserve that contract unless the API is intentionally redesigned.

## Configuration Habits

- Keep runtime application settings in `src/main/resources/application.yml`.
- Keep test-specific overrides in `src/test/resources/application-unit-test.yaml`.
- Inject credentials through environment-variable placeholders where available, as shown by `${POSTGRES_USERNAME:postgres}` and `${POSTGRES_PASSWORD:postgres}` in `src/main/resources/application.yml`.
- Keep test runtime assumptions in `build.gradle`, where the `test` task sets a default `DOCKER_HOST`.

---

*Convention analysis: 2026-03-23*
