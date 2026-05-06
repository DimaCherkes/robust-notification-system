import { authService } from '../services/authService.js';

class ApiClient {
    async fetch(url, options = {}) {
        // Add accessToken to headers if it exists
        const token = authService.getAccessToken();
        const headers = {
            ...options.headers,
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        let response = await fetch(url, { ...options, headers });

        // If 401 Unauthorized, try to refresh the access token
        if (response.status === 401) {
            try {
                const newToken = await authService.refresh();
                
                // Retry the original request with the new token
                headers['Authorization'] = `Bearer ${newToken}`;
                response = await fetch(url, { ...options, headers });
            } catch (error) {
                // If refresh fails, force logout
                await authService.logout();
                throw error;
            }
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Request failed with status ${response.status}`);
        }

        // Handle empty response body (e.g., status 204 No Content)
        if (response.status === 204 || response.headers.get('Content-Length') === '0') {
            return null;
        }

        return response.json();
    }

    get(url) {
        return this.fetch(url, { method: 'GET' });
    }

    post(url, body) {
        return this.fetch(url, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    }
}

export const apiClient = new ApiClient();
