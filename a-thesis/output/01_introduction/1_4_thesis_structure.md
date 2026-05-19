# 1.4 Thesis Structure

To systematically address the design and implementation of a fault-tolerant system, this thesis is structured as follows:

*   **Chapter 1: Introduction.** Outlines the modern challenges of reliability, the business impact of system failures, and the shift toward graceful degradation in microservices. It defines the core problems and the objectives of the project.
*   **Chapter 2: Theoretical Background.** Explores the foundational concepts of data-intensive applications, drawing heavily on Martin Kleppmann's principles. It compares monolithic and microservice architectures and delves into the mechanics of Event-Driven Architectures (EDA) and consistency models.
*   **Chapter 3: Analysis and System Design.** Translates the theoretical concepts into concrete architectural plans. This chapter details the microservice decomposition strategy, domain data modeling, and how the AWS SNS/SQS "Fan-out" pattern is utilized to decouple system components.
*   **Chapter 4: Implementation and Cloud Infrastructure.** Details the technical execution of the project, including the use of Java 21, Spring Boot 3, and Docker. It thoroughly explains the "Infrastructure as Code" approach and the deployment to AWS ECS Fargate.
*   **Chapter 5: Achieving Robustness and Reliability.** Focuses exclusively on the resilience patterns implemented in the code, such as Dead Letter Queues (DLQs), exponential backoffs, idempotency checks, and the throttling logic designed to protect both the user and the system resources.
*   **Chapter 6: Testing and Evaluation.** Presents the methodology and results of validating the system's robustness. This includes local failure simulation using LocalStack, performance load testing, and a brief cost-benefit analysis of the chosen cloud infrastructure.
*   **Chapter 7: Discussion and Future Work.** Provides a critical reflection on the complexities introduced by the distributed architecture and proposes future enhancements, such as migrating to Apache Kafka or introducing real-time WebSockets.
*   **Chapter 8: Conclusion.** Summarizes the findings and reaffirms the thesis's main argument regarding the necessity of designing for failure in modern software engineering.
