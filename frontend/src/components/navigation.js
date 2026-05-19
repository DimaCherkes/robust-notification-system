import { authService } from '../services/authService.js';

export const Navigation = () => {
    const isAuth = authService.isAuthenticated();
    const isDashboard = window.location.hash === '#/dashboard';

    return `
        <header>
            <h1>Robust Notification System</h1>
            <div class="nav-links">
                ${isAuth ? `
                    <a href="#/dashboard" class="nav-link" style="${isDashboard ? 'color: #9333ea;' : ''}">Dashboard</a>
                    <button id="logout-btn" class="primary-btn logout-btn">Logout</button>
                ` : `
                    <div style="display:flex; gap:1rem;">
                        <a href="#/login" class="nav-link">Login</a>
                        <a href="#/register" class="nav-link">Register</a>
                    </div>
                `}
            </div>
        </header>
    `;
};

document.addEventListener('click', async (e) => {
    if (e.target && e.target.id === 'logout-btn') {
        await authService.logout();
    }
});
