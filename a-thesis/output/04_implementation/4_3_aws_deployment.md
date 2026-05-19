# 4.3 Infrastructure as Code and AWS Deployment

The deployment of the system to AWS is fully automated, following the principles of **Infrastructure as Code (IaC)**. This ensures that the environment is reproducible and version-controlled.

### 4.3.1 Networking Foundation (VPC)
The deployment begins with a custom **Virtual Private Cloud (VPC)**.
*   **Multi-AZ Deployment:** Services are distributed across two Availability Zones (AZs) for high availability.
*   **IGW and NAT Gateways:** An Internet Gateway (IGW) allows the ALB to receive traffic, while NAT Gateways allow services in private subnets to reach external APIs (like OpenWeather) securely.

### 4.3.2 Application Load Balancer (ALB) and Path-Based Routing
The ALB serves as the single entry point for the system. We use **Path-based routing** to direct traffic to the correct service:
*   `/api/v1/iam-service/*` -> IAM Service.
*   `/api/v1/subscriptions/*` -> Subscription Service.
*   `*` -> Frontend Service (Nginx).

This setup allows us to present a unified API to the frontend while maintaining independent backend services.

### 4.3.3 ECS Fargate and Task Definitions
Each service is deployed as an **ECS Task**. The **Task Definition** specifies the Docker image (from AWS ECR), CPU/Memory limits, and environment variables (including database credentials and SNS topic ARNs). ECS handles the orchestration, including health checks and automatic replacement of failed tasks.

[DIAGRAM: AWS Infrastructure Overview: VPC, ALB, ECS Fargate, and RDS]

This cloud-native deployment strategy provides the system with professional-grade resilience and the ability to scale to meet any demand.
