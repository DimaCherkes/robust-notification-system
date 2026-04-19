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
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:subscription-consume-queue

awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:notification-consume-queue

awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:iam-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:decision-consume-queue

# 5. Configure Subscription Topic
awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:weather-consume-queue

awslocal sns subscribe \
    --topic-arn arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:eu-central-1:000000000000:decision-consume-queue

echo "Infrastructure initialized successfully."
