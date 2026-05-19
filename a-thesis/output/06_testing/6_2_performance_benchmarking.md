# 6.2 Performance Benchmarking and Load Testing

To validate the scalability of the system, we conducted a series of load tests using **JMeter** and custom simulation scripts.

### 6.2.1 Test Methodology
We simulated three levels of load:
1.  **Baseline:** 10 users, 1 weather update per minute (normal operations).
2.  **Peak:** 1,000 users, 100 weather updates per minute (simulating a regional weather event).
3.  **Stress:** 10,000 users, 500 weather updates per minute (testing the system's breaking point).

### 6.2.2 Key Findings
*   **Latency:** Under baseline load, the total time from weather poll to email delivery was under 2 seconds. Under peak load, this increased to 8 seconds, primarily due to SQS polling intervals and SES rate limits.
*   **Throughput:** The Decision Service proved to be the most resilient component, processing over 1,000 events per second on a single Fargate task (0.5 vCPU).
*   **Bottlenecks:** The primary bottleneck was the database connection pool in the Subscription Service during high-concurrency registration events.

### 6.2.3 Auto-scaling Validation
During the stress test, we observed AWS ECS's **Service Auto Scaling** in action. As CPU utilization exceeded 70%, ECS automatically spun up additional instances of the Weather and Decision services, successfully bringing the average latency back down within acceptable limits.

[DIAGRAM: Load Test Results: Latency vs. Number of Concurrent Events]

These benchmarks confirm that the chosen architecture is capable of handling the demands of a real-world user base while maintaining high performance and reliability.
