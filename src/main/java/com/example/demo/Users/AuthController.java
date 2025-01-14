package com.example.demo.Users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Mail.EmailService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

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
                        Rol.ROLE_USER);
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
            String token = JwtTokenUtil.generateToken(usuario.get());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    usuario.get(), // Principal (Usuario autenticado)
                    null, // Credenciales (pueden ser null después de autenticación)
                    List.of(new SimpleGrantedAuthority(usuario.get().getRol().name())) // Roles/Authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Si es su primer login del día damos una recompensa
            if (usuario.get().isFirstLoginOfDay()) {
                usuario.get().setCoins(usuario.get().getCoins() + 20);
                usuarioService.updateUser(usuario.get().getId(), usuario.get());
            }
            usuarioService.updateUserLoginDate(usuario.get().getId());

            return ResponseEntity.ok(token); // Devuelve el token como texto plano
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        if (usuario.isPresent() && usuario.get().getRol() == Rol.ROLE_ADMIN) {
            String token = JwtTokenUtil.generateToken(usuario.get());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    usuario.get(), // Principal (Usuario autenticado)
                    null, // Credenciales (pueden ser null después de autenticación)
                    List.of(new SimpleGrantedAuthority(usuario.get().getRol().name())) // Roles/Authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok(token); // Devuelve el token como texto plano
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Acceso denegado. Solo los administradores pueden acceder.");
    }

    @PostMapping("/olvidar-contrasena")
    public ResponseEntity<?> olvidarContrasena(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            // Generar token de restablecimiento de contraseña
            String token = UUID.randomUUID().toString();
            usuarioService.guardarTokenRestablecimientoContrasena(usuarioOpt.get(), token);

            // Enviar correo electrónico con el enlace de restablecimiento
            String mensaje = System.getenv().get("WEB_HOST") + "/restablecer-contrasena?token=" + token;
            emailService.enviarCorreo(email, "Restablecimiento de contraseña", mensaje);

            return ResponseEntity.ok("Correo electrónico enviado con éxito");
        } else {
            return ResponseEntity.badRequest().body("Correo electrónico no encontrado");
        }
    }

    @PostMapping("/restablecer-contrasena/{token}")
    public ResponseEntity<?> restablecerContrasena(@PathVariable String token, @RequestBody NuevaContrasenaRequest nuevaContrasenaRequest) {
        Optional<Usuario> usuarioOpt = usuarioService.findByTokenRestablecimientoContrasena(token);
        if (usuarioOpt.isPresent()) {
            usuarioService.actualizarContrasena(usuarioOpt.get(), nuevaContrasenaRequest.getNuevaContrasena());
            return ResponseEntity.ok("Contraseña restablecida con éxito");
        } else {
            return ResponseEntity.badRequest().body("Token inválido");
        }
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

class EmailRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class NuevaContrasenaRequest {
    private String nuevaContrasena;

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}