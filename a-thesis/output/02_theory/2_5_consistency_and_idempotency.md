# 2.5 Consistency Models and Idempotency

In a distributed system, maintaining data consistency is a complex challenge. The Robust Notification System navigates this by choosing appropriate consistency models for different scenarios.

### 2.5.1 Eventual Consistency
Because we use asynchronous messaging, our system is **eventually consistent**. When a user updates a subscription, it may take a few hundred milliseconds for that change to be reflected in the Decision Service's logic. In the context of weather notifications, this is a perfectly acceptable trade-off for the increased availability and partition tolerance (as per the **CAP Theorem**).

### 2.5.2 The Idempotency Problem
One of the most significant risks in an EDA is the delivery of duplicate messages. This can happen if a consumer processes a message but fails to send an acknowledgment back to SQS due to a network glitch. SQS will then re-deliver the message.

In our system, if a `NotificationRequest` is processed twice, a user might receive two identical emails—a poor user experience.

### 2.5.3 Implementing Idempotency
To prevent this, the system implements idempotency logic in the Notification Service. 
1.  **Unique Message IDs:** Each event carries a unique UUID.
2.  **Processed Store:** Before sending an email, the service checks a "processed messages" table in its database. 
3.  **Atomic Operations:** If the ID is not present, it records the ID and sends the email in an atomic transaction (or as close to it as the external API allows).

[DIAGRAM: Idempotency check flow in the Notification Service]

By acknowledging and handling the realities of distributed consistency, the system ensures that it remains reliable even when the underlying network is not.
