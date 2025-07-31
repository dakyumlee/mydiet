package com.mydiet.controller;

import com.mydiet.dto.EmotionRequest;
import com.mydiet.entity.EmotionLog;
import com.mydiet.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request) {
        EmotionLog saved = emotionService.saveEmotion(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(@RequestParam Long userId) {
        return ResponseEntity.ok(emotionService.getTodayEmotions(userId));
    }
}