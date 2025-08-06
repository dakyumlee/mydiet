package com.mydiet.controller;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIChatController {

    private final ClaudeApiClient claudeApiClient;
    private final UserRepository userRepository;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askClaude(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("=== Claude AI 질문 요청 ===");
        
        try {
            String question = (String) request.get("question");
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "질문을 입력해주세요"
                ));
            }

            // 사용자 정보 가져오기 (선택사항)
            Long userId = (Long) session.getAttribute("userId");
            String userContext = "";
            
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    userContext = String.format("사용자: %s (목표체중: %.1fkg, 감정모드: %s)", 
                            user.getNickname(), user.getWeightGoal(), user.getEmotionMode());
                }
            }

            // Claude API 호출을 위한 프롬프트 구성
            String prompt = buildChatPrompt(question, userContext);
            
            log.info("Claude에게 질문: {}", question);
            String response = claudeApiClient.askClaude(prompt);
            log.info("Claude 응답 길이: {} 문자", response.length());

            return ResponseEntity.ok(Map.of(
                "answer", response,
                "question", question,
                "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            log.error("Claude AI 질문 처리 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "죄송합니다. 현재 AI 서비스에 문제가 있습니다. 잠시 후 다시 시도해주세요.",
                "details", e.getMessage()
            ));
        }
    }

    private String buildChatPrompt(String question, String userContext) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 MyDiet 앱의 건강 관리 AI 어시스턴트입니다.\n\n");
        
        if (!userContext.isEmpty()) {
            prompt.append("현재 사용자 정보: ").append(userContext).append("\n\n");
        }
        
        prompt.append("사용자 질문: ").append(question).append("\n\n");
        
        prompt.append("다음 원칙에 따라 답변해주세요:\n");
        prompt.append("1. 친근하고 도움이 되는 톤으로 답변\n");
        prompt.append("2. 건강과 관련된 질문에는 전문적이지만 이해하기 쉽게 설명\n");
        prompt.append("3. 구체적인 의학적 진단이나 처방은 하지 말고 일반적인 건강 정보만 제공\n");
        prompt.append("4. 답변은 200자 이내로 간결하게\n");
        prompt.append("5. 이모지를 적절히 사용해서 친근하게\n\n");
        
        prompt.append("답변:");
        
        return prompt.toString();
    }
}