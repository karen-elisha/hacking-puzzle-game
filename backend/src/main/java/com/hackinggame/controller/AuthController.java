package main.java.com.hackinggame.controller;

import com.hackinggame.model.User;
import com.hackinggame.repository.UserRepository;
import com.hackinggame.security.JwtTokenProvider;
import com.hackinggame.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final SupabaseService supabaseService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        
        // Create user in Supabase Auth
        boolean supabaseCreated = supabaseService.createSupabaseUser(email, password);
        
        if (!supabaseCreated) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create Supabase account"));
        }
        
        // Create local user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setSupabaseUserId(email); // Store email as reference
        
        userRepository.save(user);
        
        String token = tokenProvider.generateToken(username);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", username,
            "message", "Registration successful! Welcome to the hacking underground!"
        ));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        String token = tokenProvider.generateToken(username);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", username,
            "level", user.getLevel(),
            "xp", user.getXp(),
            "coins", user.getCoins(),
            "vaultLevel", user.getVaultLevel(),
            "supabaseEnabled", true,
            "message", "Access granted. Welcome back, hacker!"
        ));
    }
    
    @PostMapping("/supabase-login")
    public ResponseEntity<?> supabaseLogin(@RequestBody Map<String, String> request) {
        String supabaseToken = request.get("supabaseToken");
        
        // Verify Supabase token
        if (!supabaseService.verifySupabaseToken(supabaseToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Supabase token"));
        }
        
        // Get user info from Supabase
        Map<String, Object> supabaseUser = supabaseService.getSupabaseUser(supabaseToken);
        String email = (String) supabaseUser.get("email");
        
        // Find or create local user
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            // Auto-create user if doesn't exist
            user = new User();
            user.setUsername(email.split("@")[0]);
            user.setEmail(email);
            user.setPassword("supabase_oauth_user");
            user.setSupabaseUserId(email);
            user = userRepository.save(user);
        }
        
        String jwtToken = tokenProvider.generateToken(user.getUsername());
        
        return ResponseEntity.ok(Map.of(
            "token", jwtToken,
            "username", user.getUsername(),
            "level", user.getLevel(),
            "xp", user.getXp(),
            "coins", user.getCoins(),
            "vaultLevel", user.getVaultLevel(),
            "message", "Supabase authentication successful!"
        ));
    }
}