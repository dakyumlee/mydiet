package com.mydiet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final ClaudeService claudeService;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // 임시 시뮬레이션 데이터
            dashboard.put("totalUsers", (long)(Math.random() * 100) + 10);
            dashboard.put("todayMeals", (long)(Math.random() * 50));
            dashboard.put("todayEmotions", (long)(Math.random() * 30));
            dashboard.put("todayWorkouts", (long)(Math.random() * 40));
            dashboard.put("totalClaudeResponses", (long)(Math.random() * 500) + 100);
            
            log.info("대시보드 데이터 로딩 완료 (시뮬레이션)");
            
        } catch (Exception e) {
            log.error("대시보드 데이터 로딩 실패", e);
            // 오류 시 기본값 설정
            dashboard.put("totalUsers", 0L);
            dashboard.put("todayMeals", 0L);
            dashboard.put("todayEmotions", 0L);
            dashboard.put("todayWorkouts", 0L);
            dashboard.put("totalClaudeResponses", 0L);
        }
        
        return dashboard;
    }

    public String testClaudeResponse(Long userId) {
        try {
            return claudeService.generateResponse(userId);
        } catch (Exception e) {
            log.error("Claude API 테스트 실패 - 사용자 ID: {}", userId, e);
            return "Claude API 테스트 실패: " + e.getMessage();
        }
    }
}