import { authService } from './services/authService.js';
import { Login } from './pages/login.js';
import { Register } from './pages/register.js';
import { Dashboard } from './pages/dashboard.js';
import { Navigation } from './components/navigation.js';

const routes = {
    '#/login': { component: Login, private: false },
    '#/register': { component: Register, private: false },
    '#/dashboard': { component: Dashboard, private: true },
};

async function router() {
    const content = document.getElementById('main-content');
    const navContainer = document.getElementById('nav-container');
    const hash = window.location.hash || '#/login';

    const route = routes[hash] || routes['#/login'];

    // Route Guard: Redirect to login if route is private and user is not authenticated
    if (route.private && !authService.isAuthenticated()) {
        window.location.hash = '#/login';
        return;
    }

    // Render navigation component
    navContainer.innerHTML = Navigation();

    // Render page component
    content.innerHTML = await route.component.render();
    if (route.component.afterRender) {
        await route.component.afterRender();
    }
}

// Listen for hash changes and page load
window.addEventListener('hashchange', router);
window.addEventListener('load', router);
