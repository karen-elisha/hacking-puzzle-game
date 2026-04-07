package com.hackinggame.service;

import com.hackinggame.config.SupabaseConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupabaseService {
    
    private final SupabaseConfig supabaseConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public boolean verifySupabaseToken(String supabaseToken) {
        try {
            String url = supabaseConfig.getUrl() + "/auth/v1/user";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseToken);
            headers.set("apikey", supabaseConfig.getKey());
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Map<String, Object> getSupabaseUser(String supabaseToken) {
        try {
            String url = supabaseConfig.getUrl() + "/auth/v1/user";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseToken);
            headers.set("apikey", supabaseConfig.getKey());
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean createSupabaseUser(String email, String password) {
        try {
            String url = supabaseConfig.getUrl() + "/auth/v1/signup";
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseConfig.getKey());
            headers.set("Content-Type", "application/json");
            
            Map<String, String> body = Map.of(
                "email", email,
                "password", password
            );
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}