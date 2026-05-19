# 1.3 Objectives and Scope

The primary objective of this bachelor thesis is to design, implement, and evaluate a **Robust Notification System** using a cloud-native microservices architecture. Rather than treating fault tolerance as an afterthought, this system will be built "by design" to withstand the inevitable failures inherent in distributed environments.

To achieve this, the thesis sets out the following specific goals:

1.  **Implement an Event-Driven Architecture (EDA):** Move away from fragile, synchronous REST calls between backend services. Instead, utilize Message-Oriented Middleware (specifically Amazon SNS and SQS) to decouple services, allowing them to operate and fail independently.
2.  **Apply the Reverse-Engineering Mindset:** Design the system by assuming specific components (like the external Weather API or the Email Delivery service) are already offline. Implement patterns like Dead Letter Queues (DLQ), retry backoffs, and idempotency to ensure the system gracefully degrades and recovers without data loss.
3.  **Develop a Cloud-Native Deployment Strategy:** Utilize modern DevOps practices by containerizing the microservices with Docker and deploying them to a serverless orchestration platform (AWS ECS Fargate). This reduces operational overhead and provides isolated environments that prevent cascading infrastructure failures.
4.  **Balance Reliability and Cost:** Acknowledge that infinite reliability is infinitely expensive. The system will implement intelligent throttling (e.g., a 12-hour notification window) to protect resources and prevent user fatigue, ensuring that the system remains both highly available and economically viable.
5.  **Validate via Simulation:** Utilize tools like LocalStack to simulate cloud infrastructure failures locally, proving that the system can survive network partitions and component outages without requiring the user to experience a "500 Internal Server Error."

The scope of the implementation focuses on the backend architecture and cloud infrastructure. While a basic frontend will be mentioned to demonstrate end-to-end functionality, the core academic focus remains on backend resilience, message routing, and graceful degradation strategies within the Spring Boot and AWS ecosystem.
