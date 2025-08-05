package com.mydiet.controller;

import com.mydiet.config.ClaudeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude-test")
@RequiredArgsConstructor
public class DirectClaudeTestController {

    private final ClaudeApiClient claudeApiClient;

    @GetMapping("/hello")
    public ResponseEntity<String> testClaude() {
        String prompt = "안녕하세요! 간단한 인사말 한 문장으로 해주세요.";
        String response = claudeApiClient.askClaude(prompt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/diet-advice")
    public ResponseEntity<String> testDietAdvice() {
        String prompt = "다이어트 중인 사람에게 무자비한 스타일로 한 마디 해주세요. 한 문장으로.";
        String response = claudeApiClient.askClaude(prompt);
        return ResponseEntity.ok(response);
    }
}