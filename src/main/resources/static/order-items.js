let allDishes = [];
let selectedDishes = [];
let currentRestaurantId = null;
let restaurantSelect;


document.addEventListener('DOMContentLoaded', () => {
    restaurantSelect = document.getElementById('orderRestaurantId');
    const orderItemsTextarea = document.getElementById('orderItems');
    if (!orderItemsTextarea || !orderItemsTextarea.parentNode) {
        console.error('orderItems textarea or its parent not found in DOM');
        return;
    }
    const selectedDishesContainer = document.createElement('div');
    selectedDishesContainer.id = 'selectedDishesContainer';
    selectedDishesContainer.style.marginBottom = '1rem';

    const dishListContainer = document.createElement('div');
    dishListContainer.id = 'dishListContainer';

    const orderForm = document.getElementById('orderForm');
    const orderItemsInput = document.getElementById('orderItems');

    orderItemsTextarea.parentNode.insertBefore(selectedDishesContainer, orderItemsTextarea);
    orderItemsTextarea.parentNode.insertBefore(dishListContainer, orderItemsTextarea);
    orderItemsTextarea.style.display = 'none';


    async function loadAllDishes(orderItems = []) {
        try {
            const response = await fetch('/api/v1/admin/menu-items', { credentials: 'include' });
            if (!response.ok) throw new Error('Failed to load menu items');
            allDishes = await response.json();

            selectedDishes = [];

            if (orderItems.length > 0) {
                for (const item of orderItems) {
                    const dish = allDishes.find(d => d.id === item.menuItemId);
                    if (dish) {
                        selectedDishes.push({ ...dish, quantity: item.quantity });
                    } else {
                        console.warn('Dish not found for menuItemId:', item.menuItemId);
                    }
                }

                if (selectedDishes.length > 0) {
                    currentRestaurantId = selectedDishes[0].restaurantId;
                    restaurantSelect.value = currentRestaurantId;
                    restaurantSelect.disabled = true;
                } else {
                    console.warn('No matching dishes found for the orderItems!');
                    currentRestaurantId = null;
                    restaurantSelect.value = '';
                    restaurantSelect.disabled = false;
                }
            } else {
                currentRestaurantId = restaurantSelect.value || null;
                restaurantSelect.disabled = false;
            }

            renderSelectedDishes();
            renderDishList();
            console.log("Restaurant Select Value:", restaurantSelect.value);
            console.log("Current Restaurant ID:", currentRestaurantId);


        } catch (err) {
            console.error(err);
        }
    }
    /**
     * Populates the selected dishes UI and internal state from the given order items.
     * Disables restaurant selection if there are selected dishes.
     * @param {Array} orderItems - Array of order item objects with menuItemId and quantity.
     */
    window.populateSelectedDishes = function (orderItems) {
        selectedDishes = [];

        for (const item of orderItems) {
            const dish = allDishes.find(d => d.id === item.menuItemId);
            if (dish) {
                selectedDishes.push({
                    ...dish,
                    quantity: item.quantity
                });
            }
        }

        restaurantSelect.disabled = selectedDishes.length > 0;

        renderSelectedDishes();
        renderDishList();
    };

    function renderDishList() {
        dishListContainer.innerHTML = '';

        if (!currentRestaurantId) {
            dishListContainer.textContent = 'Please select a restaurant to view dishes.';
            return;
        }

        let filteredDishes = allDishes.filter(d => d.restaurantId === parseInt(currentRestaurantId));

        const selectedIds = selectedDishes.map(d => d.id);
        filteredDishes = filteredDishes.filter(d => !selectedIds.includes(d.id));

        if (filteredDishes.length === 0) {
            dishListContainer.textContent = 'No dishes available for the selected restaurant.';
            return;
        }

        filteredDishes.forEach(dish => {
            const dishDiv = document.createElement('div');
            dishDiv.className = 'dish-item';
            dishDiv.style.border = '1px solid #ccc';
            dishDiv.style.padding = '0.5rem';
            dishDiv.style.marginBottom = '0.5rem';
            dishDiv.style.cursor = 'pointer';
            dishDiv.textContent = `${dish.name} - $${dish.price.toFixed(2)}`;
            dishDiv.addEventListener('click', () => {
                addDish(dish);
            });
            dishListContainer.appendChild(dishDiv);
        });
    }

    function renderSelectedDishes() {
        selectedDishesContainer.innerHTML = '';
        if (selectedDishes.length === 0) {
            selectedDishesContainer.textContent = 'No dishes selected';
            restaurantSelect.disabled = false; 
            updateOrderItemsInput();
            return;
        }

        selectedDishes.forEach(dish => {
            const dishDiv = document.createElement('div');
            dishDiv.className = 'selected-dish-item';
            dishDiv.style.border = '1px solid #007bff';
            dishDiv.style.padding = '0.5rem';
            dishDiv.style.marginBottom = '0.5rem';
            dishDiv.style.display = 'flex';
            dishDiv.style.justifyContent = 'space-between';
            dishDiv.style.alignItems = 'center';

            const nameSpan = document.createElement('span');
            nameSpan.textContent = `${dish.name} - $${dish.price.toFixed(2)}`;

            const quantityControls = document.createElement('div');

            const decrementBtn = document.createElement('button');
            decrementBtn.textContent = '-';
            decrementBtn.className = 'btn btn-sm btn-outline-primary me-2';
            decrementBtn.addEventListener('click', () => {
                changeDishQuantity(dish.id, -1);
            });

            const quantitySpan = document.createElement('span');
            quantitySpan.textContent = dish.quantity;

            const incrementBtn = document.createElement('button');
            incrementBtn.textContent = '+';
            incrementBtn.className = 'btn btn-sm btn-outline-primary ms-2';
            incrementBtn.addEventListener('click', () => {
                changeDishQuantity(dish.id, 1);
            });

            const removeBtn = document.createElement('button');
            removeBtn.textContent = 'Remove';
            removeBtn.className = 'btn btn-sm btn-outline-danger ms-3';
            removeBtn.addEventListener('click', () => {
                removeDish(dish.id);
            });

            quantityControls.appendChild(decrementBtn);
            quantityControls.appendChild(quantitySpan);
            quantityControls.appendChild(incrementBtn);
            quantityControls.appendChild(removeBtn);

            dishDiv.appendChild(nameSpan);
            dishDiv.appendChild(quantityControls);

            selectedDishesContainer.appendChild(dishDiv);
        });

        updateOrderItemsInput();
    }

    function addDish(dish) {
        if (selectedDishes.length === 0) {
            currentRestaurantId = dish.restaurantId;
            restaurantSelect.value = currentRestaurantId;
            restaurantSelect.disabled = true;
        }
        selectedDishes.push({...dish, quantity: 1});
        renderSelectedDishes();
        renderDishList();
    }

    restaurantSelect.addEventListener('change', () => {
        const selectedValue = restaurantSelect.value;
        if (selectedDishes.length > 0 && selectedValue !== currentRestaurantId) {
            if (!confirm('Changing the restaurant will clear selected dishes. Continue?')) {
                restaurantSelect.value = currentRestaurantId;
                return;
            }
            selectedDishes = [];
            renderSelectedDishes();
            restaurantSelect.disabled = false;
        }
        currentRestaurantId = selectedValue;
        renderDishList();
    });

    function changeDishQuantity(dishId, delta) {
        const dish = selectedDishes.find(d => d.id === dishId);
        if (!dish) return;

        const newQuantity = dish.quantity + delta;
        if (newQuantity > 0) {
            dish.quantity = newQuantity;
        } else {
            removeDish(dishId);
            return;
        }
        renderSelectedDishes();
    }

    function removeDish(dishId) {
        selectedDishes = selectedDishes.filter(d => d.id !== dishId);
        renderSelectedDishes();
        renderDishList();
    }

    function updateOrderItemsInput() {
        const orderItems = selectedDishes.map(d => ({menuItemId: d.id, quantity: d.quantity}));
        orderItemsInput.value = JSON.stringify(orderItems);
    }

    window.loadOrderForEdit = function(order) {
        document.getElementById('order-id').value = order.id;
        document.getElementById('customer-name').value = order.customerName;
        document.getElementById('address').value = order.address;
        document.getElementById('courier').value = order.courierName;

        if (order.restaurantId) {
            currentRestaurantId = order.restaurantId;
            restaurantSelect.value = currentRestaurantId;
            restaurantSelect.disabled = true;
        } else if (order.restaurantName) {
            const option = Array.from(restaurantSelect.options).find(opt => opt.textContent === order.restaurantName);
            if (option) {
                currentRestaurantId = option.value;
                restaurantSelect.value = currentRestaurantId;
                restaurantSelect.disabled = true;
            } else {
                console.error('Restaurant not found for name:', order.restaurantName);
            }
        } else {
            currentRestaurantId = null;
            restaurantSelect.value = '';
            restaurantSelect.disabled = false;
        }

        if (order.orderItems && Array.isArray(order.orderItems)) {
            window.populateSelectedDishes(order.orderItems);
        } else {
            selectedDishes = [];
            renderSelectedDishes();
            renderDishList();
        }
    };

    loadAllDishes();

});
