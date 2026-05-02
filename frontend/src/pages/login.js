import { authService } from '../services/authService.js';

export const Login = {
    render: async () => {
        return `
            <section class="auth-container">
                <h2>Login</h2>
                <form id="login-form">
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" id="email" required>
                    </div>
                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" id="password" required>
                    </div>
                    <button type="submit">Sign In</button>
                </form>
                <p>Don't have an account? <a href="#/register">Register here</a></p>
                <div id="error-message" class="error"></div>
            </section>
        `;
    },
    afterRender: async () => {
        document.getElementById('login-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const errorEl = document.getElementById('error-message');

            try {
                await authService.login(email, password);
                window.location.hash = '#/dashboard';
            } catch (err) {
                errorEl.textContent = 'Invalid credentials. Please try again.';
            }
        });
    }
};
