#!/usr/bin/env bash

set -euo pipefail

EXPECTED_ACCOUNT_ID="478644803547"
ROLE_NAME="PsTracker-EC2-Role"
INSTANCE_PROFILE_NAME="PsTracker-EC2-InstanceProfile"

ECR_PULL_POLICY="arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryPullOnly"
SSM_POLICY="arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"

CURRENT_ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"

if [[ "$CURRENT_ACCOUNT_ID" != "$EXPECTED_ACCOUNT_ID" ]]; then
    echo "Wrong AWS account: $CURRENT_ACCOUNT_ID" >&2
    exit 1
fi

TRUST_POLICY_FILE="$(mktemp)"
trap 'rm -f "$TRUST_POLICY_FILE"' EXIT

cat > "$TRUST_POLICY_FILE" <<'JSON'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
JSON

if aws iam get-role \
    --role-name "$ROLE_NAME" \
    >/dev/null 2>&1; then

    echo "IAM role already exists: $ROLE_NAME"
else
    echo "Creating IAM role: $ROLE_NAME"

    aws iam create-role \
        --role-name "$ROLE_NAME" \
        --assume-role-policy-document "file://$TRUST_POLICY_FILE"
fi

echo "Attaching ECR pull policy..."

aws iam attach-role-policy \
    --role-name "$ROLE_NAME" \
    --policy-arn "$ECR_PULL_POLICY"

echo "Attaching SSM policy..."

aws iam attach-role-policy \
    --role-name "$ROLE_NAME" \
    --policy-arn "$SSM_POLICY"

if aws iam get-instance-profile \
    --instance-profile-name "$INSTANCE_PROFILE_NAME" \
    >/dev/null 2>&1; then

    echo "Instance profile already exists: $INSTANCE_PROFILE_NAME"
else
    echo "Creating instance profile: $INSTANCE_PROFILE_NAME"

    aws iam create-instance-profile \
        --instance-profile-name "$INSTANCE_PROFILE_NAME"
fi

ATTACHED_ROLES="$(
    aws iam get-instance-profile \
        --instance-profile-name "$INSTANCE_PROFILE_NAME" \
        --query 'InstanceProfile.Roles[].RoleName' \
        --output text
)"

if [[ " $ATTACHED_ROLES " == *" $ROLE_NAME "* ]]; then
    echo "Role is already attached to the instance profile"
else
    echo "Attaching role to instance profile..."

    aws iam add-role-to-instance-profile \
        --instance-profile-name "$INSTANCE_PROFILE_NAME" \
        --role-name "$ROLE_NAME"
fi

echo "EC2 IAM role setup completed successfully."