# Decision Service

## Role
The **Decision Service** is the "brain" of the system. It processes incoming weather updates against user subscriptions to determine if any notification criteria are met. It maintains its own local replica of data to perform these checks efficiently without cross-service calls.

## Implemented Functionality
- **Data Synchronization (Event Consumer)**:
    - **Subscription Sync**: Listens for SNS events from Subscription Service to maintain a local replica of active subscriptions and rules. Performs **Hard Delete** on its replica regardless of whether the source deletion was soft or hard.
    - **Weather Sync**: Listens for events from Weather Service to keep local weather forecasts up to date (via Upsert logic).
- **Decision Engine**:
    - Scheduled process that iterates through cities and evaluates rules.
    - Matches forecast data against subscription rules (`GREATER_THAN`, `LESS_THAN`, etc.).
- **Alert Management**:
    - Maintains `alert_history` to prevent duplicate notifications for the same rule and forecast time (anti-spam logic).
    - Triggers notification events when rules are satisfied.
- **Tech Stack**: Spring Boot, AWS SQS, JPA, PostgreSQL, Flyway.

## Deployment to AWS Cloud

Follow these steps to build, containerize, and deploy the service to the AWS environment.

### 1. Push image to ECR
Open `decision-service` directory in terminal and run the following commands:
Build the Docker image.
If you use MacOS on Silicon chip, add --platform flag:
```shell
docker build --platform linux/amd64 -f docker/Dockerfile -t bachelor/decision-service .
```

Tag the image for Amazon ECR repository:
```shell
docker tag bachelor/decision-service:latest 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/decision-service:latest
```

Authenticate into AWS ECR
```shell
export AWS_PROFILE=your-aws-profile-name # optional, if you have multiple AWS profiles configured
```

```shell
aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 631124976834.dkr.ecr.eu-central-1.amazonaws.com
```

Push the image to ECR
```shell
docker push 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/decision-service:latest
```

### 2. Deploy to ECS

instructions to update the ECS service
```shell
aws ecs update-service --cluster bachelor-cluster --service bachelor-decision-task-service --force-new-deployment --profile bachelor
```

```shell
aws logs tail /ecs/bachelor-decision-task --follow --region eu-central-1 --profile bachelor
```

Optional (not recommended)
```shell
aws ecs update-service \
      --cluster bachelor-cluster \
      --service bachelor-decision-task-service \
      --health-check-grace-period-seconds 180 \
      --profile bachelor
```
