# 4.1 Backend Implementation with Spring Boot and Java 21

The backend of the Robust Notification System is built using **Java 21**, the latest Long-Term Support (LTS) version of the language, and **Spring Boot 3**. This combination offers a modern, high-performance, and developer-friendly environment.

### 4.1.1 Leveraging Java 21 Features
Java 21 introduces several features that enhance the development of distributed systems:
*   **Virtual Threads (Project Loom):** While not explicitly used in all services, virtual threads allow for high-throughput, non-blocking I/O, which is ideal for the Weather Service's polling logic and the Decision Service's event processing.
*   **Pattern Matching for switch:** Used in the Decision Service to elegantly handle different types of weather criteria and event payloads.
*   **Record Classes:** Used extensively for Data Transfer Objects (DTOs) and event payloads (e.g., `WeatherUpdatedEvent`), providing a concise and immutable data model.

### 4.1.2 Spring Boot 3 Ecosystem
Spring Boot 3 provides the "glue" that connects our business logic to the underlying infrastructure:
*   **Spring Data JPA:** Simplifies interaction with PostgreSQL databases, using Hibernate as the underlying provider.
*   **Spring Security:** Handles JWT validation and method-level security.
*   **Spring Cloud AWS:** Provides seamless integration with AWS services like SQS and SNS using simple annotations like `@SqsListener`.

### 4.1.3 Service Structure
Each microservice follows a clean architecture pattern:
1.  **Controller Layer:** Defines REST endpoints for external interaction.
2.  **Service Layer:** Contains the core business logic (e.g., the weather comparison logic in the Decision Service).
3.  **Repository Layer:** Handles database persistence.
4.  **Listener Layer:** Specifically for consuming messages from SQS.

[DIAGRAM: Internal component architecture of a typical microservice in the system]

This standardized structure ensures that the system is easy to navigate and maintain, even as the number of services grows.
