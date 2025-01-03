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
            loadCoins();
            cargarSkins();
            loadRanking();
            toggleSections();
        } else {
            alert('Usuario o contraseña incorrectos');
        }
    });

    // Para cambiar el title del select igual a su option
    skinSelect.addEventListener('change', () => {
        skinSelect.title = skinSelect.options[skinSelect.selectedIndex].title;
        localStorage.setItem("lastSkin", skinSelect.options[skinSelect.selectedIndex].value);
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
            authSectionTitle.innerText = 'Iniciar Sesión';
            loginForm.style.display = 'block';
            registerForm.style.display = 'none';
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
                data.reverse().forEach(skin => {
                    const option = document.createElement('option');
                    option.value = skin.name;
                    option.textContent = skin.name;
                    option.title = skin.description;
                    option.selected = skin.name == localStorage.getItem("lastSkin") ? true : false;
                    skinSelect.appendChild(option);
                });

                // Actualizamos el title del select
                skinSelect.title = skinSelect.options[skinSelect.selectedIndex].title;
            })
            .catch(error => {
                console.error('Error:', error);
            });
    };

    // Comprueba si hay token almacenado
    if (token) {
        loadCoins();
        cargarSkins();
        loadRanking();
    }

    // Inicializa el estado inicial
    toggleSections();
});

// Función para cargar las monedas del usuario
function loadCoins() {
    const coinsAmount = document.getElementById('coins-amount');
    fetch('/coins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => response.json())
        .then(data => {
            coinsAmount.textContent = data + "🪙";
        })
        .catch(error => console.error('Error:', error));
};

// Función para actualizar las monedas del usuario
function updateCoins(delta) {
    var data = { "delta": delta };
    fetch("/coins", {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            loadCoins();
            console.log("Monedas actualizadas: " + data);
        })
        .catch(error => console.error('Error:', error));
};

document.getElementById('play-button').addEventListener('click', function () {
    if (!this.classList.contains('disabled')) {
        this.classList.add('disabled');
        var data = { "skin": document.getElementById('skin').value, "cost": 1 };
        fetch('/play', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: JSON.stringify(data)
        })
            .then(response => { return response.ok ? response.json() : Promise.reject(response) })
            .then(data => {
                let resultado = data[0];
                let skin = data[1];
                const reels = document.querySelectorAll('.reel span');
                loadCoins();

                reels.forEach(reel => {
                    let currentCharacter = Math.floor(Math.random() * skin.length);;
                    const animation = setInterval(() => {
                        reel.textContent = skin[currentCharacter];
                        currentCharacter = (currentCharacter + 1) % skin.length;
                    }, 100); // Cambia el texto cada 100ms

                    // Detiene la animación después de 2 segundos
                    setTimeout(() => {
                        clearInterval(animation);
                        // Muestra el resultado final
                        document.getElementById('reel1').textContent = resultado.reel1;
                        document.getElementById('reel2').textContent = resultado.reel2;
                        document.getElementById('reel3').textContent = resultado.reel3;
                        document.getElementById('message').textContent = resultado.message;
                        if (resultado.message == "¡Ganaste!") {
                            updateCoins(50);
                        }
                        this.classList.remove('disabled');
                        loadRanking();
                    }, 2000);
                });
            })
            .catch(error => error.text().then(message => { console.error(message); this.classList.remove('disabled'); }));
    }
});

document.getElementById('show-wins-button').addEventListener('click', function () {
    fetch('/wins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('message').textContent = "Victorias: " + data;
        })
        .catch(error => console.error('Error:', error));
});

function loadRanking() {
    fetch('/ranking', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => response.json())
        .then(data => {
            let rankingHtml = "<h3>RANKING</h3><hr><table>";
            data.usuarios.forEach((usuario, i) => {
                rankingHtml += `<tr><td>${usuario.username}</td><td>${data.victorias[i]}</td></tr>`;
            });
            rankingHtml += "</table>"
            document.getElementById('ranking-panel').innerHTML = rankingHtml;
        })
        .catch(error => console.error('Error:', error));
};