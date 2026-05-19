# 2.3 Event-Driven Architecture (EDA) and Message-Oriented Middleware

Event-Driven Architecture (EDA) is a design pattern in which the flow of the program is determined by events—significant changes in state. In the context of the Robust Notification System, an "event" could be a new weather report arriving or a user updating their subscription.

### 2.3.1 Core Components of EDA
An EDA typically consists of four main components:
1.  **Event Producers:** In our system, the Weather Service acts as a producer when it publishes weather updates.
2.  **Event Consumers:** The Decision Service consumes weather events to determine if an alert is necessary.
3.  **Event Channels:** These are the conduits through which events travel. We use AWS SNS and SQS as our channels.
4.  **Event Router:** This component ensures events reach the correct consumers. AWS SNS performs this role via its subscription and filtering logic.

### 2.3.2 Benefits of Asynchronous Messaging
By using Message-Oriented Middleware (MOM) like SNS and SQS, we introduce an abstraction layer between services. This leads to:
*   **Temporal Decoupling:** The producer and consumer do not need to be active at the same time. If the Decision Service is undergoing maintenance, the Weather Service can still produce events.
*   **Load Leveling:** SQS queues can absorb sudden bursts of events, allowing consumers to process them at a steady, manageable rate. This is essential for maintaining system stability during extreme weather events.

### 2.3.3 Push vs. Pull Models
The system employs a hybrid model. SNS "pushes" messages to SQS, while the Spring Boot consumers "pull" (poll) messages from SQS. This pull-based consumption is critical for **backpressure** management, as it ensures that a service only takes on as much work as it can handle at any given moment.

[DIAGRAM: Comparison of Push (Webhooks) vs. Pull (Polling) messaging models]

Understanding these concepts is vital for implementing a system that remains responsive under varying load conditions, a core requirement of this thesis.
