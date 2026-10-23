# PsTracker project review

Review date: 2026-10-23

This document records the current state found in the repository. It is intentionally separate from the implementation backlog in [TODO.md](TODO.md).

## Verified state

| Check | Result | Notes |
|---|---|---|
| `docker compose config --quiet` | Pass | Compose syntax resolves successfully. |
| `cd back && ./mvnw clean test` | Pass | The test reports contain no failures or errors after a clean build. The application test logs Redis connection errors because no local Redis was running, but the Maven command still passed. |
| `cd Front/icpc-tracker && npm run build` | Pass | TypeScript compilation and Vite production build pass. Vite reports a JavaScript chunk larger than 500 kB. |
| `cd Front/icpc-tracker && npm run lint` | Fails | 30 errors and 3 warnings remain. |

## What is already present

- The backend Dockerfile already uses a Maven build stage and a JRE runtime stage that runs an executable JAR. The old “replace `mvn spring-boot:run`” task is no longer an accurate TODO.
- Codeforces calls are routed through `CfQueue` and `CfWorker`; the worker processes one job every two seconds and has retry handling for common remote failures.
- Frontend chat uses STOMP over SockJS through `useTeamChat`; it is not the old hardcoded mock implementation.
- Announcements and materials use API hooks and live backend endpoints.
- The backend derives several actor IDs from the authenticated principal, including announcement sender, notification owner, report owner, submission owner, and WebSocket presence identity.
- The frontend package already includes `@stomp/stompjs` and `sockjs-client`.

## Findings that affect correctness or deployment

### 1. The current Compose application container is inconsistent with the JAR image

`back/Dockerfile` copies `app.jar` into `/app`, but the `backend` service bind-mounts `./back:/app`, which hides the image contents at runtime. The same service exposes remote debug port `5005`. The Dockerfile also references `/otel/opentelemetry-javaagent.jar`, while the current image/Compose configuration does not provide that file.

This means the production-style image path must be fixed before relying on `docker compose up` for a deployment. The exact changes are in the P0 section of [TODO.md](TODO.md).

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

### 6. Frontend lint is the largest immediate quality signal

The failing lint output is concentrated in:

- state updates called directly from effects in data hooks and UI components;
- `any` usage in dashboard, API, presence, and team components;
- sorting/table logic;
- the commented route guard and fast-refresh export rules;
- three hook dependency warnings.

These are not all production blockers individually, but fixing them makes future security and API changes easier to verify.

### 7. Database migration history needs reconciliation

The migrations contain `V1`, `V2`, `V3`, `V4`, and `V6`. `V3` creates old material columns (`kind`, `size`, `url`), while `V6` renames only two of them and adds `s3_key`; the entity now uses `file_type`, `file_size`, and `s3_key`. The schema should be verified against a real PostgreSQL database and corrected with a new migration rather than editing applied migration files.

`flyway.clean-disabled: false` is appropriate only for a deliberate local/test setup, not a deployment.

### 8. Background integrations need operational safeguards

The Codeforces queue is a useful improvement and no `Thread.sleep()` is used in the reviewed worker. The next work is to add HTTP connect/read timeouts, visible sync status, checkpoint/incremental fetching, and protection against repeated overlapping jobs. Presence currently uses Redis `KEYS`, which should be replaced with a data structure or `SCAN` approach before running at larger scale.

## Review corrections to older notes

The deleted root `updated_project_review.md` contained claims that are no longer true: STOMP dependencies and live chat are present, and announcements/materials are connected to hooks. The backlog was rewritten to avoid asking for work that is already implemented.
