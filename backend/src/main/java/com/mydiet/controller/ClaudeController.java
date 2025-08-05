package com.mydiet.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/claude")
public class ClaudeController {

    @GetMapping("/message")
    public ResponseEntity<String> getClaudeMessage(@RequestParam Long userId) {
        return ResponseEntity.ok("안녕! 나는 Claude야. 새로운 API 키로 잘 작동하고 있어!");
    }
}
