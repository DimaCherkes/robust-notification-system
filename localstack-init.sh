#!/bin/bash
echo "Initializing LocalStack Infrastructure..."

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=eu-central-1

# Helper function for awslocal
function awslocal() {
  aws --endpoint-url=http://localhost:4566 "$@"
}

# 1. SNS Topics (Producers)
# Заменили точки на дефисы
awslocal sns create-topic --name iam-produce-topic
awslocal sns create-topic --name subscription-produce-topic

# 2. SQS Queues (Consumers)
# Заменили точки на дефисы
awslocal sqs create-queue --queue-name subscription-consume-queue
awslocal sqs create-queue --queue-name weather-consume-queue
awslocal sqs create-queue --queue-name decision-consume-queue
awslocal sqs create-queue --queue-name notification-consume-queue

# 3. Configure subscriptions SNS -> SQS (Fan-out)

# 4. Configure IAM Topic

# send message to subscription-queue when user delete account
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:subscription-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"user_delete\"]}"}'

# send message to notification-queue when user create, update, delete account
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:notification-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"user_create\", \"user_update\", \"user_delete\"]}"}'

# send message to decision-queue when user delete account
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:decision-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"user_delete\"]}"}'

# 5. Configure Subscription Topic

# send message to weather-queue when city.active_status change (ON_USE, NOT_USED)
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:weather-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"city_activate\", \"city_deactivate\"]}"}'

# send message to decision-queue when subscription created (1), updated (0), deleted (0)
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:decision-consume-queue \
    --attributes '{"FilterPolicy": "{\"action\": [\"subscription_created\", \"subscription_updated\", \"subscription_deleted\"]}"}'

echo "Infrastructure initialized successfully."
