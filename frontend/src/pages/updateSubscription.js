import { apiClient } from '../api/apiClient.js';

export const UpdateSubscription = {
    render: async (id) => {
        return `
            <div class="max-w-4xl mx-auto">
                <div class="mb-8">
                    <a href="#/dashboard" class="text-sm font-bold text-slate-400 hover:text-blue-600 uppercase tracking-widest flex items-center transition-colors">
                        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" /></svg>
                        Back to Dashboard
                    </a>
                    <h2 class="text-3xl font-extrabold text-slate-800 mt-4">Update Subscription</h2>
                </div>

                <div id="loading-overlay" class="py-20 text-center text-slate-500 animate-pulse font-bold">Fetching subscription details...</div>

                <form id="update-sub-form" class="space-y-8 hidden">
                    <!-- General Settings -->
                    <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
                        <h3 class="text-lg font-bold text-slate-800 mb-6 flex items-center">
                            <span class="w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center mr-3 text-sm">1</span>
                            General Settings
                        </h3>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label class="block text-sm font-bold text-slate-700 mb-2 uppercase tracking-wide">City</label>
                                <input type="text" id="city-name" disabled class="w-full px-4 py-3 border border-slate-200 rounded-xl bg-slate-50 font-bold text-slate-500 cursor-not-allowed">
                                <p class="text-[10px] text-slate-400 mt-2 font-bold uppercase tracking-wider italic">City cannot be changed</p>
                            </div>
                            <div>
                                <label class="block text-sm font-bold text-slate-700 mb-2 uppercase tracking-wide">Notify Before (Hours)</label>
                                <input type="number" id="notify-before" min="1" max="48" required class="w-full px-4 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-slate-50 font-medium">
                                <p class="text-xs text-slate-400 mt-2 font-medium text-right italic">Current interval in hours</p>
                            </div>
                        </div>
                    </div>

                    <!-- Weather Rules -->
                    <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
                        <div class="flex items-center justify-between mb-6">
                            <h3 class="text-lg font-bold text-slate-800 flex items-center">
                                <span class="w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center mr-3 text-sm">2</span>
                                Weather Rules
                            </h3>
                            <button type="button" id="add-rule-btn" class="px-4 py-2 bg-slate-100 text-slate-700 font-bold rounded-lg hover:bg-slate-200 text-sm transition-colors uppercase tracking-wider">+ Add Rule</button>
                        </div>
                        
                        <div id="rules-container" class="space-y-4">
                            <!-- Rules will be injected here -->
                        </div>
                    </div>

                    <div class="flex justify-end pt-4">
                        <button type="submit" class="px-10 py-4 bg-blue-600 text-white font-extrabold rounded-2xl hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all active:scale-95 uppercase tracking-widest text-sm">
                            Save Changes
                        </button>
                    </div>
                </form>
                <div id="message-container" class="mt-6"></div>
            </div>
        `;
    },

    afterRender: async (id) => {
        if (!id) { window.location.hash = '#/dashboard'; return; }

        const form = document.getElementById('update-sub-form');
        const loadingOverlay = document.getElementById('loading-overlay');
        const rulesContainer = document.getElementById('rules-container');
        const addRuleBtn = document.getElementById('add-rule-btn');
        const messageContainer = document.getElementById('message-container');

        let ruleCount = 0;
        let currentCityId = null;

        const createRuleHtml = (ruleId, ruleData = {}) => `
            <div class="rule-row bg-slate-50 border border-slate-200 rounded-xl p-6 flex flex-wrap md:flex-nowrap gap-4 items-end relative group" data-id="${ruleId}">
                <div class="flex-1 min-w-[150px]">
                    <label class="block text-[10px] font-extrabold text-slate-400 uppercase mb-2 tracking-widest">Parameter</label>
                    <select class="param-type w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white font-bold text-sm">
                        <option value="TEMPERATURE" ${ruleData.parameterType === 'TEMPERATURE' ? 'selected' : ''}>Temperature</option>
                        <option value="RAIN" ${ruleData.parameterType === 'RAIN' ? 'selected' : ''}>Rain</option>
                        <option value="WIND_SPEED" ${ruleData.parameterType === 'WIND_SPEED' ? 'selected' : ''}>Wind Speed</option>
                        <option value="HUMIDITY" ${ruleData.parameterType === 'HUMIDITY' ? 'selected' : ''}>Humidity</option>
                    </select>
                </div>
                <div class="flex-1 min-w-[150px]">
                    <label class="block text-[10px] font-extrabold text-slate-400 uppercase mb-2 tracking-widest">Operator</label>
                    <select class="op-type w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white font-bold text-sm">
                        <option value="LESS_THAN" ${ruleData.operator === 'LESS_THAN' ? 'selected' : ''}>Less Than</option>
                        <option value="GREATER_THAN" ${ruleData.operator === 'GREATER_THAN' ? 'selected' : ''}>Greater Than</option>
                        <option value="EQUALS" ${ruleData.operator === 'EQUALS' ? 'selected' : ''}>Equals</option>
                        <option value="BETWEEN" ${ruleData.operator === 'BETWEEN' ? 'selected' : ''}>Between</option>
                    </select>
                </div>
                <div class="w-24">
                    <label class="block text-[10px] font-extrabold text-slate-400 uppercase mb-2 tracking-widest text-center">Value 1</label>
                    <input type="number" step="0.1" class="val1 w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white font-bold text-center text-sm" value="${ruleData.value1 || ''}" required>
                </div>
                <div class="val2-group ${ruleData.operator === 'BETWEEN' ? '' : 'hidden'} w-24">
                    <label class="block text-[10px] font-extrabold text-slate-400 uppercase mb-2 tracking-widest text-center">Value 2</label>
                    <input type="number" step="0.1" class="val2 w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white font-bold text-center text-sm" value="${ruleData.value2 || ''}" ${ruleData.operator === 'BETWEEN' ? 'required' : ''}>
                </div>
                <button type="button" class="remove-rule-btn bg-white border border-slate-200 text-red-500 hover:bg-red-50 hover:border-red-200 w-9 h-9 rounded-lg flex items-center justify-center transition-all">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-4v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
            </div>
        `;

        const addRule = (ruleData = {}) => {
            if (ruleCount >= 4) return;
            ruleCount++;
            const div = document.createElement('div');
            div.innerHTML = createRuleHtml(Date.now(), ruleData);
            const ruleRow = div.firstElementChild;
            rulesContainer.appendChild(ruleRow);

            const opSelect = ruleRow.querySelector('.op-type');
            const val2Group = ruleRow.querySelector('.val2-group');
            const val2Input = ruleRow.querySelector('.val2');
            
            opSelect.addEventListener('change', () => {
                if (opSelect.value === 'BETWEEN') { val2Group.classList.remove('hidden'); val2Input.required = true; }
                else { val2Group.classList.add('hidden'); val2Input.required = false; val2Input.value = ''; }
            });

            ruleRow.querySelector('.remove-rule-btn').onclick = () => { ruleRow.remove(); ruleCount--; updateAddButtonState(); };
            updateAddButtonState();
        };

        const updateAddButtonState = () => { addRuleBtn.disabled = ruleCount >= 4; addRuleBtn.style.opacity = ruleCount >= 4 ? '0.5' : '1'; };

        try {
            const sub = await apiClient.get(`/api/v1/subscriptions/${id}`);
            document.getElementById('city-name').value = sub.cityName;
            document.getElementById('notify-before').value = sub.notifyBeforeHours;
            currentCityId = sub.cityId;
            sub.rules.forEach(rule => addRule(rule));
            loadingOverlay.classList.add('hidden');
            form.classList.remove('hidden');
        } catch (err) {
            loadingOverlay.innerHTML = `<div class="bg-red-50 text-red-700 p-6 rounded-2xl border border-red-100 font-bold">Error loading subscription: ${err.message}</div>`;
        }

        addRuleBtn.onclick = () => addRule();

        form.onsubmit = async (e) => {
            e.preventDefault();
            const rules = [];
            document.querySelectorAll('.rule-row').forEach(row => {
                rules.push({
                    parameterType: row.querySelector('.param-type').value,
                    operator: row.querySelector('.op-type').value,
                    value1: parseFloat(row.querySelector('.val1').value),
                    value2: row.querySelector('.val2').value ? parseFloat(row.querySelector('.val2').value) : null
                });
            });

            const notifyBeforeHours = parseInt(document.getElementById('notify-before').value);
            if (notifyBeforeHours < 1 || notifyBeforeHours > 48) { alert('Notification period must be between 1 and 48 hours.'); return; }

            try {
                await apiClient.fetch(`/api/v1/subscriptions/${id}`, { method: 'PUT', body: JSON.stringify({ cityId: currentCityId, notifyBeforeHours, rules, isActive: true }) });
                messageContainer.innerHTML = '<div class="bg-green-50 text-green-700 p-4 rounded-xl border border-green-200 font-bold text-center">Changes saved successfully!</div>';
                setTimeout(() => window.location.hash = '#/dashboard', 1000);
            } catch (err) {
                messageContainer.innerHTML = `<div class="bg-red-50 text-red-700 p-4 rounded-xl border border-red-200 font-bold text-center">Error: ${err.message}</div>`;
            }
        };
    }
};
