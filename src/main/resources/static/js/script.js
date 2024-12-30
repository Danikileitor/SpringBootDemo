document.addEventListener('DOMContentLoaded', () => {
    const authSection = document.getElementById('auth-section');
    const gameSection = document.getElementById('game-section');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showLoginButton = document.getElementById('showLogin');
    const showRegisterButton = document.getElementById('showRegister');
    const logoutButton = document.getElementById('logout-button');
    const authSectionTitle = document.getElementById('auth-section-title');
    const skinSelect = document.getElementById('skin');
    const token = localStorage.getItem('token');

    // Simular estado de sesión del usuario
    const checkAuth = () => !!localStorage.getItem('token');

    const toggleSections = () => {
        if (checkAuth()) {
            authSection.style.display = 'none';
            gameSection.style.display = 'block';
        } else {
            authSection.style.display = 'block';
            gameSection.style.display = 'none';
        }
    };

    // Manejo del formulario de login
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const token = await response.text(); // Podrías guardar un JWT si usas uno
            localStorage.setItem('token', token);
            toggleSections();
            cargarSkins();
        } else {
            alert('Usuario o contraseña incorrectos');
        }
    });

    // Alternar entre login y registro
    showLoginButton.addEventListener('click', () => {
        authSectionTitle.innerText = 'Iniciar Sesión';
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
    });

    showRegisterButton.addEventListener('click', () => {
        loginForm.style.display = 'none';
        authSectionTitle.innerText = 'Registrarse';
        registerForm.style.display = 'block';
    });

    // Manejo del formulario de registro
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('registerUsername').value;
        const password = document.getElementById('registerPassword').value;
        const email = document.getElementById('registerEmail').value;

        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, email, rol: 'ROLE_USER' })
        });

        if (response.ok) {
            alert('Usuario registrado exitosamente');
        } else {
            const error = await response.text();
            alert(`Error: ${error}`);
        }
    });

    // Manejo del cierre de sesión
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('token');
        toggleSections();
    });

    // Manejo para cargar las skins en el select
    const cargarSkins = () => {
        fetch('/skins/desbloqueadas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')  // Aquí pasas el token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('No se pudo cargar las skins');
                }
                return response.json();
            })
            .then(data => {
                // Limpia las opciones existentes
                skinSelect.innerHTML = '';

                // Agrega las skins desbloqueadas como opciones
                data.forEach(skin => {
                    const option = document.createElement('option');
                    option.value = skin.name;
                    option.textContent = skin.name;
                    option.setAttribute('Description', skin.description);
                    skinSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
            });
    };

    // Comprueba si hay token almacenado
    if (token) {
        cargarSkins();
    }

    // Inicializa el estado inicial
    toggleSections();
});

document.getElementById('play-button').addEventListener('click', function () {
    var data = { "skin": document.getElementById('skin').value };
    fetch('/play', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('reel1').textContent = data.reel1;
            document.getElementById('reel2').textContent = data.reel2;
            document.getElementById('reel3').textContent = data.reel3;
            document.getElementById('message').textContent = data.message;
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('show-wins-button').addEventListener('click', function () {
    fetch('/wins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('message').textContent = "Victorias: " + data;
        })
        .catch(error => console.error('Error:', error));
});
