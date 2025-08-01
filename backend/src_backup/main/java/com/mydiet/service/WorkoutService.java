@Service
@RequiredArgsConstructor
public class WorkoutService {
    
    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;
    
    public WorkoutLog saveWorkout(WorkoutRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        WorkoutLog workoutLog = WorkoutLog.builder()
            .user(user)
            .type(request.getType())
            .duration(request.getDuration())
            .caloriesBurned(calculateCalories(request.getType(), request.getDuration()))
            .date(request.getDate() != null ? request.getDate() : LocalDate.now())
            .createdAt(LocalDateTime.now())
            .build();
        
        return workoutLogRepository.save(workoutLog);
    }
    
    public List<WorkoutLog> getTodayWorkouts(Long userId) {
        return workoutLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
    
    public List<WorkoutLog> getWorkoutsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return workoutLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    private Integer calculateCalories(String workoutType, Integer duration) {
        if (duration == null) return null;
        
        // 운동별 분당 소모 칼로리 (대략적인 값)
        Map<String, Double> caloriesPerMinute = Map.of(
            "걷기", 3.5,
            "뛰기", 8.0,
            "자전거", 6.0,
            "수영", 7.0,
            "요가", 2.5,
            "헬스", 5.0,
            "계단오르기", 9.0,
            "홈트레이닝", 4.0
        );
        
        double rate = caloriesPerMinute.getOrDefault(workoutType, 4.0);
        return (int) (duration * rate);
    }
}