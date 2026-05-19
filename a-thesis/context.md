# Bachelor Thesis Master Context: Robust Notification System

## 1. General Project Overview
- **Thesis Title:** Robust Notification System for Sending Notifications
- **Author:** Dmitry Cherkes
- **Institution:** FEI STU (Slovak University of Technology in Bratislava)
- **Primary Goal:** To design and implement a scalable, fault-tolerant, and asynchronous notification system using a microservices architecture and cloud-native infrastructure.

## 2. Theoretical Foundation
- **Primary Reference:** *Designing Data-Intensive Applications* (DDIA) by Martin Kleppmann.
- **Key Concepts:**
    - **Reliability, Scalability, Maintainability:** The three pillars of data-intensive systems.
    - **Event-Driven Architecture (EDA):** Using events as the primary means of communication to achieve low coupling.
    - **Eventual Consistency:** Acknowledging that data across microservices will sync over time rather than instantaneously.
    - **Idempotency:** Ensuring that duplicate messages (from SQS/SNS) do not result in duplicate actions (e.g., sending two emails for one alert).
    - **Throttling/Backpressure:** Implementing logic to protect consumers and users from notification flooding.

## 3. System Architecture
- **Paradigm:** Microservices with a "Database per Service" pattern.
- **Communication:** 
    - **Internal:** Asynchronous messaging using **AWS SNS** (Pub/Sub) and **AWS SQS** (Point-to-Point) for a "Fan-out" pattern.
    - **External:** REST API for the frontend and inter-service authentication via **JWT**.

### Core Services:
1. **IAM Service:** Centralized user management and authentication. Emits events on user registration/update/deletion.
2. **Subscription Service:** Manages user alert criteria (e.g., "Notify me if temp > 30°C").
3. **Weather Service:** Polls external APIs (OpenWeather) and publishes current weather data to the system.
4. **Decision Service:** The "Brain". Compares weather data against active subscriptions.
    - *Key Logic:* Implements a **12-hour throttling window** per subscription to prevent spamming without deactivating the service.
5. **Notification Service:** Consumer of decision events. Integrates with **AWS SES** for email delivery.

## 4. Technology Stack
- **Backend:** Java 21, Spring Boot 3.x, Spring Security (JWT), Hibernate/JPA.
- **Database:** PostgreSQL (AWS RDS).
- **Messaging:** AWS SNS & SQS.
- **Frontend:** Vanilla JS/CSS/HTML (Single Page Application) served via Nginx.
- **DevOps:** Docker (Multi-platform amd64), AWS ECR.

## 5. AWS Infrastructure (Deployment)
- **Orchestration:** **AWS ECS Fargate** (Serverless container execution).
- **Networking:** Custom VPC with Public Subnets, Internet Gateway, and Security Groups.
- **Traffic Management:** **Application Load Balancer (ALB)** with Path-based routing:
    - `/api/v1/iam-service/*` -> IAM Target Group.
    - `/api/v1/subscriptions/*` & `/api/v1/cities/*` -> Subscription Target Group.
    - `Default` -> Frontend Target Group (Nginx).
- **Email:** **AWS SES** (verified identities in Sandbox mode).

## 6. Development & Testing Workflow
- **Local Simulation:** **LocalStack** used to simulate SNS, SQS, and SES locally.
- **Init Script:** `localstack-init.sh` configures topics, queues, and filter policies.
- **Environment Management:** Use of Spring Profiles (`local-idea`, `docker`, `aws`) to switch between LocalStack and real AWS resources.

## 7. Instructions for Sub-Agents
When working on this thesis, ensure:
1. **Consistency:** All code examples should follow the existing package structure and naming conventions (`com.dmitrycherkes.*`).
2. **Security:** Prioritize the "Least Privilege" principle for IAM and avoid hardcoding sensitive data (use `@Value` and Task Definition environment variables).
3. **Academic Tone:** Use professional academic English suitable for FEI STU requirements.
4. **Visuals:** When describing architecture, refer to the SNS/SQS Fan-out pattern and the role of the ALB as a gateway.
