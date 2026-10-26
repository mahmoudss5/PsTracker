# PsTracker prioritized TODO

Last reviewed: 2026-10-26

This is the open implementation plan, ordered by risk and dependency. Completed work is listed separately so it is not mistaken for remaining work.

Deployment classifications:

- **Critical — cannot deploy without:** required for a safe production deployment.
- **Can be fixed after deployment:** important, but the application can be deployed with a planned follow-up.
- **Enhancement:** improves quality, maintainability, or future growth without blocking deployment.
- **Completed:** implemented locally; keep the stated verification step where one remains.

## P0 — security and deployment blockers

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P0.1 | Add an authorization matrix and enforce it explicitly with `@PreAuthorize` and/or a dedicated authorization service. | `back/src/main/java/.../Config/Security/SecurityConfiguration.java`, controllers, service authorization helpers | `.anyRequest().authenticated()` proves identity but not permission. Define what Admin, Coach, Trainee, team member, team coach, and resource owner may do, then enforce and test each policy. | **Critical — cannot deploy without** |
| P0.2 | Finish the authenticated-identity and resource-owner audit. Actor derivation is now used for several current-user operations, but broad target reads/writes still need explicit owner, role, or team policy. | User, Team, Submission, Report, Notification, Announcement, Contest, Problem, Tag, Material/Storage controllers and services | Review endpoints such as `/users/{id}`, `/contests/user/{userId}`, `/reports/user/{userId}`, `/submissions/user/{userId}`, and unrestricted problem/tag operations. Add cross-user and cross-team negative tests. | **Critical — cannot deploy without** |
| P0.3 | Restrict WebSocket origins to configured frontend origins and authorize `CONNECT`, `SUBSCRIBE`, and `SEND` destinations. | `WebSocketConfig`, `WebSocketAuthInterceptor`, message/presence controllers | JWT validation currently covers `CONNECT`, but origins and destination access are not fully restricted. Add tests for another-team subscriptions and sends. | **Critical — cannot deploy without** |
| P0.4 | Fix CI path and trigger configuration; add backend and frontend jobs, then add secret scanning. | `.github/workflows/PsTrackerCi.yml` | CI must run Maven from `back/`, use valid pull-request filters, and prove `./mvnw clean test`, `npm run build`, and `npm run lint` before merge. | **Critical — cannot deploy without** |
| P0.5 | Put the application behind HTTPS, publish only the frontend/reverse-proxy port, and protect Swagger and Actuator. | Reverse proxy/deployment files, `SecurityConfiguration`, application profiles | PostgreSQL, Redis, OTLP, Prometheus, Loki, Tempo, and Grafana are still directly published in the production Compose file, while Actuator and Swagger are public. Expose only the intended public endpoints. | **Critical — cannot deploy without** |

## P1 — backend correctness and reliability

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P1.1 | Complete Bean Validation coverage and add `@Valid` at every request DTO boundary. | `back/src/main/java/.../DataAccessLayer/Dto`, controllers | Validation now covers several auth, announcement, team, message, and user DTOs. Add the remaining constraints for team join, materials, storage metadata, notifications, reports, problems, submissions, tags, and sign-in fields; reject blank, negative, oversized, and malformed values before business logic. | **Can be fixed after deployment** |
| P1.2 | Standardize exceptions and error responses. Map authentication, authorization, validation, not-found, conflict, rate-limit, and upstream failures to stable 4xx/5xx codes. | `ExceptionHandling`, `GlobalExcHandler`, services, aspects | Several application, credential, response-status, rate-limit, and Redis errors are mapped already. Add validation/access-denied/data-integrity/not-found mappings and a request/correlation ID; do not let generic runtime failures decide the API contract. | **Can be fixed after deployment** |
| P1.3 | Reconcile the Flyway schema with the entities using new corrective migrations. Keep `ddl-auto: validate` and set `flyway.clean-disabled: true` outside tests. | `back/src/main/resources/dbMigration`, `application*.yml` | `V7` now adds important constraints and indexes, but the material history still contains legacy columns, the migration sequence has a version gap, and production still allows Flyway clean. Validate against PostgreSQL and never rewrite an applied migration. | **Critical — cannot deploy without** |
| P1.4 | Add Codeforces HTTP timeouts, bounded backoff, last-success/last-failure status, checkpoints/incremental fetching, and no-overlap controls. | `ImpCfService`, `CfWorker`, `CfQueue`, `AppConfig`/HTTP client config, persistence | The queue spaces worker jobs and retries remote failures, but the HTTP client has no explicit timeout and sync still fetches a fixed recent batch without visible status or overlap protection. Do not reintroduce `Thread.sleep()` into scheduled jobs. | **Can be fixed after deployment** |
| P1.5 | Replace Redis `KEYS` presence scans and unsafe cache default typing; define Redis persistence/auth/TLS policy. | `PresenceService`, `RedisConfig`, deployment config | `KEYS` can block Redis, and unsafe polymorphic deserialization is unnecessary risk. Presence and cache failure behavior should be tested at the expected deployment scale. | **Can be fixed after deployment** |
| P1.6 | Finish securing S3 presigned URL creation/use, validate file metadata and size, and clean up abandoned uploads. | `StorageController`, storage service, Material service, S3 bucket/IAM policy | Server-owned object keys and team-coach/member checks now exist. Remaining work is strict file-name/type/size validation, ensuring download URLs verify the material record, preventing unsafe key/path values, and cleaning up uploads that never become materials. | **Critical — cannot deploy without** |
| P1.7 | Add integration tests for PostgreSQL migrations, Redis queue/rate limit/presence, WebSocket membership, and authorization boundaries. | `back/src/test`, Testcontainers configuration | Unit and focused authorization tests pass locally, but the release-critical PostgreSQL, Redis, WebSocket, and cross-user/team behavior still needs end-to-end coverage. This also completes verification for the team-membership and database-constraint work. | **Can be fixed after deployment** |

## P1 — frontend readiness

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P1.8 | Enable a real protected route guard and derive authorization state from the authenticated session. | `Front/icpc-tracker/src/pages/RootLayout.tsx`, `router.tsx`, auth context/hooks | The route guard is still commented out, so dashboard routes can render without a valid session even though API calls later fail. | **Critical — cannot deploy without** |
| P1.9 | Reduce reliance on access tokens and user identity values in `localStorage`; use secure cookies/memory where possible and document the remaining learning-project tradeoff if not migrated yet. | `AuthService.tsx`, `AuthContext.tsx`, `config/api.tsx` | XSS can read localStorage tokens and client identity values. Backend authorization must remain authoritative, but client-side storage risk should be reduced. | **Can be fixed after deployment** |
| P1.10 | Remove debug logs and remaining placeholder values, and add consistent loading, empty, retry, and forbidden states. | `Front/icpc-tracker/src/**` | Production logs still contain authentication/presence/debug output, and some hooks retain placeholder comments. Users should see meaningful states when APIs fail. | **Can be fixed after deployment** |
| P1.11 | Split the largest frontend bundle with route/component lazy loading. | `router.tsx`, Vite config | The production JavaScript chunk is about 618 kB before gzip and Vite warns above 500 kB. | **Enhancement** |

## P1 — operations

| Priority | What to add/change | Where | Why / completion condition | Deployment classification |
|---|---|---|---|---|
| P1.12 | Add health checks and health-based startup dependencies for the backend, frontend, PostgreSQL, Redis, Collector, and observability services. | Compose files, Actuator config | PostgreSQL has a health check, but Redis, backend, frontend, and most observability services do not have complete health/readiness checks or health-based dependencies. | **Critical — cannot deploy without** |
| P1.13 | Persist observability data, configure retention, and add dashboards/alerts for availability, latency, errors, Redis/DB health, Codeforces sync, restarts, and disk. | `docker-compose.prod.yml`, `otel/`, Grafana provisioning | PostgreSQL and Redis volumes exist, but observability services have no durable data/retention policy or actionable deployment alerts. | **Can be fixed after deployment** |
| P1.14 | Expand and test the runbook for deploy, restart, logs, backups/restores, secret rotation, and rollback by image version. | `docs/DEPLOYMENT.md` and deployment system | The deployment guide exists, but its recovery procedures still need to be exercised and tied to the actual deployment environment. | **Can be fixed after deployment** |

## P2 — maintainability

| Priority | What to add/change | Where | Why | Deployment classification |
|---|---|---|---|---|
| P2.1 | Add generated/validated frontend API types from the backend OpenAPI contract. | CI, frontend types, Springdoc output | Prevent DTO drift between Java and TypeScript. | **Enhancement** |
| P2.2 | Add Java/TypeScript formatting and static-analysis rules. | Maven/CI and frontend config | Keep changes consistent and catch defects before review. | **Enhancement** |
| P2.3 | Rename spelling inconsistencies such as `TraingingTracker`, `Announcment`, `Presencs`, and `Secuiry` in a planned migration. | Packages/classes and migration-safe database mappings | Naming currently increases maintenance cost; do not rename database columns without a migration plan. | **Enhancement** |
| P2.4 | Decide whether `StressTesting.summary.json` belongs in version control; remove generated artifacts if they are local outputs. | Repository root and `.gitignore` | Generated results can become stale or expose environment details. | **Enhancement** |
| P2.5 | Add a license and contribution guide. | Root documentation | Make reuse and collaboration expectations explicit. | **Enhancement** |

## Completed locally or no longer valid as TODOs

- **P0.2 from the previous plan:** development and production Compose/Docker definitions are separated; production uses the executable JAR without source/Maven mounts or the debug port.
- **Team-membership enforcement:** `@IsTeamMember` now protects team details, user lists, materials, storage URLs, announcements, and messages; `IsTeamCoachAspect` protects coach-only storage operations. Full PostgreSQL/WebSocket integration verification remains in P1.7.
- **Authenticated actor derivation:** announcement sender, notification owner, report/submission owners, presence identity, and current-user operations no longer trust the caller for the acting user. The remaining target-resource audit is P0.3.
- **Database constraints and indexes:** `V7__add_constraints_and_indexes.sql` exists and adds ownership constraints, Codeforces uniqueness, and common query indexes. PostgreSQL migration verification remains in P1.7.
- **Frontend lint:** the previous 30 errors and 3 warnings were fixed; `npm run lint` passes. Route protection and runtime UX work remain open.
- STOMP/SockJS dependencies and live chat hooks are already present; do not recreate the old mock-chat task.
- Announcements and materials are already wired to backend hooks; the remaining work is authorization, validation, error handling, and verification.
- The Codeforces worker does not use `Thread.sleep()`; keep the controlled queue/scheduler model while adding timeouts and status reporting.

## Definition of ready

- No deployable secret/default credential is present in source or image configuration.
- Production containers start from immutable images, run without debug/source mounts, and have health checks.
- Every protected endpoint has a documented role/ownership/team policy with negative tests.
- WebSocket origin and destination authorization are verified.
- PostgreSQL migrations, Redis behavior, frontend lint/build, backend tests, and image builds pass in CI.
- HTTPS, backups, observability retention, rollback, and secret rotation have been tested.
