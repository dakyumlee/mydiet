package com.mydiet.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClaudeApiClient {

    @Value("${claude.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askClaude(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey); 
        headers.set("anthropic-version", "2023-06-01");  

        Map<String, Object> requestBody = Map.of(
            "model", "claude-3-5-sonnet-20241022",  
            "max_tokens", 1000, 
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "temperature", 0.8
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages",
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                return "Claude 응답이 null입니다.";
            }

            List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");
            if (content == null || content.isEmpty()) {
                return "Claude 응답 내용이 비어있습니다.";
            }

            return (String) content.get(0).get("text");
        } catch (Exception e) {
            e.printStackTrace(); 
            return "Claude 응답 실패: " + e.getMessage();
        }
    }
}