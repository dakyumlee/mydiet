package com.mydiet.service;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public String generateResponse(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate today = LocalDate.now();

        List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
        List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
        List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

        String prompt = buildPrompt(user, meals, emotions, workouts);
        log.info("Claude 프롬프트 생성 완료 - userId: {}", userId);

        String response = claudeApiClient.askClaude(prompt);

        // 응답 저장 (변수명 충돌 해결)
        ClaudeResponse claudeLog = new ClaudeResponse();
        claudeLog.setUser(user);
        claudeLog.setType("daily");
        claudeLog.setContent(response);
        claudeLog.setCreatedAt(LocalDateTime.now());
        claudeResponseRepository.save(claudeLog);

        return response;
    }

    private String buildPrompt(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
    
        prompt.append("유저 닉네임: ").append(user.getNickname() != null ? user.getNickname() : "익명").append("\n");
        prompt.append("목표 체중: ").append(user.getWeightGoal() != null ? user.getWeightGoal() + "kg" : "미설정").append("\n");
        prompt.append("감정 모드: ").append(user.getEmotionMode() != null ? user.getEmotionMode() : "다정함").append("\n\n");
    
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
                if (emo.getNote() != null && !emo.getNote().isEmpty()) {
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
                prompt.append("- ").append(w.getType());
                if (w.getDuration() != null) {
                    prompt.append(" ").append(w.getDuration()).append("분");
                }
                if (w.getCaloriesBurned() != null) {
                    prompt.append(" (칼로리: ").append(w.getCaloriesBurned()).append(" kcal)");
                }
                prompt.append("\n");
            }
        }
    
        String emotionMode = user.getEmotionMode() != null ? user.getEmotionMode() : "다정함";
        
        prompt.append("\n\n이 유저에게 ").append(emotionMode).append(" 모드로 한 마디 해줘. 짧고 강렬하게.\n");
        
        if ("무자비".equals(emotionMode)) {
            prompt.append("응답 형식: 단 한 문장, 엄격하고 동기부여하는 스타일로");
        } else if ("츤데레".equals(emotionMode)) {
            prompt.append("응답 형식: 단 한 문장, 비꼬지만 실제로는 걱정하는 스타일로");
        } else {
            prompt.append("응답 형식: 단 한 문장, 다정하고 격려하는 스타일로");
        }
    
        return prompt.toString();
    }
}