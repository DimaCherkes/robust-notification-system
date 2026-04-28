# IAM Service

## Role
The **IAM (Identity and Access Management) Service** is responsible for user authentication, authorization, and profile management across the system. It acts as the central authority for security, issuing JWT tokens and managing user roles.

## Implemented Functionality
- **Authentication**: 
    - User registration and login.
    - JWT-based authentication (Access and Refresh tokens).
    - Password encryption and secure storage.
- **User Management**:
    - CRUD operations for user profiles.
    - Role-based access control (RBAC).
    - Retrieval of user details for other microservices (e.g., used by Subscription Service).
- **Security**:
    - Centralized security configurations.
    - Global exception handling for authentication and authorization errors.
- **Tech Stack**: Spring Boot, Spring Security, JPA, PostgreSQL, Flyway.
