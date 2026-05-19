# 3.1 Requirements Analysis

Before diving into the implementation, it is essential to define the functional and non-functional requirements that the system must satisfy. These requirements serve as the benchmarks for evaluating the project's success.

### 3.1.1 Functional Requirements (FR)
1.  **User Authentication:** Users must be able to register, log in, and receive a secure JWT for subsequent requests.
2.  **Subscription Management:** Users must be able to create, read, update, and delete (CRUD) subscriptions for specific cities and weather conditions (e.g., temperature threshold).
3.  **Weather Polling:** The system must periodically fetch weather data for all cities that have at least one active subscription.
4.  **Automated Decision Making:** The system must compare current weather against active subscriptions and trigger alerts when conditions are met.
5.  **Email Delivery:** Alerts must be delivered to the user's registered email address using AWS SES.
6.  **Throttling:** The system must ensure that a user does not receive more than one email for the same subscription within a 12-hour period.

### 3.1.2 Non-Functional Requirements (NFR)
1.  **Reliability:** The system should not lose messages. If a service fails, it should be able to resume processing from where it left off.
2.  **Scalability:** The architecture must be able to handle a 10x increase in the number of users or weather updates without significant architectural changes.
3.  **Security:** All inter-service communication and external API access must be secured. User passwords must be hashed, and JWTs must be used for session management.
4.  **Maintainability:** The codebase should follow industry standards (Clean Code, SOLID principles) and be well-documented.
5.  **Observability:** Developers must be able to track the lifecycle of a notification from weather update to email delivery.

[DIAGRAM: Requirement Traceability Matrix]

These requirements form the "contract" for the Robust Notification System, ensuring that the final implementation meets both user needs and engineering standards.
