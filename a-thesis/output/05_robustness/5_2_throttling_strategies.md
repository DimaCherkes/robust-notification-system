# 5.2 Throttling Strategies and Resource Protection

A robust system must not only be reliable but also "polite." In the context of notifications, this means protecting users from spam and protecting the system's resources from being overwhelmed.

### 5.2.1 User-Level Throttling (The 12-Hour Window)
To prevent "alert fatigue," the Decision Service implements a 12-hour throttling window. 
*   **Logic:** Before publishing a `NotificationRequest`, the service queries the `alert_history` table: `SELECT MAX(triggered_at) FROM alert_history WHERE subscription_id = :id`.
*   **Action:** If an alert was sent in the last 12 hours, the current event is ignored.
*   **Persistence:** The `triggered_at` timestamp is updated every time an alert is successfully passed to the Notification Service.

This logic ensures that if the temperature stays above 30°C for three hours, the user only gets one notification, not one every time the weather is polled.

### 5.2.2 System-Level Rate Limiting
To protect our external API quotas (e.g., OpenWeather's free tier), the Weather Service implements rate limiting. It uses a **Token Bucket** algorithm (via the Bucket4j library or similar) to ensure it does not exceed the allowed number of requests per minute.

### 5.2.3 Consumer Backpressure
As discussed in Chapter 2, SQS naturally provides backpressure. By having consumers "pull" messages, we ensure that if a service instance is busy, the messages simply wait in the queue rather than overwhelming the service's memory and crashing it.

[DIAGRAM: Multi-level Throttling Strategy (System and User level)]

These throttling and protection strategies ensure that the system remains stable and user-friendly even under heavy load or during persistent weather conditions.
