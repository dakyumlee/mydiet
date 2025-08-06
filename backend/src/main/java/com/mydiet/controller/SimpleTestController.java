package com.mydiet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/simple")
@Slf4j
public class SimpleTestController {

    /**
     * 기본 응답 테스트 - 데이터베이스 없이
     */
    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> hello() {
        log.info("=== 기본 응답 테스트 ===");
        
        try {
            Map<String, Object> response = Map.of(
                "message", "Hello! Server is working!",
                "timestamp", LocalDateTime.now().toString(),
                "status", "OK"
            );
            
            log.info("기본 응답 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("기본 응답 실패", e);
            return ResponseEntity.internalServerError().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST 요청 테스트
     */
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> request) {
        log.info("=== POST 요청 테스트 ===");
        log.info("받은 데이터: {}", request);
        
        try {
            Map<String, Object> response = Map.of(
                "received", request,
                "timestamp", LocalDateTime.now().toString(),
                "message", "데이터를 성공적으로 받았습니다"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("POST 요청 처리 실패", e);
            return ResponseEntity.internalServerError().body(
                Map.of("error", e.getMessage())
            );
        }
    }
}