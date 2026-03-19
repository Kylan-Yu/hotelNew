package com.kylan.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Backend is running");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", "test-token-" + System.currentTimeMillis());
        data.put("refreshToken", "test-refresh-" + System.currentTimeMillis());
        data.put("tokenType", "Bearer");
        data.put("expiresIn", 7200);
        data.put("userId", 1L);
        data.put("username", username);
        data.put("nickname", "Test User");
        data.put("permissions", new String[]{"*"});
        data.put("propertyScopes", new Long[]{});
        data.put("currentPropertyId", null);
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
}
