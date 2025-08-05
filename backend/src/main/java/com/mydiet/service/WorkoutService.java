@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutService {

    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;

    public WorkoutLog saveWorkout(WorkoutRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        WorkoutLog workout = new WorkoutLog();
        workout.setUser(user);
        workout.setType(request.getType());
        workout.setDuration(request.getDuration());
        workout.setCaloriesBurned(request.getCaloriesBurned());
        workout.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());

        return workoutLogRepository.save(workout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLog> getTodayWorkouts(Long userId) {
        return workoutLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public WorkoutStatsResponse getWorkoutStats(Long userId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period != null ? period : "week") {
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default: // week
                startDate = endDate.minusWeeks(1);
                break;
        }

        List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        
        int totalWorkouts = workouts.size();
        int totalDuration = workouts.stream().mapToInt(WorkoutLog::getDuration).sum();
        int totalCalories = workouts.stream().mapToInt(WorkoutLog::getCaloriesBurned).sum();
        
        Map<String, Long> typeDistribution = workouts.stream()
                .collect(Collectors.groupingBy(WorkoutLog::getType, Collectors.counting()));

        return WorkoutStatsResponse.builder()
                .totalWorkouts(totalWorkouts)
                .totalDuration(totalDuration)
                .totalCalories(totalCalories)
                .averageDuration(totalWorkouts > 0 ? totalDuration / totalWorkouts : 0)
                .typeDistribution(typeDistribution)
                .period(period)
                .build();
    }

    public void deleteWorkout(Long workoutId, Long userId) {
        WorkoutLog workout = workoutLogRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("운동 기록을 찾을 수 없습니다."));

        if (!workout.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        workoutLogRepository.delete(workout);
    }
}