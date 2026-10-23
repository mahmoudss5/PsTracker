# Development guide

## Requirements

- Java 21
- Node.js 20 or newer and npm
- Docker and Docker Compose
- A PostgreSQL database, Redis, and valid AWS S3 settings when testing storage features

## Environment

Copy the root example:

```bash
cp .env.example .env
```

Copy the frontend example for Vite:

```bash
cp Front/icpc-tracker/.env.example Front/icpc-tracker/.env
```

The frontend variables are build-time/browser configuration only:

- `VITE_API_URL`, normally `http://localhost:8080/api`
- `VITE_WS_URL`, normally `http://localhost:8080/ws`

Do not put database, JWT, AWS, or other backend secrets in the frontend environment file.

## Recommended local workflow

Start only local infrastructure first:

```bash
docker compose up -d postgres redis
```

The Compose host ports are PostgreSQL `5433` and Redis `6379`. When running the backend outside Docker, set `DB_URL` to `jdbc:postgresql://localhost:5433/pstracker` and keep Redis pointed at `localhost`.

Start the backend:

```bash
cd back
./mvnw spring-boot:run
```

Start the frontend in another terminal:

```bash
cd Front/icpc-tracker
npm ci
npm run dev
```

Use [http://localhost:5173](http://localhost:5173). The backend base URL is [http://localhost:8080/api](http://localhost:8080/api), Swagger is [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html), and the STOMP endpoint is `http://localhost:8080/ws`.

## Checks

Backend:

```bash
cd back
./mvnw clean test
```

Frontend:

```bash
cd Front/icpc-tracker
npm run build
npm run lint
```

Compose syntax:

```bash
docker compose config --quiet
```

The backend test profile uses H2 and creates/drops its test schema. It does not replace PostgreSQL migration testing. The application test may log Redis connection failures when Redis is not running because scheduled workers start with the application.

## Using the application

1. Register as a coach or trainee.
2. Log in to receive the access token and refresh cookie.
3. A coach creates a team and shares its join code.
4. A trainee joins with the code.
5. Team members can use chat, announcements, materials, and presence; coaches can publish team content according to the backend authorization policy.
6. Add a Codeforces handle to use submission and contest synchronization.

The backend is the source of truth for identity and permissions. The frontend may store convenience data for display, but it must not be trusted for authorization.

## Repository map

- `back/src/main/java/.../Controllers`: HTTP and WebSocket entry points.
- `back/src/main/java/.../BusinessLogic`: services, Codeforces queue/worker, and domain behavior.
- `back/src/main/java/.../DataAccessLayer`: entities, DTOs, repositories, and migrations.
- `back/src/main/java/.../Security`: JWT, WebSocket authentication, rate limiting, and AOP policies.
- `Front/icpc-tracker/src/pages`: routed screens.
- `Front/icpc-tracker/src/hooks`: API, query, and WebSocket state.
- `Front/icpc-tracker/src/services`: HTTP and authentication clients.
- `otel/`: local OpenTelemetry, Prometheus, Loki, Tempo, and Grafana configuration.

## Docker note

The current root Compose file combines local development mounts with the JAR image and observability services. It is useful as a configuration reference, but production deployment needs the split and fixes described in [DEPLOYMENT.md](DEPLOYMENT.md).
