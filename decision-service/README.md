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
