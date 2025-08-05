package com.mydiet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Component
@Slf4j 
public class ClaudeApiClient {

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.base-url:https://api.anthropic.com/v1}")
    private String baseUrl;

    @Value("${claude.api.model:claude-3-sonnet-20240229}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askClaude(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 1000,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.8
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/messages",
                entity,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }
            
            return "Claude 응답을 받을 수 없습니다.";
            
        } catch (Exception e) {
            log.error("Claude API 호출 실패: ", e);
            return "Claude 응답 실패: " + e.getMessage();
        }
    }
}