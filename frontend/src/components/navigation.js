import { authService } from '../services/authService.js';

export const Navigation = () => {
    const isAuth = authService.isAuthenticated();
    
    return `
        <header class="flex items-center justify-between py-6 border-b border-slate-200">
            <div class="flex items-center space-x-3">
                <div class="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-200">
                    <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z" /></svg>
                </div>
                <h1 class="text-xl font-black text-slate-800 tracking-tight">Robust Notifications</h1>
            </div>
            
            <nav class="flex items-center space-x-1">
                ${isAuth ? `
                    <a href="#/dashboard" class="px-4 py-2 text-sm font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all">Dashboard</a>
                    <a href="#/profile" class="px-4 py-2 text-sm font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all">Profile</a>
                    <button id="logout-btn" class="px-4 py-2 text-sm font-bold text-red-500 hover:bg-red-50 rounded-lg transition-all">Logout</button>
                ` : `
                    <a href="#/login" class="px-4 py-2 text-sm font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all">Login</a>
                    <a href="#/register" class="ml-2 px-5 py-2 text-sm font-bold bg-slate-900 text-white rounded-lg hover:bg-slate-800 shadow-sm transition-all active:scale-95">Register</a>
                `}
            </nav>
        </header>
    `;
};

document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-btn') {
        authService.logout();
    }
});
