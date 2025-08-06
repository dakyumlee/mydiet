package com.mydiet.controller;

import com.mydiet.model.EmotionLog;
import com.mydiet.model.User;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmotionController {
    
    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("Saving emotion: {}", request);
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            // final 변수로 선언하여 람다에서 사용 가능하게 함
            final Long finalUserId = userId;
            
            User user = userRepository.findById(finalUserId).orElseGet(() -> {
                User newUser = new User();
                newUser.setId(finalUserId);
                newUser.setEmail("user" + finalUserId + "@mydiet.com");
                newUser.setNickname("사용자" + finalUserId);
                return userRepository.save(newUser);
            });
                
            EmotionLog emotion = new EmotionLog();
            emotion.setUser(user);
            emotion.setMood((String) request.get("mood"));
            emotion.setNote((String) request.get("note"));
            emotion.setDate(LocalDate.now());
            
            EmotionLog saved = emotionLogRepository.save(emotion);
            log.info("Emotion saved with id: {}", saved.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "감정이 기록되었습니다!"
            ));
            
        } catch (Exception e) {
            log.error("Error saving emotion: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, LocalDate.now());
            return ResponseEntity.ok(emotions);
        } catch (Exception e) {
            log.error("Error fetching emotions: ", e);
            return ResponseEntity.ok(List.of());
        }
    }
}