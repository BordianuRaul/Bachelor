# AGENTS.md — HabitFinder backend

Spring Boot backend for **HabitFinder**, the bachelor's thesis app *"Detecting Habit Triggers: A Mobile
App for Identifying Behavioral Triggers"* (UBB Cluj, June 2025). It is now the starting point for the
master's dissertation (**HabitFinder 2.0**, databases master's, 2026–27). The Flutter client is a sibling
repo: `../BachelorFrontend` (see its AGENTS.md). Its API contract is mirrored at the bottom of this file.

This file describes the code **as it is** (last commit `703e497`, 13 Jun 2025). It is not a design document
for 2.0. If a 2.0 change contradicts something here, update this file in the same change.

## Stack

| Thing | Value |
|---|---|
| Language / runtime | Java 21 (`pom.xml` `java.version`) |
| Framework | Spring Boot 3.4.3: web, data-jpa, security, oauth2-client (unused), actuator |
| DB | MySQL, `jdbc:mysql://localhost:3306/bachelor` |
| Schema management | `spring.jpa.hibernate.ddl-auto=update`, so **Hibernate owns the schema**. Flyway (`flyway-core` + `flyway-mysql`) is on the classpath, so Boot runs it at every startup, but there's no `db/migration` folder, so it applies nothing. See Conventions before adding a migration to an existing DB. |
| Auth | Stateless JWT (jjwt 0.11.5, HS256), BCrypt passwords |
| Boilerplate | Lombok 1.18.36 |
| Build | Maven wrapper (`backend/mvnw`) |
| CI | `.github/workflows/build.yml`: `mvn verify` plus SonarCloud (`BordianuRaul_Bachelor`) |

## Layout

```
Bachelor/
  backend/                      <- Maven project root (run commands from here)
    src/main/java/com/example/backend/
      controller/   AppUsageController, JournalController, SRHIController, UserController
      service/      AppUsageService (habit heuristic), ApplicationService, JournalService,
                    SRHIService, UserService
      model/        JPA entities (see Data model)
      repository/   Spring Data JPA interfaces
      dto/          ApplicationDailyUsageDTO, HabitDTO, JournalDTO, JournalPageDTO, UserDTO
      utils/        JwtUtils, JwtFilter, SecurityConfig, PasswordEncoderConfig, WebConfig (CORS),
                    JournalMapperUtils
    src/main/resources/application.properties
    src/test/java/...           Mockito-only unit tests (no Spring context, no DB)
```

Layering is controller → service → repository. Controllers also parse the JWT themselves (see Auth).

## Commands

```bash
cd backend
./mvnw spring-boot:run          # needs a local MySQL with a `bachelor` database
./mvnw test                     # unit tests; no DB needed. RED on main: 2 stale UserControllerTest failures (HF-17)
./mvnw -DskipTests package      # jar in target/
```

The `application.properties` file holds the local dev DB password and the JWT secret in plain text. They
are local-only values. Don't copy them anywhere new. For 2.0, move them to env vars or a gitignored
`application-local.properties`.

## Data model (as of now)

```
users (UUID, @GeneratedValue)
 ├─1:1─ journals (UUID) ─1:N─ journal_page (UUID): habit_name, `before`, time, location   [all free text]
 ├─1:N─ habits (String UUID): name
 ├─1:N─ application_hourly_usages (String UUID): application_id, hour 0-23, seconds_spent, date
 └─1:N─ location_timestamps (String UUID): date_time, latitude, longitude, location_name   [never written]
applications (String UUID): name, package_name, category     <- global, shared across users
```

Things to know:
- **Mixed ID strategies.** `User`, `Journal` and `JournalPage` use `UUID` with `@GeneratedValue`, which maps
  to `binary(16)` in MySQL. `Habit`, `Application`, `ApplicationHourlyUsage` and `LocationTimestamp` use a
  `String` UUID assigned in a `final` field initializer (`varchar`).
- **Repository ID types are wrong** for the String-ID entities: `ApplicationRepository`,
  `ApplicationHourlyUsageRepository` and `LocationTimestampRepository` are declared `JpaRepository<…, UUID>`
  while the entity's `@Id` is a `String`. `findAll`/`save` work. `findById`/`deleteById` will not. Fix the generic type before using them.
- Because those String IDs are assigned up front (non-null, no `@Version`), Spring Data treats every new row
  as existing, so `save()` calls `merge`: a SELECT before every INSERT. Ingestion does one `save` per non-zero
  hour.
- `users.email` has **no unique constraint**. `register` checks `findByEmail`, then inserts (a race), and
  `UserRepository.findByEmail` returns a single `User`, so a duplicate email breaks login and `JwtFilter`.
- Table names in `@Table` are camelCase (`applicationHourlyUsages`). Spring's default naming strategy writes
  them to MySQL as snake_case.
- `before` is a MySQL reserved word, which is why the column is backtick-quoted in `JournalPage`.
- `applications` is keyed by **display name** (`existsByName`/`findByName`), not `packageName`, and has no
  unique constraint.
- Time is stored as `LocalDate` + `int hour`, with no timezone. All windows use the server's `LocalDate.now()`.
- **Nothing is bitemporal or versioned.** Rows have no `recorded_at`/`valid_from`, and ingestion is plain
  insert (see the ingestion bugs below).

## Auth

- `POST /api/users/register` and `POST /api/users/login` are public. Everything else is `authenticated()`
  via `SecurityConfig`.
- The JWT has subject = email, claim `id` = user UUID, and expiry `jwt.expiration` ms (36,000,000 = 10 h).
  `jwt.refresh.expiration` is defined but unused, and there are no refresh tokens.
- `JwtFilter` sets the security context. **Every controller also re-extracts the token from the
  `Authorization` header** and calls `jwtUtils.extractUserId(...)`. If you add an endpoint, follow the same
  pattern for now, or refactor all of them to `@AuthenticationPrincipal` at once. Don't mix the two.
- If a token is expired or malformed, `JwtFilter` lets the jjwt exception escape. The request is then
  forwarded to `/error`, which `SecurityConfig` doesn't permit, and with no `httpBasic`/`formLogin` the entry
  point is `Http403ForbiddenEntryPoint`. So the client gets **403 with an empty body**, not a clean 401. The
  same goes for a missing token and for any exception a controller doesn't catch. (Derived from the config;
  not yet observed against a running server.) The controllers' own "invalid token" branches are effectively
  unreachable.

## API surface

Request bodies are JSON. Responses are JSON or `text/plain` strings, as noted per row. All endpoints except
register/login need `Authorization: Bearer <jwt>`.

| Method | Path | Body → Response | Notes |
|---|---|---|---|
| POST | `/api/users/register` | `{firstName,lastName,email,password}` → 201 text | 409 if the email exists |
| POST | `/api/users/login` | `{email,password}` → `{message,user:UserDTO,token}` | 404 (empty) unknown email, 401 (empty) wrong password. `UserDTO` **includes the BCrypt hash** |
| GET | `/api/users/validateToken` | → `{message,user,token}` | used for auto-login |
| GET | `/api/users/all` | → `UserDTO[]` | any logged-in user gets every user, with hashes |
| POST | `/api/users/addHabit` | `{name}` (binds straight to the `Habit` entity) → 201 | |
| GET | `/api/users/getHabits` | → `[{id,name}]` | |
| POST | `/api/journal/addPageToJournal` | `{habitName,before,time,location}` → 200 text | **no client calls this yet** |
| POST | `/api/SRHI/result` | `[int ×12]` → plain-text interpretation | |
| GET | `/api/appUsage/lastUpdate` | → `"yyyy-MM-dd"` (plain text) | max stored date, or today−7 |
| PUT | `/api/appUsage/bulk` | `ApplicationDailyUsageDTO[]` → 202 text | `{name,packageName,category,date,hourlyUsageInSeconds[24]}` |
| GET | `/api/appUsage/findPossibleHabits` | → `string[]` (Markdown-ish `**bold**`) | the habit heuristic |

Error handling is inconsistent. `AppUsageController`, `JournalController`, `SRHIController` and
`UserController.register` catch `Exception` and return 500 with `e.getMessage()` in the body. The other
`UserController` endpoints return 500 with an **empty** body, and `addHabit` has no try/catch at all, so its
exceptions escape (see Auth for what the client then gets). There is no `@ControllerAdvice` and no validation
(`@Valid`).

## Domain logic

### Habit heuristic: `AppUsageService.findPossibleHabits`
For each app in the **global** `applications` table:
1. **Weak indication**: the user had any usage of the app on at least 4 distinct days (`APPLICATION_USAGES_THRESHOLD`)
   in `[today−6, today]` (`countUsageDaysInLastSevenDays`, `date >= today−6`).
2. **Mild indication** (only if weak): bucket each day's usage into 12 two-hour intervals and take each
   day's dominant interval. If one interval is dominant on at least `floor(days/2)` days, it's "mild", and
   the message names that interval (e.g. `20:00–22:00`).
3. A message is picked at **random** from 5 weak or 5 mild templates.

Known issues:
- The mild query uses `findBy…DateAfter(today−6)`, which is strictly after, so it covers 6 days while the
  weak count covers 7. The two windows don't match.
- `applicationUsageIntervals` is a mutable `HashMap` field on a singleton `@Service`. It is shared across
  requests and users, never cleared, and not thread-safe.
- There are N+1 queries per call: one query for every app in the system, not just the user's apps, plus a
  second for each app that passes the weak test.
- The mild test is close to automatic. Weak needs ≥4 of 7 days, so the 6-day mild window holds ≥3 days, and
  with 3 days the threshold is `floor(3/2) = 1`. When several intervals pass, it reports the first in
  `HashMap` order (the lowest index), not the most dominant one.
- Random templates make the output non-deterministic, so it can't be asserted in tests.
- It uses frequency plus time-of-day only. Location is never used, and **no trigger detection exists**,
  although the last two commit messages say "heuristics for possible triggers". The frontend's "Explore
  triggers" screen calls this same endpoint.

### Ingestion: `PUT /api/appUsage/bulk`
`ApplicationService.updateApplicationsUsed` inserts unseen apps by name. Then `AppUsageService.updateAppUsage`
inserts one `ApplicationHourlyUsage` row per non-zero hour. **It is append-only with no upsert or dedup.**
The client re-sends from `lastUpdate` (inclusive) through today, so:
- the last synced day is inserted again on every sync, and
- today's partial day is stored, then stored again in full the next time.

Hourly `seconds_spent` totals get inflated. The weak test (`COUNT DISTINCT date`) is immune, but the mild
test, which sums seconds, is not. Any 2.0 work on usage data must fix this first: use a unique key on
`(user, application, date, hour)` plus upsert, or model it properly as recorded-vs-valid time.

### SRHI: `SRHIService`
Self-Report Habit Index (Verplanken & Orbell, 2003): 12 items on a 1–7 Likert scale, summed (range 12–84).
Bands: ≤29 very weak, ≤47 weak, ≤65 moderate, ≤84 strong, >84 very strong. **"Very strong" can never
be reached** because the maximum score is 84, at least for valid input. The server doesn't check the list
length or the 1–7 range; `[int ×12]` is only the client's convention. Results are not stored, and the endpoint isn't tied to a
specific habit.

### Journal
One journal per user, created in the `User` constructor. Pages are free-text `habitName/before/time/location`.
`habitName` is a string, not a FK to `habits`. Nothing in the backend reads pages back (`JournalDTO`
and `JournalMapperUtils` exist but no endpoint uses them).

## Tests

- Plain JUnit 5 + Mockito (`@InjectMocks`/`@Mock`). No `@SpringBootTest`/`@DataJpaTest`, so nothing
  covers the repositories, queries or security config. `HabitDTOTest`, `HabitTest`, `JournalPageRepositoryTest`,
  `JournalServiceTest` and `JournalMapperUtilsTest` are empty classes. 9 tests in total.
- **The suite is red on main**: `UserControllerTest` has 2 stale tests (they expect `save()` and never stub
  `checkPassword`/`JwtUtils`). Nobody noticed because CI never ran. Fixed in HF-17.
- `AppUsageService`, `SRHIService` and the JWT code have **no tests**.
- For anything touching queries or the schema in 2.0, add `@DataJpaTest` against Testcontainers MySQL
  (H2 won't reproduce MySQL's quoting or UUID behaviour).

## CI caveat

`build.yml` sets up **JDK 17** but the POM targets **21**, so `mvn verify` in CI fails at compile time.
Bump the workflow to `java-version: 21` before relying on CI or Sonar.

## Specs (OpenSpec)

Tasks in SDD mode (criteria in the workspace `../AGENTS.md`: anything a user or the client sees, schema, API
contract, design choices, not just size) are spec-driven. Each one is an OpenSpec change in `openspec/changes/`,
archived to `openspec/changes/archive/` with its delta merged into `openspec/specs/` before the PR opens.
**`openspec/specs/` is the authoritative contract** for the behaviour it covers: read it before you change that
behaviour. The API table below stays as a summary and is updated in the same PR. If they disagree, the spec wins.
Project constraints for the artifacts are in `openspec/config.yaml`. The workflow (which tasks are spec-driven,
naming, review) is in the workspace `../AGENTS.md`.

## Conventions when changing code

- Constructor injection (some classes still have stray `@Autowired` on fields or constructors; don't add more).
- DTOs at the controller boundary. New DTOs should be `record`s, as `JournalDTO`/`JournalPageDTO` already are.
- Don't return entities or password hashes from endpoints.
- Once the schema starts changing for 2.0, switch from `ddl-auto=update` to Flyway migrations
  (`src/main/resources/db/migration/V1__baseline.sql` from the current schema, then `ddl-auto=validate`).
  An existing dev DB already has the tables (and possibly an empty `flyway_schema_history`), so either start
  from a fresh DB or set `spring.flyway.baseline-on-migrate=true` with `baseline-version=1`. Otherwise Flyway
  re-runs V1 against existing tables or refuses a non-empty schema.
  The schema is the dissertation's subject, so it has to be versioned and reviewable.
- `@CrossOrigin("http://localhost:4200")` on the controllers is a leftover from an Angular template.
  The mobile client doesn't need CORS.

## Contract with the Flutter client

The client hardcodes the base URL `http://192.168.1.219:8080/api/...` (a physical device on the LAN). For the
Android emulator use `http://10.0.2.2:8080`. When you change an endpoint's path, body or status code, change
the matching `lib/src/services/*_service.dart` in `../BachelorFrontend/frontend`. The client checks exact
status codes (e.g. register expects 201, bulk expects 200 or 202).
