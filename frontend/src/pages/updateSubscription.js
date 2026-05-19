import { apiClient } from '../api/apiClient.js';

export const UpdateSubscription = {
    render: async (id) => {
        return `
            <section id="update-subscription">
                <div class="header-with-back">
                    <a href="#/dashboard" class="back-link">&larr; Back to Dashboard</a>
                    <h2>Update Subscription</h2>
                </div>

                <div id="message-container"></div>
                <div id="loading-overlay" class="info-text" style="text-align:center; padding: 2rem;">Fetching subscription data...</div>

                <form id="update-sub-form" class="standard-form hidden">
                    <div class="form-section">
                        <h3>General Settings</h3>
                        <div class="form-group">
                            <label>City</label>
                            <input type="text" id="city-name" disabled>
                        </div>
                        <div class="form-group">
                            <label for="notify-before">Notify Before (Hours)</label>
                            <input type="number" id="notify-before" min="1" max="48" required>
                            <small class="info-text">1 to 48 hours</small>
                            <div class="error-text" id="notify-before-error"></div>
                        </div>
                    </div>

                    <div class="form-section">
                        <div class="section-header">
                            <h3>Weather Rules</h3>
                        </div>
                        <div id="rules-container">
                            <!-- Rules will be injected here -->
                        </div>
                        <div class="form-group" style="margin-top: 1rem;">
                            <button type="button" id="add-rule-btn" class="secondary-btn">+ Add Rule</button>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" id="submit-btn" class="primary-btn">Update Subscription</button>
                    </div>
                </form>
            </section>
        `;
    },

    afterRender: async (id) => {
        if (!id) {
            window.location.hash = '#/dashboard';
            return;
        }

        const form = document.getElementById('update-sub-form');
        const loadingOverlay = document.getElementById('loading-overlay');
        const rulesContainer = document.getElementById('rules-container');
        const addRuleBtn = document.getElementById('add-rule-btn');
        const submitBtn = document.getElementById('submit-btn');
        const messageContainer = document.getElementById('message-container');

        const PARAM_CONFIG = {
            TEMPERATURE: { label: 'Temperature (°C)', min: -50, max: 60, step: 0.1 },
            RAIN: { label: 'Rain Prob. (%)', min: 0, max: 100, step: 1 },
            WIND_SPEED: { label: 'Wind Speed (m/s)', min: 0, max: 100, step: 0.1 },
            HUMIDITY: { label: 'Humidity (%)', min: 0, max: 100, step: 1 }
        };

        const validateAll = () => {
            let isValid = true;
            
            const notifyInput = document.getElementById('notify-before');
            const notifyError = document.getElementById('notify-before-error');
            const notifyVal = parseInt(notifyInput.value);
            if (isNaN(notifyVal) || notifyVal < 1 || notifyVal > 48) {
                notifyError.textContent = 'Must be 1-48';
                isValid = false;
            } else {
                notifyError.textContent = '';
            }

            const ruleRows = document.querySelectorAll('.rule-row');
            if (ruleRows.length === 0) isValid = false;

            ruleRows.forEach(row => {
                const param = row.querySelector('.param-type').value;
                const op = row.querySelector('.op-type').value;
                const v1Input = row.querySelector('.val1');
                const v2Input = row.querySelector('.val2');
                const v1Error = row.querySelector('.v1-error');
                const v2Error = row.querySelector('.v2-error');
                
                if (!param) {
                    isValid = false;
                    return;
                }

                const config = PARAM_CONFIG[param];
                const v1 = parseFloat(v1Input.value);
                const v2 = parseFloat(v2Input.value);

                if (isNaN(v1) || v1 < config.min || v1 > config.max) {
                    v1Error.textContent = `Range: ${config.min}..${config.max}`;
                    isValid = false;
                } else {
                    v1Error.textContent = '';
                }

                if (op === 'BETWEEN') {
                    if (isNaN(v2) || v2 < config.min || v2 > config.max) {
                        v2Error.textContent = `Range: ${config.min}..${config.max}`;
                        isValid = false;
                    } else if (v1 >= v2) {
                        v2Error.textContent = 'Must be > Val 1';
                        isValid = false;
                    } else {
                        v2Error.textContent = '';
                    }
                }
            });

            submitBtn.disabled = !isValid;
            submitBtn.style.opacity = isValid ? '1' : '0.6';
            return isValid;
        };

        const getUsedParameters = () => {
            return Array.from(document.querySelectorAll('.param-type')).map(s => s.value);
        };

        const addRule = (ruleData = {}) => {
            const usedParams = getUsedParameters();
            if (usedParams.length >= Object.keys(PARAM_CONFIG).length && !ruleData.parameterType) return;

            const div = document.createElement('div');
            div.className = 'rule-row';
            div.innerHTML = `
                <button type="button" class="remove-rule-btn">&times;</button>
                <div class="form-group">
                    <label>Parameter</label>
                    <select class="param-type" required>
                        <option value="" disabled>Select...</option>
                        ${Object.keys(PARAM_CONFIG).map(p => 
                            `<option value="${p}" ${ruleData.parameterType === p ? 'selected' : (usedParams.includes(p) ? 'disabled' : '')}>${PARAM_CONFIG[p].label}</option>`
                        ).join('')}
                    </select>
                </div>
                <div class="form-group">
                    <label>Operator</label>
                    <select class="op-type" required>
                        <option value="LESS_THAN" ${ruleData.operator === 'LESS_THAN' ? 'selected' : ''}>Less Than</option>
                        <option value="GREATER_THAN" ${ruleData.operator === 'GREATER_THAN' ? 'selected' : ''}>Greater Than</option>
                        <option value="EQUALS" ${ruleData.operator === 'EQUALS' ? 'selected' : ''}>Equals</option>
                        <option value="BETWEEN" ${ruleData.operator === 'BETWEEN' ? 'selected' : ''}>Between</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="val1-label">${ruleData.operator === 'BETWEEN' ? 'Min Value' : 'Value'}</label>
                    <input type="number" step="0.1" class="val1" value="${ruleData.parameterType === 'RAIN' ? (ruleData.value1 * 100) : (ruleData.value1 || '')}" required>
                    <div class="error-text v1-error"></div>
                </div>
                <div class="form-group val2-group ${ruleData.operator === 'BETWEEN' ? '' : 'hidden'}">
                    <label>Max Value</label>
                    <input type="number" step="0.1" class="val2" value="${ruleData.parameterType === 'RAIN' ? (ruleData.value2 * 100) : (ruleData.value2 || '')}" ${ruleData.operator === 'BETWEEN' ? 'required' : ''}>
                    <div class="error-text v2-error"></div>
                </div>
            `;
            
            rulesContainer.appendChild(div);

            const paramSelect = div.querySelector('.param-type');
            const opSelect = div.querySelector('.op-type');
            const v1Input = div.querySelector('.val1');
            const v2Input = div.querySelector('.val2');
            const val2Group = div.querySelector('.val2-group');
            const val1Label = div.querySelector('.val1-label');

            const handleUpdate = () => {
                const config = PARAM_CONFIG[paramSelect.value];
                if (config) {
                    v1Input.min = config.min;
                    v1Input.max = config.max;
                    v2Input.min = config.min;
                    v2Input.max = config.max;
                }
                
                if (opSelect.value === 'BETWEEN') {
                    val2Group.classList.remove('hidden');
                    v2Input.required = true;
                    val1Label.textContent = 'Min Value';
                } else {
                    val2Group.classList.add('hidden');
                    v2Input.required = false;
                    v2Input.value = '';
                    val1Label.textContent = 'Value';
                }
                validateAll();
            };

            paramSelect.addEventListener('change', () => {
                handleUpdate();
                updateParameterOptions();
            });
            opSelect.addEventListener('change', handleUpdate);
            v1Input.addEventListener('input', validateAll);
            v2Input.addEventListener('input', validateAll);

            div.querySelector('.remove-rule-btn').onclick = () => {
                div.remove();
                updateParameterOptions();
                validateAll();
            };
            
            handleUpdate();
            updateParameterOptions();
        };

        const updateParameterOptions = () => {
            const usedParams = getUsedParameters();
            document.querySelectorAll('.param-type').forEach(select => {
                const currentVal = select.value;
                Array.from(select.options).forEach(opt => {
                    if (opt.value && opt.value !== currentVal) {
                        opt.disabled = usedParams.includes(opt.value);
                    }
                });
            });
            addRuleBtn.disabled = usedParams.length >= Object.keys(PARAM_CONFIG).length;
        };

        document.getElementById('notify-before').addEventListener('input', validateAll);

        // Fetch existing data
        try {
            const sub = await apiClient.get(`/api/v1/subscriptions/${id}`);
            document.getElementById('city-name').value = sub.cityName;
            document.getElementById('notify-before').value = sub.notifyBeforeHours;
            sub.rules.forEach(rule => addRule(rule));
            
            loadingOverlay.classList.add('hidden');
            form.classList.remove('hidden');
            validateAll();
        } catch (err) {
            messageContainer.innerHTML = `<div class="error-msg">${err.message}</div>`;
        }

        addRuleBtn.onclick = () => addRule();

        form.onsubmit = async (e) => {
            e.preventDefault();
            if (!validateAll()) return;

            const rules = Array.from(document.querySelectorAll('.rule-row')).map(row => {
                const param = row.querySelector('.param-type').value;
                const v1 = parseFloat(row.querySelector('.val1').value);
                const v2Val = row.querySelector('.val2').value;
                const v2 = v2Val ? parseFloat(v2Val) : null;
                
                return {
                    parameterType: param,
                    operator: row.querySelector('.op-type').value,
                    value1: param === 'RAIN' ? v1 / 100 : v1,
                    value2: param === 'RAIN' && v2 !== null ? v2 / 100 : v2
                };
            });

            try {
                await apiClient.fetch(`/api/v1/subscriptions/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify({
                        notifyBeforeHours: parseInt(document.getElementById('notify-before').value),
                        rules: rules,
                        isActive: true
                    })
                });
                messageContainer.innerHTML = '<div class="success-msg">Subscription updated!</div>';
                setTimeout(() => window.location.hash = '#/dashboard', 1000);
            } catch (err) {
                messageContainer.innerHTML = `<div class="error-msg">${err.message}</div>`;
            }
        };
    }
};
