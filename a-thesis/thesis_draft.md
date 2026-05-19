# Bachelor Thesis Draft: Robust Notification System

## Chapter 3: System Analysis and Architectural Design

### 3.1 Architectural Paradigm: Microservices and EDA
The system is designed following the **Microservices Architecture** pattern, prioritizing service autonomy and independent scalability. To address the challenges of high coupling and synchronous bottlenecks common in monolithic systems, an **Event-Driven Architecture (EDA)** was implemented. 

As Martin Kleppmann highlights in *Designing Data-Intensive Applications*, asynchronous message-passing systems allow for better decoupling by providing a "buffer" between producers and consumers. In this project, services do not call each other directly via REST for core business logic; instead, they emit events to **AWS SNS**, which are then "fanned out" to multiple **AWS SQS** queues. This ensures that the failure of one service (e.g., the Notification Service) does not block the upstream services (e.g., the Decision Service).

### 3.2 Service Decomposition and Data Sovereignty
To ensure **Maintainability** and **Scalability**, the system is decomposed into five specialized services:
1. **IAM Service:** Manages user identities and security tokens (JWT).
2. **Subscription Service:** Stores user-defined notification criteria using a "Database per Service" pattern with PostgreSQL.
3. **Weather Service:** Acts as an external data adapter, polling weather information.
4. **Decision Service:** The core logic engine that evaluates weather data against subscriptions.
5. **Notification Service:** Handles the final delivery of messages via AWS SES.

This decomposition follows Kleppmann’s principles of **partitioning**, where each service owns its data schema (e.g., `v1_iam_service`, `v1_notification_service`), preventing hidden dependencies at the database level.

---

## Chapter 4: Implementation and Cloud Infrastructure

### 4.1 Cloud-Native Deployment with AWS Fargate
The implementation leverages **AWS ECS Fargate**, a serverless container orchestration service. Fargate was chosen over traditional EC2 instances to eliminate the operational overhead of managing underlying servers, allowing the focus to remain on application logic.

Each microservice is containerized using **Docker** (targeting the `linux/amd64` platform) and stored in **AWS ECR**. The deployment utilizes **Application Load Balancer (ALB)** for sophisticated traffic routing.

### 4.2 Traffic Routing and Networking
A critical part of the robustness is the **Application Load Balancer (ALB)** configuration. Using **Path-based routing**, the ALB acts as a single entry point for the frontend, while intelligently dispatching requests to the appropriate backend target groups:
- Requests to `/api/v1/iam-service/*` are routed to the IAM service.
- Requests to `/api/v1/subscriptions/*` are routed to the Subscription service.
- All other traffic defaults to the Nginx-based Frontend service.

This setup ensures a **Single-Origin** feel for the browser, simplifying **CORS** management and allowing for secure `HttpOnly` cookie handling for JWT refresh tokens.

---

## Chapter 5: Engineering Robustness and Reliability

### 5.1 Throttling and Spam Protection
A "robust" system must protect its users from "alert fatigue." Referencing the concept of **Stream Processing** and **Flow Control** from DDIA, the **Decision Service** implements a 12-hour throttling window. 

Instead of simple deactivation, the service queries the `alert_history` table:
```java
if (lastTriggeredAt.isAfter(now.minusHours(12))) {
    return; // Skip alerting to prevent flooding
}
```
This ensures that the system remains "alive" and recurring without spamming the recipient, providing a balance between availability and user experience.

### 5.2 Fault Tolerance and Local Simulation
Reliability is verified through **LocalStack**. By simulating the entire AWS stack (SNS, SQS, SES, RDS) locally, the system's behavior during network partitions or message delays was analyzed before any cloud costs were incurred. The use of **Dead Letter Queues (DLQ)** in the SQS configuration ensures that "poison pill" messages do not crash the consumers but are instead isolated for manual inspection.

### 5.3 Security and Identity
Security is integrated at the infrastructure level. Each Fargate task is assigned an **IAM Task Role**, following the principle of **Least Privilege**. For example, only the Notification Service has the `ses:SendEmail` permission, ensuring that a breach in the Weather Service cannot lead to unauthorized email distribution.
