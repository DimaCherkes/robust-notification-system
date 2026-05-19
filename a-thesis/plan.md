# Bachelor Thesis Plan: Robust Notification System

1.  **Introduction**
    *   1.1 Context and Motivation: Importance of proactive communication and the shift to cloud-native paradigms.
    *   1.2 Problem Statement: Challenges of coupling, reliability, scalability, and operational complexity.
    *   1.3 Objectives and Scope: Goals of the thesis (Scalability, Fault Tolerance, AWS).
    *   1.4 Thesis Structure: Roadmap of the document.

2.  **Theoretical Background**
    *   2.1 Foundations of Data-Intensive Applications: Reliability, Scalability, and Maintainability (DDIA).
    *   2.2 Architectural Evolution: Comparison of Monoliths vs. Microservices.
    *   2.3 Event-Driven Architecture (EDA): Concepts of asynchronous messaging and middleware.
    *   2.4 Cloud-Native Principles: The 12-Factor App, Docker, and Serverless Containerization.
    *   2.5 Consistency Models and Idempotency: Handling distributed state and duplicate messages.

3.  **Analysis and System Design**
    *   3.1 Requirements Analysis: Functional and Non-Functional Requirements.
    *   3.2 Microservice Decomposition Strategy: Bounded Contexts and service identification.
    *   3.3 Domain Modeling and Data Sovereignty: Database per service and distributed data patterns.
    *   3.4 Asynchronous Communication Design: SNS/SQS Fan-out implementation.
    *   3.5 Security Architecture: JWT, IAM Roles, and VPC design.

4.  **Implementation and Cloud Infrastructure**
    *   4.1 Backend Implementation: Java 21 and Spring Boot 3 features.
    *   4.2 Containerization: Docker multi-stage builds and portability.
    *   4.3 Infrastructure as Code and AWS Deployment: VPC, Fargate, and ALB configuration.
    *   4.4 External API Integration: OpenWeather API and AWS SES delivery.

5.  **Achieving Robustness and Reliability**
    *   5.1 Fault Tolerance: Implementation of Retries, Backoff, and Dead Letter Queues (DLQ).
    *   5.2 Throttling Strategies: User-level (12h window) and system-level protection.
    *   5.3 Monitoring and Observability: Centralized logging and distributed tracing.

6.  **Testing and Evaluation**
    *   6.1 Local Simulation: Using LocalStack for local AWS simulation.
    *   6.2 Performance Benchmarking: Load testing results and latency analysis.
    *   6.3 Cost-Benefit Analysis: Fargate vs. EC2 for small-to-medium scale systems.

7.  **Discussion and Future Work**
    *   7.1 Critical Reflection: Analysis of trade-offs and complexity.
    *   7.2 Future Enhancements: Potential migration to Kafka, WebSockets, and AI alerts.

8.  **Conclusion**
    *   Summary of findings and final architectural recommendations.
