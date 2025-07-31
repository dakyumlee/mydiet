package com.mydiet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSummaryResponse {
    private long userCount;
    private long todayMealCount;
    private long todayClaudeCount;
    private long activeUserCount;
}
