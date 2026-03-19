package com.kylan.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/final-auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinalAuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String password = request.get("password");
        
        if ("admin".equals(username) && "Admin@123".equals(password)) {
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", "final-token-" + System.currentTimeMillis());
            data.put("refreshToken", "final-refresh-" + System.currentTimeMillis());
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 7200);
            data.put("userId", 1L);
            data.put("username", username);
            data.put("nickname", "Final Admin");
            data.put("permissions", List.of("*"));
            data.put("propertyScopes", List.of());
            data.put("currentPropertyId", null);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", data);
        } else {
            response.put("code", 401);
            response.put("message", "invalid credentials");
            response.put("data", null);
        }
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .header("Access-Control-Max-Age", "3600")
                .body(response);
    }

    @RequestMapping(value = "/login", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }
}
