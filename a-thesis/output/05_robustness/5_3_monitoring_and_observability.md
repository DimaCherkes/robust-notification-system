# 5.3 Monitoring, Logging, and Distributed Tracing

In a microservices architecture, observability is the key to robustness. If you cannot see what is happening, you cannot fix it when it breaks.

### 5.3.1 Centralized Logging (AWS CloudWatch)
Each Fargate task is configured to send its logs to **Amazon CloudWatch Logs**.
*   **Standardized Format:** We use a JSON log format (via Logback) to make logs easily searchable.
*   **Log Retention:** Logs are kept for a set period, allowing for post-mortem analysis of failures.

### 5.3.2 Metrics and Dashboards
We monitor several key performance indicators (KPIs):
*   **SQS Queue Depth:** A rising queue depth indicates that consumers cannot keep up with the load.
*   **Error Rates:** Spikes in 5xx HTTP responses or SQS retry counts trigger alerts.
*   **SES Delivery Success:** Monitoring how many emails are actually reaching their destination.

### 5.3.3 Distributed Tracing (Correlation IDs)
The most challenging part of debugging an EDA is following a single "thread" of logic across multiple services. We implement **Correlation IDs**:
1.  When a weather update begins, a unique ID is generated.
2.  This ID is passed in the metadata of the SNS message and subsequently the SQS message.
3.  Each service logs this ID.
By searching for a specific Correlation ID in CloudWatch, we can see exactly how a specific weather update flowed through the Weather, Decision, and Notification services.

[DIAGRAM: Distributed Tracing flow with Correlation IDs across multiple services]

This level of visibility is essential for the long-term maintainability and reliability of the system, allowing for rapid root-cause analysis in a complex distributed environment.
