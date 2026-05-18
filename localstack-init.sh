#!/bin/bash
echo "Initializing LocalStack Infrastructure..."

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=eu-central-1

# Helper function for awslocal
function awslocal() {
  aws --endpoint-url=http://localhost:4566 "$@"
}

# SNS Topics (Producers)
awslocal sns create-topic --name iam-produce-topic
awslocal sns create-topic --name subscription-produce-topic

# SQS Queues (Consumers)
awslocal sqs create-queue --queue-name subscription-consume-queue
awslocal sqs create-queue --queue-name weather-consume-queue
awslocal sqs create-queue --queue-name decision-consume-queue
awslocal sqs create-queue --queue-name notification-consume-queue

# SES Identity Verification (LocalStack simulation)
awslocal ses verify-email-identity --email-address noreply@bachelor-project.dmitry-cherkes.com

# Configure subscriptions SNS -> SQS (Fan-out)

## Configure IAM Topic

### send message to subscription-consume-queue when user delete account
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:subscription-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"user_delete\"]}"}'

### send message to decision-queue when user delete account
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:decision-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"user_delete\"]}"}'

##  Configure Subscription Topic

### send message to weather-queue when city.active_status change (ON_USE (1), NOT_USED (1))
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:weather-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"city_activate\", \"city_deactivate\"]}"}'

### send message to decision-queue when subscription created (1), updated (1), deleted (1)
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:decision-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"subscription_created\", \"subscription_updated\", \"subscription_deleted\"]}"}'

echo "Infrastructure initialized successfully."
