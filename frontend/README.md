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

## ⏳ Next Steps (To-Do)

- [ ] **Enhanced Security**: Transition from LocalStorage to `HttpOnly` cookies for Refresh Tokens to mitigate XSS risks.
- [ ] **UI/UX Polishing**: Implement a more modern design (e.g., using CSS Grid/Flexbox more extensively or a minimal CSS framework).
- [ ] **Subscription Management**: Add features to delete or pause subscriptions directly from the Dashboard.
- [ ] **User Profile**: Create a page to view and edit user details.
- [ ] **Global Error Handling**: Implement a "Toast" notification system for displaying API errors to the user.
- [ ] **Loading States**: Add skeleton screens or spinners during API requests.

## 📦 How to Run

The frontend is integrated into the root `docker-compose.yml`. To start:

```bash
docker-compose up --build frontend
```

Access the application at `http://localhost`.
