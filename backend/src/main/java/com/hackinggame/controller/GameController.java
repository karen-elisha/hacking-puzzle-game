package main.java.com.hackinggame.controller;

import com.hackinggame.model.User;
import com.hackinggame.repository.UserRepository;
import com.hackinggame.security.JwtTokenProvider;
import com.hackinggame.service.PuzzleService;
import com.hackinggame.service.PuzzleService.Puzzle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GameController {
    
    private final UserRepository userRepository;
    private final PuzzleService puzzleService;
    private final JwtTokenProvider tokenProvider;
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = tokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("level", user.getLevel());
            response.put("xp", user.getXp());
            response.put("coins", user.getCoins());
            response.put("vaultLevel", user.getVaultLevel());
            response.put("successfulHacks", user.getSuccessfulHacks());
            response.put("failedHacks", user.getFailedHacks());
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @GetMapping("/puzzle")
    public ResponseEntity<?> getPuzzle(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = tokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Difficulty based on user's vault level (1-3)
            int difficulty = Math.min(3, user.getVaultLevel());
            Puzzle puzzle = puzzleService.generatePuzzle(difficulty);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", puzzle.type());
            response.put("question", puzzle.question());
            response.put("reward", puzzle.reward());
            response.put("difficulty", difficulty);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPuzzle(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> request) {
        try {
            String token = authHeader.substring(7);
            String username = tokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            String answer = request.get("answer");
            String puzzleType = request.get("puzzleType");
            int reward = Integer.parseInt(request.get("reward"));
            
            // For demo purposes, we'll accept any non-empty answer
            // In production, you would verify against the actual puzzle answer
            boolean solved = answer != null && !answer.trim().isEmpty();
            
            if (solved) {
                // Add XP and coins
                user.setXp(user.getXp() + reward);
                user.setCoins(user.getCoins() + reward);
                user.setSuccessfulHacks(user.getSuccessfulHacks() + 1);
                
                // Level up logic: each level requires 500 XP
                int newLevel = (user.getXp() / 500) + 1;
                if (newLevel > user.getLevel()) {
                    user.setLevel(newLevel);
                }
                
                userRepository.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "✅ Puzzle solved! You earned " + reward + " XP and " + reward + " coins!");
                response.put("xp", user.getXp());
                response.put("coins", user.getCoins());
                response.put("level", user.getLevel());
                
                return ResponseEntity.ok(response);
            } else {
                user.setFailedHacks(user.getFailedHacks() + 1);
                userRepository.save(user);
                
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "❌ Wrong answer! Try again!"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        try {
            var topUsers = userRepository.findTop10ByOrderByXpDesc();
            
            var leaderboard = topUsers.stream().map(user -> {
                Map<String, Object> player = new HashMap<>();
                player.put("username", user.getUsername());
                player.put("level", user.getLevel());
                player.put("xp", user.getXp());
                player.put("coins", user.getCoins());
                player.put("hacks", user.getSuccessfulHacks());
                player.put("vaultLevel", user.getVaultLevel());
                return player;
            }).toList();
            
            return ResponseEntity.ok(Map.of("leaderboard", leaderboard));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load leaderboard"));
        }
    }
    
    @PostMapping("/upgrade-vault")
    public ResponseEntity<?> upgradeVault(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = tokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Calculate upgrade cost: 500 coins for level 1, 1000 for level 2, etc.
            int upgradeCost = user.getVaultLevel() * 500;
            
            if (user.getCoins() >= upgradeCost) {
                user.setCoins(user.getCoins() - upgradeCost);
                user.setVaultLevel(user.getVaultLevel() + 1);
                userRepository.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "🔒 Vault upgraded to level " + user.getVaultLevel() + "!");
                response.put("vaultLevel", user.getVaultLevel());
                response.put("coins", user.getCoins());
                response.put("nextUpgradeCost", user.getVaultLevel() * 500);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "❌ Not enough coins! Need " + upgradeCost + " coins. Current: " + user.getCoins(),
                    "needed", upgradeCost,
                    "current", user.getCoins()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = tokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalXP", user.getXp());
            stats.put("xpToNextLevel", 500 - (user.getXp() % 500));
            stats.put("totalCoins", user.getCoins());
            stats.put("successRate", user.getSuccessfulHacks() + user.getFailedHacks() > 0 ? 
                (user.getSuccessfulHacks() * 100.0 / (user.getSuccessfulHacks() + user.getFailedHacks())) : 0);
            stats.put("totalHacks", user.getSuccessfulHacks() + user.getFailedHacks());
            stats.put("successfulHacks", user.getSuccessfulHacks());
            stats.put("failedHacks", user.getFailedHacks());
            stats.put("vaultLevel", user.getVaultLevel());
            stats.put("level", user.getLevel());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchPlayers(@RequestHeader("Authorization") String authHeader,
                                           @RequestParam String query) {
        try {
            String token = authHeader.substring(7);
            String username = tokenProvider.getUsernameFromToken(token);
            User currentUser = userRepository.findByUsername(username).orElse(null);
            
            if (currentUser == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Search for other players (excluding current user)
            var users = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()))
                .limit(10)
                .map(u -> {
                    Map<String, Object> player = new HashMap<>();
                    player.put("username", u.getUsername());
                    player.put("level", u.getLevel());
                    player.put("vaultLevel", u.getVaultLevel());
                    player.put("successfulHacks", u.getSuccessfulHacks());
                    return player;
                })
                .toList();
            
            return ResponseEntity.ok(Map.of("players", users));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @PostMapping("/hack")
    public ResponseEntity<?> hackPlayer(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody Map<String, String> request) {
        try {
            String token = authHeader.substring(7);
            String attackerUsername = tokenProvider.getUsernameFromToken(token);
            User attacker = userRepository.findByUsername(attackerUsername).orElse(null);
            
            if (attacker == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Attacker not found"));
            }
            
            String victimUsername = request.get("victimUsername");
            User victim = userRepository.findByUsername(victimUsername).orElse(null);
            
            if (victim == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Victim not found"));
            }
            
            if (attacker.getId().equals(victim.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot hack yourself!"));
            }
            
            // Check if attacker's vault level is high enough
            if (attacker.getVaultLevel() <= victim.getVaultLevel()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Your vault level (" + attacker.getVaultLevel() + 
                    ") must be higher than victim's vault level (" + victim.getVaultLevel() + ") to hack!"
                ));
            }
            
            // Generate a puzzle for the attack
            int difficulty = Math.min(3, victim.getVaultLevel());
            Puzzle puzzle = puzzleService.generatePuzzle(difficulty);
            
            Map<String, Object> response = new HashMap<>();
            response.put("victim", victim.getUsername());
            response.put("victimVaultLevel", victim.getVaultLevel());
            response.put("puzzle", Map.of(
                "type", puzzle.type(),
                "question", puzzle.question(),
                "reward", puzzle.reward()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
    
    @PostMapping("/execute-hack")
    public ResponseEntity<?> executeHack(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody Map<String, String> request) {
        try {
            String token = authHeader.substring(7);
            String attackerUsername = tokenProvider.getUsernameFromToken(token);
            User attacker = userRepository.findByUsername(attackerUsername).orElse(null);
            
            if (attacker == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Attacker not found"));
            }
            
            String victimUsername = request.get("victimUsername");
            User victim = userRepository.findByUsername(victimUsername).orElse(null);
            
            if (victim == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Victim not found"));
            }
            
            String answer = request.get("answer");
            boolean hackSuccess = answer != null && !answer.trim().isEmpty();
            
            if (hackSuccess) {
                // Calculate stolen coins (10-30% of victim's coins)
                int stolenCoins = (int) (victim.getCoins() * (0.1 + Math.random() * 0.2));
                stolenCoins = Math.min(stolenCoins, victim.getCoins());
                
                // Transfer coins
                attacker.setCoins(attacker.getCoins() + stolenCoins);
                victim.setCoins(victim.getCoins() - stolenCoins);
                
                // Add XP to attacker
                int xpGained = 100 + (victim.getVaultLevel() * 50);
                attacker.setXp(attacker.getXp() + xpGained);
                attacker.setSuccessfulHacks(attacker.getSuccessfulHacks() + 1);
                
                // Update levels
                int newAttackerLevel = (attacker.getXp() / 500) + 1;
                if (newAttackerLevel > attacker.getLevel()) {
                    attacker.setLevel(newAttackerLevel);
                }
                
                userRepository.save(attacker);
                userRepository.save(victim);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "💀 HACK SUCCESSFUL! 💀\nStole " + stolenCoins + " coins from " + victim.getUsername() + "!\nEarned " + xpGained + " XP!");
                response.put("stolenCoins", stolenCoins);
                response.put("xpGained", xpGained);
                response.put("yourCoins", attacker.getCoins());
                response.put("yourXP", attacker.getXp());
                
                return ResponseEntity.ok(response);
            } else {
                // Failed hack - penalty
                int penaltyCoins = 50;
                attacker.setCoins(attacker.getCoins() - penaltyCoins);
                attacker.setFailedHacks(attacker.getFailedHacks() + 1);
                userRepository.save(attacker);
                
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "❌ HACK FAILED! You lost " + penaltyCoins + " coins as penalty!",
                    "coinsLost", penaltyCoins,
                    "yourCoins", attacker.getCoins()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
}