import { authService } from '../services/authService.js';

class ApiClient {
    async fetch(url, options = {}) {
        // Добавляем accessToken в заголовки, если он есть
        const token = authService.getAccessToken();
        const headers = {
            ...options.headers,
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        let response = await fetch(url, { ...options, headers });

        // Если получили 401, пробуем обновить токен
        if (response.status === 401) {
            try {
                const newToken = await authService.refresh();
                
                // Повторяем запрос с новым токеном
                headers['Authorization'] = `Bearer ${newToken}`;
                response = await fetch(url, { ...options, headers });
            } catch (error) {
                // Если refresh не удался, разлогиниваем
                authService.logout();
                throw error;
            }
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Request failed with status ${response.status}`);
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
