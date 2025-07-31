package com.mydiet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClaudeApiClient {

    @Value("${claude.api.key:dummy-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public String askClaude(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> requestBody = Map.of(
                "model", "claude-3-sonnet-20240229",
                "max_tokens", 1000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
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
            
            return "Claude API 응답을 받을 수 없습니다.";
            
        } catch (Exception e) {
            return "Claude API 오류: " + e.getMessage();
        }
    }
}