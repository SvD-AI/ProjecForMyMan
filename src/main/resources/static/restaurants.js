document.addEventListener('DOMContentLoaded', () => {
    loadRestaurants();

    const createRestaurantCard = document.getElementById('createRestaurantCard');
    const showCreateRestaurantBtn = document.getElementById('showCreateRestaurantBtn');
    const hideCreateRestaurantBtn = document.getElementById('hideCreateRestaurantBtn');
    const restaurantFormTitle = document.getElementById('restaurantFormTitle');
    const restaurantSubmitBtn = document.getElementById('restaurantSubmitBtn');
    const restaurantIdInput = document.getElementById('restaurantId');

    createRestaurantCard.style.display = 'none';

    showCreateRestaurantBtn.addEventListener('click', () => {
        createRestaurantCard.style.display = 'block';
        showCreateRestaurantBtn.style.display = 'none';
        restaurantFormTitle.textContent = 'Create Restaurant';
        restaurantSubmitBtn.textContent = 'Create';
        restaurantIdInput.value = '';
        clearRestaurantForm();
    });

    hideCreateRestaurantBtn.addEventListener('click', () => {
        createRestaurantCard.style.display = 'none';
        showCreateRestaurantBtn.style.display = 'inline-block';
        clearRestaurantForm();
    });

    function clearRestaurantForm() {
        document.getElementById('restaurantName').value = '';
        document.getElementById('restaurantCity').value = '';
        document.getElementById('restaurantRating').value = '';
    }

    document.getElementById('restaurantForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const id = restaurantIdInput.value;
        const data = {
            name: document.getElementById('restaurantName').value,
            city: document.getElementById('restaurantCity').value,
            rating: parseFloat(document.getElementById('restaurantRating').value) || null
        };

        try {
            let response;
            if (id) {
                response = await fetch(`/api/v1/admin/restaurants/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: "include"
                });
            } else {
                response = await fetch('/api/v1/admin/restaurants', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: "include"
                });
            }

            if (response.ok) {
                alert(id ? 'Restaurant updated!' : 'Restaurant created!');
                clearRestaurantForm();
                restaurantIdInput.value = '';
                createRestaurantCard.style.display = 'none';
                showCreateRestaurantBtn.style.display = 'inline-block';
                loadRestaurants();
            } else {
                const error = await response.json();
                alert('Error: ' + error.message);
            }
        } catch (err) {
            alert('Request failed');
        }
    });

    window.openEditRestaurant = async function (restaurantId) {
        try {
            const response = await fetch(`/api/v1/admin/restaurants/${restaurantId}`, {
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("Failed to load restaurant");
            }

            const restaurant = await response.json();

            createRestaurantCard.style.display = 'block';
            showCreateRestaurantBtn.style.display = 'none';
            restaurantFormTitle.textContent = 'Update Restaurant';
            restaurantSubmitBtn.textContent = 'Update';
            restaurantIdInput.value = restaurant.id;

            document.getElementById('restaurantName').value = restaurant.name;
            document.getElementById('restaurantCity').value = restaurant.city;
            document.getElementById('restaurantRating').value = restaurant.rating || '';

        } catch (err) {
            alert("Failed to load restaurant data");
        }
    };

    window.deleteRestaurant = async function (restaurantId) {
        if (!confirm("Are you sure you want to delete this restaurant?")) return;

        try {
            const response = await fetch(`/api/v1/admin/restaurants/${restaurantId}`, {
                method: "DELETE",
                credentials: "include"
            });

            if (response.ok) {
                loadRestaurants();
            } else {
                alert("Error deleting restaurant");
            }
        } catch (err) {
            alert("Request failed");
        }
    };

    async function loadRestaurants() {
        try {
            const response = await fetch("/api/v1/admin/restaurants", {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("Failed to load restaurants");
            }

            const restaurants = await response.json();

            renderRestaurants(restaurants);

        } catch (error) {
            console.error(error);
        }
    }

    function renderRestaurants(restaurants) {
        const tbody = document.getElementById("restaurant-table-body");
        tbody.innerHTML = "";
        restaurants.forEach(restaurant => {
            const row = `
                <tr>
                    <td>${restaurant.id}</td>
                    <td>${restaurant.name}</td>
                    <td>${restaurant.city}</td>
                    <td>${restaurant.rating !== null && restaurant.rating !== undefined ? restaurant.rating : ''}</td>
                    <td>
                        <button class="btn btn-sm btn-warning me-2" onclick="openEditRestaurant(${restaurant.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteRestaurant(${restaurant.id})">Delete</button>
                    </td>
                </tr>
            `;
            tbody.insertAdjacentHTML("beforeend", row);
        });
    }
});
