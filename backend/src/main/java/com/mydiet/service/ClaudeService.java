package com.mydiet.service;

import com.mydiet.config.ClaudeApiClient;
import com.mydiet.model.ClaudeResponse;
import com.mydiet.model.EmotionLog;
import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.model.WorkoutLog;
import com.mydiet.repository.ClaudeResponseRepository;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.WorkoutLogRepository;
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
                log.info("사용자를 찾을 수 없음 - 기본 메시지 생성");
                return generateWelcomeMessage();
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
            return generateErrorMessage();
        }
    }

    private String generateWelcomeMessage() {
        String[] welcomeMessages = {
            "안녕하세요! MyDiet에 오신 걸 환영합니다! 🍎 회원가입하고 개인화된 다이어트 조언을 받아보세요!",
            "반갑습니다! 😊 아직 등록된 사용자가 아니시네요. 가입하시면 매일 맞춤형 다이어트 멘트를 드릴게요!",
            "MyDiet에 처음 오셨군요! 🌟 계정을 만들고 식단, 운동, 감정을 기록해보세요. AI가 도와드릴게요!",
            "환영합니다! 🎉 지금 가입하시면 무자비한 다이어트 코치가 되어드릴게요. 준비 되셨나요?",
            "안녕하세요! 👋 MyDiet은 AI가 당신의 다이어트를 도와주는 서비스예요. 시작해볼까요?"
        };
        
        int randomIndex = (int) (Math.random() * welcomeMessages.length);
        return welcomeMessages[randomIndex];
    }

    private String generateErrorMessage() {
        String[] errorMessages = {
            "앗! 잠시 문제가 생겼네요. 😅 다시 시도해주세요!",
            "시스템이 살짝 삐걱거리고 있어요. 🔧 곧 돌아올게요!",
            "으악! 뭔가 잘못됐어요. 😱 기술팀이 열심히 고치고 있으니 잠시만 기다려주세요!",
            "에러가 발생했지만 걱정 마세요! 💪 다시 한 번 시도해보세요!"
        };
        
        int randomIndex = (int) (Math.random() * errorMessages.length);
        return errorMessages[randomIndex];
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