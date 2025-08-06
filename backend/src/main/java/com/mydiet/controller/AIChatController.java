package com.mydiet.controller;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.*;
import com.mydiet.repository.*;
import com.mydiet.service.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIChatController {

    private final ClaudeApiClient claudeApiClient;
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askClaude(
        @RequestBody Map<String, String> request,
        @AuthenticationPrincipal OAuth2UserPrincipal principal) {
        
        try {
            String userQuestion = request.get("question");
            if (userQuestion == null || userQuestion.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "질문을 입력해주세요."));
            }

            User user = principal.getUser();
            log.info("AI 질문 요청 - 사용자: {}, 질문: {}", user.getEmail(), userQuestion);

            // 사용자 맞춤 프롬프트 생성
            String contextPrompt = buildUserContextPrompt(user, userQuestion);
            
            // Claude API 호출
            String claudeResponse = claudeApiClient.askClaude(contextPrompt);
            
            // 응답 저장
            saveClaudeResponse(user, userQuestion, claudeResponse);
            
            return ResponseEntity.ok(Map.of(
                "response", claudeResponse,
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Claude 챗봇 오류", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }

    @GetMapping("/daily-message")
    public ResponseEntity<Map<String, Object>> getDailyMessage(
        @AuthenticationPrincipal OAuth2UserPrincipal principal) {
        
        try {
            User user = principal.getUser();
            log.info("일일 메시지 요청 - 사용자: {}", user.getEmail());

            // 오늘의 활동 데이터 수집
            LocalDate today = LocalDate.now();
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(user.getId(), today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(user.getId(), today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(user.getId(), today);

            // 일일 리포트 프롬프트 생성
            String dailyPrompt = buildDailyReportPrompt(user, meals, emotions, workouts);
            
            // Claude 응답 생성
            String claudeResponse = claudeApiClient.askClaude(dailyPrompt);
            
            // 응답 저장
            saveClaudeResponse(user, "일일 리포트", claudeResponse);
            
            return ResponseEntity.ok(Map.of(
                "message", claudeResponse,
                "mealsCount", meals.size(),
                "emotionsCount", emotions.size(),
                "workoutsCount", workouts.size(),
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("일일 메시지 생성 오류", e);
            return ResponseEntity.ok(Map.of(
                "message", "오늘도 건강한 하루 보내세요! 💪",
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    private String buildUserContextPrompt(User user, String question) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 친근하고 전문적인 건강 관리 AI 코치입니다.\n\n");
        prompt.append("사용자 정보:\n");
        prompt.append("- 닉네임: ").append(user.getNickname()).append("\n");
        prompt.append("- 목표 체중: ").append(user.getWeightGoal()).append("kg\n");
        prompt.append("- 선호하는 소통 스타일: ").append(user.getEmotionMode()).append("\n\n");
        prompt.append("사용자 질문: ").append(question).append("\n\n");
        prompt.append("위 정보를 바탕으로 사용자의 질문에 친근하고 도움이 되는 답변을 해주세요. ");
        prompt.append("건강, 다이어트, 운동, 영양에 관련된 조언을 중심으로 답변해주세요.");
        
        return prompt.toString();
    }

    private String buildDailyReportPrompt(User user, List<MealLog> meals, 
                                        List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 개인 맞춤형 건강 코치입니다. 사용자의 오늘 활동을 분석해서 ");
        prompt.append(user.getEmotionMode()).append(" 톤으로 격려 메시지를 작성해주세요.\n\n");
        
        prompt.append("사용자: ").append(user.getNickname()).append("\n");
        prompt.append("목표 체중: ").append(user.getWeightGoal()).append("kg\n\n");
        
        prompt.append("🍽️ 오늘 식사:\n");
        if (meals.isEmpty()) {
            prompt.append("- 기록된 식사 없음\n");
        } else {
            for (MealLog meal : meals) {
                prompt.append("- ").append(meal.getDescription());
                if (meal.getCaloriesEstimate() != null) {
                    prompt.append(" (").append(meal.getCaloriesEstimate()).append(" kcal)");
                }
                prompt.append("\n");
            }
        }
        
        prompt.append("\n😊 감정 상태:\n");
        if (emotions.isEmpty()) {
            prompt.append("- 기록된 감정 없음\n");
        } else {
            for (EmotionLog emotion : emotions) {
                prompt.append("- ").append(emotion.getMood()).append(": ").append(emotion.getNote()).append("\n");
            }
        }
        
        prompt.append("\n💪 운동 기록:\n");
        if (workouts.isEmpty()) {
            prompt.append("- 기록된 운동 없음\n");
        } else {
            for (WorkoutLog workout : workouts) {
                prompt.append("- ").append(workout.getType()).append(" ")
                      .append(workout.getDuration()).append("분");
                if (workout.getCaloriesBurned() != null) {
                    prompt.append(" (").append(workout.getCaloriesBurned()).append(" kcal 소모)");
                }
                prompt.append("\n");
            }
        }
        
        prompt.append("\n이 정보를 바탕으로 1-2문장의 격려 메시지를 작성해주세요. ");
        prompt.append("구체적이고 개인화된 조언을 포함해주세요.");
        
        return prompt.toString();
    }

    private void saveClaudeResponse(User user, String question, String response) {
        try {
            ClaudeResponse log = ClaudeResponse.builder()
                .user(user)
                .type("chat")
                .content(response)
                .createdAt(LocalDateTime.now())
                .build();
            claudeResponseRepository.save(log);
        } catch (Exception e) {
            log.warn("Claude 응답 저장 실패", e);
        }
    }
}