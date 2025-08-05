package com.mydiet.service;

import com.mydiet.dto.EmotionAnalyticsResponse;
import com.mydiet.dto.EmotionRequest;
import com.mydiet.model.EmotionLog;
import com.mydiet.model.User;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmotionService {

    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;

    public EmotionLog saveEmotion(EmotionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmotionLog emotionLog = new EmotionLog();
        emotionLog.setUser(user);
        emotionLog.setMood(request.getMood());
        emotionLog.setNote(request.getNote());
        emotionLog.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());

        return emotionLogRepository.save(emotionLog);
    }

    @Transactional(readOnly = true)
    public List<EmotionLog> getTodayEmotions(Long userId) {
        return emotionLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public EmotionAnalyticsResponse getEmotionAnalytics(Long userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<EmotionLog> emotions = new ArrayList<>();
        
        Map<String, Long> moodDistribution = new HashMap<>();
        moodDistribution.put("행복", 5L);
        moodDistribution.put("우울", 2L);

        Map<LocalDate, Double> dailyMoodTrend = new HashMap<>();
        dailyMoodTrend.put(LocalDate.now(), 7.5);

        List<String> suggestions = Arrays.asList("더 많은 운동을 해보세요", "충분한 휴식을 취하세요");

        return EmotionAnalyticsResponse.builder()
                .averageMood(7.5)
                .moodTrend("상승")
                .moodDistribution(moodDistribution)
                .dailyMoodTrend(dailyMoodTrend)
                .totalEntries(emotions.size())
                .suggestions(suggestions)
                .build();
    }

    public void deleteEmotion(Long emotionId, Long userId) {
        EmotionLog emotion = emotionLogRepository.findById(emotionId)
                .orElseThrow(() -> new RuntimeException("Emotion not found"));
        
        if (!emotion.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        emotionLogRepository.delete(emotion);
    }

    private String analyzeMoodTrend(Map<LocalDate, Double> dailyMoodTrend) {
        return "상승";
    }

    private List<String> generateSuggestions(double averageMood, Map<String, Long> moodDistribution) {
        return Arrays.asList("더 많은 운동을 해보세요", "충분한 휴식을 취하세요");
    }
}