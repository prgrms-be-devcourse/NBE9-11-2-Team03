# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

Gradle (Kotlin DSL) + Spring Boot 3.5.13, Java 21. Use the wrapper (`./gradlew`).

- Run the app: `./gradlew bootRun`
- Build jar: `./gradlew build`
- Full test suite: `./gradlew test`
- Single test class: `./gradlew test --tests com.example.festival.FestivalControllerTest`
- Single test method: `./gradlew test --tests com.example.festival.FestivalControllerTest.methodName`
- Start MySQL for local dev: `docker-compose up -d` (reads `.env`)

### Configuration files — read before editing

There are three YAML files and their interaction matters:

- `application.yaml` — **gitignored**, the real config for local dev against MySQL. Reads `DB_URL`, `MYSQL_USER_ID`, `MYSQL_ROOT_PASSWORD`, `JWT_SECRET`, `api.public-data.key` from the environment / `.env`.
- `application.yml` — committed fallback using H2 file DB (`jdbc:h2:./db_dev`). Spring picks `application.yaml` first when both exist, so `application.yml` is effectively only used by teammates who don't have their own `application.yaml`.
- `application-test.yaml` — in-memory H2, activated with `@ActiveProfiles("test")`. `BaseInitData` and `InitDataConfig` are `@Profile("!test")`, so they do not run during tests.
- `application-template.yaml` — documentation / reference copy of the JWT + dev-token keys; not loaded by Spring.

`.env` holds MySQL credentials and `DB_URL` consumed by both `docker-compose.yml` and `application.yaml`. **Never commit `.env` or `application.yaml`** (both already in `.gitignore`).

## Architecture

### Package layout

- `com.example.domain.<feature>` — one package per bounded context: `festival`, `member`, `review`, `reviewlike`, `reviewreport`, `bookmark`, `admin`. Each has the conventional `controller` / `service` / `repository` / `entity` / `dto` sub-packages. `admin` and `reviewlike` are thinner (no dedicated service layer — they reuse services from the related domains).
- `com.example.global` — cross-cutting: `config` (Security, Querydsl, RestTemplate, PasswordEncoder), `security` (JWT filter), `jwt` (token util), `entity` (JPA `@MappedSuperclass` bases), `exception` + `exceptionHandler`, `rsData` / `response` (envelopes), `initData` + `init` (dev seeders), `webMvc` (CORS).
- `FestivalApplication` enables JPA auditing at the root.

### Response envelopes — two coexisting styles

The codebase is mid-migration between two response wrappers. Match whichever the surrounding code uses; do not refactor across controllers unprompted.

- `RsData<T>` (record in `global.rsData`) — newer. Has static `success(msg, data)` / `fail(msg)` factories. Used by festival, admin, bookmark controllers. `GlobalExceptionHandler` always returns `RsData`.
- `ApiRes<T>` (class in `global.response`) — older. Used by `AuthController`, `ReviewController`, `MyPageController`.

### Exception handling

Throw the domain-specific subtype from `global.exception` (`BadRequestException`, `UnauthorizedException`, `ForbiddenException`, `ConflictException`, `CustomNotFoundException`, `DuplicateResourceException`). `GlobalExceptionHandler` maps each to an HTTP status + `RsData`. Do **not** catch and wrap into `ResponseEntity` inside services — let the advice handle it.

### Auth & Security

`SecurityConfig` wires `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter` with stateless sessions. Route rules:

- `permitAll`: `POST /api/auth/{signup,login,reissue}`, Swagger (`/swagger-ui/**`, `/v3/api-docs/**`), H2 console, `GET /api/festivals` and `GET /api/festivals/{id}`.
- `hasRole("ADMIN")`: `/api/admin/**`.
- Everything else requires authentication.

`JwtUtil` issues access + refresh tokens with `type` claim; access tokens embed `memberId` and `role`. Controllers read the authenticated user via `authentication.getName()` which is the `loginId` (JWT subject).

**Dev token bypass**: when `security.dev-token.enabled=true`, any request with `Authorization: Bearer <security.dev-token.value>` is authenticated as `security.dev-token.login-id` with role `security.dev-token.role`. Default config ships this enabled with `ADMIN` role for Postman convenience — it must be turned off (`DEV_TOKEN_ENABLED=false`) in any non-dev environment.

### Persistence conventions

- Extend `BaseEntity` (id + createdAt + updatedAt) or `BaseCreatedEntity` (id + createdAt only). Both use `AuditingEntityListener`; auditing is enabled globally via `@EnableJpaAuditing` on `FestivalApplication`.
- `ddl-auto: update` in both MySQL and H2 profiles — schema evolves automatically, so entity changes take effect on next boot. Tests use `create-drop`.
- QueryDSL is wired through `QuerydslConfig` (exposes `JPAQueryFactory`). Generated Q-classes land in `/src/main/generated/` (gitignored). Custom repository pattern: `XxxRepositoryCustom` interface + `XxxRepositoryImpl` + have `XxxRepository extends JpaRepository, XxxRepositoryCustom`. See `FestivalRepositoryImpl` for the canonical example.
- Concurrency-sensitive counters use `@Modifying` JPQL updates rather than entity mutation (e.g. `FestivalRepository.incrementViewCount`). Bookmark counts are mutated via entity methods inside `@Transactional` services — keep that split.
- Member deletion is **logical**: `Member.withdraw()` sets status to `WITHDRAWN` and rewrites nickname to `탈퇴한회원_{id}`. Do not add hard-delete paths.

### Festival data pipeline

Festivals are sourced from the Korean public-data API (`api.public-data.base-url` = `apis.data.go.kr/B551011/KorService2`). Flow:

1. `FestivalAdminController POST /api/admin/festivals/sync` → `FestivalSyncService.syncFestivalList` → `FestivalApiClient.fetchFestivalList` (`/searchFestival2`) → upsert by `contentId` via `FestivalApiConverter`.
2. `POST /api/admin/festivals/enrich` (or `/{contentId}/enrich` for a single row) → `fetchFestivalDetail` (`/detailCommon2`) → fills `overview`, `homepageUrl`, `contactNumber`.
3. Public `GET /api/festivals` → `FestivalRepositoryImpl.searchFestivals` (QueryDSL): filters by `regionCode` / `status` / `month` / `keyword` / bounding-box `(mapX, mapY, radiusKm)`, then applies a two-stage sort — mandatory status priority (`ONGOING` < `UPCOMING` < `ENDED`) first, then either the caller's `Pageable` sort (`viewCount` / `startDate` / `bookMarkCount`) or a default that orders ongoing/upcoming by `startDate` asc and ended by `endDate` desc.
4. `GET /api/festivals/{id}` increments `viewCount` via the `@Modifying` update before returning the entity.
5. `GET /api/festivals/nearby` returns `FestivalMarkerDto` list using the same bounding-box predicate without pagination.

The `Festival` entity declares composite indexes for the three read patterns (status+coords, region+date, status+date) — preserve them if you add migrations.

### Dev-only seeding

Two seeders run on every non-test startup and are idempotent (they check `count() > 0` before inserting):

- `global.init.InitDataConfig` → seeds 5 dummy members (including `test01` / `admin01`) and one festival.
- `global.initData.BaseInitData` → seeds 5 dummy festivals with varied statuses for sort/filter testing.

Both are marked `@Profile("!test")`. The comments in the files flag them for eventual removal.

### CORS

`WebMvcConfig` allows only `http://localhost:3000` on `/api/**` with credentials. Adjust there (not per-controller) if the frontend origin changes.
