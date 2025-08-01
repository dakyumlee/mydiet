package com.mydiet.service;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.*;
import com.mydiet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaudeService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MealLogRepository mealLogRepository;
    
    @Autowired
    private EmotionLogRepository emotionLogRepository;
    
    @Autowired
    private WorkoutLogRepository workoutLogRepository;
    
    @Autowired
    private ClaudeResponseRepository claudeResponseRepository;
    
    @Autowired
    private ClaudeApiClient claudeApiClient;

    public String generateResponse(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "사용자를 찾을 수 없습니다.";
        }

        LocalDate today = LocalDate.now();

        List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
        List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
        List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

        String prompt = buildPrompt(user, meals, emotions, workouts);
        String response = claudeApiClient.askClaude(prompt);

        // 응답 저장
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
            prompt.append("- ").append(meal.getDescription());
            if (meal.getCaloriesEstimate() != null) {
                prompt.append(" (예상 칼로리: ").append(meal.getCaloriesEstimate()).append(" kcal)");
            }
            prompt.append("\n");
        }
    
        prompt.append("\n😊 오늘 감정:\n");
        if (emotions.isEmpty()) prompt.append("- 없음\n");
        for (EmotionLog emo : emotions) {
            prompt.append("- ").append(emo.getMood());
            if (emo.getNote() != null) {
                prompt.append(": ").append(emo.getNote());
            }
            prompt.append("\n");
        }
    
        prompt.append("\n🏃 운동 기록:\n");
        if (workouts.isEmpty()) prompt.append("- 없음\n");
        for (WorkoutLog w : workouts) {
            prompt.append("- ").append(w.getType()).append(" ").append(w.getDuration()).append("분");
            if (w.getCaloriesBurned() != null) {
                prompt.append(" (칼로리: ").append(w.getCaloriesBurned()).append(" kcal)");
            }
            prompt.append("\n");
        }
    
        return prompt.toString();
    }
}
