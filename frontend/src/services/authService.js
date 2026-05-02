const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

class AuthService {
    getAccessToken() {
        return localStorage.getItem(ACCESS_TOKEN_KEY);
    }

    getRefreshToken() {
        return localStorage.getItem(REFRESH_TOKEN_KEY);
    }

    setTokens(accessToken, refreshToken) {
        localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }

    clearTokens() {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
    }

    isAuthenticated() {
        return !!this.getAccessToken();
    }

    async login(email, password) {
        const response = await fetch('/api/v1/iam-service/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        const data = await response.json();
        // Учитываем структуру DefaultApiResponse и UserProfileDTO
        const token = data.body.token; 
        const refreshToken = data.body.refreshToken || ''; 
        
        this.setTokens(token, refreshToken);
        return data.body;
    }

    async register(userData) {
        const response = await fetch('/api/v1/iam-service/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error('Registration failed');
        }

        return await response.json();
    }

    async refresh() {
        const refreshToken = this.getRefreshToken();
        if (!refreshToken) throw new Error('No refresh token');

        const response = await fetch('/api/v1/iam-service/auth/refresh/token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken })
        });

        if (!response.ok) {
            this.clearTokens();
            throw new Error('Refresh failed');
        }

        const data = await response.json();
        this.setTokens(data.body.token, data.body.refreshToken || '');
        return data.body.token;
    }

    logout() {
        this.clearTokens();
        window.location.hash = '#/login';
    }
}

export const authService = new AuthService();
