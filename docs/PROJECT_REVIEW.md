# PsTracker project review

Review date: 2026-10-23

This document records the current state found in the repository. It is intentionally separate from the implementation backlog in [TODO.md](TODO.md).

## Verified state

| Check | Result | Notes |
|---|---|---|
| `docker compose -f docker-compose.prod.yml config --quiet` | Pass | Production Compose syntax resolves successfully. The development file also resolves without `.env` because it has local fallbacks. |
| `cd back && ./mvnw clean test` | Pass | The test reports contain no failures or errors after a clean build. The application test logs Redis connection errors because no local Redis was running, but the Maven command still passed. |
| `cd Front/icpc-tracker && npm run build` | Pass | TypeScript compilation and Vite production build pass. Vite reports a JavaScript chunk larger than 500 kB. |
| `cd Front/icpc-tracker && npm run lint` | Pass | The previous 30 errors and 3 warnings were fixed. |

## What is already present

- The production backend Dockerfile uses a Maven build stage and a non-root JRE runtime stage that runs an executable JAR; the development Dockerfile retains `mvn spring-boot:run` and remote debugging.
- Codeforces calls are routed through `CfQueue` and `CfWorker`; the worker processes one job every two seconds and has retry handling for common remote failures.
- Frontend chat uses STOMP over SockJS through `useTeamChat`; it is not the old hardcoded mock implementation.
- Announcements and materials use API hooks and live backend endpoints.
- The backend derives several actor IDs from the authenticated principal, including announcement sender, notification owner, report owner, submission owner, and WebSocket presence identity.
- The frontend package already includes `@stomp/stompjs` and `sockjs-client`.

## Findings that affect correctness or deployment

### 1. Production and development Docker definitions are now separated

`docker-compose.prod.yml` uses `back/Dockerfile.prod` and no longer bind-mounts `./back:/app`, mounts Maven cache data, or publishes remote-debug port `5005`. `docker-compose.dev.yml` uses `back/Dockerfile.dev` with those development features intentionally enabled.

The production backend omits the OpenTelemetry Java agent because the repository does not supply a reviewed agent binary. Add one deliberately if production telemetry requires it.

### 2. Security is authenticated-by-default, not resource-authorized

`SecurityConfiguration` ends with `.anyRequest().authenticated()`, while many controller methods have no explicit role or ownership policy. Coach-only annotations exist on some endpoints, but team membership and resource ownership are not consistently enforced.

The remaining review should cover user, team, submission, report, notification, material/storage, problem, tag, announcement, and contest endpoints. IDs such as `teamId`, `userId`, `problemId`, and resource IDs are sometimes legitimate target identifiers, but every use must be checked against the authenticated user’s role and team relationship.

### 3. WebSocket authentication is present, but destination authorization is incomplete

The STOMP `CONNECT` frame validates the JWT, and message sending checks team membership in the service. However, WebSocket origins currently allow `*`, and there is no clear authorization check for every `SUBSCRIBE` destination. A user may therefore be able to subscribe to a topic that they should not read unless this is explicitly controlled.

### 4. Configuration contains deployment-dangerous fallbacks

`back/src/main/resources/application.yml` contains fallback values for the database password, JWT secret, and AWS credentials. They may be dummy values, but fallback credentials are still unsafe because a deployment can silently start with them. Production configuration should fail fast when required values are missing.

The root `.env` is ignored and is not tracked; do not copy its values into documentation or source files.

### 5. CI does not match the repository layout

`.github/workflows/PsTrackerCi.yml` runs Maven from the repository root even though `back/pom.xml` is the backend project file. Its `pull_request` syntax is also not in the standard GitHub Actions branch-filter shape. The workflow needs separate backend/frontend jobs and should validate the same commands documented in the repository.

### 6. Frontend lint is now clean

The previous 30 errors and 3 warnings were fixed by removing unsafe `any`, moving REST fetching to React Query, correcting table sorting, and fixing hook dependencies. Remaining frontend work is now separate from lint correctness.

### 7. Database migration history needs reconciliation

The migrations contain `V1`, `V2`, `V3`, `V4`, and `V6`. `V3` creates old material columns (`kind`, `size`, `url`), while `V6` renames only two of them and adds `s3_key`; the entity now uses `file_type`, `file_size`, and `s3_key`. The schema should be verified against a real PostgreSQL database and corrected with a new migration rather than editing applied migration files.

`flyway.clean-disabled: false` is appropriate only for a deliberate local/test setup, not a deployment.

### 8. Background integrations need operational safeguards

The Codeforces queue is a useful improvement and no `Thread.sleep()` is used in the reviewed worker. The next work is to add HTTP connect/read timeouts, visible sync status, checkpoint/incremental fetching, and protection against repeated overlapping jobs. Presence currently uses Redis `KEYS`, which should be replaced with a data structure or `SCAN` approach before running at larger scale.

## Review corrections to older notes

The deleted root `updated_project_review.md` contained claims that are no longer true: STOMP dependencies and live chat are present, and announcements/materials are connected to hooks. The backlog was rewritten to avoid asking for work that is already implemented.
