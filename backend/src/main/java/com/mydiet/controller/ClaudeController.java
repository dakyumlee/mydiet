package com.mydiet.controller;

import com.mydiet.service.ClaudeService;
import com.mydiet.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/claude")
@RequiredArgsConstructor
public class ClaudeController {

    private final ClaudeService claudeService;
    private final SessionUtil sessionUtil;

    @GetMapping("/message")
    public ResponseEntity<String> getClaudeMessage(HttpServletRequest request) {
        try {
            Long userId = sessionUtil.getCurrentUserId(request);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            String message = claudeService.generateResponse(userId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Claude 응답 생성 실패: " + e.getMessage());
        }
    }
}