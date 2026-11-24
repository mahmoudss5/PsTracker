#!/usr/bin/env bash

set -euo pipefail

APP_DIR="/opt/pstracker"
ENV_FILE="$APP_DIR/.env"
COMPOSE_FILE="$APP_DIR/docker-compose.prod.yml"

AWS_REGION="eu-central-1"
ECR_REGISTRY="478644803547.dkr.ecr.eu-central-1.amazonaws.com"

IMAGE_TAG="${1:?Usage: deploy-prod.sh <image-tag>}"
export IMAGE_TAG

cd "$APP_DIR"

aws ecr get-login-password --region "$AWS_REGION" \
  | docker login \
      --username AWS \
      --password-stdin \
      "$ECR_REGISTRY"

docker compose \
  --env-file "$ENV_FILE" \
  -f "$COMPOSE_FILE" \
  config --quiet

docker compose \
  --env-file "$ENV_FILE" \
  -f "$COMPOSE_FILE" \
  pull

docker compose \
  --env-file "$ENV_FILE" \
  -f "$COMPOSE_FILE" \
  up -d --remove-orphans

docker compose \
  --env-file "$ENV_FILE" \
  -f "$COMPOSE_FILE" \
  ps

echo "Waiting for backend health..."

for attempt in {1..40}; do
  if curl --fail --silent --max-time 5 \
    http://127.0.0.1:8080/actuator/health >/dev/null; then

    docker compose \
      --env-file "$ENV_FILE" \
      -f "$COMPOSE_FILE" \
      exec -T frontend nginx -s reload

    curl --fail --silent --show-error \
      --max-time 10 \
      --retry 5 --retry-delay 2 --retry-connrefused \
      http://127.0.0.1/ \
      | grep -F 'id="root"' >/dev/null

    status=$(curl --silent --show-error \
      --max-time 10 \
      --retry 5 --retry-delay 2 --retry-connrefused \
      --output /dev/null \
      --write-out '%{http_code}' \
      http://127.0.0.1/api/auth/refresh)

    if [[ "$status" != "400" ]]; then
      echo "API proxy check failed: expected 400, got $status" >&2
      exit 1
    fi

    echo "Deployment checks passed."
    exit 0
  fi

  sleep 3
done

echo "Backend did not become healthy." >&2
exit 1
