document.addEventListener('DOMContentLoaded', () => {
    loadMenuItems();

    const createMenuItemCard = document.getElementById('createMenuItemCard');
    const showCreateMenuItemBtn = document.getElementById('showCreateMenuItemBtn');
    const hideCreateMenuItemBtn = document.getElementById('hideCreateMenuItemBtn');
    const menuItemFormTitle = document.getElementById('menuItemFormTitle');
    const menuItemSubmitBtn = document.getElementById('menuItemSubmitBtn');
    const menuItemIdInput = document.getElementById('menuItemId');

    createMenuItemCard.style.display = 'none';

    showCreateMenuItemBtn.addEventListener('click', async () => {
        createMenuItemCard.style.display = 'block';
        showCreateMenuItemBtn.style.display = 'none';
        menuItemFormTitle.textContent = 'Create Menu Item';
        menuItemSubmitBtn.textContent = 'Create';
        menuItemIdInput.value = '';
        clearMenuItemForm();
        await loadRestaurantsForMenuItem();
    });

    hideCreateMenuItemBtn.addEventListener('click', () => {
        createMenuItemCard.style.display = 'none';
        showCreateMenuItemBtn.style.display = 'inline-block';
        clearMenuItemForm();
    });

    function clearMenuItemForm() {
        document.getElementById('menuItemRestaurantId').innerHTML = '<option value="" selected disabled>Select Restaurant</option>';
        document.getElementById('menuItemName').value = '';
        document.getElementById('menuItemPrice').value = '';
    }

    async function loadMenuItems() {
        try {
            const response = await fetch('/api/v1/admin/menu-items', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load menu items');
            }

            const menuItems = await response.json();
            renderMenuItems(menuItems);
            await loadRestaurantsForMenuItem();
        } catch (error) {
            console.error(error);
        }
    }

    function renderMenuItems(menuItems) {
        const tbody = document.getElementById('menu-item-table-body');
        tbody.innerHTML = '';
        menuItems.forEach(item => {
            const row = `
                <tr>
                    <td>${item.id}</td>
                    <td>${item.restaurantName || ''}</td>
                    <td>${item.name}</td>
                    <td>${item.price.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-sm btn-warning me-2" onclick="openEditMenuItem(${item.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteMenuItem(${item.id})">Delete</button>
                    </td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', row);
        });
    }

    async function loadRestaurantsForMenuItem() {
        try {
            const response = await fetch('/api/v1/admin/restaurants', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load restaurants');
            }

            const restaurants = await response.json();
            console.log('Restaurants loaded for menu item:', restaurants);
            const select = document.getElementById('menuItemRestaurantId');
            if (!select) {
                console.error('menuItemRestaurantId select element not found');
                return;
            }
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

    document.getElementById('menuItemForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const id = menuItemIdInput.value;
        const data = {
            restaurantId: document.getElementById('menuItemRestaurantId').value,
            name: document.getElementById('menuItemName').value,
            price: parseFloat(document.getElementById('menuItemPrice').value)
        };

        try {
            let response;
            if (id) {
                response = await fetch(`/api/v1/admin/menu-items/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: 'include'
                });
            } else {
                response = await fetch('/api/v1/admin/menu-items', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: 'include'
                });
            }

            if (response.ok) {
                alert(id ? 'Menu item updated!' : 'Menu item created!');
                clearMenuItemForm();
                menuItemIdInput.value = '';
                createMenuItemCard.style.display = 'none';
                showCreateMenuItemBtn.style.display = 'inline-block';
                loadMenuItems();
            } else {
                const error = await response.json();
                alert('Error: ' + error.message);
            }
        } catch (err) {
            alert('Request failed');
        }
    });

    window.openEditMenuItem = async function (menuItemId) {
        try {
            const response = await fetch(`/api/v1/admin/menu-items/${menuItemId}`, {
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load menu item');
            }

            const item = await response.json();

            createMenuItemCard.style.display = 'block';
            showCreateMenuItemBtn.style.display = 'none';
            menuItemFormTitle.textContent = 'Update Menu Item';
            menuItemSubmitBtn.textContent = 'Update';
            menuItemIdInput.value = item.id;

            await loadRestaurantsForMenuItem();

            document.getElementById('menuItemRestaurantId').value = item.restaurantId;
            document.getElementById('menuItemName').value = item.name;
            document.getElementById('menuItemPrice').value = item.price.toFixed(2);

        } catch (err) {
            alert('Failed to load menu item data');
        }
    };

    window.deleteMenuItem = async function (menuItemId) {
        if (!confirm('Are you sure you want to delete this menu item?')) return;

        try {
            const response = await fetch(`/api/v1/admin/menu-items/${menuItemId}`, {
                method: 'DELETE',
                credentials: 'include'
            });

            if (response.ok) {
                loadMenuItems();
            } else {
                alert('Error deleting menu item');
            }
        } catch (err) {
            alert('Request failed');
        }
    };
});
