@Service
@RequiredArgsConstructor
public class MealService {

    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;

    public MealLog saveMeal(MealRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setDescription(request.getDescription());
        mealLog.setCaloriesEstimate(request.getCaloriesEstimate());
        mealLog.setPhotoUrl(request.getPhotoUrl());
        mealLog.setDate(LocalDate.now());

        return mealLogRepository.save(mealLog);
    }

    public List<MealLog> getTodayMeals(Long userId) {
        return mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
}