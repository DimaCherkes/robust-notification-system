# 4.2 Containerization and Portability with Docker

To ensure that the Robust Notification System can be deployed reliably across different environments, we utilize **Docker** for containerization.

### 4.2.1 The Dockerfile Strategy
Each microservice contains a `Dockerfile` that defines its build and runtime environment. We use a **Multi-stage build** to minimize the size of the final image:
1.  **Build Stage:** Uses a Maven image to compile the Java code and package it into a JAR file.
2.  **Runtime Stage:** Uses a lightweight Amazon Corretto 21 (Alpine-based) JRE image to run the JAR.

This approach ensures that our production images do not contain unnecessary build tools or source code, reducing the attack surface and image size.

### 4.2.2 Cross-Platform Compatibility
Since the development environment (often macOS or Windows) may differ from the production environment (AWS Linux), we build our images for the `linux/amd64` platform. This ensures that the binary behavior is consistent across all stages of the lifecycle.

### 4.2.3 Docker Compose for Local Development
For local testing, we use `docker-compose.yml` to orchestrate the entire system, including the microservices, PostgreSQL databases, and **LocalStack** (to simulate AWS). This allows developers to run the entire system with a single command: `docker-compose up`.

[DIAGRAM: The Docker build pipeline from source code to ECR registry]

By containerizing our services, we achieve "environment parity," drastically reducing the frequency of bugs that only appear in production.
