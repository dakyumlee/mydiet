@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final WeightLogRepository weightLogRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final DiaryLogRepository diaryLogRepository;
    
    public Page<UserSummary> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        
        return users.map(user -> UserSummary.builder()
            .id(user.getId())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .dietDays(user.getDietDays())
            .currentWeight(user.getCurrentWeight())
            .weightGoal(user.getWeightGoal())
            .createdAt(user.getCreatedAt())
            .build());
    }
    
    public UserDetail getUserDetail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<WeightLog> recentWeights = weightLogRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        List<MealLog> recentMeals = mealLogRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        List<EmotionLog> recentEmotions = emotionLogRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        
        return UserDetail.builder()
            .user(user)
            .recentWeights(recentWeights)
            .recentMeals(recentMeals)
            .recentEmotions(recentEmotions)
            .build();
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public AdminStats getStats() {
        long totalUsers = userRepository.count();
        long todayRegistrations = userRepository.countByCreatedAtBetween(
            LocalDate.now().atStartOfDay(), 
            LocalDate.now().plusDays(1).atStartOfDay()
        );
        
        return AdminStats.builder()
            .totalUsers(totalUsers)
            .todayRegistrations(todayRegistrations)
            .build();
    }
}