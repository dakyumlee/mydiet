@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final UserRepository userRepository;
    private final WeightLogRepository weightLogRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final DiaryLogRepository diaryLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    
    public DashboardData getTodayData(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LocalDate today = LocalDate.now();
        
        List<WeightLog> todayWeights = weightLogRepository.findByUserIdAndDate(userId, today);
        List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
        List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
        List<DiaryLog> todayDiary = diaryLogRepository.findByUserIdAndDate(userId, today);
        List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
        
        WeightLog latestWeight = weightLogRepository.findTopByUserIdOrderByDateDescCreatedAtDesc(userId);
        
        return DashboardData.builder()
            .user(user)
            .currentWeight(latestWeight != null ? latestWeight.getWeight() : user.getCurrentWeight())
            .todayWeights(todayWeights)
            .todayMeals(todayMeals)
            .todayEmotions(todayEmotions)
            .todayDiary(todayDiary)
            .todayWorkouts(todayWorkouts)
            .dietDays(user.getDietDays())
            .build();
    }
    
    public WeightLog saveWeight(Long userId, Double weight, String note) {
        User user = userRepository.findById(userId).orElseThrow();
        
        WeightLog weightLog = new WeightLog();
        weightLog.setUser(user);
        weightLog.setWeight(weight);
        weightLog.setNote(note);
        
        user.setCurrentWeight(weight);
        userRepository.save(user);
        
        return weightLogRepository.save(weightLog);
    }
    
    public DiaryLog saveDiary(Long userId, String title, String content) {
        User user = userRepository.findById(userId).orElseThrow();
        
        LocalDate today = LocalDate.now();
        DiaryLog existing = diaryLogRepository.findByUserIdAndDate(userId, today).stream().findFirst().orElse(null);
        
        if (existing != null) {
            existing.setTitle(title);
            existing.setContent(content);
            return diaryLogRepository.save(existing);
        } else {
            DiaryLog diary = new DiaryLog();
            diary.setUser(user);
            diary.setTitle(title);
            diary.setContent(content);
            return diaryLogRepository.save(diary);
        }
    }
    
    public EmotionLog saveEmotion(Long userId, String mood, String note) {
        User user = userRepository.findById(userId).orElseThrow();
        
        EmotionLog emotion = new EmotionLog();
        emotion.setUser(user);
        emotion.setMood(mood);
        emotion.setNote(note);
        
        return emotionLogRepository.save(emotion);
    }
}
