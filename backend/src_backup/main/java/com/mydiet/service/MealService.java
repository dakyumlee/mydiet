@Service
@RequiredArgsConstructor
public class MealService {
    
    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;
    
    public MealLog saveMeal(MealRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        MealLog mealLog = MealLog.builder()
            .user(user)
            .description(request.getDescription())
            .caloriesEstimate(request.getCaloriesEstimate())
            .photoUrl(request.getPhotoUrl())
            .date(request.getDate() != null ? request.getDate() : LocalDate.now())
            .createdAt(LocalDateTime.now())
            .build();
        
        return mealLogRepository.save(mealLog);
    }
    
    public List<MealLog> getTodayMeals(Long userId) {
        return mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
    
    public List<MealLog> getMealsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    public void deleteMeal(Long mealId, Long userId) {
        MealLog meal = mealLogRepository.findById(mealId)
            .orElseThrow(() -> new RuntimeException("식단 기록을 찾을 수 없습니다"));
        
        if (!meal.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다");
        }
        
        mealLogRepository.delete(meal);
    }
}
