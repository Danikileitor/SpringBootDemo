document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logout-button');

    // Manejo del cierre de sesión
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('token');
        location.replace("/");
    });

    // Comprueba si hay token almacenado
    if (checkAuth()) {
        loadCoins();
        tiendaSkins();
    } else {
        localStorage.removeItem('token');
        location.replace("/");
    }
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

function checkAuth() {
    try {
        const token = JSON.parse(atob(localStorage.getItem('token').split('.')[1]));
        return token.exp > Math.floor(Date.now() / 1000);
    } catch (error) {
        return false;
    }
}

// Cargar todas las skins
async function loadSkinsVendibles(vendible = true) {
    try {
        const response = await fetch('/skins/vendibles', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: vendible
        });
        if (!response.ok) {
            throw new Error('No se pudo cargar las skins');
        }
        return await response.json();
    } catch (error) {
        return console.error('Error:', error);
    }
};

// Cargar las skins desbloqueadas
async function loadSkinsDesbloqueadas() {
    try {
        const response = await fetch('/skins/desbloqueadas/id', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });
        if (!response.ok) {
            throw new Error('No se pudo cargar las skins desbloqueadas');
        }
        return await response.json();
    } catch (error) {
        return console.error('Error:', error);
    }
};

//Función principal para cargar la tienda
async function tiendaSkins() {
    const skinsList = document.getElementById('skins-list');
    const skinsData = await loadSkinsVendibles(true);
    const skinsDesbloqueadas = await loadSkinsDesbloqueadas();
    const dinero = parseInt(document.getElementById('coins-amount').textContent.slice(0, -2), 10);

    skinsData.forEach(skin => {
        const fila = document.createElement('tr');
        const nombre = document.createElement('td');
        const precio = document.createElement('td');
        const emojis = document.createElement('td');
        const comprar = document.createElement('td');
        const btnComprar = document.createElement('button');

        nombre.textContent = skin.name;
        precio.textContent = skin.precio;
        skin.reels.forEach(emoji => {
            emojis.textContent += emoji;
        })
        btnComprar.textContent = "🛒";
        btnComprar.className = "comprar";
        if (dinero >= skin.precio) {
            btnComprar.title = "Comprar skin: " + skin.name;
            btnComprar.addEventListener('click', () => comprarSkin(skin.name));
        } else {
            btnComprar.title = "No tienes suficientes 🪙";
            btnComprar.classList.add('disabled');
        }

        comprar.appendChild(btnComprar);
        fila.appendChild(nombre);
        fila.appendChild(precio);
        fila.appendChild(emojis);
        fila.appendChild(comprar);
        fila.title = skin.description;
        fila.setAttribute('skinid', skin.id);

        skinsList.appendChild(fila);
    });

    skinsDesbloqueadas.forEach(skin => {
        const fila = skinsList.querySelector(`tr[skinid="${skin}"]`);
        if (fila) {
            skinsList.removeChild(fila);
        }
    });

    if (skinsList.childElementCount <= 2) {
        const shopSection = document.querySelector('.shop-section');
        const vacio = document.createElement('p');
        vacio.textContent = "¡No quedan skins por desbloquear!";

        shopSection.removeChild(shopSection.children[1]);
        shopSection.appendChild(vacio);
    }
}

function comprarSkin(name) {
    var data = { "name": name };
    fetch('/shop/api/comprar/skin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(data)
    })
        .then(response => { return response.ok ? response.text() : Promise.reject(response) })
        .then(data => {
            alert(data);
            location.reload();
        })
        .catch(error => { console.error(error); alert("Hubo un error al comprar la skin"); });
}

document.getElementById('back-button').addEventListener('click', function () {
    location.href = "/";
});