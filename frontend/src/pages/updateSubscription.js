import { apiClient } from '../api/apiClient.js';

export const UpdateSubscription = {
    render: async (id) => {
        return `
            <section id="update-subscription">
                <div class="header-with-back">
                    <a href="#/dashboard" class="back-link">&larr; Back to Dashboard</a>
                    <h2>Update Subscription</h2>
                </div>

                <div id="loading-overlay" class="loading">Fetching subscription data...</div>

                <form id="update-sub-form" class="standard-form hidden">
                    <div class="form-section">
                        <h3>General Settings</h3>
                        <div class="form-group">
                            <label>City</label>
                            <input type="text" id="city-name" disabled>
                            <small class="help-text">City cannot be changed after creation.</small>
                        </div>
                        <div class="form-group">
                            <label for="notify-before">Notify Before (Hours)</label>
                            <input type="number" id="notify-before" min="1" max="48" required>
                        </div>
                    </div>

                    <div class="form-section">
                        <div class="section-header">
                            <h3>Weather Rules</h3>
                            <button type="button" id="add-rule-btn" class="secondary-btn">+ Add Rule</button>
                        </div>
                        <div id="rules-container">
                            <!-- Rules will be injected here -->
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="primary-btn large">Update Subscription</button>
                    </div>
                </form>
                <div id="message-container"></div>
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
        const messageContainer = document.getElementById('message-container');

        let ruleCount = 0;
        let currentCityId = null;

        const createRuleHtml = (id, ruleData = {}) => `
            <div class="rule-row" data-id="${id}">
                <div class="form-group">
                    <label>Parameter</label>
                    <select class="param-type" required>
                        <option value="TEMPERATURE" ${ruleData.parameterType === 'TEMPERATURE' ? 'selected' : ''}>Temperature</option>
                        <option value="RAIN" ${ruleData.parameterType === 'RAIN' ? 'selected' : ''}>Rain</option>
                        <option value="WIND_SPEED" ${ruleData.parameterType === 'WIND_SPEED' ? 'selected' : ''}>Wind Speed</option>
                        <option value="HUMIDITY" ${ruleData.parameterType === 'HUMIDITY' ? 'selected' : ''}>Humidity</option>
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
                    <label>Value 1</label>
                    <input type="number" step="0.1" class="val1" value="${ruleData.value1 || ''}" required>
                </div>
                <div class="form-group val2-group ${ruleData.operator === 'BETWEEN' ? '' : 'hidden'}">
                    <label>Value 2</label>
                    <input type="number" step="0.1" class="val2" value="${ruleData.value2 || ''}" ${ruleData.operator === 'BETWEEN' ? 'required' : ''}>
                </div>
                <button type="button" class="remove-rule-btn">&times;</button>
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
                if (opSelect.value === 'BETWEEN') {
                    val2Group.classList.remove('hidden');
                    val2Input.required = true;
                } else {
                    val2Group.classList.add('hidden');
                    val2Input.required = false;
                    val2Input.value = '';
                }
            });

            ruleRow.querySelector('.remove-rule-btn').onclick = () => {
                ruleRow.remove();
                ruleCount--;
                updateAddButtonState();
            };
            updateAddButtonState();
        };

        const updateAddButtonState = () => {
            addRuleBtn.disabled = ruleCount >= 4;
            addRuleBtn.style.opacity = ruleCount >= 4 ? '0.5' : '1';
        };

        // Fetch existing data
        try {
            const sub = await apiClient.get(`/api/v1/subscriptions/${id}`);
            document.getElementById('city-name').value = sub.cityName;
            document.getElementById('notify-before').value = sub.notifyBeforeHours;
            currentCityId = sub.cityId;

            sub.rules.forEach(rule => addRule(rule));
            
            loadingOverlay.classList.add('hidden');
            form.classList.remove('hidden');
        } catch (err) {
            loadingOverlay.innerHTML = `<p class="error">Error loading subscription: ${err.message}</p>`;
        }

        addRuleBtn.onclick = () => addRule();

        // Handle Update
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
            if (notifyBeforeHours < 1 || notifyBeforeHours > 48) {
                alert('Notification period must be between 1 and 48 hours.');
                return;
            }

            const payload = {
                cityId: currentCityId,
                notifyBeforeHours: notifyBeforeHours,
                rules: rules,
                isActive: true
            };

            try {
                await apiClient.fetch(`/api/v1/subscriptions/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                messageContainer.innerHTML = '<p class="success">Subscription updated successfully!</p>';
                setTimeout(() => window.location.hash = '#/dashboard', 1000);
            } catch (err) {
                messageContainer.innerHTML = `<p class="error">Error: ${err.message}</p>`;
            }
        };
    }
};
