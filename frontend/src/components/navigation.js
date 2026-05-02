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

// Мы не можем использовать afterRender для навигации так же просто, 
// так как она перерендеривается при каждом переходе. 
// Поэтому добавим глобальный делегат для кнопки Logout.
document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-btn') {
        authService.logout();
    }
});
