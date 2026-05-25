import { authService } from '../services/authService.js';
import { apiClient } from '../api/apiClient.js';

export const Profile = {
    render: async () => {
        const userInfo = authService.getUserInfo();
        return `
            <section id="profile">
                <div class="header-with-back">
                    <a href="#/dashboard" class="back-link">&larr; Back to Dashboard</a>
                    <h2>User Profile</h2>
                </div>

                <div class="standard-form">
                    <div class="form-section">
                        <h3>Account Information</h3>
                        <p><strong>Username:</strong> ${userInfo?.username || 'N/A'}</p>
                        <p><strong>Email:</strong> ${userInfo?.email || 'N/A'}</p>
                    </div>

                    <div class="form-section danger-zone">
                        <h3>Danger Zone</h3>
                        <p>Once you delete your account, there is no going back. Please be certain.</p>
                        <button id="delete-account-btn" class="danger-btn">Delete My Account</button>
                    </div>
                </div>

                <!-- Delete Confirmation Modal -->
                <div id="delete-profile-modal" class="modal hidden">
                    <div class="modal-content">
                        <h3>Permanently Delete Account?</h3>
                        <p>Are you absolutely sure? All your data and subscriptions will be permanently removed. This action cannot be undone.</p>
                        <div class="modal-actions">
                            <button id="cancel-profile-delete" class="secondary-btn">Cancel</button>
                            <button id="confirm-profile-delete" class="danger-btn">Delete Account</button>
                        </div>
                    </div>
                </div>

                <div id="message-container"></div>
            </section>
        `;
    },

    afterRender: async () => {
        const deleteBtn = document.getElementById('delete-account-btn');
        const modal = document.getElementById('delete-profile-modal');
        const cancelBtn = document.getElementById('cancel-profile-delete');
        const confirmBtn = document.getElementById('confirm-profile-delete');
        const messageContainer = document.getElementById('message-container');

        const userInfo = authService.getUserInfo();

        deleteBtn.onclick = () => modal.classList.remove('hidden');
        cancelBtn.onclick = () => modal.classList.add('hidden');

        confirmBtn.onclick = async () => {
            if (!userInfo?.userId) {
                alert('User information not found.');
                return;
            }

            try {
                // Call IAM service to delete user
                await apiClient.fetch(`/api/v1/iam-service/users/${userInfo.userId}`, {
                    method: 'DELETE'
                });

                // Clear tokens and redirect to login
                authService.logout();
                window.location.hash = '#/login';
            } catch (err) {
                messageContainer.innerHTML = `<p class="error">Failed to delete account: ${err.message}</p>`;
                modal.classList.add('hidden');
            }
        };
    }
};
