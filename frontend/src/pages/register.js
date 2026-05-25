import { authService } from '../services/authService.js';

export const Register = {
    render: async () => {
        return `
            <div class="max-w-md mx-auto bg-white rounded-xl shadow-sm border border-slate-200 p-8">
                <h2 class="text-2xl font-bold text-slate-800 mb-6 text-center">Register</h2>
                <form id="register-form" class="space-y-4">
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Username</label>
                        <input type="text" id="username" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Email</label>
                        <input type="email" id="email" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Password</label>
                        <input type="password" id="password" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-slate-700 mb-1">Confirm Password</label>
                        <input type="password" id="confirm-password" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                    <button type="submit" class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 active:scale-[0.98]">
                        Create Account
                    </button>
                </form>
                <div class="mt-6 text-center">
                    <p class="text-sm text-slate-600">Already have an account? <a href="#/login" class="text-blue-600 font-semibold hover:underline">Login here</a></p>
                </div>
                <div id="error-message" class="mt-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm text-center hidden border border-red-100"></div>
                <div id="success-message" class="mt-4 p-3 rounded-lg bg-green-50 text-green-600 text-sm text-center hidden border border-green-100"></div>
            </div>
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

            errorEl.classList.add('hidden');
            successEl.classList.add('hidden');

            if (password !== confirmPassword) {
                errorEl.textContent = 'Passwords do not match!';
                errorEl.classList.remove('hidden');
                return;
            }

            try {
                // 1. Register the user
                await authService.register({ username, email, password, confirmPassword });
                successEl.textContent = 'Registration successful! Logging you in...';
                successEl.classList.remove('hidden');

                // 2. Perform login immediately
                await authService.login(email, password);

                // 3. Redirect to Dashboard
                setTimeout(() => {
                    window.location.hash = '#/dashboard';
                }, 1000);
                
            } catch (err) {
                errorEl.textContent = 'Action failed: ' + err.message;
                errorEl.classList.remove('hidden');
            }
        });
    }
};
