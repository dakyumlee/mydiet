package com.mydiet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSummaryResponse {
    private long totalUsers;
    private long totalMeals;
    private long totalClaudeResponses;
    private int activeUsers;
}
