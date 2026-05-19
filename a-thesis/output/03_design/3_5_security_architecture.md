# 3.5 Security Architecture and Identity Management

Security is not an afterthought in the Robust Notification System; it is integrated into every layer of the architecture, following the principle of **Defense in Depth**.

### 3.5.1 Centralized Authentication (JWT)
The IAM Service acts as the central authority for identity. It uses **JSON Web Tokens (JWT)** for stateless authentication.
1.  **Login:** User provides credentials; IAM validates and signs a JWT.
2.  **Authorization:** For every request to the Subscription Service, the user must include the JWT in the `Authorization` header.
3.  **Validation:** Services validate the JWT signature using a shared secret or a public key, ensuring the request is from a legitimate user without needing to call the IAM service for every request.

### 3.5.2 Infrastructure Security: IAM Roles
In the AWS environment, we use **IAM (Identity and Access Management) Roles** for tasks. Instead of hardcoding AWS access keys into our application (a major security risk), each Fargate task is assigned a specific role.
*   The Weather Service has a role that *only* allows `sns:Publish` to the weather topic.
*   The Notification Service has a role that *only* allows `sqs:ReceiveMessage` and `ses:SendEmail`.

This follows the **Principle of Least Privilege**, ensuring that if one service is compromised, the attacker has limited ability to move laterally through the infrastructure.

### 3.5.3 Network Security: VPC and Security Groups
The services are deployed within a **Virtual Private Cloud (VPC)**.
*   **Public Subnets:** Only the Application Load Balancer (ALB) is in a public subnet.
*   **Private Subnets:** All microservices run in private subnets, unreachable from the public internet.
*   **Security Groups:** Acting as virtual firewalls, these ensure that only the ALB can talk to the microservices, and microservices can only talk to the specific RDS instances and AWS services they require.

[DIAGRAM: Security Architecture showing JWT flow and VPC Security Groups]

This comprehensive security strategy ensures that user data and system integrity are protected against both external and internal threats.
