package com.mydiet.service;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        User user = userRepository.findById(userId).orElseThrow();
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
    }

    private String buildPrompt(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
    
        prompt.append("유저 닉네임: ").append(user.getNickname()).append("\n");
        prompt.append("오늘 목표 체중: ").append(user.getWeightGoal()).append("kg\n");
        prompt.append("감정 모드: ").append(user.getEmotionMode()).append("\n\n");
    
        prompt.append("🥗 오늘 먹은 음식:\n");
        if (meals.isEmpty()) prompt.append("- 없음\n");
        for (MealLog meal : meals) {
            prompt.append("- ").append(meal.getDescription())
                  .append(" (예상 칼로리: ").append(meal.getCaloriesEstimate()).append(" kcal)\n");
        }
    
        prompt.append("\n😵 오늘 감정:\n");
        if (emotions.isEmpty()) prompt.append("- 없음\n");
        for (EmotionLog emo : emotions) {
            prompt.append("- ").append(emo.getMood()).append(": ").append(emo.getNote()).append("\n");
        }
    
        prompt.append("\n🏃 운동 기록:\n");
        if (workouts.isEmpty()) prompt.append("- 없음\n");
        for (WorkoutLog w : workouts) {
            prompt.append("- ").append(w.getType()).append(" ").append(w.getDuration())
                  .append("분 (칼로리: ").append(w.getCaloriesBurned()).append(" kcal)\n");
        }
    
        prompt.append("\n\n이 유저에게 감정 모드에 맞춰 한 마디 해줘. 짧고 강렬하게. 욕 가능.\n");
        prompt.append("응답 형식: 단 한 문장, 비꼬거나 감정 담긴 스타일로\n");
    
        return prompt.toString();
    }
}