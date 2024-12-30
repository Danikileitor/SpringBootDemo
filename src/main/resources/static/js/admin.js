document.addEventListener('DOMContentLoaded', () => {
    const authSection = document.getElementById('auth-section');
    const adminSection = document.getElementById('admin-section');
    const loginForm = document.getElementById('loginForm');
    const logoutButton = document.getElementById('logout-button');
    const token = localStorage.getItem('token');

    // Simular estado de sesión del usuario
    const checkAuth = () => !!localStorage.getItem('token');

    const toggleSections = () => {
        if (checkAuth()) {
            authSection.style.display = 'none';
            adminSection.style.display = 'block';
        } else {
            authSection.style.display = 'block';
            adminSection.style.display = 'none';
        }
    };

    // Manejo del formulario de login
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

        const response = await fetch('/api/auth/admin/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const token = await response.text(); // Podrías guardar un JWT si usas uno
            localStorage.setItem('token', token);
            toggleSections();
            cargarPanelAdmin();
        } else {
            alert('Usuario o contraseña incorrectos');
        }
    });

    // Manejo del cierre de sesión
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('token');
        toggleSections();
    });

    // Manejo para cargar el panel del admin
    const cargarPanelAdmin = () => {
        const userTableBody = document.querySelector('#userTable tbody');

        // Cargar usuarios
        const loadUsers = () => {
            fetch('/admin/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('No se pudo cargar los usuarios');
                }
                return response.json();
            })
            .then(data => {
                // Limpia la tabla
                userTableBody.innerHTML = '';

                data.forEach(user => {
                    console.info(user);
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>
                    <select data-id="${user.id}" class="user-role">
                    <option value="USER" ${user.rol === 'USER' ? 'selected' : ''}>USER</option>
                    <option value="ADMIN" ${user.rol === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    <option value="VIP" ${user.rol === 'VIP' ? 'selected' : ''}>VIP</option>
                    </select>
                    </td>
                    <td>${user.skins.map(skin => skin).join(', ')}</td>
                    <td><button data-id="${user.id}" class="save-button">Guardar</button></td>
                    `;
                    userTableBody.appendChild(row);
                });
            }).catch(error => {
                console.error('Error:', error);
            });

            // Añadir eventos a los botones
            document.querySelectorAll('.save-button').forEach(button => {
                button.addEventListener('click', async (e) => {
                    const id = e.target.dataset.id;
                    const row = e.target.closest('tr');
                    const roleSelect = row.querySelector('.user-role').value;

                    const response = await fetch(`/admin/api/users/${id}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': 'Bearer ' + localStorage.getItem('token')
                        },
                        body: JSON.stringify({ rol: roleSelect })
                    });

                    if (response.ok) {
                        alert('Usuario actualizado');
                    } else {
                        alert('Error al actualizar el usuario');
                    }
                });
            });
        };

        // Inicializa la tabla
        loadUsers();
    };

    // Comprueba si hay token almacenado
    if (token) {
        cargarPanelAdmin();
    }

    // Inicializa el estado inicial
    toggleSections();
});