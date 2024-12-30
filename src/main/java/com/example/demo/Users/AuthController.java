package com.example.demo.Users;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistroRequest registroRequest) {
        if (registroRequest.getRol() != null) {
            try {
                Usuario usuario = usuarioService.registrarUsuario(
                        registroRequest.getUsername(),
                        registroRequest.getPassword(),
                        registroRequest.getEmail(),
                        Rol.valueOf(registroRequest.getRol().toUpperCase()));
                return ResponseEntity.ok(usuario);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            try {
                Usuario usuario = usuarioService.registrarUsuario(
                        registroRequest.getUsername(),
                        registroRequest.getPassword(),
                        registroRequest.getEmail(),
                        Rol.USER);
                return ResponseEntity.ok(usuario);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        if (usuario.isPresent()) {
            String token = JwtTokenUtil.generateToken(usuario.get().getUsername());
            return ResponseEntity.ok(token); // Devuelve el token como texto plano
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
    }
}

class RegistroRequest {
    private String username;
    private String password;
    private String email;
    private String rol;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}