package com.mydiet.controller;

import com.mydiet.service.ClaudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude")
@RequiredArgsConstructor
public class ClaudeController {

    private final ClaudeService claudeService;

    @GetMapping("/message")
    public ResponseEntity<String> getClaudeMessage(@RequestParam Long userId) {
        String message = claudeService.generateResponse(userId);
        return ResponseEntity.ok(message);
    }
}
