# Robust Notification System

A microservices-based platform designed to provide highly reliable, event-driven weather notifications based on custom user-defined criteria. The system monitors weather conditions and triggers alerts when they match user preferences (e.g., temperature thresholds, rain alerts).

---

## 🏗️ Architecture Overview

- **Event-Driven**: Services communicate via AWS SNS/SQS for data synchronization and alert processing.
- **Resilience**: Implements Circuit Breakers for external API calls and handles intermittent failures in message processing.
- **Local Development**: Uses **LocalStack** to emulate AWS cloud services (SNS, SQS, SES) locally.

---

## 🚀 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/get-started) and [Docker Compose](https://docs.docker.com/compose/install/)
- (Optional) Java 21 and Maven (for local development outside of Docker)

### Configuration

For **local development** using Docker Compose, no manual environment variable setup is required. The system is pre-configured to work out of the box with the provided `docker-compose.yml`.

The `.env` file is only necessary if you need to:
- Connect to **production infrastructure** from your local machine.
- Override default service configurations (e.g., ports, database credentials) for custom local setups.

### Running the System

To start the entire system, including all microservices, databases, and the frontend, run the following command from the root directory:

```bash
docker compose up --build
```

This will:
1. Spin up PostgreSQL instances for each service.
2. Start LocalStack and initialize AWS infrastructure (SNS topics, SQS queues).
3. Build and start all Java microservices.
4. Start the Frontend application served by Nginx.

---

## 🏢 Microservices

| Service | Port | Description | Documentation |
| :--- | :--- | :--- | :--- |
| **Frontend** | `80` | Web application for user interaction. | [README](./frontend/README.md) |
| **IAM Service** | `8180` | Handles Authentication, Authorization (JWT), and User Profiles. | [README](./iam-service/README.md) |
| **Subscription Service** | `8181` | Manages user notification intents, rules, and monitored cities. | [README](./subscription-service/README.md) |
| **Weather Service** | `8182` | Synchronizes data from OpenWeatherMap and detects changes. | [README](./weather-service/README.md) |
| **Decision Service** | `8183` | Evaluates weather updates against user rules to trigger alerts. | [README](./decision-service/README.md) |
| **Notification Service**| `8184` | Dispatches alerts via various channels (e.g., Email). | [README](./notification-service/README.md) |

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.x
- **Frontend**: Vanilla JS, HTML5, CSS3, Nginx
- **Messaging**: AWS SNS, AWS SQS (LocalStack)
- **Database**: PostgreSQL 15, Flyway (Migrations)
- **Security**: JWT, Spring Security
- **Resilience**: Resilience4j (Circuit Breaker, Retry)
- **Monitoring**: Spring Boot Actuator

---

## 🔄 Key System Flows

1. **Subscription Creation**: User creates a subscription in the `Subscription Service` ➔ Event published to SNS ➔ `Decision Service` updates its local replica.
2. **Weather Synchronization**: `Weather Service` fetches data from OpenWeatherMap ➔ Detects significant changes ➔ Publishes update to `Decision Service`.
3. **Decision & Alerting**: `Decision Service` matches weather data against active rules ➔ If satisfied, triggers an alert ➔ `Notification Service` sends the actual notification.

---

## 💻 Development

### LocalStack Initialization
The AWS infrastructure is automatically initialized via `localstack-init.sh` when running with Docker Compose. If you need to manually re-run setup:
```bash
./aws-infra-setup.sh
```

### Individual Service Build
To build an individual service using Maven:
```bash
cd <service-directory>
mvn clean package -DskipTests
```
