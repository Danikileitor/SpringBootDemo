package com.example.demo.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DynamicSlotMachineService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private DynamicSlotMachineService dynamicSlotMachineService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(String username, String password, String email, Rol rol) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), email);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            return usuario;
        }
        return Optional.empty();
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    public Usuario updateUser(String id, Usuario updatedUser) {
        Usuario user = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!updatedUser.getRol().equals(null)) {
            user.setRol(updatedUser.getRol());
        }
        if (updatedUser.getSkins().size() > 0) {
            user.setSkins(updatedUser.getSkins());
        }
        if (updatedUser.getCoins() >= 0) {
            user.setCoins(updatedUser.getCoins());
        }
        return usuarioRepository.save(user);
    }

    public Usuario updateUserLoginDate(String id) {
        Usuario user = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setLastLoginDate(new Date());
        return usuarioRepository.save(user);
    }

    public boolean deleteUser(String id) {
        Optional<Usuario> user = usuarioRepository.findById(id);
        if (user.isPresent()) {
            usuarioRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public long getWins(String id) {
        Usuario user = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return dynamicSlotMachineService.getCountByMessage(user.getUsername(), "¡Ganaste!");
    }

    public List<Usuario> getTop5Winners() {
        List<Usuario> usuarios = getAllUsers();

        usuarios.sort((u1, u2) -> {
            long wins1 = getWins(u1.getId());
            long wins2 = getWins(u2.getId());
            return Long.compare(wins2, wins1);
        });

        return usuarios.subList(0, Math.min(5, usuarios.size()));
    }
}