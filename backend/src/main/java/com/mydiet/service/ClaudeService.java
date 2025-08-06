package com.mydiet.service;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import com.mydiet.config.ClaudeApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaudeService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;
    private final ClaudeApiClient claudeApiClient;

    public String generateResponse(Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return "아직 사용자 정보가 없습니다. 프로필을 설정해주세요!";
            }
            
            LocalDate today = LocalDate.now();

            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

            String prompt = buildPrompt(user, meals, emotions, workouts);
            String response = claudeApiClient.askClaude(prompt);

            ClaudeResponse log = new ClaudeResponse();
            log.setUser(user);
            log.setType("daily");
            log.setContent(response);
            log.setCreatedAt(LocalDateTime.now());
            claudeResponseRepository.save(log);

            return response;
            
        } catch (Exception e) {
            log.error("Error generating Claude response: ", e);
            return "오늘도 화이팅! 💪";
        }
    }

    private String buildPrompt(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
    
        prompt.append("유저 닉네임: ").append(user.getNickname()).append("\n");
        prompt.append("오늘 목표 체중: ").append(user.getWeightGoal()).append("kg\n");
        prompt.append("감정 모드: ").append(user.getEmotionMode()).append("\n\n");
    
        prompt.append("🥗 오늘 먹은 음식:\n");
        if (meals.isEmpty()) prompt.append("- 없음\n");
        for (MealLog meal : meals) {
            prompt.append("- ").append(meal.getDescription()).append(" (예상 칼로리: ").append(meal.getCaloriesEstimate()).append(" kcal)\n");
        }
    
        prompt.append("\n😵 오늘 감정:\n");
        if (emotions.isEmpty()) prompt.append("- 없음\n");
        for (EmotionLog emo : emotions) {
            prompt.append("- ").append(emo.getMood()).append(": ").append(emo.getNote()).append("\n");
        }
    
        prompt.append("\n🏃 운동 기록:\n");
        if (workouts.isEmpty()) prompt.append("- 없음\n");
        for (WorkoutLog w : workouts) {
            prompt.append("- ").append(w.getType()).append(" ").append(w.getDuration()).append("분 ").append("(칼로리: ").append(w.getCaloriesBurned()).append(" kcal)\n");
        }
    
        prompt.append("\n\n이 유저에게 감정 모드에 맞춰 한 마디 해줘. 짧고 강렬하게.\n");
        prompt.append("응답 형식: 단 한 문장, 비꼬거나 감정 담긴 스타일로\n");
    
        return prompt.toString();
    }
}