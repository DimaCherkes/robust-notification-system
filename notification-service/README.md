
## Deployment to AWS Cloud

Follow these steps to build, containerize, and deploy the service to the AWS environment.

### 1. Push image to ECR
Open `notification-service` directory in terminal and run the following commands:
Build the Docker image.
If you use MacOS on Silicon chip, add --platform flag:
```shell
docker build --platform linux/amd64 -f docker/Dockerfile -t bachelor/notification-service .
```

Tag the image for Amazon ECR repository:
```shell
docker tag bachelor/notification-service:latest 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/notification-service:latest
```

Authenticate into AWS ECR
```shell
export AWS_PROFILE=bachelor # optional, if you have multiple AWS profiles configured
```

```shell
aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 631124976834.dkr.ecr.eu-central-1.amazonaws.com
```

Push the image to ECR
```shell
docker push 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/notification-service:latest
```

### 2. Deploy to ECS

instructions to update the ECS service
```shell
aws ecs update-service --cluster bachelor-cluster --service bachelor-notification-task-service --force-new-deployment --profile bachelor
```

```shell
aws logs tail /ecs/bachelor-notification-task --follow --region eu-central-1 --profile bachelor
```

```shell
aws ecs update-service \
      --cluster bachelor-cluster \
      --service bachelor-notification-task-service \
      --health-check-grace-period-seconds 180 \
      --profile bachelor
```

