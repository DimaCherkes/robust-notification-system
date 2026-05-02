import { apiClient } from '../api/apiClient.js';

export const Dashboard = {
    render: async () => {
        return `
            <section id="dashboard">
                <div class="dashboard-header">
                    <h2>Your Weather Subscriptions</h2>
                    <a href="#/subscriptions/create" class="primary-btn">+ New Subscription</a>
                </div>
                
                <div id="subscription-list" class="subscription-grid">
                    <p class="loading">Loading your subscriptions...</p>
                </div>

                <!-- Custom Confirmation Modal -->
                <div id="confirm-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Confirm Deletion</h3>
                        <p>Are you sure you want to permanently delete this subscription? This action cannot be undone.</p>
                        <div class="modal-actions">
                            <button id="cancel-delete" class="secondary-btn">Cancel</button>
                            <button id="confirm-delete" class="danger-btn">Delete</button>
                        </div>
                    </div>
                </div>
            </section>
        `;
    },
    afterRender: async () => {
        const listEl = document.getElementById('subscription-list');
        const confirmModal = document.getElementById('confirm-modal');
        const confirmBtn = document.getElementById('confirm-delete');
        const cancelBtn = document.getElementById('cancel-delete');
        
        let subToDelete = null;

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

        const loadSubscriptions = async () => {
            try {
                const subs = await apiClient.get('/api/v1/subscriptions/all');
                
                if (!subs || subs.length === 0) {
                    listEl.innerHTML = `
                        <div class="empty-state">
                            <p>You don't have any subscriptions yet.</p>
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
                            <div class="card-actions">
                                <a href="#/subscriptions/edit/${sub.id}" class="edit-link">Edit</a>
                                <button class="delete-link-btn" data-id="${sub.id}">Delete</button>
                            </div>
                        </div>
                    </div>
                `).join('');

                // Add listeners to delete buttons
                document.querySelectorAll('.delete-link-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToDelete = btn.dataset.id;
                        confirmModal.classList.remove('hidden');
                    };
                });

            } catch (err) {
                listEl.innerHTML = `<p class="error">Failed to load subscriptions: ${err.message}</p>`;
            }
        };

        // Modal actions
        cancelBtn.onclick = () => {
            confirmModal.classList.add('hidden');
            subToDelete = null;
        };

        confirmBtn.onclick = async () => {
            if (subToDelete) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/hard/${subToDelete}`, { method: 'DELETE' });
                    confirmModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) {
                    alert('Delete failed: ' + err.message);
                }
            }
        };

        await loadSubscriptions();
    }
};
