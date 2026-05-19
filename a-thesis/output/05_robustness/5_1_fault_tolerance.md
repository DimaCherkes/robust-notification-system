# 5.1 Fault Tolerance: Retries, Backoff, and DLQs

In a distributed system, failures are not a matter of "if" but "when." The Robust Notification System implements several strategies to ensure that transient failures do not lead to message loss or system instability.

### 5.1.1 Retry Mechanisms and Exponential Backoff
When a service fails to process a message (e.g., the Decision Service cannot reach the Subscription Service due to a momentary network glitch), it does not immediately give up.
*   **Local Retries:** Spring SQS listeners can be configured to retry processing a message a set number of times.
*   **Exponential Backoff:** Rather than retrying immediately, the system waits for an increasing amount of time (e.g., 1s, 2s, 4s, 8s). This prevents "hammering" a struggling service and gives it time to recover.

### 5.1.2 Dead Letter Queues (DLQ)
If a message fails to be processed after the maximum number of retries, it is moved to a **Dead Letter Queue (DLQ)**. 
*   **Isolation:** The "poison pill" message is removed from the primary queue, allowing the system to continue processing other messages.
*   **Inspection:** Developers can inspect the DLQ to understand why certain messages are failing (e.g., malformed JSON or unexpected data states) and re-drive them once the issue is resolved.

### 5.1.3 Handling External Service Failures
For external calls (SES or OpenWeather), the system uses the **Circuit Breaker** pattern (conceptually, via retry logic). If the external service is down, the messages stay in the SQS queue, ensuring that no alerts are lost; they are simply delayed until the external service is healthy again.

[DIAGRAM: Message lifecycle showing retries and transition to DLQ]

These fault-tolerance mechanisms are what transform a "simple" notification system into a "robust" one, capable of surviving the unpredictable nature of cloud environments.
