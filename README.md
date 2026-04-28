# Robust Notification System

A microservices-based platform designed to provide highly reliable, event-driven weather notifications based on custom user-defined criteria.

## System Overview

The system monitors weather conditions for various cities and triggers alerts when they match specific user preferences (e.g., temperature thresholds, rain alerts, wind speed). It is built with a focus on **reliability**, **scalability**, and **eventual consistency**.

### Architecture Highlights
- **Event-Driven**: Services communicate via AWS SNS/SQS for data synchronization and alert processing.
- **Data Locality**: The Decision Service maintains local replicas of necessary data to ensure high performance during rule evaluation.
- **Resilience**: Implements Circuit Breakers for external API calls and handles intermittent failures in message processing.
- **Local Development**: Fully compatible with LocalStack for AWS cloud service emulation.

## Microservices

The system is composed of the following services:

| Service | Description | Documentation |
| :--- | :--- | :--- |
| **IAM Service** | Handles authentication, authorization, and user profiles using JWT. | [README](./iam-service/README.md) |
| **Subscription Service** | Manages user notification intents, rules, and monitored cities. | [README](./subscription-service/README.md) |
| **Weather Service** | Synchronizes data from OpenWeatherMap and detects atmospheric changes. | [README](./weather-service/README.md) |
| **Decision Service** | Evaluates weather updates against user rules to trigger alerts. | [README](./decision-service/README.md) |

## Key Flows

1. **Subscription Flow**: User creates a subscription in the `Subscription Service` -> Event published to SNS -> `Decision Service` updates its local replica.
2. **Weather Sync Flow**: `Weather Service` fetches data from OpenWeatherMap -> Detects changes -> Sends update to `Decision Service`.
3. **Decision Flow**: `Decision Service` matches new weather data against stored subscriptions -> If a rule is satisfied, an alert is triggered (deduplicated by Alert History).

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.x
- **Messaging**: AWS SNS, AWS SQS (LocalStack)
- **Database**: PostgreSQL (with Flyway for migrations)
- **Security**: JWT, Spring Security
- **Communication**: Spring Cloud OpenFeign
- **Reliability**: Resilience4j (Circuit Breaker)
