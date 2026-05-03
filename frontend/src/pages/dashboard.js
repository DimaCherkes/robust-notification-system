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

                <!-- Custom Confirmation Modal (Hard Delete) -->
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

                <!-- Custom Deactivation Modal (Soft Delete) -->
                <div id="deactivate-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Deactivate Subscription</h3>
                        <p>Are you sure you want to deactivate this subscription? You will stop receiving notifications, but you can see it in your dashboard.</p>
                        <div class="modal-actions">
                            <button id="cancel-deactivate" class="secondary-btn">Cancel</button>
                            <button id="confirm-deactivate" class="secondary-btn danger">Deactivate</button>
                        </div>
                    </div>
                </div>

                <!-- Custom Activation Modal -->
                <div id="activate-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Activate Subscription</h3>
                        <p>Are you sure you want to reactivate this subscription? You will start receiving notifications again.</p>
                        <div class="modal-actions">
                            <button id="cancel-activate" class="secondary-btn">Cancel</button>
                            <button id="confirm-activate" class="primary-btn">Activate</button>
                        </div>
                    </div>
                </div>
            </section>
        `;
    },
    afterRender: async () => {
        const listEl = document.getElementById('subscription-list');
        const confirmModal = document.getElementById('confirm-modal');
        const deactivateModal = document.getElementById('deactivate-modal');
        const activateModal = document.getElementById('activate-modal');
        
        let subToProcess = null;

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
                    <div class="sub-card ${sub.isActive ? 'active' : 'deactivated'}">
                        <div class="sub-card-header">
                            <div>
                                <h3>${sub.cityName}</h3>
                                <span class="status-indicator">${sub.isActive ? 'Active' : 'Deactivated'}</span>
                            </div>
                            <span class="badge">Notify ${sub.notifyBeforeHours}h before</span>
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
                                ${sub.isActive 
                                    ? `<button class="deactivate-link-btn" data-id="${sub.id}">Deactivate</button>` 
                                    : `<button class="activate-link-btn" data-id="${sub.id}">Activate</button>`}
                                <button class="delete-link-btn" data-id="${sub.id}">Delete</button>
                            </div>
                        </div>
                    </div>
                `).join('');

                // Add listeners to delete buttons
                document.querySelectorAll('.delete-link-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToProcess = btn.dataset.id;
                        confirmModal.classList.remove('hidden');
                    };
                });

                // Add listeners to deactivate buttons
                document.querySelectorAll('.deactivate-link-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToProcess = btn.dataset.id;
                        deactivateModal.classList.remove('hidden');
                    };
                });

                // Add listeners to activate buttons
                document.querySelectorAll('.activate-link-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToProcess = btn.dataset.id;
                        activateModal.classList.remove('hidden');
                    };
                });

            } catch (err) {
                listEl.innerHTML = `<p class="error">Failed to load subscriptions: ${err.message}</p>`;
            }
        };

        // Delete Modal actions
        document.getElementById('cancel-delete').onclick = () => {
            confirmModal.classList.add('hidden');
            subToProcess = null;
        };

        document.getElementById('confirm-delete').onclick = async () => {
            if (subToProcess) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/hard/${subToProcess}`, { method: 'DELETE' });
                    confirmModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) {
                    alert('Delete failed: ' + err.message);
                }
            }
        };

        // Deactivate Modal actions
        document.getElementById('cancel-deactivate').onclick = () => {
            deactivateModal.classList.add('hidden');
            subToProcess = null;
        };

        document.getElementById('confirm-deactivate').onclick = async () => {
            if (subToProcess) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/soft/${subToProcess}`, { method: 'DELETE' });
                    deactivateModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) {
                    alert('Deactivation failed: ' + err.message);
                }
            }
        };

        // Activate Modal actions
        document.getElementById('cancel-activate').onclick = () => {
            activateModal.classList.add('hidden');
            subToProcess = null;
        };

        document.getElementById('confirm-activate').onclick = async () => {
            if (subToProcess) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/${subToProcess}/activate`, { method: 'PUT' });
                    activateModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) {
                    alert('Activation failed: ' + err.message);
                }
            }
        };

        await loadSubscriptions();
    }
};
