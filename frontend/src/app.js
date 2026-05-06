import { authService } from './services/authService.js';
import { Login } from './pages/login.js';
import { Register } from './pages/register.js';
import { Dashboard } from './pages/dashboard.js';
import { CreateSubscription } from './pages/createSubscription.js';
import { UpdateSubscription } from './pages/updateSubscription.js';
import { Navigation } from './components/navigation.js';

const routes = {
    '#/login': { component: Login, private: false },
    '#/register': { component: Register, private: false },
    '#/dashboard': { component: Dashboard, private: true },
    '#/subscriptions/create': { component: CreateSubscription, private: true },
    '#/subscriptions/edit': { component: UpdateSubscription, private: true },
};

async function router() {
    const content = document.getElementById('main-content');
    const navContainer = document.getElementById('nav-container');
    let hash = window.location.hash || '#/login';

    // Handle dynamic routes like #/subscriptions/edit/UUID
    let routeKey = hash;
    let params = null;

    if (hash.startsWith('#/subscriptions/edit/')) {
        routeKey = '#/subscriptions/edit';
        params = hash.replace('#/subscriptions/edit/', '');
    }

    const route = routes[routeKey] || routes['#/login'];

    // Route Guard: Redirect to login if route is private and user is not authenticated
    if (route.private && !authService.isAuthenticated()) {
        try {
            await authService.refresh();
        } catch (error) {
            window.location.hash = '#/login';
            return;
        }
    }

    // Render navigation component
    navContainer.innerHTML = Navigation();

    // Render page component
    content.innerHTML = await route.component.render(params);
    if (route.component.afterRender) {
        await route.component.afterRender(params);
    }
}

// Listen for hash changes and page load
window.addEventListener('hashchange', router);
window.addEventListener('load', router);
