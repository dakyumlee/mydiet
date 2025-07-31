package com.mydiet.controller;

import com.mydiet.dto.AdminLoginRequest;
import com.mydiet.entity.User;
import com.mydiet.service.AdminService;
import com.mydiet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest request) {
        if ("admin@mydiet.com".equals(request.getEmail()) && 
            "admin123".equals(request.getPassword())) {
            
            String token = "admin-token-" + System.currentTimeMillis();
            return ResponseEntity.ok(Map.of("token", token, "message", "관리자 로그인 성공"));
        }
        
        return ResponseEntity.badRequest()
            .body(Map.of("message", "관리자 인증에 실패했습니다."));
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "사용자가 삭제되었습니다."));
    }

    @GetMapping("/today-stats")
    public ResponseEntity<?> getTodayStats() {
        return ResponseEntity.ok(adminService.getTodayStats());
    }
}