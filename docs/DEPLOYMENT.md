# Deployment and operations guide

## Current status

The repository provides separate development and production Docker definitions. Use `docker-compose.prod.yml` for the production-style JAR/Nginx images and `docker-compose.dev.yml` for source-mounted local development.

The development stack intentionally includes:

- `backend` source and Maven cache mounts.
- The backend's `5005` remote-debug port.
- Vite and Spring Boot live development commands.

The production stack removes those development mounts and debug settings:

- the backend runs the executable JAR from `back/Dockerfile.prod`;
- the frontend is built into an Nginx image from `Front/icpc-tracker/Dockerfile.prod`;
- no source bind mounts, Maven cache mount, or remote-debug port are configured.

The local `.env` files are ignored; commit only the `.env.example` templates.

Remaining deployment blockers to resolve before a public release include:

- Database, Redis, and observability services are still published directly on host ports.
- WebSocket origins currently allow `*`.

## Target deployment shape

Use one of these explicit layouts:

1. `docker-compose.dev.yml`: source mounts, local ports, debug tools, and fast development feedback.
2. `docker-compose.prod.yml`: executable-JAR/Nginx images, no source or Maven mounts, no debug port, and deployment-secret checks.

The production backend should run the executable JAR from the runtime image. If OpenTelemetry is required, copy a reviewed agent into the image or mount it read-only through a deliberate deployment volume. Otherwise remove the `-javaagent` option from the production entrypoint.

## Required production controls

### Secrets

Inject these through the deployment secret mechanism and do not commit them:

- PostgreSQL URL, username, and password.
- A strong JWT signing secret.
- AWS access through an IAM role or least-privilege access key, region, and bucket.
- Exact frontend origins.
- Grafana credentials if Grafana is deployed.

`application.yml` and both Compose files now require the database password, JWT secret, AWS credentials/region/bucket, populated frontend origins, and Grafana password from environment or deployment secrets. If a credential may ever have been real, rotate it before deployment.

### Network

- Put the frontend/reverse proxy behind HTTPS.
- Publish only the reverse proxy port.
- Keep PostgreSQL, Redis, OpenTelemetry, Prometheus, Loki, Tempo, and Grafana on a private network.
- Restrict CORS and WebSocket origins to the real frontend origin.
- Protect or disable Swagger and non-health Actuator endpoints.

### Containers

- Run the application as a non-root user.
- Pin reviewed base images or immutable digests.
- Add image version/commit labels.
- Scan backend and frontend images for vulnerabilities in CI.
- Add health checks and resource limits.
- Use restart policies appropriate to the deployment.

### Database and storage

- Run Flyway migrations with `ddl-auto=validate`.
- Keep `flyway.clean-disabled=true` outside tests.
- Back up PostgreSQL and test a restore.
- Reconcile the material migration history before production; do not edit applied migrations.
- Configure S3 block-public-access, encryption, lifecycle rules, CORS, and least-privilege IAM.

### Operations

Monitor API availability, latency, error rate, database/Redis health, Codeforces queue depth and failures, container restarts, and disk usage. Persist observability data with explicit retention limits.

At minimum, the runbook should document:

1. deploy a versioned image;
2. check readiness and recent logs;
3. restart one service safely;
4. restore PostgreSQL from backup;
5. rotate JWT/AWS/database secrets;
6. roll back to the previous image version.

## Release gate

Before deployment, run:

```bash
(cd back && ./mvnw clean test)
(cd Front/icpc-tracker && npm run build)
(cd Front/icpc-tracker && npm run lint)
docker compose -f docker-compose.prod.yml config --quiet
```

Then build both images and run a smoke test for login, refresh, team membership, chat, materials, and Codeforces queue behavior. The release is not ready while frontend lint fails or while cross-user/team authorization has no negative tests.
