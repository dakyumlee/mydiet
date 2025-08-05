package com.mydiet.controller;

import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class SimpleTestController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Map<String, Object> response = new HashMap<>();
        long count = userRepository.count();
        response.put("userCount", count);
        response.put("message", count > 0 ? "사용자가 존재합니다" : "사용자가 없습니다");
        return ResponseEntity.ok(response);
    }
}