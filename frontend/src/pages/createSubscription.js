import { apiClient } from '../api/apiClient.js';

export const CreateSubscription = {
    render: async () => {
        return `
            <section id="create-subscription">
                <div class="header-with-back">
                    <a href="#/dashboard" class="back-link">&larr; Back to Dashboard</a>
                    <h2>Create New Weather Subscription</h2>
                </div>

                <form id="create-sub-form" class="standard-form">
                    <div class="form-section">
                        <h3>General Settings</h3>
                        <div class="form-group">
                            <label for="city-select">Select City</label>
                            <select id="city-select" required>
                                <option value="" disabled selected>Loading cities...</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="notify-before">Notify Before (Hours)</label>
                            <input type="number" id="notify-before" value="2" min="1" max="48" required>
                            <small class="help-text">Must be between 1 and 48 hours.</small>
                        </div>
                    </div>

                    <div class="form-section">
                        <div class="section-header">
                            <h3>Weather Rules</h3>
                            <button type="button" id="add-rule-btn" class="secondary-btn">+ Add Rule</button>
                        </div>
                        <p class="info-text">You can add up to 4 rules (at least 1 is required).</p>
                        <div id="rules-container">
                            <!-- Rules will be injected here -->
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="primary-btn large">Create Subscription</button>
                    </div>
                </form>
                <div id="message-container"></div>
            </section>
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
            citySelect.innerHTML = '<option value="" disabled selected>-- Select a City --</option>' + 
                cities.map(city => `<option value="${city.id}">${city.name} ${city.status === 'ON_USE' ? '(Active)' : ''}</option>`).join('');
        } catch (err) {
            citySelect.innerHTML = '<option value="" disabled>Error loading cities</option>';
            console.error(err);
        }

        const createRuleHtml = (id) => `
            <div class="rule-row" data-id="${id}">
                <div class="form-group">
                    <label>Parameter</label>
                    <select class="param-type" required>
                        <option value="TEMPERATURE">Temperature</option>
                        <option value="RAIN">Rain</option>
                        <option value="WIND_SPEED">Wind Speed</option>
                        <option value="HUMIDITY">Humidity</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Operator</label>
                    <select class="op-type" required>
                        <option value="LESS_THAN">Less Than</option>
                        <option value="GREATER_THAN">Greater Than</option>
                        <option value="EQUALS">Equals</option>
                        <option value="BETWEEN">Between</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Value 1</label>
                    <input type="number" step="0.1" class="val1" required>
                </div>
                <div class="form-group val2-group hidden">
                    <label>Value 2</label>
                    <input type="number" step="0.1" class="val2">
                </div>
                <button type="button" class="remove-rule-btn">&times;</button>
            </div>
        `;

        const addRule = () => {
            if (ruleCount >= 4) return;
            ruleCount++;
            const div = document.createElement('div');
            div.innerHTML = createRuleHtml(Date.now());
            const ruleRow = div.firstElementChild;
            rulesContainer.appendChild(ruleRow);

            // Toggle Value 2 visibility for BETWEEN operator
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

            // Remove rule logic
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

        // Add initial rule
        addRule();

        addRuleBtn.onclick = addRule;

        // Form submission
        form.onsubmit = async (e) => {
            e.preventDefault();
            messageContainer.innerHTML = '';

            const rules = [];
            document.querySelectorAll('.rule-row').forEach(row => {
                rules.push({
                    parameterType: row.querySelector('.param-type').value,
                    operator: row.querySelector('.op-type').value,
                    value1: parseFloat(row.querySelector('.val1').value),
                    value2: row.querySelector('.val2').value ? parseFloat(row.querySelector('.val2').value) : null
                });
            });

            if (rules.length === 0) {
                alert('At least one rule is required');
                return;
            }

            const notifyBeforeHours = parseInt(document.getElementById('notify-before').value);
            if (notifyBeforeHours < 1 || notifyBeforeHours > 48) {
                alert('Notification period must be between 1 and 48 hours.');
                return;
            }

            const payload = {
                cityId: parseInt(citySelect.value),
                notifyBeforeHours: notifyBeforeHours,
                rules: rules,
                isActive: true
            };

            try {
                await apiClient.post('/api/v1/subscriptions/create', payload);
                messageContainer.innerHTML = '<p class="success">Subscription created successfully! Redirecting...</p>';
                setTimeout(() => window.location.hash = '#/dashboard', 1500);
            } catch (err) {
                messageContainer.innerHTML = `<p class="error">Error: ${err.message}</p>`;
            }
        };
    }
};
