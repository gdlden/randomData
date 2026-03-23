# Codebase Structure

**Analysis Date:** 2026-03-23

## Directory Layout

```text
randomData/
├── build.gradle                         # Gradle build, plugins, and dependencies
├── settings.gradle                      # Gradle project settings
├── gradlew.bat                          # Windows Gradle wrapper entry
├── src/
│   ├── main/
│   │   ├── java/nl/hukss/randomdata/randomdata/
│   │   │   ├── config/                  # Spring Security configuration
│   │   │   ├── controller/              # HTTP endpoints
│   │   │   ├── entity/                  # MyBatis-Plus entities
│   │   │   ├── filter/                  # Servlet/security filters
│   │   │   ├── mapper/                  # Mapper interfaces
│   │   │   ├── service/                 # Service contracts
│   │   │   ├── service/impl/            # Service and auth implementations
│   │   │   ├── util/                    # Utility classes such as JWT helpers
│   │   │   ├── vo/                      # Response/value objects
│   │   │   └── RandomDataApplication.java
│   │   └── resources/
│   │       ├── application.yml          # Runtime configuration
│   │       └── static/                  # Static frontend assets
│   └── test/
│       ├── java/                        # Spring Boot and unit tests
│       └── resources/                   # Test configuration
└── .planning/codebase/                  # Generated codebase map documents
```

## Directory Purposes

**`src/main/java/nl/hukss/randomdata/randomdata/config`:**
- Purpose: Hold framework-level configuration.
- Contains: Spring Security beans.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`

**`src/main/java/nl/hukss/randomdata/randomdata/controller`:**
- Purpose: Accept HTTP requests and shape responses.
- Contains: REST controllers.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`, `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`

**`src/main/java/nl/hukss/randomdata/randomdata/entity`:**
- Purpose: Map Java models to database tables.
- Contains: Annotated entity classes.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/entity/User.java`, `src/main/java/nl/hukss/randomdata/randomdata/entity/PersonalInfo.java`, `src/main/java/nl/hukss/randomdata/randomdata/entity/Company.java`

**`src/main/java/nl/hukss/randomdata/randomdata/filter`:**
- Purpose: Intercept servlet requests before controllers.
- Contains: JWT auth filter and an additional custom filter placeholder.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`, `src/main/java/nl/hukss/randomdata/randomdata/filter/CustomFilter.java`

**`src/main/java/nl/hukss/randomdata/randomdata/mapper`:**
- Purpose: Provide MyBatis-Plus mapper interfaces.
- Contains: `BaseMapper` specializations only.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/mapper/UserMapper.java`, `src/main/java/nl/hukss/randomdata/randomdata/mapper/CompanyMapper.java`, `src/main/java/nl/hukss/randomdata/randomdata/mapper/PersonalInfoMapper.java`

**`src/main/java/nl/hukss/randomdata/randomdata/service`:**
- Purpose: Define service contracts used by controllers.
- Contains: `IService` extensions plus auth-facing methods.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/service/UserService.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/CompanyService.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/PersonalInfoService.java`

**`src/main/java/nl/hukss/randomdata/randomdata/service/impl`:**
- Purpose: Implement service contracts and Spring Security adapters.
- Contains: MyBatis-Plus `ServiceImpl` classes, `UserDetailsServiceImpl`, and `UserDetailsImpl`.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImpl.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsImpl.java`

**`src/main/java/nl/hukss/randomdata/randomdata/util`:**
- Purpose: Hold shared helpers without Spring MVC semantics.
- Contains: JWT creation/parsing logic.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`

**`src/main/java/nl/hukss/randomdata/randomdata/vo`:**
- Purpose: Define response wrappers and frontend-facing payload shapes.
- Contains: Result envelope, status codes, and generated personal info payload.
- Key files: `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java`, `src/main/java/nl/hukss/randomdata/randomdata/vo/ReturnCode.java`, `src/main/java/nl/hukss/randomdata/randomdata/vo/PersonalInfoVo.java`

**`src/main/resources/static`:**
- Purpose: Serve the built-in browser UI and vendor JS assets.
- Contains: `index.html` plus bundled JavaScript files.
- Key files: `src/main/resources/static/index.html`, `src/main/resources/static/js/jquery-3.7.1.min.js`, `src/main/resources/static/js/iife.min.js`

**`src/test/java`:**
- Purpose: Verify application behavior through unit and Spring Boot integration tests.
- Contains: Package-mirrored tests for controllers, services, utils, and VOs.
- Key files: `src/test/java/nl/hukss/randomdata/randomdata/RandomDataApplicationTests.java`, `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java`, `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImplTests.java`

## Key File Locations

**Entry Points:**
- `src/main/java/nl/hukss/randomdata/randomdata/RandomDataApplication.java`: Spring Boot main class.
- `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`: Random-data and OCR endpoints.
- `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`: Login and register endpoints.

**Configuration:**
- `build.gradle`: Dependencies, Java toolchain, and test task configuration.
- `src/main/resources/application.yml`: Runtime datasource and server configuration.
- `src/test/resources/application-unit-test.yaml`: Test-only datasource configuration.
- `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`: Security filter chain and auth manager bean.

**Core Logic:**
- `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`: Login flow and JWT issuance.
- `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImpl.java`: User lookup for authentication.
- `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`: JWT-to-security-context bridge.
- `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`: Token signing and parsing.

**Testing:**
- `src/test/java/nl/hukss/randomdata/randomdata/RandomDataApplicationTests.java`: PostgreSQL-backed CRUD smoke tests.
- `src/test/java/nl/hukss/randomdata/randomdata/controller/RandomDataControllerTests.java`: Controller integration tests with database setup.
- `src/test/java/nl/hukss/randomdata/randomdata/util/JwtUtilTests.java`: Token utility unit tests.

## Naming Conventions

**Files:**
- `*Controller.java` for HTTP adapters, for example `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`.
- `*Service.java` and `*ServiceImpl.java` for service contracts and implementations, for example `src/main/java/nl/hukss/randomdata/randomdata/service/CompanyService.java`.
- `*Mapper.java` for persistence interfaces, for example `src/main/java/nl/hukss/randomdata/randomdata/mapper/PersonalInfoMapper.java`.
- `*Tests.java` for test classes, for example `src/test/java/nl/hukss/randomdata/randomdata/vo/ResultDataTests.java`.

**Directories:**
- Lowercase package-style directories under `src/main/java/nl/hukss/randomdata/randomdata/`.
- Layer-specific leaf directories such as `controller`, `service`, `mapper`, and `entity`.

## Where to Add New Code

**New Feature:**
- Primary code: Add the controller in `src/main/java/nl/hukss/randomdata/randomdata/controller/`, the service contract in `src/main/java/nl/hukss/randomdata/randomdata/service/`, and the implementation in `src/main/java/nl/hukss/randomdata/randomdata/service/impl/`.
- Persistence: Add the entity in `src/main/java/nl/hukss/randomdata/randomdata/entity/` and the mapper in `src/main/java/nl/hukss/randomdata/randomdata/mapper/`.
- Tests: Add mirrored test classes under `src/test/java/nl/hukss/randomdata/randomdata/`.

**New Security Behavior:**
- Filter-chain changes belong in `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`.
- Request authentication helpers belong in `src/main/java/nl/hukss/randomdata/randomdata/filter/` or `src/main/java/nl/hukss/randomdata/randomdata/util/`.

**Utilities:**
- Shared helpers belong in `src/main/java/nl/hukss/randomdata/randomdata/util/`.
- Shared response/value objects belong in `src/main/java/nl/hukss/randomdata/randomdata/vo/`.

## Special Directories

**`build/`:**
- Purpose: Gradle build outputs.
- Generated: Yes.
- Committed: No.

**`.planning/codebase/`:**
- Purpose: Generated codebase reference documents for GSD workflows.
- Generated: Yes.
- Committed: Usually yes when planning artifacts are tracked.

**`src/main/resources/static/js/`:**
- Purpose: Vendor frontend JavaScript assets served as static files.
- Generated: No.
- Committed: Yes.

**`src/main/node_modules/`:**
- Purpose: Local Node packages present under `src/main/`.
- Generated: Yes.
- Committed: Current repository state includes it in the working tree and it should be treated as existing project content until cleaned up intentionally.

---

*Structure analysis: 2026-03-23*
