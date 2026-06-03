import { apiClient } from '../api/apiClient.js';

const PARAMETER_CONFIG = {
    'TEMPERATURE': { min: -60, max: 60, step: 0.1, unit: '°C', label: 'Temp', icon: '🌡️' },
    'RAIN': { min: 0, max: 1, step: 0.01, unit: 'prob', label: 'Precipitation', icon: '💧' },
    'WIND_SPEED': { min: 0, max: 150, step: 0.1, unit: 'm/s', label: 'Wind', icon: '💨' },
    'HUMIDITY': { min: 0, max: 100, step: 1, unit: '%', label: 'Humidity', icon: '☁️' }
};

export const CreateSubscription = {
    render: async () => {
        return `
            <div class="max-w-4xl mx-auto pb-20">
                <div class="mb-10">
                    <a href="#/dashboard" class="group text-sm font-bold text-slate-400 hover:text-blue-600 uppercase tracking-widest flex items-center transition-all">
                        <svg class="w-4 h-4 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" /></svg>
                        Back to Dashboard
                    </a>
                    <h2 class="text-4xl font-black text-slate-800 mt-4 tracking-tight">New Subscription</h2>
                    <p class="text-slate-500 mt-2">Set up custom alerts for specific weather conditions.</p>
                </div>

                <form id="create-sub-form" class="space-y-6">
                    <!-- General Settings -->
                    <div class="bg-white rounded-3xl shadow-sm border border-slate-200 p-8 hover:border-blue-200 transition-colors">
                        <h3 class="text-xl font-bold text-slate-800 mb-8 flex items-center">
                            <span class="w-10 h-10 bg-blue-600 text-white rounded-xl flex items-center justify-center mr-4 shadow-lg shadow-blue-100">1</span>
                            Target Location & Timing
                        </h3>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                            <div class="space-y-2">
                                <label class="block text-xs font-black text-slate-400 uppercase tracking-widest">Select City</label>
                                <select id="city-select" required class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all font-bold text-slate-700 appearance-none cursor-pointer">
                                    <option value="" disabled selected>Loading cities...</option>
                                </select>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-xs font-black text-slate-400 uppercase tracking-widest">Notify Before</label>
                                <div class="relative">
                                    <input type="number" id="notify-before" value="2" min="1" max="48" required class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all font-bold text-slate-700">
                                    <span class="absolute right-5 top-1/2 -translate-y-1/2 text-slate-400 font-bold">hours</span>
                                </div>
                                <p class="text-[10px] text-slate-400 font-bold uppercase ml-1">Range: 1-48h</p>
                            </div>
                        </div>
                    </div>

                    <!-- Weather Rules -->
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
                        
                        <div id="rules-container" class="space-y-4">
                            <!-- Rules dynamic injection -->
                        </div>
                    </div>

                    <div class="flex justify-end mt-10">
                        <button type="submit" class="group relative px-12 py-5 bg-blue-600 text-white font-black rounded-2xl hover:bg-blue-700 shadow-xl shadow-blue-200 transition-all active:scale-95 uppercase tracking-widest text-sm overflow-hidden">
                            <span class="relative z-10 flex items-center">
                                Create Subscription
                                <svg class="ml-3 w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M14 5l7 7m0 0l-7 7m7-7H3" /></svg>
                            </span>
                        </button>
                    </div>
                </form>
                <div id="message-container" class="mt-8"></div>
            </div>
        `;
    },

    afterRender: async () => {
        const citySelect = document.getElementById('city-select');
        const rulesContainer = document.getElementById('rules-container');
        const addRuleBtn = document.getElementById('add-rule-btn');
        const form = document.getElementById('create-sub-form');
        const messageContainer = document.getElementById('message-container');

        let ruleCount = 0;

        // Fetch cities
        try {
            const cities = await apiClient.get('/api/v1/cities/all');
            citySelect.innerHTML = '<option value="" disabled selected>-- Choose City --</option>' + 
                cities.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        } catch (err) {
            citySelect.innerHTML = '<option value="" disabled>Error loading cities</option>';
        }

        const validateInput = (input, config) => {
            const val = parseFloat(input.value);
            const errorContainer = input.parentElement.querySelector('.error-msg');
            
            if (isNaN(val)) {
                input.classList.add('border-red-500', 'bg-red-50');
                if (errorContainer) errorContainer.textContent = 'Numbers only';
                return false;
            }
            if (val < config.min || val > config.max) {
                input.classList.add('border-red-500', 'bg-red-50');
                if (errorContainer) errorContainer.textContent = `Range: ${config.min} to ${config.max}`;
                return false;
            }
            
            input.classList.remove('border-red-500', 'bg-red-50');
            if (errorContainer) errorContainer.textContent = '';
            return true;
        };

        const updateParameterOptions = () => {
            const selectedParams = Array.from(document.querySelectorAll('.param-type')).map(s => s.value);
            document.querySelectorAll('.param-type').forEach(select => {
                const cur = select.value;
                Array.from(select.options).forEach(opt => {
                    opt.disabled = (opt.value !== cur && selectedParams.includes(opt.value));
                });
            });
        };

        const addRule = () => {
            if (ruleCount >= 4) return;
            const id = Date.now();
            const div = document.createElement('div');
            div.className = "rule-row group bg-slate-50 border border-slate-200 rounded-2xl p-6 flex flex-wrap md:flex-nowrap gap-6 items-start relative transition-all hover:shadow-md hover:bg-white hover:border-blue-100";
            
            const availableParams = Object.keys(PARAMETER_CONFIG);
            const selectedNow = Array.from(document.querySelectorAll('.param-type')).map(s => s.value);
            const nextP = availableParams.find(p => !selectedNow.includes(p)) || availableParams[0];

            div.innerHTML = `
                <div class="flex-grow space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest">Parameter</label>
                    <select class="param-type w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:border-blue-500 font-bold text-sm">
                        ${availableParams.map(p => `<option value="${p}" ${p === nextP ? 'selected' : ''}>${PARAMETER_CONFIG[p].icon} ${PARAMETER_CONFIG[p].label}</option>`).join('')}
                    </select>
                </div>
                <div class="flex-grow space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest">Condition</label>
                    <select class="op-type w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:border-blue-500 font-bold text-sm">
                        <option value="LESS_THAN">is less than</option>
                        <option value="GREATER_THAN">is greater than</option>
                        <option value="EQUALS">is exactly</option>
                        <option value="BETWEEN">is between</option>
                    </select>
                </div>
                <div class="w-32 space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest text-center text-val-label">Value</label>
                    <div class="relative">
                        <input type="number" step="0.01" class="val1 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500/20 font-bold text-center text-sm" required>
                        <span class="unit-label absolute -bottom-5 left-0 w-full text-center text-[9px] font-bold text-slate-400 uppercase tracking-tighter"></span>
                        <div class="error-msg absolute -top-8 right-0 text-[9px] font-black text-red-500 uppercase tracking-tighter"></div>
                    </div>
                </div>
                <div class="val2-group hidden w-32 space-y-2">
                    <label class="block text-[10px] font-black text-slate-400 uppercase tracking-widest text-center text-val-label">And</label>
                    <div class="relative">
                        <input type="number" step="0.01" class="val2 w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500/20 font-bold text-center text-sm">
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

            const refreshRow = () => {
                const conf = PARAMETER_CONFIG[pSel.value];
                v1.placeholder = conf.min;
                v2.placeholder = conf.max;
                unit.textContent = `${conf.min} to ${conf.max} ${conf.unit}`;
                validateInput(v1, conf);
                if (oSel.value === 'BETWEEN') validateInput(v2, conf);
            };

            pSel.onchange = () => { refreshRow(); updateParameterOptions(); };
            oSel.onchange = () => {
                const isB = oSel.value === 'BETWEEN';
                v2G.classList.toggle('hidden', !isB);
                v2.required = isB;
                div.querySelector('.text-val-label').textContent = isB ? 'From' : 'Value';
                refreshRow();
            };

            v1.oninput = () => validateInput(v1, PARAMETER_CONFIG[pSel.value]);
            v2.oninput = () => validateInput(v2, PARAMETER_CONFIG[pSel.value]);

            div.querySelector('.remove-rule-btn').onclick = () => {
                div.remove();
                ruleCount--;
                updateParameterOptions();
                addRuleBtn.disabled = false;
            };

            refreshRow();
            updateParameterOptions();
            if (ruleCount >= 4) addRuleBtn.disabled = true;
        };

        addRule();
        addRuleBtn.onclick = addRule;

        form.onsubmit = async (e) => {
            e.preventDefault();
            const rules = [];
            let globalValid = true;

            document.querySelectorAll('.rule-row').forEach(row => {
                const p = row.querySelector('.param-type').value;
                const conf = PARAMETER_CONFIG[p];
                const i1 = row.querySelector('.val1');
                const i2 = row.querySelector('.val2');
                const op = row.querySelector('.op-type').value;

                if (!validateInput(i1, conf)) globalValid = false;
                if (op === 'BETWEEN' && !validateInput(i2, conf)) globalValid = false;
                
                if (op === 'BETWEEN' && parseFloat(i1.value) >= parseFloat(i2.value)) {
                    i2.classList.add('border-red-500');
                    alert(`For ${p}, the first value must be less than the second.`);
                    globalValid = false;
                }

                rules.push({
                    parameterType: p,
                    operator: op,
                    value1: parseFloat(i1.value),
                    value2: op === 'BETWEEN' ? parseFloat(i2.value) : null
                });
            });

            if (!globalValid) return;
            if (rules.length === 0) { alert('Add at least one rule'); return; }

            try {
                await apiClient.post('/api/v1/subscriptions/create', {
                    cityId: parseInt(citySelect.value),
                    notifyBeforeHours: parseInt(document.getElementById('notify-before').value),
                    rules,
                    isActive: true
                });
                messageContainer.innerHTML = '<div class="bg-blue-600 text-white p-6 rounded-3xl shadow-xl shadow-blue-100 font-bold text-center animate-bounce">Success! Getting you back to dashboard...</div>';
                setTimeout(() => window.location.hash = '#/dashboard', 1500);
            } catch (err) {
                messageContainer.innerHTML = `<div class="bg-red-50 text-red-600 p-6 rounded-3xl border border-red-100 font-bold text-center">Error: ${err.message}</div>`;
            }
        };
    }
};
