
# AWS LocalStack

Show the queues in the localstack SQS service:
```bash
docker exec -it aws-infra awslocal sqs list-queues --region eu-central-1
```

Show the topics in the localstack SNS service:
```bash
docker exec -it aws-infra awslocal sns list-topics --region eu-central-1
```
