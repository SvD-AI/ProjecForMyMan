document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/v1/auth/me', { credentials: 'include' });
        if (!response.ok) throw new Error('Unauthorized');
        const user = await response.json();
        console.log(user);
        const role = user.roles?.[0]?.roleName?.toUpperCase() || user.roles?.[0]?.name?.toUpperCase();
        localStorage.setItem('role', role); 
        localStorage.setItem('userId', user.id); 

        initRoleBasedUI(role); 
    } catch (err) {
        console.error('Access denied or session expired', err);
        window.location.href = '/login';
    }
});

function initRoleBasedUI(role) {
    if (!role) {
        console.warn("Role not found. Access control skipped.");
        return;
    }

    const permissions = {
        ROLE_ADMIN: {
            tabs: ['usersTab', 'restaurantsTab', 'menuTab', 'ordersTab'],
        },
        ROLE_MANAGER: {
            tabs: ['restaurantsTab', 'menuTab', 'ordersTab'],
        },
        ROLE_COURIER: {
            tabs: ['ordersTab'],
        },
        ROLE_CUSTOMER: {
            tabs: ['restaurantsTab', 'menuTab', 'ordersTab'],
        },
    };

    const allowedTabs = permissions[role]?.tabs || [];

    document.querySelectorAll('#adminTabs button').forEach(tabBtn => {
        const targetId = tabBtn.getAttribute('data-bs-target')?.substring(1);
        if (!allowedTabs.includes(targetId)) {
            tabBtn.closest('li').remove();
            document.getElementById(targetId)?.remove();
        }
    });

    if (role !== 'ROLE_ADMIN') {
        document.getElementById('createUserCard')?.remove();
        document.getElementById('showCreateUserBtn')?.remove();
        document.getElementById('usersTab')?.remove();
    }

    if (role === 'ROLE_COURIER') {
        document.getElementById('createOrderCard')?.remove();
        document.getElementById('showCreateOrderBtn')?.remove();

        const observer = new MutationObserver(() => {
            document.querySelectorAll('#order-table-body .btn').forEach(btn => btn.remove());
        });
        const orderTable = document.getElementById('order-table-body');
        if (orderTable) {
            observer.observe(orderTable, { childList: true, subtree: true });
        }
    }

    if (role === 'ROLE_CUSTOMER' || role === 'ROLE_COURIER') {
        if (role === 'ROLE_CUSTOMER') {
            document.getElementById('orderCourierId')?.closest('.form-group')?.remove();
        }

        const userId = localStorage.getItem('userId');
        if (userId) {
            if (typeof loadOrders === 'function') {
                loadOrders(role, parseInt(userId));
            }
        }
    }
}
