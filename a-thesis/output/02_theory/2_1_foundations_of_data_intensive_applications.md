# 2.1 Foundations of Data-Intensive Applications

To build a "robust" system, one must first define what robustness means in the context of modern software. This thesis relies heavily on the definitions provided by Martin Kleppmann, who identifies three primary pillars of data-intensive systems: Reliability, Scalability, and Maintainability.

### 2.1.1 Reliability
Reliability, in this context, refers to the system's ability to continue functioning correctly even when things go wrong. These "things" are referred to as *faults*, and systems that anticipate and handle faults are called *fault-tolerant*. It is important to distinguish between a *fault* (one component deviating from its spec) and a *failure* (the whole system stopping). 

In our notification system, reliability is achieved by:
*   **Decoupling:** Ensuring that the failure of the Notification Service doesn't prevent the Weather Service from publishing updates.
*   **Redundancy:** Using AWS ECS Fargate to run multiple instances of a service across different Availability Zones.
*   **Message Persistence:** Using AWS SQS to store messages if the consumer is temporarily down, ensuring no data is lost during transient outages.

### 2.1.2 Scalability
Scalability is the system's ability to handle increased load. Load can be measured in different ways depending on the system; for a notification system, the key metrics are the number of weather updates per second and the number of active subscriptions. 

The system adopts a **horizontal scaling** strategy. Rather than increasing the power of a single server (vertical scaling), we add more instances of our microservices. The use of an Application Load Balancer (ALB) and SQS queues allows the system to distribute incoming requests and messages across these instances evenly.

[DIAGRAM: Vertical vs. Horizontal Scaling in Cloud Environments]

### 2.1.3 Maintainability
As Kleppmann notes, the majority of the cost of software is not in its initial development but in its ongoing maintenance. Maintainability is composed of three sub-principles:
1.  **Operability:** Making it easy for operations teams to keep the system running (achieved via AWS CloudWatch monitoring and ECS logging).
2.  **Simplicity:** Managing complexity so that new engineers can understand the system. We achieve this through clear service boundaries and the use of the Spring Boot framework's conventions.
3.  **Evolvability:** Making it easy to make changes to the system in the future. The microservices pattern allows us to replace or upgrade the Weather Service, for example, without affecting the IAM Service.

These foundations provide the theoretical framework within which all architectural decisions in this project were made.
