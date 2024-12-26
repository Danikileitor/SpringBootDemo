package com.example.demo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@Controller
public class SlotMachineController {

    @Autowired
    private SlotMachineResultRepository repository;

    private String getRandomReel(String skin) {
        String[] reels;
        switch (skin) {
            case "coches":
                reels = new String[] { "🚗", "🚕", "🏎️", "🚒", "🚓" };
                break;

            default:// comidaBasura
                reels = new String[] { "🍕", "🍔", "🍟", "🌭", "🍿" };
                break;
        }
        return reels[(int) (Math.random() * reels.length)];
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping(value = "/play", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SlotMachineResult play(@RequestBody PlayRequest request) {
        // Simulate slot machine reels
        String reel1 = getRandomReel(request.getSkin());
        String reel2 = getRandomReel(request.getSkin());
        String reel3 = getRandomReel(request.getSkin());

        // Check for winning combination
        String message;
        if (reel1.equals(reel2) && reel2.equals(reel3)) {
            message = "¡Ganaste!";
        } else {
            message = "¡Sigue intentando!";
        }

        SlotMachineResult result = new SlotMachineResult(repository.getNextAttemptNumber(), reel1, reel2, reel3,
                message, new Date());
        repository.save(result);
        return result;
    }

    public static class PlayRequest {
        @JsonProperty("skin")
        private String skin = "comidaBasura";

        public String getSkin() {
            return skin;
        }

        public void setSkin(String skin) {
            this.skin = skin;
        }
    }

    @PostMapping(value = "/wins", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Long> getWins() {
        long count = repository.countByMessage("¡Ganaste!");
        return ResponseEntity.ok(count);
    }

    @JsonAutoDetect
    @Document(collection = "slot_machine_results")
    public static class SlotMachineResult {
        @Id
        private String id;
        private int attemptNumber;
        private String reel1;
        private String reel2;
        private String reel3;
        private String message;
        private Date executionDate;

        public SlotMachineResult() {
        }

        public SlotMachineResult(int attemptNumber, String reel1, String reel2, String reel3, String message,
                Date executionDate) {
            this.attemptNumber = attemptNumber;
            this.reel1 = reel1;
            this.reel2 = reel2;
            this.reel3 = reel3;
            this.message = message;
            this.executionDate = executionDate;
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