import { apiClient } from '../api/apiClient.js';

export const Dashboard = {
    render: async () => {
        return `
            <div class="space-y-8">
                <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <h2 class="text-3xl font-extrabold text-slate-800 tracking-tight">Your Subscriptions</h2>
                    <a href="#/subscriptions/create" class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-semibold rounded-xl hover:bg-blue-700 shadow-sm transition-all active:scale-[0.98]">
                        <span class="mr-2 text-xl">+</span> New Subscription
                    </a>
                </div>
                
                <div id="subscription-list" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div class="col-span-full py-12 text-center text-slate-500 animate-pulse">Loading your subscriptions...</div>
                </div>

                <!-- Hard Delete Modal -->
                <div id="confirm-modal" class="modal fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm hidden">
                    <div class="bg-white rounded-2xl shadow-xl border border-slate-200 max-w-sm w-full p-8">
                        <div class="w-12 h-12 bg-red-100 text-red-600 rounded-full flex items-center justify-center mb-4 mx-auto">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-4v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                        </div>
                        <h3 class="text-xl font-bold text-slate-900 text-center mb-2">Delete Subscription?</h3>
                        <p class="text-slate-600 text-center mb-8">This action is permanent and cannot be undone.</p>
                        <div class="flex flex-col gap-3">
                            <button id="confirm-delete" class="w-full bg-red-600 text-white font-semibold py-2.5 rounded-xl hover:bg-red-700 transition-colors">Delete Permanently</button>
                            <button id="cancel-delete" class="w-full bg-slate-100 text-slate-700 font-semibold py-2.5 rounded-xl hover:bg-slate-200 transition-colors">Cancel</button>
                        </div>
                    </div>
                </div>

                <!-- Soft Delete Modal -->
                <div id="deactivate-modal" class="modal fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm hidden">
                    <div class="bg-white rounded-2xl shadow-xl border border-slate-200 max-w-sm w-full p-8">
                        <div class="w-12 h-12 bg-amber-100 text-amber-600 rounded-full flex items-center justify-center mb-4 mx-auto">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 9v6m4-6v6m7-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                        </div>
                        <h3 class="text-xl font-bold text-slate-900 text-center mb-2">Deactivate?</h3>
                        <p class="text-slate-600 text-center mb-8">You will stop receiving notifications for this city.</p>
                        <div class="flex flex-col gap-3">
                            <button id="confirm-deactivate" class="w-full bg-amber-600 text-white font-semibold py-2.5 rounded-xl hover:bg-amber-700 transition-colors">Deactivate</button>
                            <button id="cancel-deactivate" class="w-full bg-slate-100 text-slate-700 font-semibold py-2.5 rounded-xl hover:bg-slate-200 transition-colors">Keep Active</button>
                        </div>
                    </div>
                </div>

                <!-- Activate Modal -->
                <div id="activate-modal" class="modal fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm hidden">
                    <div class="bg-white rounded-2xl shadow-xl border border-slate-200 max-w-sm w-full p-8">
                        <div class="w-12 h-12 bg-green-100 text-green-600 rounded-full flex items-center justify-center mb-4 mx-auto">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                        </div>
                        <h3 class="text-xl font-bold text-slate-900 text-center mb-2">Activate Subscription?</h3>
                        <p class="text-slate-600 text-center mb-8">We will resume weather monitoring and notifications.</p>
                        <div class="flex flex-col gap-3">
                            <button id="confirm-activate" class="w-full bg-green-600 text-white font-semibold py-2.5 rounded-xl hover:bg-green-700 transition-colors">Activate Now</button>
                            <button id="cancel-activate" class="w-full bg-slate-100 text-slate-700 font-semibold py-2.5 rounded-xl hover:bg-slate-200 transition-colors">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    },
    afterRender: async () => {
        const listEl = document.getElementById('subscription-list');
        const confirmModal = document.getElementById('confirm-modal');
        const deactivateModal = document.getElementById('deactivate-modal');
        const activateModal = document.getElementById('activate-modal');
        
        let subToProcess = null;

        const formatRule = (rule) => {
            const opMap = { 'LESS_THAN': '<', 'GREATER_THAN': '>', 'EQUALS': '=', 'BETWEEN': 'between' };
            const op = opMap[rule.operator] || rule.operator;
            const val = rule.value2 ? `${rule.value1} and ${rule.value2}` : rule.value1;
            return `<span class="font-bold text-slate-700">${rule.parameterType}</span> ${op} ${val}`;
        };

        const loadSubscriptions = async () => {
            try {
                const subs = await apiClient.get('/api/v1/subscriptions/all');
                
                if (!subs || subs.length === 0) {
                    listEl.innerHTML = `
                        <div class="col-span-full bg-white rounded-2xl p-12 text-center border border-dashed border-slate-300">
                            <p class="text-slate-500 mb-4 text-lg font-medium">You don't have any subscriptions yet.</p>
                            <a href="#/subscriptions/create" class="text-blue-600 font-bold hover:underline">Create your first one &rarr;</a>
                        </div>
                    `;
                    return;
                }

                listEl.innerHTML = subs.map(sub => `
                    <div class="group bg-white rounded-2xl border ${sub.isActive ? 'border-slate-200' : 'border-slate-200 bg-slate-50/50'} shadow-sm hover:shadow-md transition-all p-6 relative flex flex-col h-full">
                        <div class="flex items-start justify-between mb-4">
                            <div>
                                <h3 class="text-xl font-bold ${sub.isActive ? 'text-slate-800' : 'text-slate-500'}">${sub.cityName}</h3>
                                <span class="inline-flex mt-2 items-center px-2.5 py-0.5 rounded-full text-xs font-bold uppercase tracking-wider ${sub.isActive ? 'bg-green-100 text-green-700' : 'bg-slate-200 text-slate-600'}">
                                    ${sub.isActive ? 'Active' : 'Deactivated'}
                                </span>
                            </div>
                            <div class="px-3 py-1.5 bg-blue-50 text-blue-700 rounded-lg text-xs font-bold">
                                ${sub.notifyBeforeHours}h before
                            </div>
                        </div>

                        <div class="flex-grow space-y-3 mb-6">
                            <p class="text-xs font-bold text-slate-400 uppercase tracking-widest">Weather Conditions</p>
                            <ul class="space-y-2">
                                ${sub.rules.map(rule => `
                                    <li class="text-sm text-slate-600 bg-slate-50 px-3 py-2 rounded-lg border border-slate-100 flex items-center">
                                        <div class="w-1.5 h-1.5 rounded-full bg-blue-400 mr-3"></div>
                                        ${formatRule(rule)}
                                    </li>
                                `).join('')}
                            </ul>
                        </div>

                        <div class="flex items-center justify-between pt-4 border-t border-slate-100">
                            <span class="text-xs text-slate-400">${new Date(sub.createdAt).toLocaleDateString()}</span>
                            <div class="flex gap-3">
                                <a href="#/subscriptions/edit/${sub.id}" class="text-xs font-bold text-blue-600 hover:text-blue-800 uppercase tracking-wider">Edit</a>
                                ${sub.isActive 
                                    ? `<button class="deactivate-link-btn text-xs font-bold text-amber-600 hover:text-amber-800 uppercase tracking-wider" data-id="${sub.id}">Deactivate</button>` 
                                    : `<button class="activate-link-btn text-xs font-bold text-green-600 hover:text-green-800 uppercase tracking-wider" data-id="${sub.id}">Activate</button>`}
                                <button class="delete-link-btn text-xs font-bold text-red-600 hover:text-red-800 uppercase tracking-wider" data-id="${sub.id}">Delete</button>
                            </div>
                        </div>
                    </div>
                `).join('');

                // Event Listeners
                document.querySelectorAll('.delete-link-btn').forEach(btn => btn.onclick = () => { subToProcess = btn.dataset.id; confirmModal.classList.remove('hidden'); });
                document.querySelectorAll('.deactivate-link-btn').forEach(btn => btn.onclick = () => { subToProcess = btn.dataset.id; deactivateModal.classList.remove('hidden'); });
                document.querySelectorAll('.activate-link-btn').forEach(btn => btn.onclick = () => { subToProcess = btn.dataset.id; activateModal.classList.remove('hidden'); });

            } catch (err) {
                listEl.innerHTML = `<p class="text-red-500 text-center col-span-full font-bold">Failed to load subscriptions: ${err.message}</p>`;
            }
        };

        const closeModal = (modal) => { modal.classList.add('hidden'); subToProcess = null; };

        document.getElementById('cancel-delete').onclick = () => closeModal(confirmModal);
        document.getElementById('cancel-deactivate').onclick = () => closeModal(deactivateModal);
        document.getElementById('cancel-activate').onclick = () => closeModal(activateModal);

        document.getElementById('confirm-delete').onclick = async () => {
            try { await apiClient.fetch(`/api/v1/subscriptions/hard/${subToProcess}`, { method: 'DELETE' }); closeModal(confirmModal); await loadSubscriptions(); } catch (err) { alert('Error: ' + err.message); }
        };
        document.getElementById('confirm-deactivate').onclick = async () => {
            try { await apiClient.fetch(`/api/v1/subscriptions/soft/${subToProcess}`, { method: 'DELETE' }); closeModal(deactivateModal); await loadSubscriptions(); } catch (err) { alert('Error: ' + err.message); }
        };
        document.getElementById('confirm-activate').onclick = async () => {
            try { await apiClient.fetch(`/api/v1/subscriptions/${subToProcess}/activate`, { method: 'PUT' }); closeModal(activateModal); await loadSubscriptions(); } catch (err) { alert('Error: ' + err.message); }
        };

        await loadSubscriptions();
    }
};
