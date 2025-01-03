package com.example.demo.Admin;

import com.example.demo.Skin;
import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/users")
    public List<Usuario> getAllUsers() {
        return usuarioService.getAllUsers();
    }

    @PostMapping("/skins")
    public ResponseEntity<List<String>> getAllSkins() {
        List<String> skins = Arrays.stream(Skin.values())
                .map(Skin::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skins);
    }

    @PutMapping("/users/{id}")
    public Usuario updateUser(@PathVariable String id, @RequestBody Usuario usuario) {
        return usuarioService.updateUser(id, usuario);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (usuarioService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}