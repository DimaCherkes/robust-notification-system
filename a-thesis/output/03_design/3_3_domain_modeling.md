# 3.3 Domain Modeling and Data Sovereignty

One of the core tenets of microservices is that each service should own its data. This principle, known as **Data Sovereignty**, is strictly followed in the Robust Notification System.

### 3.3.1 Database per Service
Rather than a single shared database, each service has its own schema (and in a production environment, its own RDS instance).
*   **IAM Service:** Owns the `users` and `roles` tables.
*   **Subscription Service:** Owns the `subscriptions` and `cities` tables.
*   **Decision Service:** Owns the `alert_history` table (used for throttling).
*   **Weather/Notification Services:** These are largely stateless in the current version, though the Notification Service could store a `delivery_log`.

### 3.3.2 Domain Entities
The system's logic is built around several key entities:
*   **User:** Represented by email and hashed password.
*   **Subscription:** Links a User ID to a City and a set of criteria (e.g., `temp > 30`).
*   **WeatherReport:** A snapshot of weather for a city at a specific time.
*   **Alert:** A notification that needs to be sent.

### 3.3.3 The Challenge of Distributed Joins
When each service has its own database, you cannot perform a SQL `JOIN` across services. For example, when the Decision Service needs to know a user's email to send an alert, it cannot simply join a `subscriptions` table with a `users` table.

We solve this using **Data Duplication (Projection)**:
When a user registers, the IAM Service emits a `UserCreatedEvent`. The Subscription and Decision services can listen to this event and store a local copy of the necessary user data (ID and Email). 

[DIAGRAM: Data Sovereignty and the Event-Driven Data Replication pattern]

This approach increases complexity and requires handling eventual consistency, but it provides the ultimate level of service autonomy and performance, as each service can query its own local data without making synchronous network calls to other services.
