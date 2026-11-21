#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT_DIR"

ENVIRONMENT="${1:-}"
case "$ENVIRONMENT" in
    dev)
        COMPOSE_FILE="docker-compose.dev.yml"
        ;;
    prod)
        COMPOSE_FILE="docker-compose.prod.yml"
        ;;
    *)
        echo "Usage: $0 {dev|prod}" >&2
        exit 1
        ;;
esac

echo "Deploying $ENVIRONMENT environment..."
echo "Using $COMPOSE_FILE"

docker compose -f "$COMPOSE_FILE" config --quiet


# Read S3 configuration
S3_CONFIG=$(docker compose -f "$COMPOSE_FILE" config --format json |
    jq -er '.services.backend.environment |
    [.AWS_BUCKET_NAME, .AWS_REGION] | @tsv')

IFS=$'\t' read -r AWS_BUCKET_NAME AWS_REGION <<< "$S3_CONFIG"

# Provision infrastructure
echo "Setting up infrastructure..."

AWS_BUCKET_NAME="$AWS_BUCKET_NAME" \
AWS_REGION="$AWS_REGION" \
bash back/Infra/aws/createS3Bucket.sh

# Deploy application
echo "Deploying application..."

docker compose -f "$COMPOSE_FILE" up -d --build

echo "Deployment completed successfully."
