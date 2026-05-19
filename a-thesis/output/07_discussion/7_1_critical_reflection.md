# 7.1 Critical Reflection on the Architecture

While the Robust Notification System successfully meets its requirements, no architecture is perfect. Reflecting on the project reveals several areas of trade-off and complexity.

### 7.1.1 The Complexity of EDA
The move to an Event-Driven Architecture (EDA) solved many coupling issues but introduced "Hidden Complexity." Debugging an issue that spans three services and two messaging queues is significantly harder than debugging a single monolithic process. While Correlation IDs help, the cognitive load on developers is higher.

### 7.1.2 Eventual Consistency Challenges
The system's reliance on eventual consistency means that "race conditions" are possible. For example, if a user deletes a subscription at the exact same moment a weather alert is being processed, they might still receive one final email. Handling these "edge cases" requires defensive programming and idempotent consumers.

### 7.1.3 The "Lambda" Alternative
One could argue that the entire system could have been built using **AWS Lambda** (Function-as-a-Service) instead of Fargate containers. This would have reduced costs even further but would have introduced challenges with "Cold Starts" and the complexity of managing many small functions. The choice of Fargate provided a better balance of performance and a familiar development model (Spring Boot).

Despite these challenges, the architecture's ability to isolate failures and scale independently makes it a superior choice for a mission-critical notification system.
