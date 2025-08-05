@Service
@RequiredArgsConstructor
@Transactional
public class EmotionService {

    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;

    public EmotionLog saveEmotion(EmotionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        EmotionLog emotion = new EmotionLog();
        emotion.setUser(user);
        emotion.setMood(request.getMood());
        emotion.setNote(request.getNote());
        emotion.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());

        return emotionLogRepository.save(emotion);
    }

    @Transactional(readOnly = true)
    public List<EmotionLog> getTodayEmotions(Long userId) {
        return emotionLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public EmotionAnalyticsResponse getEmotionAnalytics(Long userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
         
        Map<String, Integer> moodScores = Map.of(
                "매우나쁨", 1,
                "나쁨", 2,
                "보통", 3,
                "좋음", 4,
                "매우좋음", 5
        );
 
        double averageMood = emotions.stream()
                .mapToInt(e -> moodScores.getOrDefault(e.getMood(), 3))
                .average()
                .orElse(3.0);
 
        Map<String, Long> moodDistribution = emotions.stream()
                .collect(Collectors.groupingBy(EmotionLog::getMood, Collectors.counting()));
 
        Map<LocalDate, Double> dailyMoodTrend = emotions.stream()
                .collect(Collectors.groupingBy(EmotionLog::getDate,
                        Collectors.averagingInt(e -> moodScores.getOrDefault(e.getMood(), 3))));
 
        String moodTrend = analyzeMoodTrend(dailyMoodTrend);
 
        List<String> suggestions = generateSuggestions(averageMood, moodDistribution);

        return EmotionAnalyticsResponse.builder()
                .averageMood(Math.round(averageMood * 10.0) / 10.0)
                .totalEntries(emotions.size())
                .moodDistribution(moodDistribution)
                .dailyMoodTrend(dailyMoodTrend)
                .moodTrend(moodTrend)
                .suggestions(suggestions)
                .period(days)
                .build();
    }

    private String analyzeMoodTrend(Map<LocalDate, Double> dailyMoodTrend) {
        if (dailyMoodTrend.size() < 2) {
            return "데이터 부족";
        }

        List<Double> values = dailyMoodTrend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        double first = values.get(0);
        double last = values.get(values.size() - 1);
        double change = last - first;

        if (change > 0.5) {
            return "상승세";
        } else if (change < -0.5) {
            return "하락세";
        } else {
            return "안정적";
        }
    }

    private List<String> generateSuggestions(double averageMood, Map<String, Long> moodDistribution) {
        List<String> suggestions = new ArrayList<>();

        if (averageMood < 3) {
            suggestions.add("규칙적인 운동을 통해 기분을 개선해보세요");
            suggestions.add("충분한 수면을 취하는 것이 중요합니다");
            suggestions.add("친구나 가족과 시간을 보내보세요");
        } else if (averageMood >= 4) {
            suggestions.add("좋은 컨디션을 유지하고 있어요! 계속 화이팅!");
            suggestions.add("현재의 좋은 습관들을 꾸준히 유지해보세요");
        } else {
            suggestions.add("스트레스 관리를 위한 취미 활동을 해보세요");
            suggestions.add("건강한 식단으로 기분도 좋아질 수 있어요");
        }
 
        long negativeCount = moodDistribution.getOrDefault("매우나쁨", 0L) + 
                           moodDistribution.getOrDefault("나쁨", 0L);
        long totalCount = moodDistribution.values().stream().mapToLong(Long::longValue).sum();

        if (totalCount > 0 && (double) negativeCount / totalCount > 0.5) {
            suggestions.add("전문가의 도움을 받는 것도 좋은 방법입니다");
        }

        return suggestions;
    }

    public void deleteEmotion(Long emotionId, Long userId) {
        EmotionLog emotion = emotionLogRepository.findById(emotionId)
                .orElseThrow(() -> new RuntimeException("감정 기록을 찾을 수 없습니다."));

        if (!emotion.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        emotionLogRepository.delete(emotion);
    }
}