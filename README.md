# PsTracker

PsTracker is a full-stack ICPC training platform. Coaches manage teams, announcements, materials, and trainees; trainees can view Codeforces submissions, rating/contest data, analytics, notifications, and team chat.

## Main components

- `back/`: Spring Boot 4 API, PostgreSQL persistence, Redis queue/cache, JWT authentication, S3 presigned URLs, Codeforces synchronization, and STOMP/WebSocket features.
- `Front/icpc-tracker/`: React, TypeScript, Vite, and Tailwind frontend.
- `docker-compose.prod.yml`: production images and runtime services.
- `docker-compose.dev.yml`: local development services with source mounts and debug support.

## Run locally

Requirements: Java 21, Node.js 20+, npm, Docker, and Docker Compose.

1. Copy `.env.example` to `.env` and replace the placeholders.
2. Start the development stack:

   ```bash
   docker compose -f docker-compose.dev.yml up --build
   ```

   The development Compose file reads values from the root `.env` automatically and provides safe local fallbacks when a value is absent.

3. To run the production-style images locally, use:

   ```bash
   docker compose -f docker-compose.prod.yml up --build
   ```

   The production Compose file requires deployment secrets such as `DB_PASSWORD`, `JWT_SECRET`, and AWS credentials.

4. To run the backend directly from `back/` with the values from `.env`, use:

   ```text
   DB_URL=jdbc:postgresql://localhost:5433/pstracker
   SPRING_DATA_REDIS_HOST=localhost
   ```

   Then run:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Start the frontend outside Docker:

   ```bash
   cd Front/icpc-tracker
   cp .env.example .env
   npm ci
   npm run dev
   ```

Open the frontend at [http://localhost:5173](http://localhost:5173). The API is available at [http://localhost:8080](http://localhost:8080), and Swagger is at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

The development and production Compose files are intentionally separate. See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) before deploying the production stack anywhere public.

## Useful checks

```bash
(cd back && ./mvnw clean test)
(cd Front/icpc-tracker && npm run build)
(cd Front/icpc-tracker && npm run lint)
```

The frontend build and lint checks currently pass. The prioritized remaining project work is in [docs/TODO.md](docs/TODO.md).

## Documentation

- [Development guide](docs/DEVELOPMENT.md)
- [Current project review](docs/PROJECT_REVIEW.md)
- [Prioritized TODO and removal/update plan](docs/TODO.md)
- [Deployment and operations guide](docs/DEPLOYMENT.md)

## License

No project license has been selected yet.
