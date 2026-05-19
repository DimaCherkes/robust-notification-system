import { apiClient } from '../api/apiClient.js';

export const Dashboard = {
    render: async () => {
        return `
            <section id="dashboard">
                <div class="dashboard-header" style="margin-top: 2rem;">
                    <h2>Your Weather Subscriptions</h2>
                    <a href="#/subscriptions/create" class="primary-btn">+ New Subscription</a>
                </div>
                
                <div id="subscription-list" class="subscription-grid">
                    <p style="text-align: center; color: var(--text-muted);">Loading your subscriptions...</p>
                </div>

                <!-- Fix: Modal structure aligned with style.css -->
                <div id="confirm-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Confirm Deletion</h3>
                        <p>Are you sure you want to permanently delete this subscription?</p>
                        <div style="display:flex; gap:1rem; margin-top:1.5rem; justify-content:center;">
                            <button id="cancel-delete" class="secondary-btn">Cancel</button>
                            <button id="confirm-delete" class="primary-btn" style="background:var(--danger-text);">Delete</button>
                        </div>
                    </div>
                </div>

                <div id="deactivate-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Deactivate Subscription</h3>
                        <p>Stop receiving notifications for this city?</p>
                        <div style="display:flex; gap:1rem; margin-top:1.5rem; justify-content:center;">
                            <button id="cancel-deactivate" class="secondary-btn">Cancel</button>
                            <button id="confirm-deactivate" class="primary-btn" style="background:var(--text-muted);">Deactivate</button>
                        </div>
                    </div>
                </div>

                <div id="activate-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Activate Subscription</h3>
                        <p>Start receiving notifications again?</p>
                        <div style="display:flex; gap:1rem; margin-top:1.5rem; justify-content:center;">
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

        const formatRule = (rule) => {
            const opMap = {
                'LESS_THAN': '<',
                'GREATER_THAN': '>',
                'EQUALS': '=',
                'BETWEEN': 'between'
            };
            const op = opMap[rule.operator] || rule.operator;
            const val = rule.value2 ? `${rule.value1} and ${rule.value2}` : rule.value1;
            return `<strong>${rule.parameterType.toUpperCase()}</strong> ${op} ${val}`;
        };

        const loadSubscriptions = async () => {
            try {
                const subs = await apiClient.get('/api/v1/subscriptions/all');
                
                if (!subs || subs.length === 0) {
                    listEl.innerHTML = `
                        <div style="text-align: center; padding: 3rem; color: var(--text-muted);">
                            <p>You don't have any subscriptions yet.</p>
                        </div>
                    `;
                    return;
                }

                listEl.innerHTML = subs.map(sub => `
                    <div class="sub-card">
                        <div class="sub-card-header">
                            <div>
                                <h3 class="city-name">${sub.cityName}</h3>
                                <span class="status-badge" style="${!sub.isActive ? 'background:#f1f5f9; color:#64748b;' : ''}">
                                    ${sub.isActive ? 'ACTIVE' : 'DEACTIVATED'}
                                </span>
                            </div>
                            <span class="notify-pill">Notify ${sub.notifyBeforeHours}h before</span>
                        </div>
                        
                        <div style="margin: 1.5rem 0;">
                            <p style="font-size: 0.9rem; font-weight: 600; color: var(--text-muted); margin-bottom: 0.5rem;">Rules:</p>
                            ${sub.rules.map(rule => `
                                <div class="rule-item">
                                    ${formatRule(rule)}
                                </div>
                            `).join('')}
                        </div>
                        
                        <div style="font-size: 0.85rem; color: var(--text-muted);">
                            Created: ${new Date(sub.createdAt).toLocaleDateString()}
                        </div>
                        
                        <div class="card-actions">
                            <a href="#/subscriptions/edit/${sub.id}" class="action-link">Edit</a>
                            ${sub.isActive 
                                ? `<span class="action-link muted deactivate-btn" data-id="${sub.id}">Deactivate</span>` 
                                : `<span class="action-link activate-btn" data-id="${sub.id}">Activate</span>`}
                            <span class="action-link danger delete-btn" data-id="${sub.id}">Delete</span>
                        </div>
                    </div>
                `).join('');

                document.querySelectorAll('.delete-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToProcess = btn.dataset.id;
                        confirmModal.classList.remove('hidden');
                    };
                });

                document.querySelectorAll('.deactivate-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToProcess = btn.dataset.id;
                        deactivateModal.classList.remove('hidden');
                    };
                });

                document.querySelectorAll('.activate-btn').forEach(btn => {
                    btn.onclick = () => {
                        subToProcess = btn.dataset.id;
                        activateModal.classList.remove('hidden');
                    };
                });

            } catch (err) {
                listEl.innerHTML = `<p class="error-msg">Failed to load subscriptions: ${err.message}</p>`;
            }
        };

        // Modal actions
        document.getElementById('cancel-delete').onclick = () => confirmModal.classList.add('hidden');
        document.getElementById('confirm-delete').onclick = async () => {
            if (subToProcess) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/hard/${subToProcess}`, { method: 'DELETE' });
                    confirmModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) { alert(err.message); }
            }
        };

        document.getElementById('cancel-deactivate').onclick = () => deactivateModal.classList.add('hidden');
        document.getElementById('confirm-deactivate').onclick = async () => {
            if (subToProcess) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/soft/${subToProcess}`, { method: 'DELETE' });
                    deactivateModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) { alert(err.message); }
            }
        };

        document.getElementById('cancel-activate').onclick = () => activateModal.classList.add('hidden');
        document.getElementById('confirm-activate').onclick = async () => {
            if (subToProcess) {
                try {
                    await apiClient.fetch(`/api/v1/subscriptions/${subToProcess}/activate`, { method: 'PUT' });
                    activateModal.classList.add('hidden');
                    await loadSubscriptions();
                } catch (err) { alert(err.message); }
            }
        };

        await loadSubscriptions();
    }
};
