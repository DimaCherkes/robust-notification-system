import { apiClient } from '../api/apiClient.js';

export const Dashboard = {
    render: async () => {
        return `
            <section id="dashboard">
                <h2>Your Subscriptions</h2>
                <div id="subscription-list" class="loading">Loading subscriptions...</div>
                
                <hr>
                
                <h3>Create New Subscription</h3>
                <form id="sub-form">
                    <div class="form-group">
                        <label>Location (City)</label>
                        <input type="text" id="location" placeholder="e.g. London" required>
                    </div>
                    <button type="submit">Subscribe</button>
                </form>
            </section>
        `;
    },
    afterRender: async () => {
        const listEl = document.getElementById('subscription-list');
        const form = document.getElementById('sub-form');

        // Загрузка списка
        const loadSubscriptions = async () => {
            try {
                // В subscription-service нет явного GET для списка всех подписок в SubscriptionController, 
                // возможно он в другом месте или нужно добавить. 
                // Пока используем /api/v1/subscriptions как базовый.
                const subs = await apiClient.get('/api/v1/subscriptions');
                if (!subs || subs.length === 0) {
                    listEl.innerHTML = '<p>No subscriptions found. Create your first one!</p>';
                } else {
                    listEl.innerHTML = `<ul>${subs.map(s => `<li>${s.location} - ${s.status}</li>`).join('')}</ul>`;
                }
            } catch (err) {
                listEl.innerHTML = `<p class="error">Error loading subscriptions: ${err.message}</p>`;
            }
        };

        await loadSubscriptions();

        // Создание новой
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const location = document.getElementById('location').value;
            try {
                await apiClient.post('/api/v1/subscriptions/create', { location });
                document.getElementById('location').value = '';
                await loadSubscriptions();
            } catch (err) {
                alert('Failed to create subscription: ' + err.message);
            }
        });
    }
};
