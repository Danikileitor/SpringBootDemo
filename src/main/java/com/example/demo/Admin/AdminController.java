package com.example.demo.Admin;

import com.example.demo.Skins.Skin;
import com.example.demo.Skins.SkinRepository;
import com.example.demo.Skins.SkinService;
import com.example.demo.Users.Rol;
import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/api")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SkinService skinService;

    @Autowired
    private SkinRepository skinRepository;

    @PostMapping("/users")
    public List<Usuario> getAllUsers() {
        return usuarioService.getAllUsers();
    }

    @PostMapping(value = "/skins/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Skin> createSkin(@RequestBody Skin skin) {
        System.out.println(skin);
        Skin newSkin = skinService.createSkin(skin);
        System.out.println(newSkin);
        return ResponseEntity.ok(newSkin);
    }

    @PutMapping("/users/{id}")
    public Usuario updateUser(@PathVariable String id, @RequestBody UsuarioRequest request) {
        Usuario updatedUser = new Usuario();
        updatedUser.setRol(request.getRol());
        updatedUser.setSkins(Stream.of(request.getSkins())
                .map(skinId -> {
                    return skinRepository.findById(skinId).get().getId();
                })
                .collect(Collectors.toSet()));
        updatedUser.setCoins(request.getCoins());
        return usuarioService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (usuarioService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public static class UsuarioRequest {
        @JsonProperty("rol")
        private Rol rol;
        @JsonProperty("coins")
        private int coins;
        @JsonProperty("skinsId")
        private String[] skinsId;

        public Rol getRol() {
            return rol;
        }

        public void setRol(Rol rol) {
            this.rol = rol;
        }

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public String[] getSkins() {
            return skinsId;
        }

        public void setSkins(String[] skinsId) {
            this.skinsId = skinsId;
        }
    }
}