# 3.4 Asynchronous Communication Design (SNS/SQS Fan-out)

The backbone of the system's robustness is its communication strategy. While the frontend interacts with the backend via synchronous REST APIs, internal service-to-service communication is almost entirely asynchronous, utilizing the **AWS SNS + SQS Fan-out pattern**.

### 3.4.1 The SNS + SQS Fan-out Pattern
In this pattern, a producer service (e.g., the Weather Service) sends a single message to an **Amazon Simple Notification Service (SNS)** topic. SNS then "fans out" this message to one or more **Amazon Simple Queue Service (SQS)** queues.

[DIAGRAM: SNS/SQS Fan-out Architecture showing producers and multiple consumers]

This approach provides several critical advantages for a robust system:

1.  **Decoupling of Producers and Consumers:** The Weather Service does not need to know which services are interested in weather data. It simply "shouts" to the topic. New services (e.g., a "History Logging Service") can be added later by subscribing a new queue to the topic without changing a single line of code in the producer.
2.  **Durability and Buffering:** SQS acts as a buffer. If the Decision Service is overloaded or offline, messages are stored safely in the queue for up to 14 days. This prevents message loss and allows for "smoothing" of traffic spikes.
3.  **Independent Retries:** If the Decision Service fails to process a message, it can retry independently without affecting other consumers (like the Notification Service).

### 3.4.2 Implementation in the Notification System
The system utilizes two primary topics:
*   `weather-updates-topic`: Published by the Weather Service, consumed by the Decision Service.
*   `notification-requests-topic`: Published by the Decision Service, consumed by the Notification Service.

### 3.4.3 Message Filtering
A sophisticated feature of AWS SNS is **Message Filtering**. This allows consumers to receive only a subset of messages based on attributes. For example, the Decision Service could theoretically have multiple queues, each handling a different geographic region, with SNS routing messages based on a `region` attribute in the message header. While not fully implemented in the current prototype, the architecture is designed to support this level of granular scaling.

### 3.4.4 Dealing with Latency
Asynchronous communication introduces "Eventual Consistency." There is a slight delay between a weather update and the user receiving an email. However, in the context of weather alerts (which are usually polled every 15-60 minutes), this millisecond-level latency is a negligible price to pay for the massive gains in reliability and scalability.
