package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import com.mydiet.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserDashboardController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;

    /**
     * 사용자 대시보드 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = 1L; // 개발용 기본값
            }

            final Long finalUserId = userId;
            final LocalDate today = LocalDate.now();
            
            log.info("=== 대시보드 통계 요청 ===");
            log.info("userId: {}, date: {}", finalUserId, today);

            // 오늘의 데이터 조회
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(finalUserId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(finalUserId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(finalUserId, today);
            
            log.info("조회 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    todayMeals.size(), todayWorkouts.size(), todayEmotions.size());

            // 통계 계산
            int mealCount = todayMeals.size();
            
            int totalCaloriesBurned = todayWorkouts.stream()
                    .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                    .sum();
            
            int totalCaloriesConsumed = todayMeals.stream()
                    .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                    .sum();

            // 운동 시간 합계 (분)
            int totalWorkoutMinutes = todayWorkouts.stream()
                    .mapToInt(workout -> workout.getDuration() != null ? workout.getDuration() : 0)
                    .sum();

            // 목표 달성도 계산 (칼로리 기준)
            double goalAchievement = 0.0;
            if (totalCaloriesConsumed > 0) {
                // 목표 칼로리를 2000kcal로 가정 (실제로는 User의 목표에서 가져와야 함)
                int dailyCalorieGoal = 2000;
                goalAchievement = Math.min(100.0, (double) totalCaloriesConsumed / dailyCalorieGoal * 100);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("mealCount", mealCount);
            stats.put("burnedCalories", totalCaloriesBurned);
            stats.put("consumedCalories", totalCaloriesConsumed);
            stats.put("workoutMinutes", totalWorkoutMinutes);
            stats.put("goalAchievement", Math.round(goalAchievement * 10.0) / 10.0); // 소수점 1자리

            log.info("=== 최종 통계 결과 ===");
            log.info("식단 수: {}, 소모 칼로리: {}, 섭취 칼로리: {}, 운동 시간: {}분, 목표 달성도: {}%", 
                    mealCount, totalCaloriesBurned, totalCaloriesConsumed, totalWorkoutMinutes, goalAchievement);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 오류 발생", e);
            
            // 오류 발생 시 기본값 반환
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("mealCount", 0);
            defaultStats.put("burnedCalories", 0);
            defaultStats.put("consumedCalories", 0);
            defaultStats.put("workoutMinutes", 0);
            defaultStats.put("goalAchievement", 0.0);
            
            return ResponseEntity.ok(defaultStats);
        }
    }

    /**
     * 오늘의 모든 기록 조회
     */
    @GetMapping("/today-data")
    public ResponseEntity<Map<String, Object>> getTodayData(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = 1L; // 개발용 기본값
            }

            final Long finalUserId = userId;
            final LocalDate today = LocalDate.now();

            log.info("=== 오늘 데이터 요청 ===");
            log.info("userId: {}, date: {}", finalUserId, today);

            // 오늘의 모든 데이터 조회
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(finalUserId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(finalUserId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(finalUserId, today);

            log.info("오늘 데이터 조회 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    meals.size(), workouts.size(), emotions.size());

            // 데이터 상세 로그
            meals.forEach(meal -> 
                log.debug("식단: {} ({}kcal)", meal.getDescription(), meal.getCaloriesEstimate()));
            workouts.forEach(workout -> 
                log.debug("운동: {} {}분 ({}kcal)", workout.getType(), workout.getDuration(), workout.getCaloriesBurned()));
            emotions.forEach(emotion -> 
                log.debug("감정: {} - {}", emotion.getMood(), emotion.getNote()));

            Map<String, Object> result = new HashMap<>();
            result.put("meals", meals);
            result.put("workouts", workouts);
            result.put("emotions", emotions);
            result.put("date", today.toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("오늘 데이터 조회 중 오류 발생", e);
            
            // 오류 발생 시 빈 데이터 반환
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("meals", new ArrayList<>());
            emptyResult.put("workouts", new ArrayList<>());
            emptyResult.put("emotions", new ArrayList<>());
            emptyResult.put("date", LocalDate.now().toString());
            
            return ResponseEntity.ok(emptyResult);
        }
    }

    /**
     * 주간 통계 조회 (최근 7일)
     */
    @GetMapping("/weekly-stats")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = 1L; // 개발용 기본값
            }

            final Long finalUserId = userId;
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6); // 7일간

            log.info("=== 주간 통계 요청 ===");
            log.info("userId: {}, 기간: {} ~ {}", finalUserId, startDate, endDate);

            List<Map<String, Object>> weeklyData = new ArrayList<>();
            
            // 7일간 데이터 생성
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                final LocalDate currentDate = date;
                
                List<MealLog> dayMeals = mealLogRepository.findByUserIdAndDate(finalUserId, currentDate);
                List<WorkoutLog> dayWorkouts = workoutLogRepository.findByUserIdAndDate(finalUserId, currentDate);
                List<EmotionLog> dayEmotions = emotionLogRepository.findByUserIdAndDate(finalUserId, currentDate);
                
                int dayCaloriesConsumed = dayMeals.stream()
                        .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                        .sum();
                        
                int dayCaloriesBurned = dayWorkouts.stream()
                        .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                        .sum();

                int dayWorkoutMinutes = dayWorkouts.stream()
                        .mapToInt(workout -> workout.getDuration() != null ? workout.getDuration() : 0)
                        .sum();

                // 주요 감정 추출
                String primaryMood = dayEmotions.isEmpty() ? "없음" : dayEmotions.get(0).getMood();

                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", currentDate.toString());
                dayData.put("day", currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN));
                dayData.put("consumed", dayCaloriesConsumed);
                dayData.put("burned", dayCaloriesBurned);
                dayData.put("workoutMinutes", dayWorkoutMinutes);
                dayData.put("mealCount", dayMeals.size());
                dayData.put("workoutCount", dayWorkouts.size());
                dayData.put("emotionCount", dayEmotions.size());
                dayData.put("primaryMood", primaryMood);
                
                weeklyData.add(dayData);
            }

            log.info("주간 데이터 생성 완료: {}일치 데이터", weeklyData.size());
            return ResponseEntity.ok(weeklyData);

        } catch (Exception e) {
            log.error("주간 통계 조회 중 오류 발생", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * 월간 통계 조회 (최근 30일)
     */
    @GetMapping("/monthly-stats")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = 1L; // 개발용 기본값
            }

            final Long finalUserId = userId;
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29); // 30일간

            log.info("=== 월간 통계 요청 ===");
            log.info("userId: {}, 기간: {} ~ {}", finalUserId, startDate, endDate);

            // 전체 기간 데이터 조회 (Repository에 기간 조회 메서드가 있다면 사용)
            List<MealLog> monthlyMeals = new ArrayList<>();
            List<WorkoutLog> monthlyWorkouts = new ArrayList<>();
            List<EmotionLog> monthlyEmotions = new ArrayList<>();
            
            // 날짜별로 조회 (비효율적이지만 현재 Repository 구조상 어쩔 수 없음)
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                final LocalDate currentDate = date;
                monthlyMeals.addAll(mealLogRepository.findByUserIdAndDate(finalUserId, currentDate));
                monthlyWorkouts.addAll(workoutLogRepository.findByUserIdAndDate(finalUserId, currentDate));
                monthlyEmotions.addAll(emotionLogRepository.findByUserIdAndDate(finalUserId, currentDate));
            }

            // 월간 통계 계산
            int totalMeals = monthlyMeals.size();
            int totalWorkouts = monthlyWorkouts.size();
            int totalEmotions = monthlyEmotions.size();
            
            int totalCaloriesConsumed = monthlyMeals.stream()
                    .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                    .sum();
            
            int totalCaloriesBurned = monthlyWorkouts.stream()
                    .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                    .sum();
            
            int totalWorkoutMinutes = monthlyWorkouts.stream()
                    .mapToInt(workout -> workout.getDuration() != null ? workout.getDuration() : 0)
                    .sum();

            // 평균 계산
            double avgCaloriesPerDay = totalCaloriesConsumed / 30.0;
            double avgWorkoutPerDay = totalWorkoutMinutes / 30.0;
            
            // 가장 많이 기록된 감정
            Map<String, Long> moodCounts = monthlyEmotions.stream()
                    .collect(Collectors.groupingBy(EmotionLog::getMood, Collectors.counting()));
            String mostCommonMood = moodCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("없음");

            Map<String, Object> monthlyStats = new HashMap<>();
            monthlyStats.put("totalMeals", totalMeals);
            monthlyStats.put("totalWorkouts", totalWorkouts);
            monthlyStats.put("totalEmotions", totalEmotions);
            monthlyStats.put("totalCaloriesConsumed", totalCaloriesConsumed);
            monthlyStats.put("totalCaloriesBurned", totalCaloriesBurned);
            monthlyStats.put("totalWorkoutMinutes", totalWorkoutMinutes);
            monthlyStats.put("avgCaloriesPerDay", Math.round(avgCaloriesPerDay * 10.0) / 10.0);
            monthlyStats.put("avgWorkoutPerDay", Math.round(avgWorkoutPerDay * 10.0) / 10.0);
            monthlyStats.put("mostCommonMood", mostCommonMood);
            monthlyStats.put("startDate", startDate.toString());
            monthlyStats.put("endDate", endDate.toString());

            log.info("월간 통계 생성 완료 - 식단: {}개, 운동: {}개, 감정: {}개", totalMeals, totalWorkouts, totalEmotions);
            return ResponseEntity.ok(monthlyStats);

        } catch (Exception e) {
            log.error("월간 통계 조회 중 오류 발생", e);
            
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalMeals", 0);
            emptyStats.put("totalWorkouts", 0);
            emptyStats.put("totalEmotions", 0);
            emptyStats.put("totalCaloriesConsumed", 0);
            emptyStats.put("totalCaloriesBurned", 0);
            emptyStats.put("totalWorkoutMinutes", 0);
            emptyStats.put("avgCaloriesPerDay", 0.0);
            emptyStats.put("avgWorkoutPerDay", 0.0);
            emptyStats.put("mostCommonMood", "없음");
            emptyStats.put("startDate", LocalDate.now().minusDays(29).toString());
            emptyStats.put("endDate", LocalDate.now().toString());
            
            return ResponseEntity.ok(emptyStats);
        }
    }

    /**
     * 사용자 요약 정보 조회
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getUserSummary(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = 1L; // 개발용 기본값
            }

            final Long finalUserId = userId;
            
            log.info("=== 사용자 요약 정보 요청 ===");
            log.info("userId: {}", finalUserId);

            // 사용자 정보 조회
            Optional<User> userOpt = userRepository.findById(finalUserId);
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음: userId={}", finalUserId);
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("userId", user.getId());
            summary.put("nickname", user.getNickname());
            summary.put("email", user.getEmail());
            summary.put("weightGoal", user.getWeightGoal());
            summary.put("emotionMode", user.getEmotionMode());
            summary.put("createdAt", user.getCreatedAt());

            log.info("사용자 요약 정보 조회 완료: {}", user.getNickname());
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("사용자 요약 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}