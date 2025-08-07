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
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            LocalDate today = LocalDate.now();

            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

            String prompt = buildPrompt(user, meals, emotions, workouts);
            String response = claudeApiClient.askClaude(prompt);

            // Claude 응답 로그 저장
            ClaudeResponse log = ClaudeResponse.builder()
                .user(user)
                .type("daily")
                .content(response)
                .createdAt(LocalDateTime.now())
                .build();
            claudeResponseRepository.save(log);

            return response;
        } catch (Exception e) {
            return "Claude 응답 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    private String buildPrompt(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
    
        prompt.append("유저 닉네임: ").append(user.getNickname()).append("\n");
        prompt.append("오늘 목표 체중: ").append(user.getWeightGoal()).append("kg\n");
        prompt.append("감정 모드: ").append(user.getEmotionMode()).append("\n\n");
    
        prompt.append("🥗 오늘 먹은 음식:\n");
        if (meals.isEmpty()) {
            prompt.append("- 없음\n");
        } else {
            for (MealLog meal : meals) {
                prompt.append("- ").append(meal.getDescription());
                if (meal.getCaloriesEstimate() != null) {
                    prompt.append(" (예상 칼로리: ").append(meal.getCaloriesEstimate()).append(" kcal)");
                }
                prompt.append("\n");
            }
        }
    
        prompt.append("\n😊 오늘 감정:\n");
        if (emotions.isEmpty()) {
            prompt.append("- 없음\n");
        } else {
            for (EmotionLog emo : emotions) {
                prompt.append("- ").append(emo.getMood());
                if (emo.getNote() != null && !emo.getNote().trim().isEmpty()) {
                    prompt.append(": ").append(emo.getNote());
                }
                prompt.append("\n");
            }
        }
    
        prompt.append("\n🏃 운동 기록:\n");
        if (workouts.isEmpty()) {
            prompt.append("- 없음\n");
        } else {
            for (WorkoutLog w : workouts) {
                prompt.append("- ").append(w.getType()).append(" ").append(w.getDuration()).append("분");
                if (w.getCaloriesBurned() != null) {
                    prompt.append(" (칼로리: ").append(w.getCaloriesBurned()).append(" kcal)");
                }
                prompt.append("\n");
            }
        }
    
        prompt.append("\n\n이 유저에게 감정 모드('").append(user.getEmotionMode()).append("')에 맞춰 한국어로 한 마디 해줘.\n");
        prompt.append("응답 형식: 단 한 문장, 감정 모드에 맞는 스타일로. 친근하고 격려하는 톤으로.\n");
    
        return prompt.toString();
    }
}