package com.mydiet.service;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    /**
     * 관리자 대시보드용 통계 조회
     */
    public Map<String, Object> getDashboardStats() {
        try {
            // 기본 통계
            long totalUsers = userRepository.count();
            long adminCount = userRepository.countByRole("ADMIN");
            long userCount = userRepository.countByRole("USER");
            
            // 최근 30일 내 가입자 수
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long recentUsers = userRepository.countByCreatedAtAfter(thirtyDaysAgo);
            
            // 활성 사용자 수 (최근 30일 내 업데이트된 사용자)
            long activeUsers = userRepository.countActiveUsers(thirtyDaysAgo);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("adminCount", adminCount);
            stats.put("userCount", userCount);
            stats.put("recentUsers", recentUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("timestamp", LocalDateTime.now());
            
            // 성장률 계산
            if (totalUsers > 0) {
                double recentGrowthRate = (double) recentUsers / totalUsers * 100;
                double activeRate = (double) activeUsers / totalUsers * 100;
                stats.put("recentGrowthRate", Math.round(recentGrowthRate * 100.0) / 100.0);
                stats.put("activeRate", Math.round(activeRate * 100.0) / 100.0);
            } else {
                stats.put("recentGrowthRate", 0.0);
                stats.put("activeRate", 0.0);
            }
            
            log.info("관리자 통계 조회 완료 - 전체: {}, 관리자: {}, 일반: {}, 최근: {}, 활성: {}", 
                    totalUsers, adminCount, userCount, recentUsers, activeUsers);
            
            return stats;
            
        } catch (Exception e) {
            log.error("관리자 통계 조회 실패", e);
            return Map.of(
                "totalUsers", 0L,
                "adminCount", 0L, 
                "userCount", 0L,
                "recentUsers", 0L,
                "activeUsers", 0L,
                "error", "통계 조회 실패"
            );
        }
    }

    /**
     * 최근 가입한 사용자 목록 조회
     */
    public List<User> getRecentUsers(int limit) {
        try {
            List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();
            log.info("최근 가입 사용자 조회 완료: {}명", recentUsers.size());
            return recentUsers;
        } catch (Exception e) {
            log.error("최근 사용자 조회 실패", e);
            return List.of();
        }
    }

    /**
     * 사용자 역할별 통계
     */
    public Map<String, Object> getUserRoleStats() {
        try {
            Map<String, Object> roleStats = new HashMap<>();
            
            // 각 감정 모드별 사용자 수
            long gentleUsers = userRepository.countByEmotionMode("다정함");
            long tsundereUsers = userRepository.countByEmotionMode("츤데레");
            long ruthlessUsers = userRepository.countByEmotionMode("무자비");
            
            roleStats.put("gentleUsers", gentleUsers);
            roleStats.put("tsundereUsers", tsundereUsers);
            roleStats.put("ruthlessUsers", ruthlessUsers);
            
            // 목표 체중대별 분포 (예시)
            long lightWeight = userRepository.countByWeightGoalBetween(40.0, 55.0);
            long mediumWeight = userRepository.countByWeightGoalBetween(55.1, 70.0);
            long heavyWeight = userRepository.countByWeightGoalBetween(70.1, 100.0);
            
            roleStats.put("lightWeightUsers", lightWeight);
            roleStats.put("mediumWeightUsers", mediumWeight);
            roleStats.put("heavyWeightUsers", heavyWeight);
            
            roleStats.put("timestamp", LocalDateTime.now());
            
            return roleStats;
            
        } catch (Exception e) {
            log.error("사용자 역할 통계 조회 실패", e);
            return Map.of("error", "역할 통계 조회 실패");
        }
    }

    /**
     * 시스템 건강 상태 체크
     */
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // 데이터베이스 연결 체크
            long userCount = userRepository.count();
            health.put("database", "healthy");
            health.put("userCount", userCount);
            
            // JVM 메모리 사용률
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double memoryUsage = (double) usedMemory / totalMemory * 100;
            
            health.put("memoryUsage", Math.round(memoryUsage * 100.0) / 100.0);
            health.put("memoryStatus", memoryUsage > 80 ? "warning" : "healthy");
            
            health.put("status", "healthy");
            health.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("시스템 건강 상태 체크 실패", e);
            health.put("status", "unhealthy");
            health.put("error", e.getMessage());
        }
        
        return health;
    }
}