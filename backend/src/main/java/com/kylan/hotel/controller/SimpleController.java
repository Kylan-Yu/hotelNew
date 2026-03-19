package com.kylan.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simple")
public class SimpleController {

    @GetMapping("/ping")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "pong");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .body(response);
    }

    @PostMapping("/echo")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", request);
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "success");
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .body(response);
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String password = request.get("password");
        
        if ("admin".equals(username) && "Admin@123".equals(password)) {
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", "test-token-" + System.currentTimeMillis());
            data.put("refreshToken", "test-refresh-" + System.currentTimeMillis());
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 7200);
            data.put("userId", 1L);
            data.put("username", username);
            data.put("nickname", "Test Admin");
            data.put("permissions", new String[]{"*"});
            data.put("propertyScopes", new Long[]{});
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
                .body(response);
    }
}
