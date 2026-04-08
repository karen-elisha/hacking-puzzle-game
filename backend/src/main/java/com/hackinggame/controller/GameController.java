package com.hackinggame.controller;

import com.hackinggame.model.Player;
import com.hackinggame.repository.PlayerRepository;
import com.hackinggame.service.AuditLogService;
import com.hackinggame.service.PuzzleService;
import com.hackinggame.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PuzzleService puzzleService;
    
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String getUsernameFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) return null;
        return jwtTokenProvider.getUsernameFromToken(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String auth) {
        String username = getUsernameFromAuth(auth);
        if (username == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Player player = playerRepository.findByUsername(username).orElse(null);
        if (player == null) return ResponseEntity.status(404).body(Map.of("error", "Player not found"));
        return ResponseEntity.ok(player);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        List<Player> lb = playerRepository.findAllByOrderByXpDesc();
        List<Map<String, Object>> mapped = lb.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", p.getUsername());
            map.put("level", p.getLevel());
            map.put("xp", p.getXp());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("leaderboard", mapped));
    }

    @GetMapping("/puzzle")
    public ResponseEntity<?> getPuzzle(@RequestHeader(value = "Authorization", required = false) String auth) {
        String username = getUsernameFromAuth(auth);
        if (username == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Player player = playerRepository.findByUsername(username).orElse(null);
        if (player == null) return ResponseEntity.status(404).body(Map.of("error", "Player not found"));
        
        PuzzleService.Puzzle puzzle = puzzleService.generatePuzzle(player.getVaultLevel());
        
        return ResponseEntity.ok(Map.of(
            "type", puzzle.type(),
            "question", puzzle.question(),
            "reward", puzzle.reward()
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPuzzle(@RequestHeader(value = "Authorization", required = false) String auth,
                                          @RequestBody Map<String, String> request) {
        String username = getUsernameFromAuth(auth);
        if (username == null) return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));

        String answer = request.get("answer");
        String pType = request.get("puzzleType");
        int reward = Integer.parseInt(request.getOrDefault("reward", "50"));

        if (answer != null && !answer.trim().isEmpty()) {
            Player player = playerRepository.findByUsername(username).orElse(null);
            if (player != null) {
                player.addXp(reward);
                player.setSuccessfulHacks(player.getSuccessfulHacks() + 1);
                playerRepository.save(player);
                auditLogService.logSuccessfulHack(username, reward, pType); // File I/O Logic integration
                return ResponseEntity.ok(Map.of("success", true, "message", "Hack Successful! Earned " + reward + " XP."));
            }
        }
        
        return ResponseEntity.ok(Map.of("success", false, "message", "Hack Failed! Incorrect answer."));
    }

    @PostMapping("/upgrade-vault")
    public ResponseEntity<?> upgradeVault(@RequestHeader(value = "Authorization", required = false) String auth) {
        String username = getUsernameFromAuth(auth);
        if (username == null) return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));

        Player player = playerRepository.findByUsername(username).orElse(null);
        if (player == null) return ResponseEntity.status(404).body(Map.of("error", "Player not found"));
        
        int cost = player.getVaultLevel() * 150;
        
        if (player.getCoins() >= cost) {
            player.setCoins(player.getCoins() - cost);
            player.setVaultLevel(player.getVaultLevel() + 1);
            playerRepository.save(player);
            auditLogService.logSystemEvent("User " + username + " upgraded vault to level " + player.getVaultLevel());
            return ResponseEntity.ok(Map.of("success", true, "message", "Vault upgraded to Level " + player.getVaultLevel() + "!"));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Not enough coins! Need " + cost + " coins."));
        }
    }
}