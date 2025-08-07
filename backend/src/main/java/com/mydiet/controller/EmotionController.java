package com.mydiet.controller;

import com.mydiet.dto.EmotionRequest;
import com.mydiet.model.EmotionLog;
import com.mydiet.service.EmotionService;
import com.mydiet.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;
    private final SessionUtil sessionUtil;

    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = sessionUtil.getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            request.setUserId(userId);
            EmotionLog saved = emotionService.saveEmotion(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("감정 기록 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(HttpServletRequest httpRequest) {
        try {
            Long userId = sessionUtil.getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            return ResponseEntity.ok(emotionService.getTodayEmotions(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("감정 기록 조회 실패: " + e.getMessage());
        }
    }
}