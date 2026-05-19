# 2.2 Architectural Evolution: From Monoliths to Microservices

The transition from monolithic architectures to microservices represents one of the most significant shifts in software engineering over the last decade. Understanding this evolution is crucial for justifying the design of the Robust Notification System.

### 2.2.1 The Monolithic Paradigm
In a monolithic architecture, all functional requirements of a system are packaged into a single unit of deployment. For a notification system, this would mean the user management, weather polling, subscription logic, and email sending all reside within the same codebase, share the same database, and run in the same process.

While monoliths are simpler to develop initially and easier to test in isolation, they suffer from several drawbacks as they grow:
*   **Deployment Bottlenecks:** A change in the email template requires a full redeploy of the entire system.
*   **Scaling Inefficiency:** If the weather polling logic is CPU-intensive, you must scale the entire monolith, even if the user management part requires very little resources.
*   **Technology Lock-in:** It is difficult to adopt new technologies (e.g., moving from Java to Go for a specific component) because the entire system is tied to a single stack.

### 2.2.2 The Microservices Approach
Microservices break the application into small, independent services that communicate over a network. Each service is responsible for a specific business capability and owns its own data.

In this project, we have decomposed the system into five core services:
1.  **IAM (Identity and Access Management):** Handles authentication.
2.  **Subscription:** Manages what users want to be notified about.
3.  **Weather:** Interacts with the external world (OpenWeather API).
4.  **Decision:** The "brain" that connects weather data with user subscriptions.
5.  **Notification:** The final delivery mechanism.

### 2.2.3 Trade-offs and Challenges
However, microservices are not a "silver bullet." They introduce new complexities, often referred to as the "Fallacies of Distributed Computing." These include network latency, partial failures, and the difficulty of maintaining data consistency.

[DIAGRAM: Comparison of Monolithic and Microservices Architecture]

As Martin Kleppmann argues, the move to microservices is often a move from "technical complexity" (large, messy codebases) to "operational complexity" (managing many moving parts). This thesis demonstrates how cloud-native tools (AWS) and robust design patterns (EDA) can mitigate this operational complexity, making the trade-off worthwhile for systems that require high reliability and independent scalability.
