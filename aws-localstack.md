
# AWS LocalStack

Show the queues in the localstack SQS service:
```bash
docker exec -it aws-infra awslocal sqs list-queues --region eu-central-1
```

```json
{
    "QueueUrls": [
        "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/subscription-consume-queue",
        "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/weather-consume-queue",
        "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/decision-consume-queue",
        "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/notification-consume-queue"
    ]
}
```

Show the topics in the localstack SNS service:
```bash
docker exec -it aws-infra awslocal sns list-topics --region eu-central-1
```

```json
{
    "Topics": [
        {
            "TopicArn": "arn:aws:sns:eu-central-1:000000000000:iam-produce-topic"
        },
        {
            "TopicArn": "arn:aws:sns:eu-central-1:000000000000:subscription-produce-topic"
        }
    ]
}
```

## Subscription service infra

Sent message directly to the subscription-produce-topic topic:
```bash
docker exec -it aws-infra awslocal sqs send-message  \
  --queue-url http://localhost:4566/000000000000/subscription-consume-queue  \
  --message-body "Hello-from-SQS"  \
  --region eu-central-1
```

Read message from the subscription-consume-queue queue:
```bash
docker exec -it aws-infra awslocal sqs receive-message  \
  --queue-url http://localhost:4566/000000000000/subscription-consume-queue  \
  --max-number-of-messages 10 \
  --region eu-central-1
```


