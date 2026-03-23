# Codebase Concerns

**Analysis Date:** 2026-03-23

## Tech Debt

**User registration path is stubbed:**
- Issue: `register` returns an empty string and does not persist users, validate input, or encode passwords.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`, `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`
- Impact: `/user/register` cannot create usable accounts, and follow-on login behavior depends on out-of-band database writes.
- Fix approach: Implement persistence through `UserMapper`, add password encoding, validate duplicate usernames, and return a structured response such as `ResultData`.

**OCR feature is coupled to one developer workstation:**
- Issue: The image-code endpoint shells out to a hard-coded Python interpreter and script under `C:\Users\hukss\...`.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Impact: The endpoint fails outside the original machine, blocks deployments, and bypasses Spring-managed dependency boundaries.
- Fix approach: Replace the external process call with an internal service abstraction or configurable command path injected from environment/configuration.

**Response contract is inconsistent across controllers:**
- Issue: `RandomDataController` returns `ResultData`, while `UserController` returns raw `String`.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`, `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`, `src/main/java/nl/hukss/randomdata/randomdata/vo/ResultData.java`
- Impact: Clients cannot rely on one API envelope, and error handling becomes endpoint-specific.
- Fix approach: Standardize controller outputs on `ResultData` or another single response model and document error codes centrally.

**Persistence logic assumes available seed data without guardrails:**
- Issue: `getOne` assumes a company row with `personal_info_id IS NULL` always exists and dereferences it immediately.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Impact: Requests fail with null dereference when seed data is exhausted or absent.
- Fix approach: Add explicit empty-result handling, seed strategy documentation, and tests for no-available-company behavior.

## Known Bugs

**JWT signing key initialization can fail at runtime:**
- Symptoms: Token creation or parsing can throw during Base64 decoding because the configured secret is treated as Base64 text without validation.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`
- Trigger: Any call to `JwtUtil.createJWT` or `JwtUtil.parseJWT` on environments where the literal key is not valid Base64 input for the chosen algorithm length.
- Workaround: None in application flow; tests only pass if current JJWT handling tolerates the configured key string.

**Unauthenticated users can access registration only by convention, not security config:**
- Symptoms: `/user/register` is not listed in `permitAll`, so access depends on Spring defaults and may be blocked once the endpoint is exercised through the filter chain.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`, `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`
- Trigger: Calling `POST /user/register` with the current `SecurityFilterChain`.
- Workaround: None in code; manual security config change is required.

## Security Considerations

**Secrets are committed in code and config defaults are production-like:**
- Risk: The JWT secret is hard-coded in source, and datasource config points to a specific PostgreSQL host with default credentials fallback.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/util/JwtUtil.java`, `src/main/resources/application.yml`
- Current mitigation: Username and password can be overridden by environment variables.
- Recommendations: Move JWT secret to environment-backed configuration, remove default credentials, and isolate local/dev/prod profiles.

**CSRF is disabled while form login remains enabled:**
- Risk: Browser-based authenticated flows using default form login and remember-me are exposed to CSRF unless the application is strictly stateless and token-only.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`
- Current mitigation: JWT filter is present for bearer-token requests.
- Recommendations: Either disable form login and remember-me for a stateless API or re-enable CSRF protection for browser sessions.

**Authentication object omits authorities entirely:**
- Risk: Security context is populated with `null` authorities, leaving no role model for future authorization rules.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsImpl.java`
- Current mitigation: Current endpoints use coarse authenticated-or-not access only.
- Recommendations: Populate authorities from user state and use `loginUser.getAuthorities()` when creating `UsernamePasswordAuthenticationToken`.

## Performance Bottlenecks

**Per-request external process execution for OCR:**
- Problem: Each OCR request starts a new Python process and blocks until completion.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Cause: `Runtime.getRuntime().exec(...)` and `ProcessBuilder` are used directly inside the controller.
- Improvement path: Move OCR into a reusable service, use async job execution if needed, and eliminate process startup on the request path.

**Random data request performs read-modify-write over multiple tables without transaction boundary:**
- Problem: `getOne` loads a company, saves `personal_info`, then updates `companies` in separate operations.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Cause: Controller-level orchestration spans multiple persistence calls without `@Transactional`.
- Improvement path: Move the workflow into a service method with transaction management and locking or optimistic conflict handling.

## Fragile Areas

**Security filter wiring bypasses container-managed construction:**
- Files: `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`, `src/main/java/nl/hukss/randomdata/randomdata/filter/JwtAuthenticationTokenFilter.java`
- Why fragile: `SecurityConfig` inserts `new JwtAuthenticationTokenFilter()` instead of the Spring bean, while the filter itself relies on `@Autowired UserMapper`.
- Safe modification: Inject the filter bean into `SecurityConfig` and add an integration test that exercises authenticated requests through the full filter chain.
- Test coverage: There is filter behavior coverage in `src/test/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsServiceImplTests.java`, but it calls the bean directly rather than verifying actual security-chain wiring.

**Character encoding and text assets are inconsistent:**
- Files: `src/main/resources/static/index.html`, `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`, `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserDetailsImpl.java`
- Why fragile: Several Chinese strings appear mojibake-encoded, which makes UI text, exception messages, and comments hard to trust and maintain.
- Safe modification: Normalize file encodings to UTF-8, then add a quick smoke test for UI/static assets and user-facing messages.
- Test coverage: No automated tests assert rendered static content or localized messages.

## Scaling Limits

**Company allocation model is single-use and depletion-based:**
- Current capacity: Limited to the number of rows in `companies` where `personal_info_id` is `NULL`.
- Limit: `GET /randomData/getOne/{userId}` stops working once that pool is exhausted.
- Scaling path: Generate company data on demand, batch replenish the table, or decouple personal-info generation from one-time company reservation in `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

**Test execution depends on reachable Docker infrastructure:**
- Current capacity: Test runs require Docker and default to a remote daemon at `tcp://10.35.11.202:2375`.
- Limit: Builds fail or hang when that host is unreachable or unsuitable for the current developer environment.
- Scaling path: Prefer local Docker by default, keep remote Docker optional, and document environment selection near `build.gradle`.

## Dependencies at Risk

**Spring Security configuration mixes starter-managed and pinned versions:**
- Risk: `org.springframework.boot:spring-boot-starter-security:3.3.4` is pinned while the Spring Boot plugin is `3.5.3`.
- Impact: Dependency alignment can drift from the BOM managed by the Boot plugin.
- Migration plan: Remove explicit version pins for Boot-managed starters in `build.gradle` unless a documented override is required.

**Test config appears copied from another project baseline:**
- Risk: `application-unit-test.yaml` includes unrelated sections such as `yshop`, Redis, and Druid-oriented comments that do not match this codebase.
- Impact: Future maintainers may infer nonexistent runtime dependencies or spend time diagnosing dead configuration.
- Migration plan: Trim test configuration to only the properties exercised by `src/test/java/**` and the current Spring Boot stack.

## Missing Critical Features

**Password storage and registration flow:**
- Problem: The application exposes login and registration endpoints without an implemented secure registration pipeline.
- Blocks: Real user onboarding and any safe production authentication flow.

**Input validation and API-level error mapping:**
- Problem: Controllers accept request bodies and path variables without Bean Validation or centralized exception translation.
- Blocks: Predictable client behavior and safe handling of malformed input across `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java` and `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`.

## Test Coverage Gaps

**Security configuration coverage is incomplete:**
- What's not tested: Actual access rules for `/user/login`, `/user/register`, `/randomData/**`, form login, CSRF settings, and remember-me behavior.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/config/SecurityConfig.java`
- Risk: Security regressions can ship unnoticed because current tests target service/filter behavior, not the HTTP security contract.
- Priority: High

**User registration and controller contract coverage is missing:**
- What's not tested: `UserServiceImpl.register`, `UserController.register`, and the response shape for login/register endpoints.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/service/impl/UserServiceImpl.java`, `src/main/java/nl/hukss/randomdata/randomdata/controller/UserController.java`
- Risk: The registration path is already nonfunctional and could remain broken without detection.
- Priority: High

**OCR endpoint coverage is missing:**
- What's not tested: `POST /randomData/getImageCode`, subprocess failures, stdout/stderr parsing, and portability behavior.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Risk: The most environment-specific endpoint has no regression net and can fail silently across machines.
- Priority: High

**No-data and failure-path coverage around random data generation:**
- What's not tested: Empty `companies` table, database write failure after company selection, and concurrent allocation behavior.
- Files: `src/main/java/nl/hukss/randomdata/randomdata/controller/RandomDataController.java`
- Risk: Production failures will surface as runtime exceptions or inconsistent table state.
- Priority: Medium

---

*Concerns audit: 2026-03-23*
