const createOrderCard = document.getElementById('createOrderCard');
const showCreateOrderBtn = document.getElementById('showCreateOrderBtn');
const hideCreateOrderBtn = document.getElementById('hideCreateOrderBtn');
const orderFormTitle = document.getElementById('orderFormTitle');
const orderSubmitBtn = document.getElementById('orderSubmitBtn');
const orderIdInput = document.getElementById('orderId');

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/v1/auth/me', { credentials: 'include' });
        if (!response.ok) throw new Error('Unauthorized');

        const user = await response.json();
        console.log('User info:', user);

        const role = user.roles?.[0]?.roleName?.toUpperCase() || user.roles?.[0]?.name?.toUpperCase();

        if (!role) {
            throw new Error('Role not found in user object');
        }

        localStorage.setItem('role', role);
        localStorage.setItem('userId', user.id);

        await loadOrders(role, user.id);
    } catch (err) {
        console.error('Access denied or session expired', err);
        window.location.href = '/login';
    }

    createOrderCard.style.display = 'none';

    showCreateOrderBtn.addEventListener('click', async () => {
        createOrderCard.style.display = 'block';
        showCreateOrderBtn.style.display = 'none';
        orderFormTitle.textContent = 'Create Order';
        orderSubmitBtn.textContent = 'Create';
        orderIdInput.value = '';
        clearOrderForm();
        await loadRestaurantsForOrder();
        await loadCouriersForOrder();
    });

    hideCreateOrderBtn.addEventListener('click', () => {
        createOrderCard.style.display = 'none';
        showCreateOrderBtn.style.display = 'inline-block';
        clearOrderForm();
    });

    function clearOrderForm() {
        const customerNameInput = document.getElementById('orderCustomerName');
        const addressInput = document.getElementById('orderAddress');
        const restaurantSelect = document.getElementById('orderRestaurantId');
        const courierSelect = document.getElementById('orderCourierId');
        const totalPriceInput = document.getElementById('orderTotalPrice');
        const orderItemsInput = document.getElementById('orderItems');

        if (customerNameInput) customerNameInput.value = '';
        if (addressInput) addressInput.value = '';
        if (restaurantSelect) restaurantSelect.innerHTML = '<option value="" selected disabled>Select Restaurant</option>';
        if (courierSelect) courierSelect.innerHTML = '<option value="" selected disabled>Select Courier (optional)</option>';
        if (totalPriceInput) totalPriceInput.value = '';
        if (orderItemsInput) orderItemsInput.value = '';
    }

    async function loadOrders(role, userId) {
        try {
            let url;
            console.log('ROLE:', role);
            if (role === 'ROLE_CUSTOMER' || role === 'ROLE_COURIER') {
                url = '/api/v1/admin/orders/current-user';
            } else {
                url = '/api/v1/admin/orders'; 
            }
            const response = await fetch(url, {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load orders');
            }

            const orders = await response.json();
            renderOrders(orders, role, userId);
            await loadRestaurantsForOrder();
            await loadCouriersForOrder();
        } catch (error) {
            console.error(error);
        }
    }

    function renderOrders(orders, role, userId) {
        let filteredOrders = orders;

        if (role === 'ROLE_COURIER') {
            filteredOrders = orders.filter(order => order.courierId === userId);
        }

        if (role === 'ROLE_CUSTOMER') {
            filteredOrders = orders.filter(order => order.customerId === userId);
        }

        const tbody = document.getElementById('order-table-body');
        tbody.innerHTML = '';
        filteredOrders.forEach(order => {
            const orderItemsStr = order.orderItems && order.orderItems.length > 0
                ? order.orderItems.map(item => `${item.name} x${item.quantity}`).join(', ')
                : '';
            const row = `
                <tr data-customer-id="${order.customerId}" data-courier-id="${order.courierId || ''}">
                    <td>${order.id}</td>
                    <td>${order.customerName}</td>
                    <td>${order.address}</td>
                    <td>${order.restaurantName || ''}</td>
                    <td>${order.courierName || ''}</td>
                    <td>${orderItemsStr}</td>
                    <td>${typeof order.totalPrice === 'number' ? order.totalPrice.toFixed(2) : '0.00'}</td>
                    <td>
                        <button class="btn btn-sm btn-warning me-2" onclick="openEditOrder(${order.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteOrder(${order.id})">Delete</button>
                    </td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', row);
        });
    }

    async function loadRestaurantsForOrder() {
        try {
            const response = await fetch('/api/v1/admin/restaurants', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load restaurants');
            }

            const restaurants = await response.json();
            const select = document.getElementById('orderRestaurantId');
            select.innerHTML = '<option value="" selected disabled>Select Restaurant</option>';
            restaurants.forEach(r => {
                const option = document.createElement('option');
                option.value = r.id;
                option.textContent = r.name;
                select.appendChild(option);
            });
        } catch (error) {
            console.error(error);
        }
    }

    async function loadCouriersForOrder() {
        try {
            const response = await fetch('/api/v1/admin/users?role=ROLE_COURIER', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load couriers');
            }

            const pageData = await response.json();
            const couriers = pageData.content;
            console.log('Couriers loaded for order:', couriers);
            const select = document.getElementById('orderCourierId');
            select.innerHTML = '<option value="" selected disabled>Select Courier (optional)</option>';
            couriers.forEach(courier => {
                const option = document.createElement('option');
                option.value = courier.id;
                option.textContent = courier.firstName + ' ' + courier.lastName;
                select.appendChild(option);
            });
        } catch (error) {
            console.error(error);
        }
    }

    document.getElementById('orderForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const orderCustomerNameEl = document.getElementById('orderCustomerName');
        const orderAddressEl = document.getElementById('orderAddress');
        const orderRestaurantIdEl = document.getElementById('orderRestaurantId');
        const orderCourierIdEl = document.getElementById('orderCourierId');
        const orderTotalPriceEl = document.getElementById('orderTotalPrice');
        const orderItemsEl = document.getElementById('orderItems');

        if (!orderCustomerNameEl || !orderAddressEl || !orderRestaurantIdEl || !orderCourierIdEl || !orderTotalPriceEl || !orderItemsEl) {
            alert('Form elements missing. Please refresh the page.');
            return;
        }

        if (!orderCustomerNameEl.value.trim()) {
            alert('Customer name is required.');
            orderCustomerNameEl.focus();
            return;
        }

        const id = orderIdInput.value;
        const data = {
            customerName: orderCustomerNameEl.value,
            address: orderAddressEl.value,
            restaurantId: orderRestaurantIdEl.value,
            courierId: orderCourierIdEl.value || null,
            totalPrice: parseFloat(orderTotalPriceEl.value),
            orderItems: JSON.parse(orderItemsEl.value)
        };

        try {
            let response;
            if (id) {
                response = await fetch(`/api/v1/admin/orders/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: 'include'
                });
            } else {
                response = await fetch('/api/v1/admin/orders', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: 'include'
                });
            }

            if (response.ok) {
                alert(id ? 'Order updated!' : 'Order created!');
                clearOrderForm();
                orderIdInput.value = '';
                createOrderCard.style.display = 'none';
                showCreateOrderBtn.style.display = 'inline-block';
                loadOrders();
            } else {
                const error = await response.json();
                alert('Error: ' + error.message);
            }
        } catch (err) {
            alert('Request failed');
        }
    });

    window.openEditOrder = async function (orderId) {
        try {
            const response = await fetch(`/api/v1/admin/orders/${orderId}`, {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load order');
            }

            const order = await response.json();

            createOrderCard.style.display = 'block';
            showCreateOrderBtn.style.display = 'none';
            orderFormTitle.textContent = 'Update Order';
            orderSubmitBtn.textContent = 'Update';
            orderIdInput.value = order.id;

            window.populateSelectedDishes(order.orderItems);

            await loadRestaurantsForOrder();
            await loadCouriersForOrder();

            document.getElementById('orderCustomerName').value = order.customerName;
            document.getElementById('orderAddress').value = order.address;
            document.getElementById('orderRestaurantId').value = order.restaurantId;
            document.getElementById('orderCourierId').value = order.courierId || '';
            document.getElementById('orderTotalPrice').value = order.totalPrice.toFixed(2);
            const parsedOrderItems = order.orderItems.map(item => ({
                menuItemId: item.menuItemId,
                quantity: item.quantity
            }));
            document.getElementById('orderItems').value = JSON.stringify(parsedOrderItems);
            await loadAllDishes(parsedOrderItems);

        } catch (err) {
            alert('Failed to load order data');
            console.error('Error loading order data:', err);
        }
    };


    window.deleteOrder = async function (orderId) {
        if (!confirm('Are you sure you want to delete this order?')) return;

        try {
            const response = await fetch(`/api/v1/admin/orders/${orderId}`, {
                method: 'DELETE',
                credentials: 'include'
            });

            if (response.ok) {
                loadOrders();
            } else {
                alert('Error deleting order');
            }
        } catch (err) {
            alert('Request failed');
        }
    };
});
