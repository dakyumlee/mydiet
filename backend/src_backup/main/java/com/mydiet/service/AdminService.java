@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;
    
    public AdminSummaryResponse getDashboardSummary() {
        long totalUsers = userRepository.count();
        long totalMeals = mealLogRepository.count();
        long totalClaudeResponses = claudeResponseRepository.count();
        
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        Set<Long> activeUserIds = new HashSet<>();
        
        List<MealLog> recentMeals = mealLogRepository.findByDateAfter(weekAgo);
        activeUserIds.addAll(recentMeals.stream().map(m -> m.getUser().getId()).collect(Collectors.toSet()));
        
        List<EmotionLog> recentEmotions = emotionLogRepository.findByDateAfter(weekAgo);
        activeUserIds.addAll(recentEmotions.stream().map(e -> e.getUser().getId()).collect(Collectors.toSet()));
        
        List<WorkoutLog> recentWorkouts = workoutLogRepository.findByDateAfter(weekAgo);
        activeUserIds.addAll(recentWorkouts.stream().map(w -> w.getUser().getId()).collect(Collectors.toSet()));
        
        return AdminSummaryResponse.builder()
            .totalUsers(totalUsers)
            .totalMeals(totalMeals)
            .totalClaudeResponses(totalClaudeResponses)
            .activeUsers(activeUserIds.size())
            .build();
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
    }
    
    public List<MealLogResponse> getRecentMeals(int limit) {
        return mealLogRepository.findAllByOrderByDateDescCreatedAtDesc(PageRequest.of(0, limit))
            .stream()
            .map(this::convertToMealLogResponse)
            .collect(Collectors.toList());
    }
    
    public List<ClaudeResponseDto> getRecentClaudeResponses(int limit) {
        return claudeResponseRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
            .stream()
            .map(this::convertToClaudeResponseDto)
            .collect(Collectors.toList());
    }
    
    public List<ActiveUserResponse> getActiveUsers() {
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        
        Map<Long, ActiveUserData> userActivityMap = new HashMap<>();
        
        List<MealLog> recentMeals = mealLogRepository.findByDateAfter(weekAgo);
        for (MealLog meal : recentMeals) {
            Long userId = meal.getUser().getId();
            userActivityMap.computeIfAbsent(userId, k -> new ActiveUserData(meal.getUser()))
                .addActivity("식단기록", meal.getDate());
        }
        
        List<EmotionLog> recentEmotions = emotionLogRepository.findByDateAfter(weekAgo);
        for (EmotionLog emotion : recentEmotions) {
            Long userId = emotion.getUser().getId();
            userActivityMap.computeIfAbsent(userId, k -> new ActiveUserData(emotion.getUser()))
                .addActivity("감정기록", emotion.getDate());
        }
        
        List<WorkoutLog> recentWorkouts = workoutLogRepository.findByDateAfter(weekAgo);
        for (WorkoutLog workout : recentWorkouts) {
            Long userId = workout.getUser().getId();
            userActivityMap.computeIfAbsent(userId, k -> new ActiveUserData(workout.getUser()))
                .addActivity("운동기록", workout.getDate());
        }
        
        return userActivityMap.values().stream()
            .map(ActiveUserData::toResponse)
            .sorted((a, b) -> b.getLastActivity().compareTo(a.getLastActivity()))
            .collect(Collectors.toList());
    }
    
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .weightGoal(user.getWeightGoal())
            .emotionMode(user.getEmotionMode())
            .createdAt(user.getCreatedAt())
            .build();
    }
    
    private MealLogResponse convertToMealLogResponse(MealLog meal) {
        return MealLogResponse.builder()
            .id(meal.getId())
            .userNickname(meal.getUser().getNickname())
            .userEmail(meal.getUser().getEmail())
            .description(meal.getDescription())
            .caloriesEstimate(meal.getCaloriesEstimate())
            .date(meal.getDate())
            .build();
    }
    
    private ClaudeResponseDto convertToClaudeResponseDto(ClaudeResponse response) {
        return ClaudeResponseDto.builder()
            .id(response.getId())
            .userNickname(response.getUser().getNickname())
            .userEmail(response.getUser().getEmail())
            .type(response.getType())
            .content(response.getContent())
            .createdAt(response.getCreatedAt())
            .build();
    }
    
    private static class ActiveUserData {
        private final User user;
        private LocalDate lastActivity;
        private String activityType;
        private int activityCount = 0;
        
        public ActiveUserData(User user) {
            this.user = user;
        }
        
        public void addActivity(String type, LocalDate date) {
            if (lastActivity == null || date.isAfter(lastActivity)) {
                lastActivity = date;
                activityType = type;
            }
            activityCount++;
        }
        
        public ActiveUserResponse toResponse() {
            return ActiveUserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .lastActivity(lastActivity)
                .activityType(activityType)
                .activityCount(activityCount)
                .build();
        }
    }
}