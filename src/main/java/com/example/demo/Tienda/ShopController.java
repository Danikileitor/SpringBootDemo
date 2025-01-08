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

import com.example.demo.Skins.Skin;
import com.example.demo.Skins.SkinRepository;
import com.example.demo.Users.JwtTokenUtil;
import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/shop/api")
public class ShopController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SkinRepository skinRepository;

    @PostMapping("/comprar/skin")
    public ResponseEntity<?> buySkin(@RequestHeader("Authorization") String token,
            @RequestBody BuySkinRequest request) {

        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);

        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Optional<Skin> skinOpt = skinRepository.findByName(request.getName());
                if (skinOpt.isPresent()) {
                    if (usuarioOpt.get().getCoins() >= skinOpt.get().getPrecio()) {
                        int newCoins = usuarioOpt.get().getCoins() - skinOpt.get().getPrecio();
                        usuarioOpt.get().setCoins(newCoins);
                        if (usuarioOpt.get().desbloquearSkin(skinOpt.get().getId())) {
                            usuarioService.updateUser(usuarioOpt.get().getId(), usuarioOpt.get());
                            return ResponseEntity.ok("Has desbloqueado la skin: " + skinOpt.get().getName());
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ya tienes la skin: " + request.getName());    
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tienes suficientes monedas");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe la skin: " + request.getName());
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
