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