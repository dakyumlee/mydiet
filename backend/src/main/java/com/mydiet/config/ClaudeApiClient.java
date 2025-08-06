package com.mydiet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaudeApiClient {

    @Value("${claude.api.key:sk-ant-test}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askClaude(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> requestBody = Map.of(
                "model", "claude-3-sonnet-20240229",
                "max_tokens", 1024,
                "messages", List.of(Map.of(
                    "role", "user", 
                    "content", prompt
                ))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages",
                entity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }
            
            return "Claude 서비스가 일시적으로 사용할 수 없습니다.";
            
        } catch (Exception e) {
            log.error("Claude API 호출 실패", e);
            return generateFallbackResponse(prompt);
        }
    }

    // Claude API 실패 시 대체 응답
    private String generateFallbackResponse(String prompt) {
        if (prompt.contains("운동") && prompt.contains("없음")) {
            return "오늘 운동 안 했네요? 계단이라도 올라가세요! 💪";
        } else if (prompt.contains("감정") && prompt.contains("우울")) {
            return "우울할 때일수록 몸을 움직여야 해요. 작은 걸음부터 시작해보세요! 🌱";
        } else if (prompt.contains("음식") && prompt.contains("과식")) {
            return "과식했다고 자책하지 마세요. 내일부터 다시 시작하면 됩니다! 🎯";
        } else {
            return "오늘도 건강한 하루 보내세요! 작은 노력들이 모여 큰 변화를 만듭니다! ✨";
        }
    }
}