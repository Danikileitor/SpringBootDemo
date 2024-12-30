document.addEventListener('DOMContentLoaded', () => {
    const authSection = document.getElementById('auth-section');
    const adminSection = document.getElementById('admin-section');
    const loginForm = document.getElementById('loginForm');
    const logoutButton = document.getElementById('logout-button');
    const token = localStorage.getItem('token');

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
            const token = await response.text(); // Puedes guardar un JWT si usas uno
            localStorage.setItem('token', token);
            toggleSections();
            cargarPanelAdmin();
        } else {
            alert('Usuario o contraseña incorrectos');
        }
    });

    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('token');
        toggleSections();
    });

    // Cargar skins disponibles con checkboxes
    const loadSkins = async () => {
        const response = await fetch('/admin/api/skins', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });

        if (!response.ok) {
            throw new Error('No se pudo cargar las skins');
        }

        return response.json();
    };

    const cargarPanelAdmin = () => {
        const userTableBody = document.querySelector('#userTable tbody');

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
                    // Limpiar tabla antes de cargar
                    userTableBody.innerHTML = '';

                    data.forEach(user => {
                        const row = document.createElement('tr');
                        row.dataset.user = JSON.stringify(user);  // Aquí agregamos la referencia al usuario en la fila
                        row.innerHTML = `
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>
                        <select data-id="${user.id}" class="user-role">
                            <option value="ROLE_USER" ${user.rol === 'ROLE_USER' ? 'selected' : ''}>USER</option>
                            <option value="ROLE_ADMIN" ${user.rol === 'ROLE_ADMIN' ? 'selected' : ''}>ADMIN</option>
                            <option value="ROLE_VIP" ${user.rol === 'ROLE_VIP' ? 'selected' : ''}>VIP</option>
                        </select>
                    </td>
                    <td>
                        <div class="skins">
                            <button data-id="${user.id}" class="edit-skins-button">✏️</button>
                            <div class="user-skins">${user.skins.map(skin => skin).join(', ')}</div>
                            <div data-id="${user.id}" class="skins-checkboxes-container" style="display: none;">
                                <!-- Se llenará con las skins -->
                            </div>
                        </div>
                    </td>
                    <td><button data-id="${user.id}" class="save-button">Guardar</button></td>
                    `;
                        userTableBody.appendChild(row);
                    });

                    // Delegado de eventos para los botones de "Editar Skins"
                    userTableBody.addEventListener('click', async (e) => {
                        // Editar Skins (Mostrar checkboxes)
                        if (e.target && e.target.classList.contains('edit-skins-button')) {
                            const row = e.target.closest('tr');
                            const user = JSON.parse(row.dataset.user);  // Recuperamos el usuario desde la fila
                            const userSkins = e.target.closest('td').querySelector('.user-skins');
                            const checkboxesContainer = row.querySelector('.skins-checkboxes-container');

                            if (checkboxesContainer.style.display === 'none') {
                                userSkins.style.display = 'none';
                                checkboxesContainer.style.display = 'block';
                            } else {
                                checkboxesContainer.style.display = 'none';
                                userSkins.style.display = 'block';
                            }

                            if (checkboxesContainer.style.display === 'block') {
                                // Cargar las skins disponibles
                                const skinsData = await loadSkins();

                                // Limpiar contenedor previo y agregar las skins
                                checkboxesContainer.innerHTML = ''; // Limpiar contenedor

                                skinsData.forEach(skin => {
                                    const checkboxLabel = document.createElement('label');
                                    const checkbox = document.createElement('input');
                                    checkbox.type = 'checkbox';
                                    checkbox.value = skin;
                                    checkbox.name = 'skins';

                                    const span = document.createElement('span');
                                    span.textContent = skin;
                                    checkboxLabel.appendChild(checkbox);
                                    checkboxLabel.appendChild(span);

                                    checkboxesContainer.appendChild(checkboxLabel);
                                    checkboxesContainer.appendChild(document.createElement('br'));
                                });

                                // Pre-seleccionar skins del usuario
                                const currentSkins = user.skins || [];
                                currentSkins.forEach(skin => {
                                    const checkbox = checkboxesContainer.querySelector(`input[value="${skin}"]`);
                                    if (checkbox) {
                                        checkbox.checked = true;
                                    }
                                });
                            }
                        }

                        // Guardar los cambios (rol y skins)
                        if (e.target && e.target.classList.contains('save-button')) {
                            const id = e.target.dataset.id;
                            const row = e.target.closest('tr');
                            const roleSelect = row.querySelector('.user-role').value;
                            const checkboxesContainer = row.querySelector('.skins-checkboxes-container');

                            // Obtener las skins seleccionadas
                            const selectedSkins = Array.from(checkboxesContainer.querySelectorAll('input:checked'))
                                .map(checkbox => checkbox.value);

                            // Realizar el PUT con los datos actualizados
                            const response = await fetch(`/admin/api/users/${id}`, {
                                method: 'PUT',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                                },
                                body: JSON.stringify({ rol: roleSelect, skins: selectedSkins })
                            });

                            if (response.ok) {
                                alert('Usuario actualizado');
                                location.reload();
                            } else {
                                alert('Error al actualizar el usuario');
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        };

        loadUsers();
    };

    if (token) {
        cargarPanelAdmin();
    }

    toggleSections();
});