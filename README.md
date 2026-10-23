# PsTracker

PsTracker is a full-stack ICPC training platform. Coaches manage teams, announcements, materials, and trainees; trainees can view Codeforces submissions, rating/contest data, analytics, notifications, and team chat.

## Main components

- `back/`: Spring Boot 4 API, PostgreSQL persistence, Redis queue/cache, JWT authentication, S3 presigned URLs, Codeforces synchronization, and STOMP/WebSocket features.
- `Front/icpc-tracker/`: React, TypeScript, Vite, and Tailwind frontend.
- `docker-compose.yml`: local infrastructure plus the application and observability services.

## Run locally

Requirements: Java 21, Node.js 20+, npm, Docker, and Docker Compose.

1. Copy `.env.example` to `.env` and replace the placeholders.
2. Start local infrastructure:

   ```bash
   docker compose up -d postgres redis
   ```

3. Start the backend from `back/` with the values from `.env` exported in your shell or IDE. For the Compose PostgreSQL mapping, use:

   ```text
   DB_URL=jdbc:postgresql://localhost:5433/pstracker
   SPRING_DATA_REDIS_HOST=localhost
   ```

   Then run:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Start the frontend:

   ```bash
   cd Front/icpc-tracker
   cp .env.example .env
   npm ci
   npm run dev
   ```

Open the frontend at [http://localhost:5173](http://localhost:5173). The API is available at [http://localhost:8080](http://localhost:8080), and Swagger is at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

The current Compose file still contains development/debug mounts and should not be treated as a production deployment. See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) before deploying it anywhere public.

## Useful checks

```bash
(cd back && ./mvnw clean test)
(cd Front/icpc-tracker && npm run build)
(cd Front/icpc-tracker && npm run lint)
```

The build and backend tests currently pass. Frontend lint currently reports known errors; the prioritized fix list is in [docs/TODO.md](docs/TODO.md).

## Documentation

- [Development guide](docs/DEVELOPMENT.md)
- [Current project review](docs/PROJECT_REVIEW.md)
- [Prioritized TODO and removal/update plan](docs/TODO.md)
- [Deployment and operations guide](docs/DEPLOYMENT.md)

## License

No project license has been selected yet.
