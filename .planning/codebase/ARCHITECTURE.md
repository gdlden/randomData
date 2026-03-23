# Architecture

**Analysis Date:** 2026-03-23

## Pattern Overview

**Overall:** Layered Spring Boot monolith with MyBatis-Plus persistence and Spring Security authentication.

**Key Characteristics:**
- HTTP endpoints live in `src/main/java/nl/hukss/randomdata/randomdata/controller/` and call service interfaces rather than mappers directly.
- Persistence is split into entities in `src/main/java/nl/hukss/randomdata/randomdata/entity/`, mapper interfaces in `src/main/java/nl/hukss/randomdata/randomdata/mapper/`, and `ServiceImpl` adapters in `src/main/java/nl/hukss/randomdata/randomdata/service/impl/`.
- Authentication is handled as a filter-chain concern in `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java` and `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`.

## Layers

**Application Bootstrap:**
- Purpose: Start Spring Boot and register MyBatis mapper scanning.
- Location: `src/main/java/nl/hukss/randomdata/randomdata/RandomDataApplication.java`
- Contains: `@SpringBootApplication` entry point and `@MapperScan`.
- Depends on: Spring Boot auto-configuration and MyBatis Spring integration.
- Used by: The JVM process started through `gradlew bootRun` or packaged application startup.

**Web Layer:**
- Purpose: Expose HTTP endpoints for random data generation and user auth.
- Location: `src/main/java/nl/hukss/randomdata/randomdata/controller/`
- Contains: `RandomDataController` and `UserController`.
- Depends on: Service interfaces, servlet request objects, response VOs such as `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java`.
- Used by: Browser calls from `src/main/resources/static/index.html` and API clients posting to `/user/*`.

**Service Layer:**
- Purpose: Hold orchestration logic and bridge controller calls to persistence/auth utilities.
- Location: `src/main/java/nl/hukss/randomdata/randomdata/service/` and `src/main/java/nl/hukss/randomdata/randomdata/service/impl/`
- Contains: `IService`-based service interfaces plus `ServiceImpl` implementations and Spring Security user-detail services.
- Depends on: Mapper interfaces, Spring Security `AuthenticationManager`, `JwtUtil`, and MyBatis-Plus base classes.
- Used by: Controllers and the security authentication flow.

**Persistence Layer:**
- Purpose: Map Java objects to relational tables and provide CRUD/query access.
- Location: `src/main/java/nl/hukss/randomdata/randomdata/entity/` and `src/main/java/nl/hukss/randomdata/randomdata/mapper/`
- Contains: `User`, `PersonalInfo`, `Company` entities and `BaseMapper` interfaces.
- Depends on: MyBatis-Plus annotations such as `@TableName` and `@TableId`.
- Used by: Service implementations and `JwtAuthenticationTokenFilter`.

**Security Layer:**
- Purpose: Define authorization rules, login processing, and JWT-based request authentication.
- Location: `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`, `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImpl.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsImpl.java`, `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`
- Contains: Filter-chain configuration, a once-per-request JWT filter, `UserDetailsService`, `UserDetails`, and token utilities.
- Depends on: Spring Security, `UserMapper`, and JJWT.
- Used by: `/user/login` and every authenticated request.

## Data Flow

**Random Data Generation Flow:**

1. `src/main/resources/static/index.html` calls `GET /randomData/getOne/{userId}`.
2. `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java` chooses a user identifier, generates random person/company fields, and queries one unassigned company via `CompanyService`.
3. `src/main/java/nl/hukss/randomdata/randomdata/service/impl/PersonalInfoServiceImpl.java` persists a `PersonalInfo` record, and `src/main/java/nl/hukss/randomdata/randomdata/service/impl/CompanyServiceImpl.java` links the chosen company to that record.
4. `RandomDataController` returns a wrapped payload through `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java`.

**Login and JWT Flow:**

1. A client posts credentials to `/user/login`, handled by `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`.
2. `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java` builds a `UsernamePasswordAuthenticationToken` and delegates authentication to Spring Security's `AuthenticationManager`.
3. `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImpl.java` loads the user from `UserMapper`; `UserDetailsImpl` adapts the entity to `UserDetails`.
4. On success, `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java` creates a signed JWT whose subject is the user ID.

**Authenticated Request Flow:**

1. `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java` inserts `JwtAuthenticationTokenFilter` before `UsernamePasswordAuthenticationFilter`.
2. `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java` reads the `Authorization` header, parses the bearer token, and loads the user through `UserMapper`.
3. The filter stores a `UsernamePasswordAuthenticationToken` in `SecurityContextHolder`.
4. Controllers execute with an authenticated security context if the token is valid.

**State Management:**
- Persistent state lives in PostgreSQL tables mapped by `User`, `PersonalInfo`, and `Company`.
- Request-local auth state lives in Spring Security's `SecurityContextHolder`.
- Frontend state is minimal and remains in DOM fields inside `src/main/resources/static/index.html`.

## Key Abstractions

**Entity + Mapper Pair:**
- Purpose: Represent table rows and keep SQL access at the mapper boundary.
- Examples: `src/main/java/nl/hukss/randomdata/randomdata/entity/User.java`, `src/main/java/nl/hukss/randomdata/randomdata/mapper/UserMapper.java`, `src/main/java/nl/hukss/randomdata/randomdata/entity/Company.java`, `src/main/java/nl/hukss/randomdata/randomdata/mapper/CompanyMapper.java`
- Pattern: MyBatis-Plus `BaseMapper` plus annotation-based table metadata.

**IService + ServiceImpl Pair:**
- Purpose: Expose reusable business operations and inherit CRUD behavior.
- Examples: `src/main/java/nl/hukss/randomdata/randomdata/service/CompanyService.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/CompanyServiceImpl.java`
- Pattern: MyBatis-Plus `IService<T>` and `ServiceImpl<M, T>`.

**Result Wrapper:**
- Purpose: Normalize successful random-data responses into a status/message/data envelope.
- Examples: `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java`, `src/main/java/nl/hukss/randomdata/randomdata/vo/ReturnCode.java`
- Pattern: Static factory methods for success/failure.

## Entry Points

**Application Main:**
- Location: `src/main/java/nl/hukss/randomdata/randomdata/RandomDataApplication.java`
- Triggers: Process startup.
- Responsibilities: Boot Spring and enable mapper scanning.

**Random Data API:**
- Location: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Triggers: `GET /randomData/getOne/{userId}` and `POST /randomData/getImageCode`.
- Responsibilities: Generate a payload, persist linked data, and invoke the OCR Python process.

**User API:**
- Location: `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`
- Triggers: `POST /user/register` and `POST /user/login`.
- Responsibilities: Delegate registration/login to `UserService`.

**Static UI:**
- Location: `src/main/resources/static/index.html`
- Triggers: Browser request to the default static page.
- Responsibilities: Provide a jQuery-based UI that calls backend endpoints.

## Error Handling

**Strategy:** Exceptions generally bubble out of controllers and filters; there is no centralized exception translation layer.

**Patterns:**
- `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java` throws `RuntimeException` when the OCR child process exits non-zero.
- `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java` throws runtime exceptions for invalid JWT parsing and missing users.
- `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImpl.java` uses `UsernameNotFoundException` for unknown users.

## Cross-Cutting Concerns

**Logging:** No dedicated logging abstraction is present; direct `System.out.println` calls appear in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

**Validation:** Request validation annotations are not present on controller inputs such as `User` in `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` or the map payload in `RandomDataController`.

**Authentication:** `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java` permits `/user/login` and requires authentication for other routes, with JWT verification in `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`.

**Persistence Boundaries:** Controllers call service interfaces, but services mostly inherit CRUD behavior from MyBatis-Plus and pass through to mapper-generated queries; there is no repository abstraction beyond mapper interfaces in `src/main/java/nl/hukss/randomdata/randomdata/mapper/`.

---

*Architecture analysis: 2026-03-23*
