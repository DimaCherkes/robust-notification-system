import { apiClient } from '../api/apiClient.js';

const PARAMETER_CONFIG = {
    'TEMPERATURE': { min: -60, max: 60, step: 0.1, unit: '°C', label: 'Temp', icon: '🌡️' },
    'RAIN': { min: 0, max: 1, step: 0.01, unit: 'prob', label: 'Precipitation', icon: '💧' },
    'WIND_SPEED': { min: 0, max: 150, step: 0.1, unit: 'm/s', label: 'Wind', icon: '💨' },
    'HUMIDITY': { min: 0, max: 100, step: 1, unit: '%', label: 'Humidity', icon: '☁️' }
};

export const UpdateSubscription = {
    render: async (id) => {
        return `
            <div class="max-w-4xl mx-auto pb-20">
                <div class="mb-10">
                    <a href="#/dashboard" class="group text-sm font-bold text-slate-400 hover:text-blue-600 uppercase tracking-widest flex items-center transition-all">
                        <svg class="w-4 h-4 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" /></svg>
                        Back to Dashboard
                    </a>
                    <h2 class="text-4xl font-black text-slate-800 mt-4 tracking-tight">Update Subscription</h2>
                </div>

                <div id="loading-overlay" class="py-20 text-center text-slate-500 animate-pulse font-bold text-lg">Fetching details...</div>

                <form id="update-sub-form" class="space-y-6 hidden">
                    <div class="bg-white rounded-3xl shadow-sm border border-slate-200 p-8 hover:border-blue-200 transition-colors">
                        <h3 class="text-xl font-bold text-slate-800 mb-8 flex items-center">
                            <span class="w-10 h-10 bg-blue-600 text-white rounded-xl flex items-center justify-center mr-4 shadow-lg shadow-blue-100">1</span>
                            General Settings
                        </h3>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                            <div class="space-y-2">
                                <label class="block text-xs font-black text-slate-400 uppercase tracking-widest">City</label>
                                <input type="text" id="city-name" disabled class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl font-bold text-slate-400 cursor-not-allowed">
                                <p class="text-[10px] text-slate-400 font-bold uppercase ml-1 italic">Location fixed after creation</p>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-xs font-black text-slate-400 uppercase tracking-widest">Notify Before</label>
                                <div class="relative">
                                    <input type="number" id="notify-before" min="1" max="48" required class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all font-bold text-slate-700">
                                    <span class="absolute right-5 top-1/2 -translate-y-1/2 text-slate-400 font-bold">hours</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="bg-white rounded-3xl shadow-sm border border-slate-200 p-8 hover:border-blue-200 transition-colors">
                        <div class="flex items-center justify-between mb-8">
                            <h3 class="text-xl font-bold text-slate-800 flex items-center">
                                <span class="w-10 h-10 bg-blue-600 text-white rounded-xl flex items-center justify-center mr-4 shadow-lg shadow-blue-100">2</span>
                                Weather Conditions
                            </h3>
                            <button type="button" id="add-rule-btn" class="px-5 py-2.5 bg-slate-900 text-white font-bold rounded-xl hover:bg-slate-800 transition-all active:scale-95 text-sm uppercase tracking-wider disabled:opacity-30">
                                + Add Rule
                            </button>
                        </div>
                        
                        <div id="rules-container" class="space-y-4"></div>
                    </div>

                    <div class="flex justify-end mt-10">
                        <button type="submit" class="group relative px-12 py-5 bg-blue-600 text-white font-black rounded-2xl hover:bg-blue-700 shadow-xl shadow-blue-200 transition-all active:scale-95 uppercase tracking-widest text-sm overflow-hidden">
                            <span class="relative z-10 flex items-center text-lg">Save Changes</span>
                        </button>
                    </div>
                </form>
                <div id="message-container" class="mt-8"></div>
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

        const validateInput = (input, config) => {
            const val = parseFloat(input.value);
            const errorContainer = input.parentElement.querySelector('.error-msg');
            if (isNaN(val) || val < config.min || val > config.max) {
                input.classList.add('border-red-500', 'bg-red-50');
                if (errorContainer) errorContainer.textContent = `Range: ${config.min}-${config.max}`;
                return false;
            }
            input.classList.remove('border-red-500', 'bg-red-50');
            if (errorContainer) errorContainer.textContent = '';
            return true;
        };

        const updateParameterOptions = () => {
            const selected = Array.from(document.querySelectorAll('.param-type')).map(s => s.value);
            document.querySelectorAll('.param-type').forEach(select => {
                const cur = select.value;
                Array.from(select.options).forEach(opt => opt.disabled = (opt.value !== cur && selected.includes(opt.value)));
            });
        };

        const addRule = (ruleData = null) => {
            if (ruleCount >= 4) return;
            const div = document.createElement('div');
            div.className = "rule-row group bg-slate-50 border border-slate-200 rounded-2xl p-6 flex flex-wrap md:flex-nowrap gap-6 items-start relative transition-all hover:shadow-md hover:bg-white hover:border-blue-100";
            
            const availableParams = Object.keys(PARAMETER_CONFIG);
            const pType = ruleData ? ruleData.parameterType : (availableParams.find(p => !Array.from(document.querySelectorAll('.param-type')).map(s => s.value).includes(p)) || availableParams[0]);

            div.innerHTML = `
                <div class="flex-grow space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest">Parameter</label>
                    <select class="param-type w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:border-blue-500 font-bold text-sm">
                        ${availableParams.map(p => `<option value="${p}" ${p === pType ? 'selected' : ''}>${PARAMETER_CONFIG[p].icon} ${PARAMETER_CONFIG[p].label}</option>`).join('')}
                    </select>
                </div>
                <div class="flex-grow space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest">Condition</label>
                    <select class="op-type w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:border-blue-500 font-bold text-sm">
                        <option value="LESS_THAN" ${ruleData?.operator === 'LESS_THAN' ? 'selected' : ''}>is less than</option>
                        <option value="GREATER_THAN" ${ruleData?.operator === 'GREATER_THAN' ? 'selected' : ''}>is greater than</option>
                        <option value="EQUALS" ${ruleData?.operator === 'EQUALS' ? 'selected' : ''}>is exactly</option>
                        <option value="BETWEEN" ${ruleData?.operator === 'BETWEEN' ? 'selected' : ''}>is between</option>
                    </select>
                </div>
                <div class="w-32 space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest text-center text-val-label">Value</label>
                    <div class="relative">
                        <input type="number" step="0.01" class="val1 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none font-bold text-center text-sm" value="${ruleData?.value1 ?? ''}" required>
                        <span class="unit-label absolute -bottom-5 left-0 w-full text-center text-[9px] font-bold text-slate-400 uppercase tracking-tighter"></span>
                        <div class="error-msg absolute -top-8 right-0 text-[9px] font-black text-red-500 uppercase tracking-tighter"></div>
                    </div>
                </div>
                <div class="val2-group ${ruleData?.operator === 'BETWEEN' ? '' : 'hidden'} w-32 space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest text-center">And</label>
                    <div class="relative">
                        <input type="number" step="0.01" class="val2 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none font-bold text-center text-sm" value="${ruleData?.value2 ?? ''}">
                        <div class="error-msg absolute -top-8 right-0 text-[9px] font-black text-red-500 uppercase tracking-tighter"></div>
                    </div>
                </div>
                <button type="button" class="remove-rule-btn mt-6 p-3 text-slate-300 hover:text-red-500 hover:bg-red-50 rounded-xl transition-all">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-4v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
            `;

            rulesContainer.appendChild(div);
            ruleCount++;

            const pSel = div.querySelector('.param-type');
            const oSel = div.querySelector('.op-type');
            const v1 = div.querySelector('.val1');
            const v2 = div.querySelector('.val2');
            const v2G = div.querySelector('.val2-group');
            const unit = div.querySelector('.unit-label');

            const refresh = () => {
                const conf = PARAMETER_CONFIG[pSel.value];
                unit.textContent = `${conf.min} to ${conf.max} ${conf.unit}`;
                validateInput(v1, conf);
                if (oSel.value === 'BETWEEN') validateInput(v2, conf);
            };

            pSel.onchange = () => { refresh(); updateParameterOptions(); };
            oSel.onchange = () => {
                const isB = oSel.value === 'BETWEEN';
                v2G.classList.toggle('hidden', !isB);
                v2.required = isB;
                refresh();
            };
            v1.oninput = () => validateInput(v1, PARAMETER_CONFIG[pSel.value]);
            v2.oninput = () => validateInput(v2, PARAMETER_CONFIG[pSel.value]);
            div.querySelector('.remove-rule-btn').onclick = () => { div.remove(); ruleCount--; updateParameterOptions(); addRuleBtn.disabled = false; };

            refresh();
            updateParameterOptions();
            if (ruleCount >= 4) addRuleBtn.disabled = true;
        };

        try {
            const sub = await apiClient.get(`/api/v1/subscriptions/${id}`);
            document.getElementById('city-name').value = sub.cityName;
            document.getElementById('notify-before').value = sub.notifyBeforeHours;
            currentCityId = sub.cityId;
            sub.rules.forEach(r => addRule(r));
            loadingOverlay.classList.add('hidden');
            form.classList.remove('hidden');
        } catch (err) {
            loadingOverlay.innerHTML = `<div class="bg-red-50 text-red-700 p-8 rounded-3xl font-bold">Error: ${err.message}</div>`;
        }

        addRuleBtn.onclick = () => addRule();

        form.onsubmit = async (e) => {
            e.preventDefault();
            const rules = [];
            let valid = true;

            document.querySelectorAll('.rule-row').forEach(row => {
                const p = row.querySelector('.param-type').value;
                const conf = PARAMETER_CONFIG[p];
                const i1 = row.querySelector('.val1');
                const i2 = row.querySelector('.val2');
                const op = row.querySelector('.op-type').value;

                if (!validateInput(i1, conf)) valid = false;
                if (op === 'BETWEEN' && !validateInput(i2, conf)) valid = false;
                if (valid) rules.push({ parameterType: p, operator: op, value1: parseFloat(i1.value), value2: op === 'BETWEEN' ? parseFloat(i2.value) : null });
            });

            if (!valid || rules.length === 0) return;

            try {
                await apiClient.fetch(`/api/v1/subscriptions/${id}`, { method: 'PUT', body: JSON.stringify({ cityId: currentCityId, notifyBeforeHours: parseInt(document.getElementById('notify-before').value), rules, isActive: true }) });
                messageContainer.innerHTML = '<div class="bg-blue-600 text-white p-6 rounded-3xl font-bold text-center">Changes saved! Redirecting...</div>';
                setTimeout(() => window.location.hash = '#/dashboard', 1000);
            } catch (err) {
                messageContainer.innerHTML = `<div class="bg-red-50 text-red-600 p-6 rounded-3xl font-bold text-center border border-red-100">${err.message}</div>`;
            }
        };
    }
};
