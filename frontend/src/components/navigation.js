import { authService } from '../services/authService.js';

export const Navigation = () => {
    const isAuth = authService.isAuthenticated();
    
    return `
        <header class="relative py-6 border-b border-slate-200">
            <div class="flex items-center justify-between">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-200">
                        <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z" /></svg>
                    </div>
                    <h1 class="text-xl font-black text-slate-800 tracking-tight">Robust Notifications</h1>
                </div>
                
                <nav class="desktop-nav items-center space-x-1">
                    ${isAuth ? `
                        <a href="#/dashboard" class="px-4 py-2 text-sm font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all">Dashboard</a>
                        <a href="#/profile" class="px-4 py-2 text-sm font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all">Profile</a>
                        <button id="logout-btn" class="px-4 py-2 text-sm font-bold text-red-500 hover:bg-red-50 rounded-lg transition-all">Logout</button>
                    ` : `
                        <a href="#/login" class="px-4 py-2 text-sm font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all">Login</a>
                        <a href="#/register" class="ml-2 px-5 py-2 text-sm font-bold bg-slate-900 text-white rounded-lg hover:bg-slate-800 shadow-sm transition-all active:scale-95">Register</a>
                    `}
                </nav>

                <button id="mobile-menu-btn" class="mobile-toggle p-2 text-slate-600 hover:bg-slate-100 rounded-xl transition-colors focus:outline-none items-center justify-center">
                    <svg id="hamburger-icon" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7" />
                    </svg>
                    <svg id="close-icon" class="w-6 h-6 hidden" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>

            <div id="mobile-menu" class="hidden absolute top-full left-0 right-0 z-50 bg-white/95 backdrop-blur-xl border-b border-slate-100 shadow-xl shadow-slate-200/50 animate-fadeIn origin-top">
                <nav class="flex flex-col p-4 space-y-2">
                    ${isAuth ? `
                        <a href="#/dashboard" class="mobile-nav-link px-4 py-4 text-base font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-2xl transition-all">Dashboard</a>
                        <a href="#/profile" class="mobile-nav-link px-4 py-4 text-base font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-2xl transition-all">Profile</a>
                        <button id="mobile-logout-btn" class="w-full text-left px-4 py-4 text-base font-bold text-red-500 hover:bg-red-50 rounded-2xl transition-all">Logout</button>
                    ` : `
                        <a href="#/login" class="mobile-nav-link px-4 py-4 text-base font-bold text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-2xl transition-all">Login</a>
                        <a href="#/register" class="mobile-nav-link px-4 py-4 text-base font-bold bg-slate-900 text-white rounded-2xl text-center shadow-lg transition-all active:scale-95">Register</a>
                    `}
                </nav>
            </div>
        </header>
    `;
};

document.addEventListener('click', (e) => {
    const mobileMenu = document.getElementById('mobile-menu');
    const hamburgerIcon = document.getElementById('hamburger-icon');
    const closeIcon = document.getElementById('close-icon');
    
    if (!mobileMenu) return;

    const menuBtn = e.target.closest('#mobile-menu-btn');

    if (menuBtn) {
        const isHidden = mobileMenu.classList.contains('hidden');
        if (isHidden) {
            mobileMenu.classList.remove('hidden');
            hamburgerIcon?.classList.add('hidden');
            closeIcon?.classList.remove('hidden');
        } else {
            mobileMenu.classList.add('hidden');
            hamburgerIcon?.classList.remove('hidden');
            closeIcon?.classList.add('hidden');
        }
    }

    if (e.target.closest('.mobile-nav-link') || e.target.id === 'mobile-logout-btn') {
        mobileMenu.classList.add('hidden');
        hamburgerIcon?.classList.remove('hidden');
        closeIcon?.classList.add('hidden');
    }

    if (e.target.id === 'logout-btn' || e.target.id === 'mobile-logout-btn') {
        authService.logout();
    }
});
