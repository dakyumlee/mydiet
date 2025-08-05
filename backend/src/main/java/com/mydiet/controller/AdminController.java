package com.mydiet.controller;

import com.mydiet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(adminService.getUserCount());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserDetail(userId));
    }

    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserStats(userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/claude-responses")
    public ResponseEntity<?> getClaudeResponses() {
        return ResponseEntity.ok(adminService.getClaudeResponses());
    }

    @GetMapping("/stats/users")
    public ResponseEntity<?> getUserStats() {
        return ResponseEntity.ok(adminService.getUserCountStats());
    }

    @GetMapping("/stats/meals")
    public ResponseEntity<?> getMealStats() {
        return ResponseEntity.ok(adminService.getMealCountStats());
    }

    @GetMapping("/stats/emotions")
    public ResponseEntity<?> getEmotionStats() {
        return ResponseEntity.ok(adminService.getEmotionCountStats());
    }

    @GetMapping("/stats/workouts")
    public ResponseEntity<?> getWorkoutStats() {
        return ResponseEntity.ok(adminService.getWorkoutCountStats());
    }
}