# Weather Service

## Role
The **Weather Service** is responsible for providing current and forecast weather data. It acts as an aggregator that fetches data from external providers (OpenWeatherMap) and distributes updates to other services in the system.

## Implemented Functionality
- **Weather Synchronization**:
    - Scheduled tasks to fetch weather data for all monitored cities.
    - Integration with OpenWeatherMap API (One Call 3.0).
- **Change Detection**:
    - Compares new weather data with stored data to identify significant changes.
    - Tracks hourly forecasts for temperature, humidity, wind, and precipitation.
- **Event Dispatching**:
    - Publishes `weather_update` events directly to SQS when significant weather changes are detected.
- **Resilience**:
    - Implements Circuit Breaker pattern (Resilience4j) for external API calls to ensure system stability during provider outages.
- **Tech Stack**: Spring Boot, Resilience4j, AWS SQS, RestClient, PostgreSQL, Flyway.

## Deployment to AWS Cloud

Follow these steps to build, containerize, and deploy the service to the AWS environment.

### 1. Push image to ECR
Open `weather-service` directory in terminal and run the following commands:
Build the Docker image.
If you use MacOS on Silicon chip, add --platform flag:
```shell
docker build --platform linux/amd64 -f docker/Dockerfile -t bachelor/weather-service .
```

Tag the image for Amazon ECR repository:
```shell
docker tag bachelor/weather-service:latest 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/weather-service:latest
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
docker push 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/weather-service:latest
```

### 2. Deploy to ECS

instructions to update the ECS service
```shell
aws ecs update-service --cluster bachelor-cluster --service bachelor-weather-task-service --force-new-deployment --profile bachelor
```

```shell
aws logs tail /ecs/bachelor-weather-task --follow --region eu-central-1 --profile bachelor
```

Optional (not recommended)
```shell
aws ecs update-service \
      --cluster bachelor-cluster \
      --service bachelor-weather-task-service \
      --health-check-grace-period-seconds 180 \
      --profile bachelor
```
