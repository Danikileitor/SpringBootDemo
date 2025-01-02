package com.example.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Users.JwtTokenUtil;
import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@Controller
public class SlotMachineController {

    private String getRandomReel(String skin) {
        String[] reels = Skin.fromString(skin).getReels();
        return reels[(int) (Math.random() * reels.length)];
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @Autowired
    private DynamicSlotMachineService dynamicSlotMachineService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(value = "/coins", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> getCoins(@RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                return ResponseEntity.ok(usuarioOpt.get().getCoins());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/coins")
    @ResponseBody
    public ResponseEntity<Integer> updateCoins(@RequestHeader("Authorization") String token,
            @RequestBody CoinUpdateRequest request) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                int newCoins = usuarioOpt.get().getCoins() + request.getDelta();
                if (newCoins >= 0) {
                    usuarioOpt.get().setCoins(newCoins);
                    usuarioService.updateUser(usuarioOpt.get().getId(), usuarioOpt.get());
                    return ResponseEntity.ok(newCoins);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/skins/desbloqueadas", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> obtenerSkinsDesbloqueadas(@RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);

        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Set<Skin> skins = usuarioOpt.get().getSkins();
                List<Map<String, String>> skinsData = skins.stream()
                        .map(skin -> {
                            Map<String, String> skinData = new HashMap<>();
                            skinData.put("name", skin.getName());
                            skinData.put("description", skin.getDescription());
                            return skinData;
                        }).collect(Collectors.toList());
                return ResponseEntity.ok(skinsData);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }

    @PostMapping(value = "/play", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> play(@RequestHeader("Authorization") String token, @RequestBody PlayRequest request) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);

        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                if (usuarioOpt.get().getCoins() >= 1) {
                    String username = usernameOpt.get();
                    String reel1 = getRandomReel(request.getSkin());
                    String reel2 = getRandomReel(request.getSkin());
                    String reel3 = getRandomReel(request.getSkin());
                    int cost = request.getCost();
                    String message = reel1.equals(reel2) && reel2.equals(reel3) ? "¡Ganaste!" : "¡Sigue intentando!";
                    int attemptNumber = dynamicSlotMachineService.getNextAttemptNumber(username);

                    int newCoins = usuarioOpt.get().getCoins() - cost;
                    if (newCoins < 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tienes sufiences monedas");
                    }
                    usuarioOpt.get().setCoins(newCoins);
                    usuarioService.updateUser(usuarioOpt.get().getId(), usuarioOpt.get());

                    SlotMachineResult result = new SlotMachineResult(username, attemptNumber, cost, reel1, reel2, reel3,
                            message, new Date());
                    dynamicSlotMachineService.saveResult(username, result);

                    return ResponseEntity.ok(result);
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

    public static class PlayRequest {
        @JsonProperty("skin")
        private String skin = Skin.COMIDA_BASURA.getName();
        @JsonProperty("cost")
        private int cost;

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public String getSkin() {
            return skin;
        }

        public void setSkin(String skin) {
            this.skin = skin;
        }
    }

    public static class CoinUpdateRequest {
        @JsonProperty("delta")
        private int delta;

        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }
    }

    @PostMapping(value = "/wins", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getWins(@RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        String username = usernameOpt.get();
        String collectionName = username; // Usamos el nombre del usuario como nombre de colección

        try {
            long wins = dynamicSlotMachineService.getCountByMessage(collectionName, "¡Ganaste!");
            return ResponseEntity.ok(wins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las victorias: " + e.getMessage());
        }
    }

    @JsonAutoDetect
    // @Document(collection = "slot_machine_results")
    public static class SlotMachineResult {
        @Id
        private String id;
        private String username;
        private int attemptNumber;
        private int cost;
        private String reel1;
        private String reel2;
        private String reel3;
        private String message;
        private Date executionDate;

        public SlotMachineResult() {
        }

        public SlotMachineResult(String username, int attemptNumber, int cost, String reel1, String reel2, String reel3,
                String message, Date executionDate) {
            this.username = username;
            this.attemptNumber = attemptNumber;
            this.reel1 = reel1;
            this.reel2 = reel2;
            this.reel3 = reel3;
            this.message = message;
            this.executionDate = executionDate;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public String getReel1() {
            return reel1;
        }

        public void setReel1(String reel1) {
            this.reel1 = reel1;
        }

        public String getReel2() {
            return reel2;
        }

        public void setReel2(String reel2) {
            this.reel2 = reel2;
        }

        public String getReel3() {
            return reel3;
        }

        public void setReel3(String reel3) {
            this.reel3 = reel3;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getAttemptNumber() {
            return attemptNumber;
        }

        public void setAttemptNumber(int attemptNumber) {
            this.attemptNumber = attemptNumber;
        }

        public Date getExecutionDate() {
            return executionDate;
        }

        public void setExecutionDate(Date executionDate) {
            this.executionDate = executionDate;
        }
    }
}