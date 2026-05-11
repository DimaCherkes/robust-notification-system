# IAM Service

## Role
The **IAM (Identity and Access Management) Service** is responsible for user authentication, authorization, and profile management across the system. It acts as the central authority for security, issuing JWT tokens and managing user roles.

## Implemented Functionality
- **Authentication**: 
    - User registration and login.
    - JWT-based authentication (Access and Refresh tokens).
    - Password encryption and secure storage.
- **User Management**:
    - CRUD operations for user profiles.
    - Role-based access control (RBAC).
    - Retrieval of user details for other microservices (e.g., used by Subscription Service).
- **Security**:
    - Centralized security configurations.
    - Global exception handling for authentication and authorization errors.
- **Tech Stack**: Spring Boot, Spring Security, JPA, PostgreSQL, Flyway.


## Deployment to AWS Cloud

Follow these steps to build, containerize, and deploy the service to the AWS environment.

### 1. Push image to ECR 
Build the Docker image.
If you use MacOS on Silicon chip, add --platform flag:
```shell
docker build --platform linux/amd64 -f docker/Dockerfile -t bachelor/iam-service .
```

Tag the image for Amazon ECR repository:
```shell
docker tag bachelor/iam-service:latest 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/iam-service:latest
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
docker push 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/iam-service:latest
```

### 2. Deploy to ECS

instructions to update the ECS service

```shell
aws ecs update-service --cluster bachelor-cluster --service bachelor-iam-task-service --force-new-deployment --profile bachelor
```

```shell
aws logs tail /ecs/bachelor-iam-task --follow --region eu-central-1 --profile bachelor
```
