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
- **Tech Stack**: Spring Boot, Spring Cloud OpenFeign, AWS SNS, JPA, PostgreSQL, Flyway.
