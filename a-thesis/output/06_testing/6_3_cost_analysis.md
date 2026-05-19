# 6.3 Cost-Benefit Analysis: Fargate vs. EC2

A critical part of engineering is understanding the financial implications of architectural choices. For this thesis, we compared the cost and operational overhead of **AWS Fargate** versus traditional **EC2** instances for our microservices.

### 6.3.1 Operational Overhead
*   **EC2:** Requires managing OS updates, security patching, and scaling the underlying "cluster" of machines. This "undifferentiated heavy lifting" takes time away from building features.
*   **Fargate:** AWS manages the infrastructure. We only manage the container. The operational cost is significantly lower.

### 6.3.2 Financial Cost
For a small-to-medium scale deployment like ours:
*   **Fargate** uses a "Pay-as-you-go" model based on CPU and Memory per second. Our tests showed a monthly cost of approximately $40 for the entire system running at baseline.
*   **EC2** requires paying for instances even when they are idle. While potentially cheaper for 100% steady-state load, it is less efficient for the "bursty" nature of a notification system.

### 6.3.3 Conclusion on Infrastructure
Given the goal of "Robustness," Fargate is the clear winner. The slight premium in per-resource cost is more than offset by the gains in security (isolated kernels), reliability (automatic replacement), and the massive reduction in human maintenance time.

[TABLE: Cost-Benefit comparison between EC2 and Fargate across various metrics]

This analysis reinforces the "Cloud-Native" approach as the most viable path for modern, robust software systems.
