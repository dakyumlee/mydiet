package com.mydiet.controller;

import com.mydiet.dto.EmotionRequest;
import com.mydiet.model.EmotionLog;
import com.mydiet.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request, Authentication authentication) {
        try { 
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Authentication required");
            }
             
            Long actualUserId = request.getUserId() != null ? request.getUserId() : 1L;
            request.setUserId(actualUserId);
            
            EmotionLog saved = emotionService.saveEmotion(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving emotion: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(@RequestParam(required = false) Long userId, 
                                            Authentication authentication) {
        try { 
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Collections.emptyList());  
            }
             
            Long actualUserId = userId != null ? userId : 1L;
            
            return ResponseEntity.ok(emotionService.getTodayEmotions(actualUserId));
        } catch (Exception e) { 
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}