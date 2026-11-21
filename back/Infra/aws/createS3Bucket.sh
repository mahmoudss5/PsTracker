#!/usr/bin/env bash

set -euo pipefail


BUCKET_NAME="${AWS_BUCKET_NAME:?Bucket name is required}"
REGION="${AWS_REGION:?AWS region is required}"
ACCOUNT_ID=$(aws sts get-caller-identity \
    --query Account \
    --output text)

echo "Checking bucket: $BUCKET_NAME"

if result=$(aws s3api head-bucket \
    --bucket "$BUCKET_NAME" \
    --region "$REGION" \
    --expected-bucket-owner "$ACCOUNT_ID" \
    --query BucketRegion \
    --output text 2>&1); then

    if [[ "$result" != "$REGION" ]]; then
         echo "backet region is $result"
         echo "your region is $REGION"
        echo "Bucket exists in a different region." >&2
        exit 1
    fi

    echo "Bucket already exists."

elif [[ "$result" == *"(404)"* ]]; then

    echo "Creating bucket..."

    aws s3 mb "s3://$BUCKET_NAME" \
        --region "$REGION"

    echo "Bucket created successfully."
    echo "Bucket  region is $REGION"

else
    echo "$result" >&2
    exit 1
fi