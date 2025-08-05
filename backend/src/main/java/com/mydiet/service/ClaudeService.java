package com.mydiet.service;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;
    private final ClaudeApiClient claudeApiClient;

    public String generateResponse(Long userId) {
        try {
            log.info("Claude 응답 생성 시작 - 사용자 ID: {}", userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.error("사용자를 찾을 수 없음 - ID: {}", userId);
                return "사용자를 찾을 수 없습니다. (ID: " + userId + ")";
            }
            
            User user = userOpt.get();
            LocalDate today = LocalDate.now();

            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

            log.info("데이터 조회 완료 - 식사: {}, 감정: {}, 운동: {}", meals.size(), emotions.size(), workouts.size());

            String prompt = buildPrompt(user, meals, emotions, workouts);
            String response = claudeApiClient.askClaude(prompt);

            ClaudeResponse claudeLog = new ClaudeResponse();
            claudeLog.setUser(user);
            claudeLog.setType("daily");
            claudeLog.setContent(response);
            claudeLog.setCreatedAt(LocalDateTime.now());
            claudeResponseRepository.save(claudeLog);

            log.info("Claude 응답 생성 완료");
            return response;
            
        } catch (Exception e) {
            log.error("Claude 응답 생성 중 오류 발생", e);
            return "응답 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    private String buildPrompt(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
    
        prompt.append("유저 닉네임: ").append(user.getNickname()).append("\n");
        prompt.append("목표 체중: ").append(user.getWeightGoal()).append("kg\n");
        prompt.append("감정 모드: ").append(user.getEmotionMode()).append("\n\n");
    
        prompt.append("🥗 오늘 먹은 음식:\n");
        if (meals.isEmpty()) {
            prompt.append("- 없음\n");
        } else {
            for (MealLog meal : meals) {
                prompt.append("- ").append(meal.getDescription())
                      .append(" (칼로리: ").append(meal.getCaloriesEstimate()).append(" kcal)\n");
            }
        }
    
        prompt.append("\n😵 오늘 감정:\n");
        if (emotions.isEmpty()) {
            prompt.append("- 없음\n");
        } else {
            for (EmotionLog emo : emotions) {
                prompt.append("- ").append(emo.getMood()).append(": ").append(emo.getNote()).append("\n");
            }
        }
    
        prompt.append("\n🏃 운동 기록:\n");
        if (workouts.isEmpty()) {
            prompt.append("- 없음\n");
        } else {
            for (WorkoutLog w : workouts) {
                prompt.append("- ").append(w.getType()).append(" ").append(w.getDuration())
                      .append("분 (칼로리: ").append(w.getCaloriesBurned()).append(" kcal)\n");
            }
        }
    
        prompt.append("\n\n이 유저에게 감정 모드('").append(user.getEmotionMode()).append("')에 맞춰 한 마디 해줘. ");
        prompt.append("짧고 강렬하게. 욕설도 가능.\n");
        prompt.append("응답 형식: 단 한 문장, 비꼬거나 감정이 담긴 스타일로\n");
    
        return prompt.toString();
    }
}