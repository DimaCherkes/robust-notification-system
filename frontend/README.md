# Robust Notification System - Vanilla JS Frontend

A lightweight, high-performance Single Page Application (SPA) built with Vanilla JavaScript, HTML5, and CSS3. This frontend is designed to work seamlessly with the microservices architecture using Nginx as a reverse proxy.

## 🚀 Architecture Overview

- **Pattern**: Single Page Application (SPA).
- **Routing**: Hash-based routing (`/#/login`, `/#/dashboard`).
- **State Management**: LocalStorage for JWT tokens and in-memory routing.
- **API Communication**: Centralized `ApiClient` using the `fetch` API with automatic interceptors for authentication.
- **Containerization**: Nginx-based Docker image serving static assets and acting as an **API Gateway**.

## 🛠 Features Implemented

### 1. Authentication System
- **JWT Handling**: Stores `accessToken` and `refreshToken` in LocalStorage.
- **Auto-Refresh**: The `ApiClient` detects `401 Unauthorized` errors, automatically attempts to refresh the token via `iam-service`, and retries the original request without user interruption.
- **Route Guards**: Private routes (like Dashboard) are protected; unauthenticated users are redirected to the Login page.

### 2. Reverse Proxy (Nginx)
- **CORS Solution**: Nginx proxies requests from `/api/v1/*` to the respective microservices (`iam-service` or `subscription-service`). This eliminates Cross-Origin Resource Sharing (CORS) issues as the browser sees everything on the same origin (port 80).

### 3. User Interface
- **Register**: Full registration flow with password confirmation and frontend validation.
- **Login**: Email-based authentication.
- **Dashboard**: A protected view that fetches and displays user subscriptions and allows creating new ones.
- **Navigation**: Dynamic header that changes based on the user's authentication state.

## 📂 Project Structure

```text
frontend/
├── docker/
│   ├── Dockerfile      # Nginx alpine image configuration
│   └── nginx.conf      # Reverse proxy and static file routing
├── src/
│   ├── app.js          # SPA Router and initialization
│   ├── index.html      # Main entry point (Shell)
│   ├── style.css       # Global styles and component UI
│   ├── api/
│   │   └── apiClient.js # Fetch wrapper with 401 interceptors
│   ├── services/
│   │   └── authService.js # Business logic for IAM integration
│   ├── components/
│   │   └── navigation.js  # Dynamic UI components
│   └── pages/
│       ├── login.js
│       ├── register.js
│       └── dashboard.js
└── README.md
```

## 📦 How to Run

The frontend is integrated into the root `docker-compose.yml`. To start:

```bash
docker-compose up --build frontend
```

Access the application at `http://localhost`.

## Deployment to AWS Cloud

Follow these steps to build, containerize, and deploy the service to the AWS environment.
### 1. Push image to ECR
Open `frontend` directory in terminal and run the following commands:
Build the Docker image.
If you use MacOS on Silicon chip, add --platform flag:
```shell
docker build --platform linux/amd64 -f docker/Dockerfile -t bachelor/frontend .
```

Tag the image for Amazon ECR repository:
```shell
docker tag bachelor/frontend:latest 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/frontend:latest
```

Authenticate into AWS ECR
```shell
export AWS_PROFILE=bachelor # optional, if you have multiple AWS profiles configured
```

```shell
aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 631124976834.dkr.ecr.eu-central-1.amazonaws.com
```

Push the image to ECR
```shell
docker push 631124976834.dkr.ecr.eu-central-1.amazonaws.com/bachelor/frontend:latest
```

### 2. Deploy to ECS

instructions to update the ECS service
```shell
aws ecs update-service --cluster bachelor-cluster --service bachelor-frontend-service --force-new-deployment --profile bachelor
```

```shell
aws logs tail /ecs/bachelor-frontend-task --follow --region eu-central-1 --profile bachelor
```

Optional (not recommended)
```shell
aws ecs update-service \
      --cluster bachelor-cluster \
      --service bachelor-frontend-task-service \
      --health-check-grace-period-seconds 180 \
      --profile bachelor
```
