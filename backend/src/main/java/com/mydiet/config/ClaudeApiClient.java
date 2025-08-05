package com.mydiet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaudeApiClient {

    @Value("${claude.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askClaude(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);  // Bearer 대신 x-api-key 사용
            headers.set("anthropic-version", "2023-06-01");  // 필수 헤더 추가

            Map<String, Object> requestBody = Map.of(
                "model", "claude-3-5-sonnet-20241022",  // 최신 모델
                "max_tokens", 1024,  // 필수 파라미터 추가
                "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Claude API 요청 시작 - 프롬프트 길이: {}", prompt.length());

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages",
                entity,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    String responseText = (String) content.get(0).get("text");
                    log.info("Claude API 응답 성공 - 응답 길이: {}", responseText.length());
                    return responseText;
                }
            }

            log.error("Claude API 응답 파싱 실패: {}", response.getBody());
            return "Claude가 응답을 생성하지 못했습니다.";

        } catch (HttpClientErrorException e) {
            log.error("Claude API 클라이언트 오류 - 상태코드: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            if (e.getStatusCode().value() == 401) {
                return "API 키가 유효하지 않습니다. 관리자에게 문의하세요.";
            } else if (e.getStatusCode().value() == 429) {
                return "API 호출 한도를 초과했습니다. 잠시 후 다시 시도해주세요.";
            } else {
                return "Claude API 오류가 발생했습니다. (" + e.getStatusCode() + ")";
            }
            
        } catch (HttpServerErrorException e) {
            log.error("Claude API 서버 오류 - 상태코드: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Claude 서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
            
        } catch (ResourceAccessException e) {
            log.error("Claude API 네트워크 오류: {}", e.getMessage());
            return "네트워크 연결에 문제가 있습니다. 인터넷 연결을 확인해주세요.";
            
        } catch (Exception e) {
            log.error("Claude API 예상치 못한 오류: {}", e.getMessage(), e);
            return "Claude 서비스에 문제가 발생했습니다: " + e.getMessage();
        }
    }
}