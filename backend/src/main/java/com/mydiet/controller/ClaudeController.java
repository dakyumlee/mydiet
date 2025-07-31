package com.mydiet.controller;

import com.mydiet.config.ClaudeApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ClaudeController {

    private final ClaudeApiClient claudeApiClient;

    @GetMapping("/message")
    public ResponseEntity<String> getClaudeMessage(@RequestParam(defaultValue = "1") Long userId) {
        try {
            log.info("Claude 메시지 요청 - 사용자 ID: {}", userId);
            
            String prompt = String.format(
                "안녕하세요! 사용자 %d님을 위한 건강하고 동기부여가 되는 한국어 메시지를 작성해주세요. " +
                "다이어트와 건강한 생활습관에 대한 격려의 말을 2-3문장으로 짧고 따뜻하게 써주세요.", 
                userId
            );
            
            String response = claudeApiClient.askClaude(prompt);
            log.info("Claude 응답 성공");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Claude 메시지 생성 실패", e);
            return ResponseEntity.ok(
                "오늘도 건강한 하루를 보내고 계시는군요! 🌟\n" +
                "작은 변화가 큰 결과를 만들어냅니다. 당신의 노력을 응원해요! 💪"
            );
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> testClaude(@RequestBody String prompt) {
        try {
            log.info("Claude 커스텀 질문: {}", prompt);
            
            String enhancedPrompt = "다음 질문에 대해 친근하고 도움이 되는 한국어로 답변해주세요: " + prompt;
            String response = claudeApiClient.askClaude(enhancedPrompt);
            
            log.info("Claude 커스텀 응답 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Claude 커스텀 질문 처리 실패", e);
            return ResponseEntity.ok(
                "죄송합니다. 지금은 답변을 드릴 수 없어요. 😔\n" +
                "잠시 후 다시 시도해주세요!"
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Claude API 서비스가 정상 작동 중입니다! ✅");
    }
}