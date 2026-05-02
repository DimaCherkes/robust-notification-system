import { authService } from '../services/authService.js';

export const Register = {
    render: async () => {
        return `
            <section class="auth-container">
                <h2>Register</h2>
                <form id="register-form">
                    <div class="form-group">
                        <label>Username</label>
                        <input type="text" id="username" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" id="email" required>
                    </div>
                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" id="password" required>
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <input type="password" id="confirm-password" required>
                    </div>
                    <button type="submit">Create Account</button>
                </form>
                <p>Already have an account? <a href="#/login">Login here</a></p>
                <div id="error-message" class="error"></div>
                <div id="success-message" class="success"></div>
            </section>
        `;
    },
    afterRender: async () => {
        document.getElementById('register-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            
            const errorEl = document.getElementById('error-message');
            const successEl = document.getElementById('success-message');

            // Очистка сообщений
            errorEl.textContent = '';
            successEl.textContent = '';

            // Валидация на фронтенде
            if (password !== confirmPassword) {
                errorEl.textContent = 'Passwords do not match!';
                return;
            }

            if (password.length < 3) { // Пример минимальной длины
                errorEl.textContent = 'Password must be at least 3 characters long';
                return;
            }

            try {
                // Отправляем объект, соответствующий RegistrationUserRequest на бэкенде
                await authService.register({ 
                    username, 
                    email, 
                    password, 
                    confirmPassword 
                });
                
                successEl.textContent = 'Registration successful! Redirecting to login...';
                setTimeout(() => {
                    window.location.hash = '#/login';
                }, 2000);
            } catch (err) {
                // Если бэкенд вернул ошибку валидации (например, Map с ошибками)
                errorEl.textContent = 'Registration failed: ' + err.message;
            }
        });
    }
};
