# Technology Stack

**Analysis Date:** 2026-03-23

## Languages

**Primary:**
- Java 17 - application code under `src/main/java/nl/hukss/randomdata/randomdata`

**Secondary:**
- Groovy Gradle DSL - build definition in `build.gradle` and `settings.gradle`
- YAML - runtime and test configuration in `src/main/resources/application.yml` and `src/test/resources/application-unit-test.yaml`
- HTML and JavaScript - static frontend in `src/main/resources/static/index.html` and `src/main/resources/static/js/*`

## Runtime

**Environment:**
- JVM on Java 17 via Gradle toolchain configured in `build.gradle`

**Package Manager:**
- Gradle Wrapper via `gradlew` and `gradlew.bat`
- Lockfile: missing

## Frameworks

**Core:**
- Spring Boot 3.5.3 - app bootstrap, web runtime, dependency wiring, test support from `build.gradle`
- Spring MVC - REST controllers in `src/main/java/nl/hukss/randomdata/randomdata/controller/*`
- Spring Security 3.3.4 starter - authentication and filter chain in `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`
- MyBatis-Plus 3.5.12 - mapper and service base classes in `src/main/java/nl/hukss/randomdata/randomdata/mapper/*` and `src/main/java/nl/hukss/randomdata/randomdata/service/impl/*`

**Testing:**
- JUnit Jupiter via `org.springframework.boot:spring-boot-starter-test` in `build.gradle`
- Spring Boot Test - integration-style context tests under `src/test/java/nl/hukss/randomdata/randomdata/*`
- Testcontainers PostgreSQL - container-backed database tests in `src/test/java/nl/hukss/randomdata/randomdata/RandomDataApplicationTests.java` and related test classes

**Build/Dev:**
- Gradle Wrapper - standard build entrypoint from `gradlew.bat`
- Spring Boot Gradle plugin 3.5.3 - packaging and boot run support in `build.gradle`
- Spring Dependency Management plugin 1.1.7 - dependency alignment in `build.gradle`
- Lombok 1.18.x - boilerplate reduction in entities and value objects such as `src/main/java/nl/hukss/randomdata/randomdata/entity/User.java`

## Key Dependencies

**Critical:**
- `org.springframework.boot:spring-boot-starter-web` - HTTP API surface for `UserController` and `RandomDataController`
- `org.springframework.boot:spring-boot-starter-jdbc` - JDBC access used by Spring and tests
- `com.baomidou:mybatis-plus-spring-boot3-starter:3.5.12` - ORM-like mapper/service layer for `Company`, `PersonalInfo`, and `User`
- `org.springframework.boot:spring-boot-starter-security:3.3.4` - auth pipeline configured in `SecurityConfig`
- `io.jsonwebtoken:jjwt-* 0.12.6` - JWT issue and parse logic in `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`

**Infrastructure:**
- `org.postgresql:postgresql` - production datasource driver configured in `src/main/resources/application.yml`
- `com.h2database:h2` - in-memory datasource present for tests and local fallback, referenced by `src/test/resources/application-unit-test.yaml`
- `org.testcontainers:postgresql` and `org.testcontainers:junit-jupiter` - real Postgres test environment
- `io.github.yindz:common-random:1.1.0` - synthetic person/company data generation in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- `com.alibaba.fastjson2:fastjson2:2.0.58` - available JSON utility dependency from `build.gradle`
- `org.bytedeco:javacv:1.5.9` - computer vision dependency declared in `build.gradle`

## Configuration

**Environment:**
- Main datasource comes from `src/main/resources/application.yml`
- `POSTGRES_USERNAME` and `POSTGRES_PASSWORD` override database credentials in `src/main/resources/application.yml`
- Test execution injects `DOCKER_HOST` in `build.gradle`, defaulting to `tcp://10.35.11.202:2375`
- Server port is fixed to `80` in `src/main/resources/application.yml`

**Build:**
- Build and dependency config live in `build.gradle`
- Project identity is defined in `settings.gradle`
- Gradle wrapper binaries and properties live in `gradle/wrapper/`

## Platform Requirements

**Development:**
- Java 17
- Gradle Wrapper
- Access to PostgreSQL for runtime based on `src/main/resources/application.yml`
- Docker access for tests because Testcontainers is used in `src/test/java/nl/hukss/randomdata/randomdata/*`
- Windows-specific Python runtime and script path are assumed by `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`

**Production:**
- Spring Boot web service with PostgreSQL reachable at the configured JDBC endpoint
- Security filter chain and JWT support enabled through `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`

---

*Stack analysis: 2026-03-23*
