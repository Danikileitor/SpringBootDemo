package com.example.demo.Admin;

import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/users")
    public List<Usuario> getAllUsers() {
        return usuarioService.getAllUsers();
    }

    @PutMapping("/users/{id}")
    public Usuario updateUser(@PathVariable String id, @RequestBody Usuario usuario) {
        return usuarioService.updateUser(id, usuario);
    }
}