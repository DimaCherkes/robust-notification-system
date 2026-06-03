import { authService } from '../services/authService.js';
import { apiClient } from '../api/apiClient.js';

export const Profile = {
    render: async () => {
        return `
            <div id="profile-container" class="animate-fadeIn">
                <div class="flex items-center justify-between mb-8">
                    <div>
                        <h2 class="text-3xl font-black text-slate-800">Profile Settings ✨</h2>
                        <p class="text-slate-500 font-medium">Manage your account security and settings.</p>
                    </div>
                    <a href="#/dashboard" class="flex items-center space-x-2 text-sm font-bold text-pink-500 hover:text-pink-600 transition-colors bg-pink-50 px-4 py-2 rounded-2xl">
                        <span>&larr;</span>
                        <span>Back to Dashboard</span>
                    </a>
                </div>

                <div id="profile-content" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    <!-- Loading skeleton -->
                    <div class="lg:col-span-1 bg-white rounded-[2rem] p-8 shadow-xl shadow-pink-100/50 border border-pink-50 animate-pulse">
                        <div class="w-32 h-32 bg-slate-200 rounded-full mx-auto mb-6"></div>
                        <div class="h-6 bg-slate-200 rounded w-3/4 mx-auto mb-2"></div>
                        <div class="h-4 bg-slate-200 rounded w-1/2 mx-auto"></div>
                    </div>
                    <div class="lg:col-span-2 space-y-8">
                        <div class="bg-white rounded-[2rem] p-8 shadow-xl shadow-blue-100/50 border border-blue-50 animate-pulse">
                            <div class="h-8 bg-slate-200 rounded w-1/4 mb-6"></div>
                            <div class="space-y-4">
                                <div class="h-12 bg-slate-200 rounded"></div>
                                <div class="h-12 bg-slate-200 rounded"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Admin Section (Hidden by default) -->
                <div id="admin-section" class="mt-12 hidden">
                    <div class="bg-white rounded-[2rem] p-8 shadow-xl shadow-purple-100/50 border border-purple-50">
                        <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
                            <div>
                                <h3 class="text-2xl font-black text-slate-800">User Management 🛠️</h3>
                                <p class="text-slate-500 font-medium">System administration and oversight.</p>
                            </div>
                            <div class="relative flex-1 max-w-md">
                                <input type="text" id="user-search-input" placeholder="Search by nickname or email..." 
                                    class="w-full pl-12 pr-4 py-3 bg-purple-50 border-none rounded-2xl focus:ring-2 focus:ring-purple-200 font-medium text-slate-700 placeholder-purple-300">
                                <svg class="w-5 h-5 text-purple-400 absolute left-4 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                </svg>
                            </div>
                        </div>
                        <div class="overflow-x-auto">
                            <table class="w-full">
                                <thead>
                                    <tr class="text-left text-slate-400 text-sm font-bold uppercase tracking-wider">
                                        <th class="pb-4 px-4">User</th>
                                        <th class="pb-4 px-4">Role</th>
                                        <th class="pb-4 px-4">Status</th>
                                        <th class="pb-4 px-4">Joined</th>
                                        <th class="pb-4 px-4 text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody id="user-list-body" class="divide-y divide-slate-50">
                                    <!-- Users will be loaded here -->
                                </tbody>
                            </table>
                        </div>
                        <div id="admin-pagination" class="mt-6 flex justify-center space-x-2"></div>
                    </div>
                </div>

                <!-- Delete Modal -->
                <div id="delete-modal" class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4 hidden">
                    <div class="bg-white rounded-[2rem] max-w-md w-full p-8 shadow-2xl animate-scaleIn">
                        <div class="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center mb-6">
                            <svg class="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                            </svg>
                        </div>
                        <h3 class="text-2xl font-black text-slate-800 mb-2">Confirm Account Deletion</h3>
                        <p class="text-slate-500 font-medium mb-8">Deleting your account is permanent. All your notifications and associated data will be removed from the system.</p>
                        <div class="grid grid-cols-2 gap-4">
                            <button id="cancel-delete-btn" class="py-3 px-6 bg-slate-100 hover:bg-slate-200 text-slate-600 font-bold rounded-2xl transition-all">Cancel</button>
                            <button id="confirm-delete-btn" class="py-3 px-6 bg-red-500 hover:bg-red-600 text-white font-bold rounded-2xl shadow-lg shadow-red-200 transition-all active:scale-95">Confirm Delete</button>
                        </div>
                    </div>
                </div>

                <div id="toast-container" class="fixed bottom-8 right-8 space-y-4 z-50"></div>
            </div>
        `;
    },

    afterRender: async () => {
        const userInfo = authService.getUserInfo();
        if (!userInfo) return;

        const content = document.getElementById('profile-content');
        const adminSection = document.getElementById('admin-section');
        const userListBody = document.getElementById('user-list-body');
        const searchInput = document.getElementById('user-search-input');
        const paginationContainer = document.getElementById('admin-pagination');

        const showToast = (message, type = 'success') => {
            const toastContainer = document.getElementById('toast-container');
            const toast = document.createElement('div');
            const bgColor = type === 'success' ? 'bg-emerald-500' : 'bg-red-500';
            const icon = type === 'success' ? '✅' : '❌';
            
            toast.className = `${bgColor} text-white px-6 py-4 rounded-2xl shadow-lg font-bold flex items-center space-x-3 animate-slideInRight`;
            toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
            
            toastContainer.appendChild(toast);
            setTimeout(() => {
                toast.classList.replace('animate-slideInRight', 'animate-fadeOut');
                setTimeout(() => toast.remove(), 500);
            }, 3000);
        };

        const loadProfile = async () => {
            try {
                const response = await apiClient.get(`/api/v1/iam-service/users/${userInfo.userId}`);
                const user = response.body;

                content.innerHTML = `
                    <div class="lg:col-span-1">
                        <div class="bg-white rounded-[2rem] p-8 shadow-xl shadow-pink-100/50 border border-pink-50 sticky top-8">
                            <div class="relative w-32 h-32 mx-auto mb-6 group">
                                <div class="absolute inset-0 bg-pink-200 rounded-full blur-xl group-hover:blur-2xl transition-all opacity-50"></div>
                                <div class="relative w-full h-full bg-gradient-to-tr from-pink-400 to-rose-300 rounded-full flex items-center justify-center text-4xl shadow-inner border-4 border-white">
                                    ${user.username.charAt(0).toUpperCase()}
                                </div>
                                <div class="absolute -bottom-2 -right-2 bg-white p-2 rounded-xl shadow-lg border border-pink-50">
                                    ✨
                                </div>
                            </div>
                            <div class="text-center">
                                <h3 class="text-2xl font-black text-slate-800 mb-1">${user.username}</h3>
                                <p class="text-slate-400 font-bold text-sm uppercase tracking-widest mb-6">${user.roles.map(r => r.name).join(', ')}</p>
                                
                                <div class="space-y-3">
                                    <div class="bg-slate-50 p-4 rounded-2xl text-left border border-slate-100">
                                        <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Email Address</p>
                                        <p class="text-slate-700 font-bold truncate">${user.email}</p>
                                    </div>
                                    <div class="bg-slate-50 p-4 rounded-2xl text-left border border-slate-100">
                                        <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Account Created</p>
                                        <p class="text-slate-700 font-bold">${new Date(user.created).toLocaleDateString()}</p>
                                    </div>
                                </div>

                                <div class="mt-8 pt-8 border-t border-slate-100">
                                    <button id="delete-trigger" class="w-full py-4 text-red-400 hover:text-red-500 font-black text-sm uppercase tracking-widest transition-colors">
                                        Terminate Account
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="lg:col-span-2 space-y-8">
                        <!-- Update Nickname Form -->
                        <div class="bg-white rounded-[2rem] p-8 shadow-xl shadow-blue-100/50 border border-blue-50">
                            <h4 class="text-xl font-black text-slate-800 mb-6">Update Identification 🏷️</h4>
                            <form id="update-nickname-form" class="space-y-6">
                                <div>
                                    <label class="block text-sm font-black text-slate-400 uppercase tracking-widest mb-2 ml-1">New Nickname</label>
                                    <input type="text" id="nickname-input" value="${user.username}" required
                                        class="w-full px-6 py-4 bg-blue-50/50 border-2 border-transparent focus:border-blue-200 focus:bg-white rounded-2xl outline-none font-bold text-slate-700 transition-all placeholder-blue-200">
                                </div>
                                <button type="submit" class="w-full md:w-auto px-10 py-4 bg-blue-600 hover:bg-blue-700 text-white font-black rounded-2xl shadow-lg shadow-blue-200 transition-all active:scale-95 disabled:opacity-50">
                                    Save Changes
                                </button>
                            </form>
                        </div>

                        <!-- System Status Card -->
                        <div class="bg-gradient-to-br from-slate-700 to-slate-900 rounded-[2rem] p-8 text-white shadow-xl shadow-slate-200/50 relative overflow-hidden">
                            <div class="relative z-10">
                                <h4 class="text-xl font-black mb-2">Account Status: Active</h4>
                                <p class="text-slate-300 font-medium mb-6">Your account is in good standing. All system features are currently available.</p>
                                <div class="flex items-center space-x-2 text-emerald-400 font-bold text-sm uppercase tracking-widest">
                                    <span class="w-2 h-2 bg-emerald-400 rounded-full animate-pulse"></span>
                                    <span>System Operational</span>
                                </div>
                            </div>
                            <div class="absolute -bottom-10 -right-10 w-40 h-40 bg-white/5 rounded-full blur-3xl"></div>
                        </div>
                    </div>
                `;

                document.getElementById('update-nickname-form').onsubmit = async (e) => {
                    e.preventDefault();
                    const nickname = document.getElementById('nickname-input').value;
                    const submitBtn = e.target.querySelector('button');
                    submitBtn.disabled = true;

                    try {
                        await apiClient.fetch(`/api/v1/iam-service/users/${userInfo.userId}`, {
                            method: 'PUT',
                            body: JSON.stringify({ nickname })
                        });
                        showToast('Nickname updated successfully!');
                        loadProfile();
                    } catch (err) {
                        showToast(err.message, 'error');
                    } finally {
                        submitBtn.disabled = false;
                    }
                };

                document.getElementById('delete-trigger').onclick = () => {
                    document.getElementById('delete-modal').classList.remove('hidden');
                };

                const roles = userInfo.roles || [];
                if (roles.includes('ADMIN') || roles.includes('SUPER_ADMIN')) {
                    adminSection.classList.remove('hidden');
                    loadUsers();
                }

            } catch (err) {
                showToast(err.message, 'error');
            }
        };

        const loadUsers = async (page = 0, query = '') => {
            try {
                let response;
                if (query) {
                    response = await apiClient.post(`/api/v1/iam-service/users/search?page=${page}&limit=5`, {
                        username: query,
                        email: query
                    });
                } else {
                    response = await apiClient.get(`/api/v1/iam-service/users/all?page=${page}&limit=5`);
                }

                const { content: users, pagination } = response.body;

                userListBody.innerHTML = users.map(user => `
                    <tr class="group hover:bg-purple-50/50 transition-colors">
                        <td class="py-4 px-4">
                            <div class="flex items-center space-x-3">
                                <div class="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center text-purple-600 font-bold border border-purple-200">
                                    ${user.username.charAt(0).toUpperCase()}
                                </div>
                                <div>
                                    <div class="font-bold text-slate-800">${user.username}</div>
                                    <div class="text-xs text-slate-400 font-medium">${user.email}</div>
                                </div>
                            </div>
                        </td>
                        <td class="py-4 px-4">
                            <div class="flex flex-wrap gap-1">
                                ${user.roles.map(r => `
                                    <span class="px-2 py-0.5 bg-slate-100 text-slate-500 rounded-md text-[10px] font-black uppercase tracking-wider border border-slate-200">
                                        ${r.name}
                                    </span>
                                `).join('')}
                            </div>
                        </td>
                        <td class="py-4 px-4">
                            <span class="px-2 py-1 ${user.registrationStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-600 border-emerald-100' : 'bg-amber-50 text-amber-600 border-amber-100'} rounded-lg text-[10px] font-black border uppercase tracking-wider">
                                ${user.registrationStatus}
                            </span>
                        </td>
                        <td class="py-4 px-4">
                            <div class="text-sm font-bold text-slate-500">${new Date(user.created).toLocaleDateString()}</div>
                        </td>
                        <td class="py-4 px-4 text-right">
                            <button onclick="alert('Viewing user ${user.username}')" class="p-2 text-slate-400 hover:text-purple-600 transition-colors">
                                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                            </button>
                        </td>
                    </tr>
                `).join('');

                if (pagination.totalPages > 1) {
                    paginationContainer.innerHTML = Array.from({ length: pagination.totalPages }, (_, i) => `
                        <button class="w-10 h-10 rounded-xl font-bold transition-all ${pagination.currentPage === i + 1 ? 'bg-purple-600 text-white shadow-lg shadow-purple-200' : 'bg-purple-50 text-purple-400 hover:bg-purple-100'}" 
                            onclick="window.loadUsersPage(${i})">
                            ${i + 1}
                        </button>
                    `).join('');
                } else {
                    paginationContainer.innerHTML = '';
                }

            } catch (err) {
                console.error('Failed to load users:', err);
            }
        };

        document.getElementById('cancel-delete-btn').onclick = () => {
            document.getElementById('delete-modal').classList.add('hidden');
        };

        document.getElementById('confirm-delete-btn').onclick = async () => {
            try {
                await apiClient.fetch(`/api/v1/iam-service/users/${userInfo.userId}`, {
                    method: 'DELETE'
                });
                authService.logout();
                window.location.hash = '#/login';
            } catch (err) {
                showToast(err.message, 'error');
                document.getElementById('delete-modal').classList.add('hidden');
            }
        };

        let debounceTimer;
        searchInput.oninput = (e) => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                loadUsers(0, e.target.value);
            }, 300);
        };

        window.loadUsersPage = (page) => loadUsers(page, searchInput.value);

        loadProfile();
    }
};
