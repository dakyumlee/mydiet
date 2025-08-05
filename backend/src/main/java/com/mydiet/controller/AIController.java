package com.mydiet.controller;

import com.mydiet.config.ClaudeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final ClaudeApiClient claudeApiClient;

    @PostMapping("/question")
    public ResponseEntity<String> askQuestion(@RequestBody AIQuestionRequest request) {
        String response = claudeApiClient.askClaude(request.getQuestion());
        return ResponseEntity.ok(response);
    }

    public static class AIQuestionRequest {
        private String question;
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }
}