class AuthService {
    constructor() {
        this.accessToken = null;
    }

    getAccessToken() {
        return this.accessToken;
    }

    setAccessToken(token) {
        this.accessToken = token;
    }

    clearAccessToken() {
        this.accessToken = null;
    }

    isAuthenticated() {
        return !!this.getAccessToken();
    }

    getUserInfo() {
        const token = this.getAccessToken();
        if (!token) return null;
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
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
        
        // Access token from response body
        const token = data.body.token; 
        this.setAccessToken(token);
        
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

        const data = await response.json();
        if (data.body && data.body.token) {
            this.setAccessToken(data.body.token);
        }
        return data;
    }

    async refresh() {
        const response = await fetch('/api/v1/iam-service/auth/refresh/token', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            this.clearAccessToken();
            throw new Error('Refresh failed');
        }

        const data = await response.json();
        const newToken = data.body.token;
        this.setAccessToken(newToken);
        return newToken;
    }

    async logout() {
        this.clearAccessToken();
        
        try {
            await fetch('/api/v1/iam-service/auth/logout', {
                method: 'POST'
            });
        } catch (error) {
            console.error('Logout failed on server', error);
        }
        
        window.location.hash = '#/login';
    }
}

export const authService = new AuthService();
