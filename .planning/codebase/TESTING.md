# Testing Patterns

**Analysis Date:** 2026-03-23

## Test Framework

**Runner:**
- JUnit Jupiter through `org.springframework.boot:spring-boot-starter-test` and `useJUnitPlatform()` in `build.gradle`
- Config: `build.gradle`

**Assertion Library:**
- AssertJ, used in `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java`, `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImplTests.java`, and `src/test/java/nl/hukss/randomdata/randomdata/util/JwtUtilTests.java`

**Run Commands:**
```bash
./gradlew.bat test
./gradlew.bat build
./gradlew.bat clean
```

## Test File Organization

**Location:**
- Keep tests under `src/test/java/nl/hukss/randomdata/randomdata/`, mirroring the production package structure where useful.

**Naming:**
- Use the `*Tests.java` suffix, for example `src/test/java/nl/hukss/randomdata/randomdata/vo/ResultDataTests.java`.

**Structure:**
```text
src/test/java/nl/hukss/randomdata/randomdata/
├── RandomDataApplicationTests.java
├── controller/RandomDataControllerTests.java
├── service/impl/UserDetailsImplTests.java
├── service/impl/UserDetailsServiceImplTests.java
├── util/JwtUtilTests.java
└── vo/ResultDataTests.java
```

## Test Structure

**Suite Organization:**
```java
@Testcontainers
@SpringBootTest
class RandomDataControllerTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUpSchema() {
        jdbcTemplate.execute("...");
    }

    @Test
    void shouldGenerateAndPersistDataWhenGetOneCalled() {
        // arrange
        // act
        // assert
    }
}
```

**Patterns:**
- Use `@SpringBootTest` for integration tests that load the application context, such as `src/test/java/nl/hukss/randomdata/randomdata/RandomDataApplicationTests.java`.
- Use Testcontainers PostgreSQL with `@Container` and `@ServiceConnection` in `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java` and `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImplTests.java`.
- Create and clean schema in `@BeforeEach` with `JdbcTemplate` rather than relying on an external database bootstrap.
- Keep pure unit tests out of the Spring context, as in `src/test/java/nl/hukss/randomdata/randomdata/util/JwtUtilTests.java`.

## Mocking

**Framework:** Spring mock web utilities

**Patterns:**
```java
MockHttpServletRequest request = new MockHttpServletRequest();
request.addHeader("Authorization", "Bearer " + JwtUtil.createJWT("7"));

jwtAuthenticationTokenFilter.doFilter(
        request,
        new MockHttpServletResponse(),
        new MockFilterChain()
);
```

**What to Mock:**
- Mock servlet request metadata and filter-chain plumbing with `MockHttpServletRequest`, `MockHttpServletResponse`, and `MockFilterChain`, as done in `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImplTests.java`.
- Use `MockHttpServletRequest` to control fallback behavior that depends on request metadata, as in `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java`.

**What NOT to Mock:**
- Do not mock PostgreSQL-backed persistence for repository and service wiring that is already verified with Testcontainers in `src/test/java/nl/hukss/randomdata/randomdata/RandomDataApplicationTests.java`.
- Do not mock JWT creation/parsing for utility coverage already exercised directly in `src/test/java/nl/hukss/randomdata/randomdata/util/JwtUtilTests.java`.

## Fixtures and Factories

**Test Data:**
```java
jdbcTemplate.update(
        "INSERT INTO users (id, username, password, nick_name, avatar) VALUES (?, ?, ?, ?, ?)",
        99L, "alice", "{noop}pwd", "Alice", ""
);

jdbcTemplate.update(
        "INSERT INTO companies (name, code, province, city, address, year, personal_info_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
        "Demo Co", "CODE-001", "Chongqing", "Chongqing", "Some Road", "2026", null
);
```

**Location:**
- Inline setup inside each test class. No shared fixture helpers or factory classes are present under `src/test/java/`.

## Coverage

**Requirements:** No coverage gate or report task is configured in `build.gradle`.

**View Coverage:**
```bash
Not configured
```

## Test Types

**Unit Tests:**
- Plain JUnit tests cover utility and simple wrapper behavior in `src/test/java/nl/hukss/randomdata/randomdata/util/JwtUtilTests.java`, `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsImplTests.java`, and `src/test/java/nl/hukss/randomdata/randomdata/vo/ResultDataTests.java`.

**Integration Tests:**
- Spring Boot plus PostgreSQL Testcontainers tests cover CRUD, query, controller, login, and filter behavior in `src/test/java/nl/hukss/randomdata/randomdata/RandomDataApplicationTests.java`, `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java`, and `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImplTests.java`.

**E2E Tests:**
- Not detected.

## Environment Dependencies

- Docker access is required for the committed integration suite. `build.gradle` sets `DOCKER_HOST` to `tcp://10.35.11.202:2375` unless the environment overrides it.
- `src/test/resources/application-unit-test.yaml` defines an H2-based test configuration, but the current committed integration tests rely on `@ServiceConnection` PostgreSQL containers instead.
- The current tests create their own tables through `JdbcTemplate`, so they are not coupled to a local manually managed database instance.

## Common Patterns

**Async Testing:**
```java
Not detected in the current test suite.
```

**Error Testing:**
```java
assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("missing");
```

## Coverage Gaps

- No tests cover `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`.
- No tests cover `register()` in `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`, which currently returns an empty string.
- No direct tests cover security matcher rules in `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`.
- No tests cover `getImageCode()` in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`, which depends on a machine-local Python executable and script path.
- No configured coverage reporting exists in `build.gradle`, so regressions depend on the current test selection rather than an enforced threshold.

---

*Testing analysis: 2026-03-23*
