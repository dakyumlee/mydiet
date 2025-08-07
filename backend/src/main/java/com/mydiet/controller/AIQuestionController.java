package com.mydiet.controller;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.User;
import com.mydiet.model.MealLog;
import com.mydiet.model.WorkoutLog;
import com.mydiet.model.EmotionLog;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat") // 경로 변경하여 충돌 방지
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIQuestionController {
    
    private final ClaudeApiClient claudeApiClient;
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    
    @PostMapping("/ask") // 엔드포인트 변경
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            String question = (String) request.get("question");
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            log.info("AI question from user {}: {}", userId, question);
            
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "질문을 입력해주세요."
                ));
            }
            
            // 사용자 데이터 수집
            User user = userRepository.findById(userId).orElse(null);
            LocalDate today = LocalDate.now();
            
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            // 컨텍스트가 포함된 프롬프트 생성
            String contextualPrompt = buildContextualPrompt(user, todayMeals, todayWorkouts, todayEmotions, question);
            
            // Claude API 호출
            String answer = claudeApiClient.askClaude(contextualPrompt);
            
            log.info("AI response generated for user {}", userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "question", question,
                "answer", answer,
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            log.error("Error processing AI question: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "답변 생성 중 오류가 발생했습니다.",
                "answer", "죄송합니다. 현재 답변을 생성할 수 없습니다. 잠시 후 다시 시도해주세요."
            ));
        }
    }
    
    private String buildContextualPrompt(User user, List<MealLog> meals, List<WorkoutLog> workouts, 
                                       List<EmotionLog> emotions, String question) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 MyDiet 앱의 전문 건강 컨설턴트 AI입니다. ");
        prompt.append("사용자의 건강과 다이어트에 대해 친근하고 도움이 되는 조언을 제공해주세요.\n\n");
        
        // 사용자 정보
        prompt.append("=== 사용자 정보 ===\n");
        if (user != null) {
            prompt.append("닉네임: ").append(user.getNickname() != null ? user.getNickname() : "사용자").append("\n");
            prompt.append("목표 체중: ").append(user.getWeightGoal() != null ? user.getWeightGoal() + "kg" : "설정 안됨").append("\n");
            prompt.append("감정 모드: ").append(user.getEmotionMode() != null ? user.getEmotionMode() : "보통").append("\n");
        } else {
            prompt.append("새로운 사용자 (프로필 미설정)\n");
        }
        
        // 오늘의 식단
        prompt.append("\n=== 오늘의 식단 ===\n");
        if (meals.isEmpty()) {
            prompt.append("아직 기록된 식단이 없습니다.\n");
        } else {
            int totalCalories = 0;
            for (MealLog meal : meals) {
                prompt.append("- ").append(meal.getDescription()).append(" (").append(meal.getCaloriesEstimate()).append(" kcal)\n");
                totalCalories += meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0;
            }
            prompt.append("총 섭취 칼로리: ").append(totalCalories).append(" kcal\n");
        }
        
        // 오늘의 운동
        prompt.append("\n=== 오늘의 운동 ===\n");
        if (workouts.isEmpty()) {
            prompt.append("아직 기록된 운동이 없습니다.\n");
        } else {
            int totalBurned = 0;
            for (WorkoutLog workout : workouts) {
                prompt.append("- ").append(workout.getType()).append(" ").append(workout.getDuration()).append("분 (").append(workout.getCaloriesBurned()).append(" kcal 소모)\n");
                totalBurned += workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0;
            }
            prompt.append("총 소모 칼로리: ").append(totalBurned).append(" kcal\n");
        }
        
        // 오늘의 감정
        prompt.append("\n=== 오늘의 감정 ===\n");
        if (emotions.isEmpty()) {
            prompt.append("아직 기록된 감정이 없습니다.\n");
        } else {
            for (EmotionLog emotion : emotions) {
                prompt.append("- 기분: ").append(emotion.getMood()).append(" | 메모: ").append(emotion.getNote()).append("\n");
            }
        }
        
        prompt.append("\n=== 사용자 질문 ===\n");
        prompt.append(question).append("\n\n");
        
        prompt.append("=== 답변 가이드라인 ===\n");
        prompt.append("1. 친근하고 격려하는 톤으로 답변해주세요\n");
        prompt.append("2. 사용자의 현재 상황을 고려한 맞춤형 조언을 제공해주세요\n");
        prompt.append("3. 구체적이고 실행 가능한 조언을 포함해주세요\n");
        prompt.append("4. 건강과 안전을 최우선으로 고려해주세요\n");
        prompt.append("5. 답변은 200자 이내로 간결하게 작성해주세요\n");
        prompt.append("6. 이모지를 적절히 사용해서 친근함을 표현해주세요\n\n");
        
        prompt.append("위의 정보를 바탕으로 사용자의 질문에 답변해주세요:");
        
        return prompt.toString();
    }
    
    // 추천 질문 목록 제공
    @GetMapping("/suggestions")
    public ResponseEntity<?> getQuestionSuggestions(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            // 사용자 데이터 기반으로 추천 질문 생성
            LocalDate today = LocalDate.now();
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            List<String> suggestions = List.of(
                "오늘 먹은 음식들 어떻게 생각해?",
                "목표 달성을 위한 조언 해줘",
                "어떤 운동을 추가로 하면 좋을까?",
                "건강한 간식 추천해줘",
                "물 섭취량은 어느 정도가 적당해?",
                "스트레스 관리 방법 알려줘",
                "잠자기 전 습관 추천해줘",
                "내일 식단 계획 도와줘"
            );
            
            return ResponseEntity.ok(Map.of(
                "suggestions", suggestions,
                "hasMeals", !todayMeals.isEmpty(),
                "hasWorkouts", !todayWorkouts.isEmpty(),
                "hasEmotions", !todayEmotions.isEmpty()
            ));
            
        } catch (Exception e) {
            log.error("Error getting question suggestions: ", e);
            return ResponseEntity.ok(Map.of("suggestions", List.of()));
        }
    }
}