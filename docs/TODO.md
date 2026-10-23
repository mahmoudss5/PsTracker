# PsTracker prioritized TODO

This is an implementation plan, ordered by risk and dependency. Each item says what to add/change, where to do it, and why it matters.

Deployment classifications:

- **Critical — cannot deploy without:** required for a safe production deployment.
- **Can be fixed after deployment:** important, but the application can be deployed with a planned follow-up.
- **Enhancement:** improves quality, maintainability, or future growth without blocking deployment.
- **Completed:** implemented locally; keep the stated verification step where one remains.

## P0 — security and deployment blockers

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P0.1 | Remove credential-shaped fallback values. Require `DB_PASSWORD`, `JWT_SECRET`, all required AWS settings, and deployment-specific origins through environment/secret injection. | `back/src/main/resources/application.yml`, `docker-compose.yml`, deployment secret configuration | A missing secret must stop startup instead of silently using a default. If any value was ever real, rotate it before deployment. | **Critical — cannot deploy without** |
| P0.2 | **Completed:** separate development and production Compose definitions. Keep production free of source/Maven mounts and host debug port `5005`. | `docker-compose.dev.yml`, `docker-compose.prod.yml`, `back/Dockerfile.dev`, `back/Dockerfile.prod`, `Front/icpc-tracker/Dockerfile.dev`, `Front/icpc-tracker/Dockerfile.prod` | Production now starts from the executable JAR and omits the unsupplied OpenTelemetry agent; development keeps source mounts and debugging. | **Completed** |
| P0.3 | Add an authorization matrix and enforce it explicitly with `@PreAuthorize` and/or a dedicated authorization service. | `back/src/main/java/.../Config/Security/SecurityConfiguration.java`, controllers, service authorization helpers | `.anyRequest().authenticated()` proves identity but not permission. Define what Admin, Coach, Trainee, team member, team coach, and resource owner may do. | **Critical — cannot deploy without** |
| P0.4 | Finish the authenticated-identity audit. Ignore or remove caller-supplied actor fields (`userId`, `senderId`, owner IDs); keep target IDs only when the target is authorized. | User, Team, Submission, Report, Notification, Announcement, Contest, Material/Storage controllers and services | Prevent a user from reading or mutating another user’s records by changing an ID in the URL/body. Add cross-user and cross-team negative tests. | **Critical — cannot deploy without** |
| P0.5 | **Completed locally:** apply the team-membership policy to every team-scoped read/write, including materials, storage URLs, announcements, messages, team details, and user lists. Complete the `@IsTeamMember` usage and return 403 for denied access. | Controllers/services using `teamId` or `s3Key`, `IsTeamMemberAspect`, `GlobalExcHandler` | Team IDs and material keys are checked against the authenticated user before access. Team announcements are restricted to the team coach; message services retain a defense-in-depth membership check. Add full PostgreSQL/WebSocket negative integration tests before release. | **Completed locally; integration verification remains** |
| P0.6 | Restrict WebSocket origins to configured frontend origins and authorize `CONNECT`, `SUBSCRIBE`, and `SEND` destinations. | `WebSocketConfig`, `WebSocketAuthInterceptor`, message/presence controllers | `setAllowedOriginPatterns("*")` and unrestricted topic subscriptions can expose team messages or presence across teams. Add tests for another-team subscriptions and sends. | **Critical — cannot deploy without** |
| P0.7 | Fix CI path and trigger configuration; add backend and frontend jobs, then add secret scanning. | `.github/workflows/PsTrackerCi.yml` | CI currently does not run Maven from `back/` and does not run the frontend checks. A merge should prove `./mvnw clean test`, `npm run build`, and `npm run lint`. | **Critical — cannot deploy without** |
| P0.8 | Put the application behind HTTPS, publish only the frontend/reverse-proxy port, and protect Swagger and Actuator. | Reverse proxy/deployment files, `SecurityConfiguration`, application profiles | PostgreSQL, Redis, OTLP, Prometheus, Loki, Tempo, and Grafana should remain private. Expose only health/readiness publicly and protect operational endpoints. | **Critical — cannot deploy without** |

## P1 — backend correctness and reliability

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P1.1 | Add Bean Validation to request DTOs and `@Valid` at controller boundaries. | `back/src/main/java/.../DataAccessLayer/Dto`, controllers | Reject blank content, invalid emails, weak/oversized passwords, invalid handles, negative IDs, invalid file metadata, and oversized messages before business logic. | **Can be fixed after deployment** |
| P1.2 | Standardize exceptions and error responses. Map authentication, authorization, validation, not-found, conflict, rate-limit, and upstream failures to stable 4xx/5xx codes. | `ExceptionHandling`, `GlobalExcHandler`, services, aspects | Generic `RuntimeException` currently causes ambiguous errors; authorization failures must be 403, not 500. Include a request/correlation ID. | **Can be fixed after deployment** |
| P1.3 | Reconcile the Flyway schema with the entities using new corrective migrations. Keep `ddl-auto: validate` and set `flyway.clean-disabled: true` outside tests. | `back/src/main/resources/dbMigration`, `application*.yml` | The material migrations contain legacy columns and a missing version number. Validate against PostgreSQL, not only H2. Never rewrite an applied migration. | **Critical — cannot deploy without** |
| P1.4 | **Completed locally:** add database constraints and indexes for ownership, Codeforces IDs, team membership, and common date/team/user queries. | `back/src/main/resources/dbMigration/V7__add_constraints_and_indexes.sql` | Database rules now protect uniqueness and integrity even when two requests race; run the migration against PostgreSQL in CI before marking it fully verified. | **Completed; integration verification remains** |
| P1.5 | Add Codeforces HTTP timeouts, bounded backoff, last-success/last-failure status, checkpoints/incremental fetching, and no-overlap/deduplication controls. | `ImpCfService`, `CfWorker`, `CfQueue`, `AppConfig`/HTTP client config, persistence | The queue already spaces worker jobs and retries remote failures, but a network timeout and repeated full sync still need controlled behavior. Do not reintroduce `Thread.sleep()` into scheduled jobs. | **Can be fixed after deployment** |
| P1.6 | Replace Redis `KEYS` presence scans and unsafe cache default typing; define Redis persistence/auth/TLS policy. | `PresenceService`, `RedisConfig`, deployment config | `KEYS` can block Redis, and unsafe polymorphic deserialization is unnecessary risk. Presence and cache failure behavior should be tested. | **Can be fixed after deployment** |
| P1.7 | Secure S3 presigned URL creation/use, validate file metadata and size, generate server-owned object keys, and clean up abandoned uploads. | `StorageController`, storage service, Material service, S3 bucket/IAM policy | A coach role alone is not enough; the caller must be authorized for the selected team and the object must not escape its intended prefix. | **Critical — cannot deploy without** |
| P1.8 | Add integration tests for PostgreSQL migrations, Redis queue/rate limit/presence, WebSocket membership, and authorization boundaries. | `back/src/test`, Testcontainers configuration | Unit tests pass after a clean build, but the most important security and infrastructure behavior is not covered end-to-end. | **Can be fixed after deployment** |

## P1 — frontend readiness

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P1.9 | **Completed:** fix the 30 lint errors and 3 warnings, including unsafe `any`, effect state updates, table sorting, and hook dependencies. | `Front/icpc-tracker/src/**` | `npm run lint` now passes with zero errors and warnings. | **Completed** |
| P1.10 | Enable a real protected route guard and derive authorization state from the authenticated session. | `Front/icpc-tracker/src/pages/RootLayout.tsx`, `router.tsx`, auth context/hooks | The route guard is commented out, so the UI can render dashboard routes without a valid session even though API calls later fail. | **Critical — cannot deploy without** |
| P1.11 | Reduce reliance on access tokens and user identity values in `localStorage`; use secure cookies/memory where possible and document the remaining learning-project tradeoff if not migrated yet. | `AuthService.tsx`, `AuthContext.tsx`, `config/api.tsx` | XSS can read localStorage tokens. Backend authorization must remain authoritative, but the client storage risk should be reduced. | **Can be fixed after deployment** |
| P1.12 | Remove debug logs and remaining placeholder values, and add consistent loading, empty, retry, and forbidden states. | `Front/icpc-tracker/src/**` | Production logs should not contain credentials or noisy client diagnostics, and users should see meaningful states when APIs fail. | **Can be fixed after deployment** |
| P1.13 | Split the largest frontend bundle with route/component lazy loading. | `router.tsx`, Vite config | The current production JavaScript chunk is about 618 kB before gzip and Vite warns above 500 kB. | **Enhancement** |

## P1 — operations

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P1.14 | Add health checks and health-based startup dependencies for the backend, frontend, PostgreSQL, Redis, Collector, and observability services. | Compose files, Actuator config | Startup should not race database/Redis readiness, and deployment should detect unhealthy containers. | **Critical — cannot deploy without** |
| P1.15 | Persist observability data, configure retention, and add dashboards/alerts for availability, latency, errors, Redis/DB health, Codeforces sync, restarts, and disk. | `docker-compose.yml`, `otel/`, Grafana provisioning | The current observability services are useful locally but need durable data and actionable alerts for deployment. | **Can be fixed after deployment** |
| P1.16 | Write and test a runbook for deploy, restart, logs, backups/restores, secret rotation, and rollback by image version. | `docs/DEPLOYMENT.md` and deployment system | Recovery steps must be known before an incident. | **Can be fixed after deployment** |

## P2 — maintainability

| Priority | What to add/change | Where | Why | Deployment classification |
|---|---|---|---|---|
| P2.1 | Add generated/validated frontend API types from the backend OpenAPI contract. | CI, frontend types, Springdoc output | Prevent DTO drift between Java and TypeScript. | **Enhancement** |
| P2.2 | Add Java/TypeScript formatting and static-analysis rules. | Maven/CI and frontend config | Keep changes consistent and catch defects before review. | **Enhancement** |
| P2.3 | Rename spelling inconsistencies such as `TraingingTracker`, `Announcment`, `Presencs`, and `Secuiry` in a planned migration. | Packages/classes and migration-safe database mappings | Naming currently increases maintenance cost; do not rename database columns without a migration plan. | **Enhancement** |
| P2.4 | Decide whether `StressTesting.summary.json` belongs in version control; remove generated artifacts if they are local outputs. | Repository root and `.gitignore` | Generated results can become stale or expose environment details. | **Enhancement** |
| P2.5 | Add a license and contribution guide. | Root documentation | Make reuse and collaboration expectations explicit. | **Enhancement** |

## Already completed or no longer valid as TODOs

- The backend Dockerfile already builds and runs an executable JAR; do not add the old `mvn spring-boot:run` replacement again.
- STOMP/SockJS dependencies and live chat hooks are already present; do not recreate the old mock-chat task.
- Announcements and materials are already wired to backend hooks; the remaining work is authorization, error handling, and verification.
- The Codeforces worker does not use `Thread.sleep()`; keep the controlled queue/scheduler model while adding timeouts and status reporting.
- `IsTeamCoachAspect` now checks upload/delete ownership, `ImpTeamService` implements `getTeamCoachId`, and focused authorization/team-service tests pass. Broader team authorization coverage remains under P0.5/P1.8.

## Definition of ready

- No deployable secret/default credential is present in source or image configuration.
- Production containers start from immutable images, run without debug/source mounts, and have health checks.
- Every protected endpoint has a documented role/ownership/team policy with negative tests.
- WebSocket origin and destination authorization are verified.
- PostgreSQL migrations, Redis behavior, frontend lint/build, backend tests, and image builds pass in CI.
- HTTPS, backups, observability retention, rollback, and secret rotation have been tested.
