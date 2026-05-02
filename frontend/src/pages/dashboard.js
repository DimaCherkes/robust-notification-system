import { apiClient } from '../api/apiClient.js';

export const Dashboard = {
    render: async () => {
        return `
            <section id="dashboard">
                <div class="dashboard-header">
                    <h2>Your Weather Subscriptions</h2>
                    <button id="add-sub-btn" class="primary-btn">+ New Subscription</button>
                </div>
                
                <div id="subscription-list" class="subscription-grid">
                    <p class="loading">Loading your subscriptions...</p>
                </div>

                <!-- Modal for creating subscription (Simplified for now) -->
                <div id="sub-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Create Subscription</h3>
                        <form id="sub-form">
                            <div class="form-group">
                                <label>City ID (Temporary)</label>
                                <input type="number" id="cityId" placeholder="e.g. 1" required>
                            </div>
                            <div class="form-group">
                                <label>Notify Before (Hours)</label>
                                <input type="number" id="notifyHours" value="2" required>
                            </div>
                            <p class="info-text">Default rules (Temp < 0, Rain = 1) will be applied.</p>
                            <div class="modal-actions">
                                <button type="button" id="close-modal" class="secondary-btn">Cancel</button>
                                <button type="submit" class="primary-btn">Save</button>
                            </div>
                        </form>
                    </div>
                </div>
            </section>
        `;
    },
    afterRender: async () => {
        const listEl = document.getElementById('subscription-list');
        const modal = document.getElementById('sub-modal');
        const addBtn = document.getElementById('add-sub-btn');
        const closeBtn = document.getElementById('close-modal');
        const form = document.getElementById('sub-form');

        // Logic to open/close modal
        addBtn.onclick = () => modal.classList.remove('hidden');
        closeBtn.onclick = () => modal.classList.add('hidden');

        // Helper to format rule strings
        const formatRule = (rule) => {
            const opMap = {
                'LESS_THAN': '<',
                'GREATER_THAN': '>',
                'EQUALS': '=',
                'BETWEEN': 'between'
            };
            const op = opMap[rule.operator] || rule.operator;
            const val = rule.value2 ? `${rule.value1} and ${rule.value2}` : rule.value1;
            return `<strong>${rule.parameterType}</strong> ${op} ${val}`;
        };

        // Fetch subscription list
        const loadSubscriptions = async () => {
            try {
                const subs = await apiClient.get('/api/v1/subscriptions/all');
                
                if (!subs || subs.length === 0) {
                    listEl.innerHTML = `
                        <div class="empty-state">
                            <p>You don't have any subscriptions yet.</p>
                            <p>Stay ahead of the weather by creating one!</p>
                        </div>
                    `;
                    return;
                }

                listEl.innerHTML = subs.map(sub => `
                    <div class="sub-card">
                        <div class="sub-card-header">
                            <h3>${sub.cityName}</h3>
                            <span class="badge">Every ${sub.notifyBeforeHours}h</span>
                        </div>
                        <div class="sub-rules">
                            <p class="rule-title">Rules:</p>
                            <ul>
                                ${sub.rules.map(rule => `<li>${formatRule(rule)}</li>`).join('')}
                            </ul>
                        </div>
                        <div class="sub-card-footer">
                            <small>Created: ${new Date(sub.createdAt).toLocaleDateString()}</small>
                        </div>
                    </div>
                `).join('');

            } catch (err) {
                listEl.innerHTML = `<p class="error">Failed to load subscriptions: ${err.message}</p>`;
            }
        };

        await loadSubscriptions();

        // Handle form submission
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const payload = {
                cityId: parseInt(document.getElementById('cityId').value),
                notifyBeforeHours: parseInt(document.getElementById('notifyHours').value),
                rules: [
                    { parameterType: 'TEMPERATURE', operator: 'LESS_THAN', value1: 0 },
                    { parameterType: 'RAIN', operator: 'EQUALS', value1: 1 }
                ]
            };

            try {
                await apiClient.post('/api/v1/subscriptions/create', payload);
                modal.classList.add('hidden');
                form.reset();
                await loadSubscriptions();
            } catch (err) {
                alert('Error creating subscription: ' + err.message);
            }
        });
    }
};
