# Subscription Service

## Role
The **Subscription Service** manages user intents to receive weather notifications. It allows users to define specific rules (e.g., "notify me if temperature > 30°C") for monitored cities. It serves as the source of truth for all user subscription data.

## Implemented Functionality
- **Subscription Lifecycle**:
    - Creation and updating of subscriptions with multiple rules.
    - **Soft Delete**: Users can deactivate/soft-delete subscriptions (sets `isActive=false`).
    - **Hard Delete**: Permanent removal of subscription data from the database.
- **Rule Management**:
    - Support for various parameter types: `TEMPERATURE`, `HUMIDITY`, `RAIN`, `WIND_SPEED`.
    - Support for multiple operators: `GREATER_THAN`, `LESS_THAN`, `EQUALS`, `BETWEEN`.
- **Event-Driven Integration**:
    - Publishes events to SNS (`subscription_created`, `subscription_updated`, `subscription_deleted`) to synchronize data with the Decision Service.
- **City Management**:
    - Maintains a reference list of cities and tracks the number of active subscriptions per city.
- **Inter-service Communication**:
    - Integrates with IAM Service via Feign client to validate users.
- **Tech Stack**: Spring Boot, AWS SNS, JPA, PostgreSQL, Flyway.


## Deployment to AWS Cloud

Follow these steps to build, containerize, and deploy the service to the AWS environment.

### 1. Push image to ECR
Open `subscription-service` directory in terminal and run the following commands:
Build the Docker image.
If you use MacOS on Silicon chip, add --platform flag:
```shell
docker build --platform linux/amd64 -f docker/Dockerfile -t bachelor/subscription-service .
```

Tag the image for Amazon ECR repository:
```shell
docker tag bachelor/subscription-service:latest 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/subscription-service:latest
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
docker push 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/subscription-service:latest
```

### 2. Deploy to ECS

instructions to update the ECS service
```shell
aws ecs update-service --cluster bachelor-cluster --service bachelor-subscription-task-service --force-new-deployment --profile bachelor
```

```shell
aws logs tail /ecs/bachelor-subscription-task --follow --region eu-central-1 --profile bachelor
```

```shell
aws ecs update-service \
      --cluster bachelor-cluster \
      --service bachelor-subscription-task-service \
      --health-check-grace-period-seconds 180 \
      --profile bachelor
```

