package com.mydiet.controller;

import com.mydiet.service.ClaudeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/claude")
@RequiredArgsConstructor
public class ClaudeController {

    private final ClaudeService claudeService;

    @GetMapping("/message")
    public ResponseEntity<String> getClaudeMessage(@RequestParam(required = false) Long userId,
                                                   HttpSession session) {
        try {
            if (userId == null) {
                userId = (Long) session.getAttribute("userId");
            }
            
            if (userId == null) {
                userId = 1L;
            }
            
            String message = claudeService.generateResponse(userId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error getting Claude message: ", e);
            return ResponseEntity.ok("오늘도 좋은 하루 보내세요! 🌟");
        }
    }
}