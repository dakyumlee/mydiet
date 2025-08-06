package com.mydiet.controller;

import com.mydiet.dto.EmotionRequest;
import com.mydiet.model.EmotionLog;
import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import com.mydiet.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request) {
        Long userId = request.getUserId();
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            user = new User();
            user.setId(userId);
            user.setEmail("user" + userId + "@mydiet.com");
            user.setNickname("사용자" + userId);
            user.setWeightGoal(65.0);
            user.setEmotionMode("보통");
            user.setCreatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        EmotionLog saved = emotionService.saveEmotion(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(@RequestParam Long userId) {
        return ResponseEntity.ok(emotionService.getTodayEmotions(userId));
    }
}