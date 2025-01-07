package com.example.demo.Tienda;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Skin;
import com.example.demo.Users.JwtTokenUtil;
import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/shop/api")
public class ShopController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/comprar/skin")
    public ResponseEntity<?> buySkin(@RequestHeader("Authorization") String token,
            @RequestBody BuySkinRequest request) {

        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);

        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Skin skin = Skin.fromString(request.getName());
                if (usuarioOpt.get().getCoins() >= skin.getPrecio()) {
                    int newCoins = usuarioOpt.get().getCoins() - skin.getPrecio();
                    usuarioOpt.get().setCoins(newCoins);
                    usuarioOpt.get().desbloquearSkin(skin);
                    usuarioService.updateUser(usuarioOpt.get().getId(), usuarioOpt.get());
                    return ResponseEntity.ok("Has desbloqueado la skin: " + skin.getName());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tienes suficientes monedas");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    public static class BuySkinRequest {
        @JsonProperty("name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
