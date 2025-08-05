package com.mydiet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private long mealCount;
    private long emotionCount;
    private long workoutCount;
    private LocalDateTime lastActivity;
}