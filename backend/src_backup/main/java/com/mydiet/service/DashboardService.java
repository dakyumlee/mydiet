@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;
    
    public TodayStatsResponse getTodayStats(Long userId) {
        LocalDate today = LocalDate.now();
        
        List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
        int totalCalories = todayMeals.stream()
            .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
            .sum();
        
        List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
        int totalExerciseTime = todayWorkouts.stream()
            .mapToInt(workout -> workout.getDuration() != null ? workout.getDuration() : 0)
            .sum();
        int burnedCalories = todayWorkouts.stream()
            .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
            .sum();
        
        List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
        
        return TodayStatsResponse.builder()
            .mealsCount(todayMeals.size())
            .totalCalories(totalCalories)
            .exerciseMinutes(totalExerciseTime)
            .burnedCalories(burnedCalories)
            .emotionLogsCount(todayEmotions.size())
            .build();
    }
    
    public StatisticsResponse getWeeklyStats(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        
        List<MealLog> weeklyMeals = mealLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        int avgDailyCalories = (int) weeklyMeals.stream()
            .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
            .average()
            .orElse(0);
        
        List<WorkoutLog> weeklyWorkouts = workoutLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        int avgExerciseTime = (int) weeklyWorkouts.stream()
            .mapToInt(workout -> workout.getDuration() != null ? workout.getDuration() : 0)
            .average()
            .orElse(0);
        
        User user = userRepository.findById(userId).orElseThrow();
        double targetCalories = user.getTargetCalories() != null ? user.getTargetCalories() : 2000;
        int achievementRate = (int) ((avgDailyCalories / targetCalories) * 100);
        
        return StatisticsResponse.builder()
            .avgDailyCalories(avgDailyCalories)
            .avgExerciseMinutes(avgExerciseTime)
            .achievementRate(Math.min(achievementRate, 100))
            .weeklyMealsCount(weeklyMeals.size())
            .weeklyWorkoutsCount(weeklyWorkouts.size())
            .build();
    }
    
    public List<ActivityResponse> getRecentActivities(Long userId, int limit) {
        List<ActivityResponse> activities = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        List<MealLog> recentMeals = mealLogRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId)
            .stream().limit(limit / 3).collect(Collectors.toList());
        
        for (MealLog meal : recentMeals) {
            activities.add(ActivityResponse.builder()
                .type("MEAL")
                .description(meal.getDescription())
                .date(meal.getDate())
                .value(meal.getCaloriesEstimate() + "kcal")
                .build());
        }
        
        List<WorkoutLog> recentWorkouts = workoutLogRepository.findByUserIdOrderByDateDescCreatedAtDesc(userId)
            .stream().limit(limit / 3).collect(Collectors.toList());
            
        for (WorkoutLog workout : recentWorkouts) {
            activities.add(ActivityResponse.builder()
                .type("WORKOUT")
                .description(workout.getType())
                .date(workout.getDate())
                .value(workout.getDuration() + "분")
                .build());
        }
        
        List<EmotionLog> recentEmotions = emotionLogRepository.findByUserIdOrderByDateDesc(userId)
            .stream().limit(limit / 3).collect(Collectors.toList());
            
        for (EmotionLog emotion : recentEmotions) {
            activities.add(ActivityResponse.builder()
                .type("EMOTION")
                .description(emotion.getMood())
                .date(emotion.getDate())
                .value(emotion.getNote())
                .build());
        }
        
        return activities.stream()
            .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
            .limit(limit)
            .collect(Collectors.toList());
    }
}