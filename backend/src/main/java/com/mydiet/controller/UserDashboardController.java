package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.model.MealLog;
import com.mydiet.model.WorkoutLog;
import com.mydiet.model.EmotionLog;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user") // 경로 변경하여 충돌 방지
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserDashboardController {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    
    // 사용자 대시보드 통계
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L; // 기본값
            
            log.info("대시보드 통계 요청 - 사용자 ID: {}", userId);
            
            LocalDate today = LocalDate.now();
            log.info("오늘 날짜: {}", today);
            
            // 오늘 데이터 조회
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            log.info("조회된 데이터 - 식단: {}개, 운동: {}개, 감정: {}개", 
                todayMeals.size(), todayWorkouts.size(), todayEmotions.size());
            
            // 만약 오늘 데이터가 없다면, 전체 데이터에서 오늘 날짜 데이터 찾기
            if (todayMeals.isEmpty() && todayWorkouts.isEmpty() && todayEmotions.isEmpty()) {
                log.info("오늘 데이터가 없음. 전체 데이터에서 오늘 날짜 찾기...");
                
                List<MealLog> allMeals = mealLogRepository.findAll();
                List<WorkoutLog> allWorkouts = workoutLogRepository.findAll();
                List<EmotionLog> allEmotions = emotionLogRepository.findAll();
                
                // 수동으로 오늘 날짜 필터링
                todayMeals = allMeals.stream()
                    .filter(meal -> meal.getUser().getId().equals(userId) && today.equals(meal.getDate()))
                    .collect(java.util.stream.Collectors.toList());
                    
                todayWorkouts = allWorkouts.stream()
                    .filter(workout -> workout.getUser().getId().equals(userId) && today.equals(workout.getDate()))
                    .collect(java.util.stream.Collectors.toList());
                    
                todayEmotions = allEmotions.stream()
                    .filter(emotion -> emotion.getUser().getId().equals(userId) && today.equals(emotion.getDate()))
                    .collect(java.util.stream.Collectors.toList());
                    
                log.info("수동 필터링 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    todayMeals.size(), todayWorkouts.size(), todayEmotions.size());
            }
            
            // 통계 계산
            int mealCount = todayMeals.size();
            int workoutCount = todayWorkouts.size();
            int emotionCount = todayEmotions.size();
            
            // 총 섭취 칼로리
            int totalCalories = todayMeals.stream()
                .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                .sum();
            
            // 총 소모 칼로리
            int burnedCalories = todayWorkouts.stream()
                .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                .sum();
            
            // 목표 달성률 (예: 목표 2000kcal 기준)
            double goalAchievement = totalCalories > 0 ? Math.min((double) totalCalories / 2000 * 100, 100) : 0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("mealCount", mealCount);
            stats.put("workoutCount", workoutCount);
            stats.put("emotionCount", emotionCount);
            stats.put("totalCalories", totalCalories);
            stats.put("burnedCalories", burnedCalories);
            stats.put("goalAchievement", Math.round(goalAchievement));
            stats.put("netCalories", totalCalories - burnedCalories); // 순 칼로리
            
            log.info("계산된 통계: {}", stats);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: ", e);
            return ResponseEntity.ok(Map.of(
                "mealCount", 0,
                "workoutCount", 0,
                "emotionCount", 0,
                "totalCalories", 0,
                "burnedCalories", 0,
                "goalAchievement", 0,
                "netCalories", 0
            ));
        }
    }
    
    // 사용자 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                // 기본 사용자 생성 (람다 없이)
                user = new User();
                user.setId(userId);
                user.setNickname("새 사용자");
                user.setEmail("user" + userId + "@mydiet.com");
                user.setWeightGoal(65.0);
                user.setEmotionMode("보통");
                user.setCreatedAt(LocalDateTime.now());
                user = userRepository.save(user);
                log.info("Created new default user: {}", user.getId());
            }
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("nickname", user.getNickname());
            profile.put("email", user.getEmail());
            profile.put("weightGoal", user.getWeightGoal());
            profile.put("emotionMode", user.getEmotionMode());
            profile.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(profile);
            
        } catch (Exception e) {
            log.error("Error fetching user profile: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
    
    // 프로필 업데이트 (람다 없이)
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                // 새 사용자 생성 (람다 없이)
                user = new User();
                user.setId(userId);
                user.setCreatedAt(LocalDateTime.now());
            }
            
            // 프로필 정보 업데이트
            if (request.containsKey("nickname")) {
                user.setNickname((String) request.get("nickname"));
            }
            if (request.containsKey("email")) {
                user.setEmail((String) request.get("email"));
            }
            if (request.containsKey("weightGoal")) {
                user.setWeightGoal(Double.valueOf(request.get("weightGoal").toString()));
            }
            if (request.containsKey("emotionMode")) {
                user.setEmotionMode((String) request.get("emotionMode"));
            }
            
            User savedUser = userRepository.save(user);
            log.info("Profile updated for user: {}", savedUser.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "프로필이 업데이트되었습니다!",
                "user", savedUser
            ));
            
        } catch (Exception e) {
            log.error("Error updating profile: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // 오늘의 모든 기록 조회
    @GetMapping("/today-data")
    public ResponseEntity<?> getTodayData(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            LocalDate today = LocalDate.now();
            log.info("오늘 데이터 조회 - 사용자 ID: {}, 날짜: {}", userId, today);
            
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            log.info("Repository 조회 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                meals.size(), workouts.size(), emotions.size());
            
            // 만약 Repository 쿼리가 제대로 작동하지 않으면 수동 필터링
            if (meals.isEmpty() && workouts.isEmpty() && emotions.isEmpty()) {
                log.info("Repository 쿼리 결과가 없음. 수동 필터링 시도...");
                
                List<MealLog> allMeals = mealLogRepository.findAll();
                List<WorkoutLog> allWorkouts = workoutLogRepository.findAll();
                List<EmotionLog> allEmotions = emotionLogRepository.findAll();
                
                meals = allMeals.stream()
                    .filter(meal -> meal.getUser().getId().equals(userId) && today.equals(meal.getDate()))
                    .collect(java.util.stream.Collectors.toList());
                    
                workouts = allWorkouts.stream()
                    .filter(workout -> workout.getUser().getId().equals(userId) && today.equals(workout.getDate()))
                    .collect(java.util.stream.Collectors.toList());
                    
                emotions = allEmotions.stream()
                    .filter(emotion -> emotion.getUser().getId().equals(userId) && today.equals(emotion.getDate()))
                    .collect(java.util.stream.Collectors.toList());
                    
                log.info("수동 필터링 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    meals.size(), workouts.size(), emotions.size());
            }
            
            Map<String, Object> todayData = new HashMap<>();
            todayData.put("meals", meals);
            todayData.put("workouts", workouts);
            todayData.put("emotions", emotions);
            todayData.put("date", today.toString());
            
            log.info("Today data for user {}: {} meals, {} workouts, {} emotions", 
                userId, meals.size(), workouts.size(), emotions.size());
            
            return ResponseEntity.ok(todayData);
            
        } catch (Exception e) {
            log.error("Error fetching today data: ", e);
            return ResponseEntity.ok(Map.of(
                "meals", List.of(),
                "workouts", List.of(),
                "emotions", List.of(),
                "error", e.getMessage()
            ));
        }
    }
    
    // 최근 7일 통계
    @GetMapping("/weekly-stats")
    public ResponseEntity<?> getWeeklyStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            LocalDate today = LocalDate.now();
            Map<String, Object> weeklyStats = new HashMap<>();
            
            // 최근 7일간의 데이터
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                
                List<MealLog> dayMeals = mealLogRepository.findByUserIdAndDate(userId, date);
                List<WorkoutLog> dayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, date);
                List<EmotionLog> dayEmotions = emotionLogRepository.findByUserIdAndDate(userId, date);
                
                int dayCalories = dayMeals.stream()
                    .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                    .sum();
                
                int dayBurnedCalories = dayWorkouts.stream()
                    .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                    .sum();
                
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("meals", dayMeals.size());
                dayData.put("workouts", dayWorkouts.size());
                dayData.put("emotions", dayEmotions.size());
                dayData.put("calories", dayCalories);
                dayData.put("burnedCalories", dayBurnedCalories);
                
                weeklyStats.put(date.toString(), dayData);
            }
            
            return ResponseEntity.ok(weeklyStats);
            
        } catch (Exception e) {
            log.error("Error fetching weekly stats: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}