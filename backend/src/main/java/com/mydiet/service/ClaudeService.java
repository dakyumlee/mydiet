package com.mydiet.service;

import com.mydiet.config.ClaudeApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeService {

    private final ClaudeApiClient claudeApiClient;

    public String generateResponse(Long userId) {
        try {
            String prompt = buildPrompt(userId);
            log.info("Claude 프롬프트 생성 완료 - 사용자 ID: {}", userId);

            String response = claudeApiClient.askClaude(prompt);
            log.info("Claude 응답 생성 완료 - 사용자 ID: {}", userId);

            return response;
        } catch (Exception e) {
            log.error("Claude 응답 생성 실패 - 사용자 ID: {}", userId, e);
            return "안녕하세요! 오늘도 건강한 하루를 위해 노력하는 당신을 응원합니다! 💪\n" +
                   "작은 변화도 큰 성과로 이어집니다. 꾸준히 실천해보세요! 🌟";
        }
    }

    private String buildPrompt(Long userId) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("사용자 ID ").append(userId).append("번 사용자에게 ");
        prompt.append("건강하고 동기부여가 되는 한국어 메시지를 2-3문장으로 작성해주세요. ");
        prompt.append("다이어트와 건강한 생활습관에 대한 격려와 조언을 포함해주세요. ");
        prompt.append("친근하고 따뜻한 톤으로 작성해주세요.");
    
        return prompt.toString();
    }
}