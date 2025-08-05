package com.mydiet.controller;

import com.mydiet.dto.EmotionRequest;
import com.mydiet.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request) {
        return ResponseEntity.ok(emotionService.saveEmotion(request));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(@RequestParam Long userId) {
        return ResponseEntity.ok(emotionService.getTodayEmotions(userId));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getEmotionAnalytics(@RequestParam Long userId,
                                               @RequestParam(required = false) Integer days) {
        return ResponseEntity.ok(emotionService.getEmotionAnalytics(userId, days != null ? days : 7));
    }

    @DeleteMapping("/{emotionId}")
    public ResponseEntity<?> deleteEmotion(@PathVariable Long emotionId,
                                         @RequestParam Long userId) {
        emotionService.deleteEmotion(emotionId, userId);
        return ResponseEntity.ok().build();
    }
}