package com.mydiet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionAnalyticsResponse {
    private double averageMood;
    private String moodTrend;
    private Map<String, Long> moodDistribution;
    private Map<LocalDate, Double> dailyMoodTrend;
    private int totalEntries;
    private List<String> suggestions;
}