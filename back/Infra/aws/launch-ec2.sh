#!/usr/bin/env bash

set -euo pipefail

EXPECTED_ACCOUNT_ID="${EXPECTED_ACCOUNT_ID:-478644803547}"
AWS_REGION="${AWS_REGION:-eu-central-1}"

ROLE_NAME="${ROLE_NAME:-PsTracker-EC2-Role}"
INSTANCE_NAME="${INSTANCE_NAME:-PsTracker-Prod}"
INSTANCE_TYPE="${INSTANCE_TYPE:-t3.medium}"
ROOT_VOLUME_SIZE="${ROOT_VOLUME_SIZE:-30}"
SECURITY_GROUP_NAME="${SECURITY_GROUP_NAME:-pstracker-prod-sg}"

AMI_PARAMETER="resolve:ssm:/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"

require_command() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "Required command not found: $1" >&2
        exit 1
    fi
}

require_command aws

CURRENT_ACCOUNT_ID="$(
    aws sts get-caller-identity \
        --query Account \
        --output text
)"

if [[ "$CURRENT_ACCOUNT_ID" != "$EXPECTED_ACCOUNT_ID" ]]; then
    echo "Wrong AWS account: $CURRENT_ACCOUNT_ID" >&2
    echo "Expected AWS account: $EXPECTED_ACCOUNT_ID" >&2
    exit 1
fi

echo "Using AWS account: $CURRENT_ACCOUNT_ID"
echo "Using AWS region: $AWS_REGION"

EXISTING_INSTANCE_ID="$(
    aws ec2 describe-instances \
        --region "$AWS_REGION" \
        --filters \
            "Name=tag:Name,Values=$INSTANCE_NAME" \
            "Name=instance-state-name,Values=pending,running,stopping,stopped" \
        --query 'Reservations[].Instances[].InstanceId | [0]' \
        --output text
)"

if [[ -n "$EXISTING_INSTANCE_ID" && "$EXISTING_INSTANCE_ID" != "None" ]]; then
    echo "An EC2 instance named $INSTANCE_NAME already exists."

    aws ec2 describe-instances \
        --region "$AWS_REGION" \
        --instance-ids "$EXISTING_INSTANCE_ID" \
        --query 'Reservations[0].Instances[0].{InstanceId:InstanceId,State:State.Name,PublicIp:PublicIpAddress,PrivateIp:PrivateIpAddress,InstanceType:InstanceType}' \
        --output table

    exit 0
fi

INSTANCE_PROFILE_NAME="$(
    aws iam list-instance-profiles-for-role \
        --role-name "$ROLE_NAME" \
        --query 'InstanceProfiles[0].InstanceProfileName' \
        --output text
)"

if [[ -z "$INSTANCE_PROFILE_NAME" || "$INSTANCE_PROFILE_NAME" == "None" ]]; then
    echo "No instance profile is attached to IAM role: $ROLE_NAME" >&2
    exit 1
fi

echo "Using IAM instance profile: $INSTANCE_PROFILE_NAME"

VPC_ID="$(
    aws ec2 describe-vpcs \
        --region "$AWS_REGION" \
        --filters 'Name=is-default,Values=true' \
        --query 'Vpcs[0].VpcId' \
        --output text
)"

if [[ -z "$VPC_ID" || "$VPC_ID" == "None" ]]; then
    echo "No default VPC was found in region $AWS_REGION" >&2
    exit 1
fi

echo "Using default VPC: $VPC_ID"

SUBNET_ID="$(
    aws ec2 describe-subnets \
        --region "$AWS_REGION" \
        --filters \
            "Name=vpc-id,Values=$VPC_ID" \
            'Name=map-public-ip-on-launch,Values=true' \
        --query 'Subnets[0].SubnetId' \
        --output text
)"

if [[ -z "$SUBNET_ID" || "$SUBNET_ID" == "None" ]]; then
    echo "No public subnet was found in default VPC $VPC_ID" >&2
    exit 1
fi

echo "Using public subnet: $SUBNET_ID"

SECURITY_GROUP_ID="$(
    aws ec2 describe-security-groups \
        --region "$AWS_REGION" \
        --filters \
            "Name=group-name,Values=$SECURITY_GROUP_NAME" \
            "Name=vpc-id,Values=$VPC_ID" \
        --query 'SecurityGroups[0].GroupId' \
        --output text
)"

if [[ -z "$SECURITY_GROUP_ID" || "$SECURITY_GROUP_ID" == "None" ]]; then
    echo "Creating security group: $SECURITY_GROUP_NAME"

    SECURITY_GROUP_ID="$(
        aws ec2 create-security-group \
            --region "$AWS_REGION" \
            --group-name "$SECURITY_GROUP_NAME" \
            --description 'Public web access for PsTracker production' \
            --vpc-id "$VPC_ID" \
            --query GroupId \
            --output text
    )"

    aws ec2 authorize-security-group-ingress \
        --region "$AWS_REGION" \
        --group-id "$SECURITY_GROUP_ID" \
        --ip-permissions \
            'IpProtocol=tcp,FromPort=80,ToPort=80,IpRanges=[{CidrIp=0.0.0.0/0,Description="Public HTTP"}]' \
            'IpProtocol=tcp,FromPort=443,ToPort=443,IpRanges=[{CidrIp=0.0.0.0/0,Description="Public HTTPS"}]' \
        >/dev/null
else
    echo "Using existing security group: $SECURITY_GROUP_NAME"
fi

echo "Using security group ID: $SECURITY_GROUP_ID"
echo "Launching EC2 instance: $INSTANCE_NAME"

INSTANCE_ID="$(
    aws ec2 run-instances \
        --region "$AWS_REGION" \
        --count 1 \
        --image-id "$AMI_PARAMETER" \
        --instance-type "$INSTANCE_TYPE" \
        --iam-instance-profile "Name=$INSTANCE_PROFILE_NAME" \
        --network-interfaces "DeviceIndex=0,AssociatePublicIpAddress=true,SubnetId=$SUBNET_ID,Groups=$SECURITY_GROUP_ID" \
        --block-device-mappings "DeviceName=/dev/xvda,Ebs={VolumeSize=$ROOT_VOLUME_SIZE,VolumeType=gp3,DeleteOnTermination=true,Encrypted=true}" \
        --metadata-options 'HttpEndpoint=enabled,HttpTokens=required' \
        --tag-specifications \
            "ResourceType=instance,Tags=[{Key=Name,Value=$INSTANCE_NAME},{Key=Environment,Value=production},{Key=Project,Value=PsTracker}]" \
            "ResourceType=volume,Tags=[{Key=Name,Value=$INSTANCE_NAME-root},{Key=Environment,Value=production},{Key=Project,Value=PsTracker}]" \
        --query 'Instances[0].InstanceId' \
        --output text
)"

echo "Created EC2 instance: $INSTANCE_ID"
echo "Waiting for the instance to enter the running state..."

aws ec2 wait instance-running \
    --region "$AWS_REGION" \
    --instance-ids "$INSTANCE_ID"

aws ec2 describe-instances \
    --region "$AWS_REGION" \
    --instance-ids "$INSTANCE_ID" \
    --query 'Reservations[0].Instances[0].{InstanceId:InstanceId,State:State.Name,PublicIp:PublicIpAddress,PrivateIp:PrivateIpAddress,InstanceType:InstanceType,AvailabilityZone:Placement.AvailabilityZone}' \
    --output table

echo "EC2 launch completed successfully."
echo "Wait for both EC2 status checks to pass before connecting with Session Manager."
