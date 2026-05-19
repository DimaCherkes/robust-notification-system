# 3.2 Microservice Decomposition Strategy

The decomposition of the Robust Notification System into specialized services is guided by the principle of **Bounded Contexts** from Domain-Driven Design (DDD). Each service is designed to be autonomous, owning its logic, data, and lifecycle.

### 3.2.1 Identification of Bounded Contexts
To achieve a high degree of decoupling, we identified five distinct contexts:

1.  **Identity and Access Management (IAM) Context:**
    *   *Responsibility:* User registration, login, and JWT issuance.
    *   *Autonomy:* This service does not need to know about weather or subscriptions. It only manages the "Who."
2.  **Subscription Context:**
    *   *Responsibility:* Maintaining the mapping between users and their alert criteria (e.g., "User X wants an alert for London if Temp > 25°C").
    *   *Autonomy:* It manages the "What" and "Where."
3.  **Weather Context:**
    *   *Responsibility:* Periodic polling of the OpenWeather API and normalization of raw data.
    *   *Autonomy:* It acts as an Anti-Corruption Layer (ACL), shielding the rest of the system from the specific formats of the external API.
4.  **Decision Context:**
    *   *Responsibility:* Matching incoming weather events against stored subscriptions.
    *   *Autonomy:* This is a stateless processing engine (with the exception of its throttling history). It implements the core "If-Then" logic.
5.  **Notification Context:**
    *   *Responsibility:* Interfacing with AWS SES to deliver emails.
    *   *Autonomy:* It focuses purely on delivery logistics, retries, and formatting.

### 3.2.2 Service Interaction Model
Services interact primarily through events. For example, when the Weather Service finishes polling, it doesn't call the Decision Service's API. Instead, it publishes a `WeatherUpdatedEvent` to an SNS topic.

[DIAGRAM: Service Decomposition and Event Flow between Bounded Contexts]

### 3.2.3 Benefits of this Decomposition
*   **Independent Scaling:** During a storm, the Weather and Decision services can be scaled up, while the IAM service remains at a baseline level.
*   **Fault Isolation:** If the Subscription database goes down, the Weather service can continue to poll and publish events; the events will simply wait in the Decision service's queue until the Subscription service is healthy again.
*   **Team Autonomy:** In a larger organization, different teams could own different services, using different technologies if necessary, as long as they adhere to the event contracts.

This strategic decomposition is the foundation of the system's robustness, ensuring that no single component becomes a bottleneck or a universal point of failure.
