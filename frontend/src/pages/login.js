import { authService } from '../services/authService.js';

export const Login = {
    render: async () => {
        return `
            <div class="max-w-md mx-auto bg-white rounded-xl shadow-sm border border-slate-200 p-8">
                <h2 class="text-2xl font-bold text-slate-800 mb-6 text-center">Login</h2>
                <form id="login-form" class="space-y-4">
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Email</label>
                        <input type="email" id="email" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Password</label>
                        <input type="password" id="password" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all">
                    </div>
                    <button type="submit" class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 active:scale-[0.98] transition-all">
                        Sign In
                    </button>
                </form>
                <div class="mt-6 text-center">
                    <p class="text-sm text-slate-600">Don't have an account? <a href="#/register" class="text-blue-600 font-semibold hover:underline">Register here</a></p>
                </div>
                <div id="error-message" class="mt-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm text-center hidden border border-red-100"></div>
            </div>
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
                errorEl.classList.remove('hidden');
            }
        });
    }
};
