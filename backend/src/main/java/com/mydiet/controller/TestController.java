package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createTestUser() {
        User testUser = new User();
        testUser.setNickname("테스트유저");
        testUser.setEmail("test@example.com");
        testUser.setWeightGoal(70.0);
        testUser.setEmotionMode("무자비");
        
        User saved = userRepository.save(testUser);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/create-user-simple")
    public ResponseEntity<String> createTestUserSimple() {
        User testUser = new User();
        testUser.setNickname("테스트유저");
        testUser.setEmail("test@example.com");
        testUser.setWeightGoal(70.0);
        testUser.setEmotionMode("무자비");
        
        User saved = userRepository.save(testUser);
        return ResponseEntity.ok("사용자 생성됨: ID=" + saved.getId());
    }
}