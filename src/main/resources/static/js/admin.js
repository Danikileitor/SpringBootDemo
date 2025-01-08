document.addEventListener('DOMContentLoaded', () => {
    const authSection = document.getElementById('auth-section');
    const adminSection = document.getElementById('admin-section');
    const loginForm = document.getElementById('loginForm');
    const logoutButton = document.getElementById('logout-button');
    const token = localStorage.getItem('token');

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

    // Cargar skins disponibles
    const loadSkins = async () => {
        const response = await fetch('/skins', {
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
                        <div class="coins">
                            <button data-id="${user.id}" class="edit-coins-button">✏️</button>
                            <span>${user.coins}</span>
                            <input type="number" class="coins-input" value="${user.coins}" style="display: none;">
                        </div>
                    </td>
                    <td>
                        <div class="skins">
                            <button data-id="${user.id}" class="edit-skins-button">✏️</button>
                            <div class="user-skins">${user.skins.toReversed().map(skin => skin.name).join(', ')}</div>
                            <div data-id="${user.id}" class="skins-checkboxes-container" style="display: none;">
                                <!-- Se llenará con las skins -->
                            </div>
                        </div>
                    </td>
                    <td>
                        <div class="acciones">
                            <button data-id="${user.id}" class="save-button">Guardar</button>
                            <button data-id="${user.id}" class="delete-button">Eliminar</button>
                        </div>
                    </td>
                    `;
                        userTableBody.appendChild(row);
                    });

                    // Delegado de eventos para los botones de "Editar ✏️"
                    userTableBody.addEventListener('click', async (e) => {
                        // Editar monedas
                        if (e.target && e.target.classList.contains('edit-coins-button')) {
                            const row = e.target.closest('tr');
                            const user = JSON.parse(row.dataset.user);
                            const coinsSpan = row.querySelector('span');
                            const coinsInput = row.querySelector('.coins-input');

                            if (coinsInput.style.display === 'none') {
                                coinsSpan.style.display = 'none';
                                coinsInput.style.display = 'block';
                            } else {
                                coinsInput.style.display = 'none';
                                coinsSpan.style.display = 'block';
                            }

                            if (coinsInput.style.display === 'block') {
                                coinsInput.value = user.coins;
                            }
                        }
                        // Editar Skins (Mostrar checkboxes)
                        if (e.target && e.target.classList.contains('edit-skins-button')) {
                            const row = e.target.closest('tr');
                            const user = JSON.parse(row.dataset.user);
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

                                // Limpiar contenedor previo y agregar las skins con checkboxes
                                checkboxesContainer.innerHTML = ''; // Limpiar contenedor

                                skinsData.forEach(skin => {
                                    const checkboxLabel = document.createElement('label');
                                    const checkbox = document.createElement('input');
                                    checkbox.type = 'checkbox';
                                    checkbox.value = skin.id;
                                    checkbox.name = 'skins';

                                    const span = document.createElement('span');
                                    span.textContent = skin.name;
                                    checkboxLabel.appendChild(checkbox);
                                    checkboxLabel.appendChild(span);

                                    checkboxesContainer.appendChild(checkboxLabel);
                                    checkboxesContainer.appendChild(document.createElement('br'));
                                });

                                // Pre-seleccionar skins del usuario
                                const currentSkins = user.skins || [];
                                currentSkins.forEach(skin => {
                                    const checkbox = checkboxesContainer.querySelector(`input[value="${skin.id}"]`);
                                    if (checkbox) {
                                        checkbox.checked = true;
                                    }
                                });
                            }
                        }

                        // Guardar los cambios (rol, monedas y skins)
                        if (e.target && e.target.classList.contains('save-button')) {
                            const id = e.target.dataset.id;
                            const row = e.target.closest('tr');
                            const roleSelect = row.querySelector('.user-role').value;
                            const coinsInput = parseInt(row.querySelector('.coins-input').value);
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
                                body: JSON.stringify({ rol: roleSelect, coins: coinsInput, skinsId: selectedSkins })
                            });

                            if (response.ok) {
                                alert('Usuario actualizado');
                                location.reload();
                            } else {
                                alert('Error al actualizar el usuario');
                            }
                        }

                        // Eliminar usuario con confirmación
                        if (e.target && e.target.classList.contains('delete-button')) {
                            const id = e.target.dataset.id;
                            const nombre = e.target.closest('tr').cells[0].innerHTML;
                            const confirmDelete = confirm(`¿Estás seguro de que deseas eliminar al usuario ${nombre}?`);

                            if (confirmDelete) {
                                const response = await fetch(`/admin/api/users/${id}`, {
                                    method: 'DELETE',
                                    headers: {
                                        'Content-Type': 'application/json',
                                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                                    }
                                });

                                if (response.ok) {
                                    alert('Usuario ' + nombre + ' eliminado con éxito');
                                    location.reload();
                                } else {
                                    alert('Error al eliminar al usuario ' + nombre);
                                }
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        };

        const loadSkinPanel = async () => {
            const skinSection = document.getElementById('skins-section');
            //Formulario para crear skin
            const skinFormulario = document.createElement('form');
            const skinFormularioTitle = document.createElement('h2');
            const skinFormularioTable = document.createElement('table');
            const skinFormularioFila = document.createElement('tr');
            const skinFormularioNombre = document.createElement('td');
            const skinFormularioPrecio = document.createElement('td');
            const skinFormularioDescription = document.createElement('td');
            const skinFormularioReels = document.createElement('td');
            const skinFormularioVendible = document.createElement('td');
            const skinFormularioCrear = document.createElement('td');

            const nombreInput = document.createElement('input');
            nombreInput.type = 'text';
            nombreInput.placeholder = 'Nombre';
            nombreInput.title = 'Nombre';
            nombreInput.id = 'skinFormularioNombre';
            skinFormularioNombre.appendChild(nombreInput);
            skinFormularioFila.appendChild(skinFormularioNombre);

            const precioInput = document.createElement('input');
            precioInput.type = 'number';
            precioInput.title = 'Precio';
            precioInput.id = 'skinFormularioPrecio';
            skinFormularioPrecio.appendChild(precioInput);
            skinFormularioFila.appendChild(skinFormularioPrecio);

            const descriptionInput = document.createElement('input');
            descriptionInput.type = 'text';
            descriptionInput.placeholder = 'Descripción';
            descriptionInput.title = 'Descripción';
            descriptionInput.id = 'skinFormularioDescription';
            skinFormularioDescription.appendChild(descriptionInput);
            skinFormularioFila.appendChild(skinFormularioDescription);

            const reelsInput = document.createElement('input');
            reelsInput.type = 'text';
            reelsInput.placeholder = '😀,😅,😆,😊,😎';
            reelsInput.title = 'Emojis';
            reelsInput.id = 'skinFormularioReels';
            skinFormularioReels.appendChild(reelsInput);
            skinFormularioFila.appendChild(skinFormularioReels);

            const vendibleInput = document.createElement('input');
            vendibleInput.type = 'checkbox';
            vendibleInput.name = 'vendible';
            vendibleInput.checked = true;
            vendibleInput.title = 'Vendible';
            vendibleInput.id = 'skinFormularioVendible';
            skinFormularioVendible.appendChild(vendibleInput);
            skinFormularioFila.appendChild(skinFormularioVendible);

            const crearInput = document.createElement('input');
            crearInput.type = 'submit';
            crearInput.title = 'Crear skin';
            crearInput.id = 'skinFormularioCrear';
            skinFormularioCrear.appendChild(crearInput);
            skinFormularioFila.appendChild(skinFormularioCrear);

            skinFormularioTable.appendChild(skinFormularioFila);
            skinFormularioTitle.textContent = 'Crear Skin';
            skinFormularioTitle.id = 'skinFormularioTitle';
            skinFormulario.appendChild(skinFormularioTitle);
            skinFormulario.appendChild(skinFormularioTable);

            skinFormulario.addEventListener('submit', (e) => {
                e.preventDefault();

                const name = document.getElementById('skinFormularioNombre').value;
                const precio = document.getElementById('skinFormularioPrecio').value;
                const description = document.getElementById('skinFormularioDescription').value;
                const reels = document.getElementById('skinFormularioReels').value.split(',');
                const vendible = document.getElementById('skinFormularioVendible').checked;
                const skin = {
                    name,
                    precio: parseInt(precio),
                    description,
                    reels,
                    vendible
                };
                fetch('/admin/api/skins/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: JSON.stringify(skin),
                })
                    .then((response) => response.json())
                    .then((data) => { alert("Se ha creado la skin: " + data.name); location.reload(); })
                    .catch((error) => { console.error(error); alert("Error al crear la skin"); });
            });
            skinSection.appendChild(skinFormulario);

            //Creamos la tabla para listar todas las skins
            const skinTable = document.createElement('table');
            const cabeceras = document.createElement('tr');
            const cNombre = document.createElement('th');
            const cPrecio = document.createElement('th');
            const cDescription = document.createElement('th');
            const cReels = document.createElement('th');
            const cVendible = document.createElement('th');

            cNombre.textContent = "Nombre";
            cPrecio.textContent = "Precio";
            cDescription.textContent = "Descripción";
            cReels.textContent = "Emojis";
            cVendible.textContent = "🛒";

            cabeceras.appendChild(cNombre);
            cabeceras.appendChild(cPrecio);
            cabeceras.appendChild(cDescription);
            cabeceras.appendChild(cReels);
            cabeceras.appendChild(cVendible);
            skinTable.appendChild(cabeceras);

            //Por cada skin añadimos una fila a la tabla
            const skinsData = await loadSkins();
            skinsData.forEach(skin => {
                const fila = document.createElement('tr');
                const nombre = document.createElement('td');
                const precio = document.createElement('td');
                const description = document.createElement('td');
                const reels = document.createElement('td');
                const vendible = document.createElement('td');

                nombre.textContent = skin.name;
                precio.textContent = skin.precio;
                description.textContent = skin.description;
                reels.textContent = skin.reels;
                vendible.textContent = skin.vendible;

                fila.appendChild(nombre);
                fila.appendChild(precio);
                fila.appendChild(description);
                fila.appendChild(reels);
                fila.appendChild(vendible);
                skinTable.appendChild(fila);
            });
            skinSection.appendChild(skinTable);
        }

        loadUsers();
        loadSkinPanel();
    };

    if (token) {
        cargarPanelAdmin();
    }

    toggleSections();
});

function checkAuth() {
    try {
        const token = JSON.parse(atob(localStorage.getItem('token').split('.')[1]));
        return token.exp > Math.floor(Date.now() / 1000);
    } catch (error) {
        console.error("Error al decodificar el token:" + error);
        return false;
    }
}