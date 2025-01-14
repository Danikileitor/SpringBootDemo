const formulario = document.getElementById('formulario-restablecer');
const tokenInput = document.getElementById('token');
const nuevaContrasenaInput = document.getElementById('nuevaContrasena');
const confirmarContrasenaInput = document.getElementById('confirmarContrasena');
const mensajeDiv = document.getElementById('mensaje');

formulario.addEventListener('submit', (e) => {
    e.preventDefault();
    const token = tokenInput.value.trim();
    const nuevaContrasena = nuevaContrasenaInput.value.trim();
    const confirmarContrasena = confirmarContrasenaInput.value.trim();
    if (token !== '' && nuevaContrasena !== '' && confirmarContrasena !== '') {
        if (nuevaContrasena === confirmarContrasena) {
            fetch('/api/auth/restablecer-contrasena/' + token, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ nuevaContrasena: nuevaContrasena })
            })
                .then(response => response.text())
                .then(data => mensajeDiv.innerHTML = data)
                .catch(error => mensajeDiv.innerHTML = error);
        } else {
            mensajeDiv.innerHTML = 'Las contraseñas no coinciden.';
        }
    } else {
        mensajeDiv.innerHTML = 'Por favor, ingrese todos los campos.';
    }
});