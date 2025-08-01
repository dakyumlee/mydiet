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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            LocalDate today = LocalDate.now();
            
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

            String prompt = buildPrompt(user, meals, emotions, workouts);
            String response = claudeApiClient.askClaude(prompt);

            // 응답 저장
            ClaudeResponse log = ClaudeResponse.builder()
                .user(user)
                .type("daily")
                .content(response)
                .createdAt(LocalDateTime.now())
                .build();
            claudeResponseRepository.save(log);

            return response;
            
        } catch (Exception e) {
            String errorMsg = "오늘은 Claude가 휴가 중이에요 😅 내일 다시 만나요!";
            System.err.println("Claude 서비스 오류: " + e.getMessage());
            return errorMsg;
        }
    }

    private String buildPrompt(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 다이어트 코치입니다. 다음 사용자 정보를 바탕으로 한 줄 멘트를 해주세요.\n\n");
        
        prompt.append("📊 사용자 정보:\n");
        prompt.append("- 닉네임: ").append(user.getNickname()).append("\n");
        prompt.append("- 목표 체중: ").append(user.getWeightGoal()).append("kg\n");
        prompt.append("- 선호 톤: ").append(user.getEmotionMode()).append("\n\n");
        
        prompt.append("🍽️ 오늘 식단:\n");
        if (meals.isEmpty()) {
            prompt.append("- 기록된 식사가 없습니다\n");
        } else {
            int totalCalories = 0;
            for (MealLog meal : meals) {
                prompt.append("- ").append(meal.getDescription());
                if (meal.getCaloriesEstimate() != null) {
                    prompt.append(" (").append(meal.getCaloriesEstimate()).append("kcal)");
                    totalCalories += meal.getCaloriesEstimate();
                }
                prompt.append("\n");
            }
            prompt.append("총 칼로리: ").append(totalCalories).append("kcal\n");
        }
        
        prompt.append("\n💪 오늘 운동:\n");
        if (workouts.isEmpty()) {
            prompt.append("- 운동 기록이 없습니다\n");
        } else {
            int totalTime = 0;
            int totalBurned = 0;
            for (WorkoutLog workout : workouts) {
                prompt.append("- ").append(workout.getType());
                if (workout.getDuration() != null) {
                    prompt.append(" ").append(workout.getDuration()).append("분");
                    totalTime += workout.getDuration();
                }
                if (workout.getCaloriesBurned() != null) {
                    prompt.append(" (").append(workout.getCaloriesBurned()).append("kcal 소모)");
                    totalBurned += workout.getCaloriesBurned();
                }
                prompt.append("\n");
            }
            prompt.append("총 운동시간: ").append(totalTime).append("분, 소모 칼로리: ").append(totalBurned).append("kcal\n");
        }
        
        prompt.append("\n😊 오늘 감정:\n");
        if (emotions.isEmpty()) {
            prompt.append("- 감정 기록이 없습니다\n");
        } else {
            for (EmotionLog emotion : emotions) {
                prompt.append("- ").append(emotion.getMood());
                if (emotion.getNote() != null && !emotion.getNote().trim().isEmpty()) {
                    prompt.append(": ").append(emotion.getNote());
                }
                prompt.append("\n");
            }
        }
        
        prompt.append("\n🎯 요청사항:\n");
        prompt.append("위 정보를 바탕으로 ").append(user.getEmotionMode()).append(" 톤으로 ");
        prompt.append("다이어트에 도움이 되는 한 줄 멘트를 해주세요. ");
        prompt.append("격려, 조언, 또는 피드백 중 적절한 것으로 응답해주세요. ");
        prompt.append("최대 50자 이내로 간결하게 작성해주세요.");
        
        return prompt.toString();
    }
}