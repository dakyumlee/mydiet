package com.mydiet.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Value("${claude.api.key:NOT_SET}")
    private String claudeApiKey;

    @GetMapping("/env")
    public ResponseEntity<Map<String, Object>> checkEnvironment() {
        Map<String, Object> response = new HashMap<>();
        
        if (claudeApiKey != null && claudeApiKey.length() > 10) {
            response.put("claudeApiKey", claudeApiKey.substring(0, 10) + "...");
            response.put("claudeApiKeyLength", claudeApiKey.length());
        } else {
            response.put("claudeApiKey", "NOT_SET");
        }
        
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("status", "OK");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simple-test")
    public ResponseEntity<String> simpleTest() {
        try {
            return ResponseEntity.ok("간단한 테스트 성공!");
        } catch (Exception e) {
            return ResponseEntity.ok("에러: " + e.getMessage());
        }
    }
}