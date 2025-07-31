package com.mydiet.controller;

import com.mydiet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    @PostMapping("/claude/test")
    public ResponseEntity<String> testClaudeApi(@RequestParam Long userId) {
        return ResponseEntity.ok(adminService.testClaudeResponse(userId));
    }
}