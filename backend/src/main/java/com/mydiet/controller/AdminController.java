package com.mydiet.controller;

import com.mydiet.service.AdminService;
import com.mydiet.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Map<String, Object> stats = adminService.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting admin stats: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error getting all users: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            Map<String, Object> userStats = adminService.getUserStats(userId);
            return ResponseEntity.ok(userStats);
        } catch (Exception e) {
            log.error("Error getting user stats: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        String password = credentials.get("password");
        
        if ("oicrcutie1998".equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "관리자 로그인 성공");
            
            log.info("Admin login successful");
            return ResponseEntity.ok(response);
        }
        
        log.warn("Admin login failed - wrong password");
        return ResponseEntity.badRequest().body(Map.of(
            "success", false,
            "message", "비밀번호가 틀렸습니다"
        ));
    }
}