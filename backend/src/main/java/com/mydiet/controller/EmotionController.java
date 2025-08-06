package com.mydiet.controller;

import com.mydiet.model.EmotionLog;
import com.mydiet.model.User;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.service.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {
    
    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveEmotion(@AuthenticationPrincipal OAuth2UserPrincipal principal,
                                         @RequestBody Map<String, Object> request) {
        try {
            User user = userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            EmotionLog emotion = new EmotionLog();
            emotion.setUser(user);
            emotion.setMood((String) request.get("mood"));
            emotion.setNote((String) request.get("note"));
            emotion.setDate(LocalDate.now());
            
            EmotionLog saved = emotionLogRepository.save(emotion);
            log.info("Emotion saved for user {}: {}", user.getEmail(), saved.getMood());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "감정이 기록되었습니다."
            ));
        } catch (Exception e) {
            log.error("Error saving emotion: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        try {
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(
                principal.getUserId(), 
                LocalDate.now()
            );
            return ResponseEntity.ok(emotions);
        } catch (Exception e) {
            log.error("Error fetching emotions: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}