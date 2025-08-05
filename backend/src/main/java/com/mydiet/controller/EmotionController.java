package com.mydiet.controller;

import com.mydiet.model.EmotionLog;
import com.mydiet.model.User;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> saveEmotion(@RequestBody EmotionRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        EmotionLog emotion = new EmotionLog();
        emotion.setUser(user);
        emotion.setMood(request.getMood());
        emotion.setNote(request.getNote());
        emotion.setDate(LocalDate.now());
        
        emotionLogRepository.save(emotion);
        return ResponseEntity.ok("감정이 저장되었습니다!");
    }

    @GetMapping("/today")
    public ResponseEntity<List<EmotionLog>> getTodayEmotions(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(user.getId(), LocalDate.now());
        return ResponseEntity.ok(emotions);
    }

    private User getCurrentUser(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String oauthId = String.valueOf(oAuth2User.getAttributes().get("id"));
        String userIdentifier = email != null ? email : oauthId;
        
        return userRepository.findByEmail(userIdentifier)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    public static class EmotionRequest {
        private String mood;
        private String note;
        
        public String getMood() { return mood; }
        public void setMood(String mood) { this.mood = mood; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }
}