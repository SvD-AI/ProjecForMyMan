document.addEventListener('DOMContentLoaded', () => {
    loadUsers();

    const createUserCard = document.getElementById('createUserCard');
    if (createUserCard) {
        createUserCard.style.display = 'none';
    }

    const showCreateUserBtn = document.getElementById('showCreateUserBtn');
    const hideCreateUserBtn = document.getElementById('hideCreateUserBtn');
    const formTitle = document.getElementById('formTitle');
    const submitBtn = document.getElementById('submitBtn');
    const userIdInput = document.getElementById('userId');

    if (showCreateUserBtn && createUserCard && hideCreateUserBtn) {
        showCreateUserBtn.addEventListener('click', () => {
            createUserCard.style.display = 'block';
            showCreateUserBtn.style.display = 'none';
            formTitle.textContent = 'Create User';
            submitBtn.textContent = 'Create';
            userIdInput.value = '';
            clearForm();
        });

        hideCreateUserBtn.addEventListener('click', () => {
            createUserCard.style.display = 'none';
            showCreateUserBtn.style.display = 'inline-block';
            clearForm();
        });
    }

    function clearForm() {
        document.getElementById('firstName').value = '';
        document.getElementById('lastName').value = '';
        document.getElementById('email').value = '';
        document.getElementById('phoneNumber').value = '';
        document.getElementById('password').value = '';
        document.getElementById('role').value = '';
    }

    document.getElementById('userForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const id = userIdInput.value;
        const data = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            username: document.getElementById('email').value,
            phoneNumber: document.getElementById('phoneNumber').value,
            password: document.getElementById('password').value,
            role: document.getElementById('role').value.toLowerCase()
        };

        try {
            let response;
            if (id) {
                response = await fetch(`/api/v1/admin/users/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        firstName: data.firstName,
                        lastName: data.lastName,
                        username: data.username,
                        phoneNumber: data.phoneNumber,
                        roles: [data.role]
                    }),
                    credentials: "include"
                });
            } else {
                response = await fetch('/api/v1/admin/create-user', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: "include"
                });
            }

            if (response.ok) {
                alert(id ? 'User updated!' : 'User created!');
                clearForm();
                userIdInput.value = '';
                createUserCard.style.display = 'none';
                showCreateUserBtn.style.display = 'inline-block';
                loadUsers();
            } else {
                const error = await response.json();
                alert('Error: ' + error.message);
            }
        } catch (err) {
            alert('Request failed');
        }
    });

    window.openEditModal = async function (userId) {
        try {
            const response = await fetch(`/api/v1/admin/users/${userId}`, {
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("Failed to load user");
            }

            const user = await response.json();

            createUserCard.style.display = 'block';
            showCreateUserBtn.style.display = 'none';
            formTitle.textContent = 'Update User';
            submitBtn.textContent = 'Update';
            userIdInput.value = user.id;

            document.getElementById("firstName").value = user.firstName;
            document.getElementById("lastName").value = user.lastName;
            document.getElementById("email").value = user.username;
            document.getElementById("phoneNumber").value = user.phoneNumber;
            document.getElementById("password").value = '';
            document.getElementById("role").value = user.roles[0]?.name.toUpperCase() || '';

        } catch (err) {
            alert("Failed to load user data");
        }
    };

    window.deleteUser = async function (userId) {
        if (!confirm("Are you sure you want to delete this user?")) return;

        try {
            const response = await fetch(`/api/v1/admin/users/${userId}`, {
                method: "DELETE",
                credentials: "include"
            });

            if (response.ok) {
                loadUsers();
            } else {
                alert("Error deleting user");
            }
        } catch (err) {
            alert("Request failed");
        }
    };

    async function loadUsers() {
        try {
            const response = await fetch("/api/v1/admin/users", {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) {
                throw new Error("Failed to load users");
            }

            const pageData = await response.json();
            const users = pageData.content;

            renderUsers(users);

        } catch (error) {
            console.error(error);
        }
    }

    function renderUsers(users) {
        const tbody = document.getElementById("user-table-body");
        tbody.innerHTML = "";
        users.forEach(user => {
            const roles = user.roles.map(r => r.name).join(", ");
            const row = `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td>${user.username}</td>
                    <td>${user.phoneNumber || ""}</td>
                    <td>${roles}</td>
                    <td>
                        <button class="btn btn-sm btn-warning me-2" onclick="openEditModal(${user.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">Delete</button>
                    </td>
                </tr>
            `;
            tbody.insertAdjacentHTML("beforeend", row);
        });
    }
});
