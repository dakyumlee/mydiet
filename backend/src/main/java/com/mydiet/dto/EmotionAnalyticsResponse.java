package com.mydiet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionAnalyticsResponse {
    private double averageMood;
    private int totalEntries;
    private Map<String, Long> moodDistribution;
    private Map<LocalDate, Double> dailyMoodTrend;
    private String moodTrend;
    private List<String> suggestions;
    private int period;
}