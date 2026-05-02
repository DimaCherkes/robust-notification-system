import { authService } from '../services/authService.js';

export const Navigation = () => {
    const isAuth = authService.isAuthenticated();
    
    return `
        <header>
            <h1>Robust Notification System</h1>
            <nav id="nav">
                ${isAuth ? `
                    <a href="#/dashboard">Dashboard</a>
                    <button id="logout-btn">Logout</button>
                ` : `
                    <a href="#/login">Login</a>
                    <a href="#/register">Register</a>
                `}
            </nav>
        </header>
    `;
};

/**
 * Since navigation is re-rendered on every route change, 
 * we use global event delegation for the logout button.
 */
document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-btn') {
        authService.logout();
    }
});
