@Service
@RequiredArgsConstructor
public class EmotionService {
    
    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;
    
    public EmotionLog saveEmotion(EmotionRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        EmotionLog emotionLog = EmotionLog.builder()
            .user(user)
            .mood(request.getMood())
            .note(request.getNote())
            .date(request.getDate() != null ? request.getDate() : LocalDate.now())
            .createdAt(LocalDateTime.now())
            .build();
        
        return emotionLogRepository.save(emotionLog);
    }
    
    public List<EmotionLog> getTodayEmotions(Long userId) {
        return emotionLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
    
    public List<EmotionLog> getEmotionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return emotionLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
}
