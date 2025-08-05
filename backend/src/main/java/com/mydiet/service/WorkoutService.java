package com.mydiet.service;

import com.mydiet.dto.WorkoutRequest;
import com.mydiet.model.User;
import com.mydiet.model.WorkoutLog;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutService {

    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;

    public WorkoutLog saveWorkout(WorkoutRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(user);
        workoutLog.setType(request.getType());
        workoutLog.setDuration(request.getDuration());
        workoutLog.setCaloriesBurned(request.getCaloriesBurned());
        workoutLog.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());

        return workoutLogRepository.save(workoutLog);
    }

    @Transactional(readOnly = true)
    public List<WorkoutLog> getTodayWorkouts(Long userId) {
        return workoutLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public String getWorkoutStats(Long userId, String period) {
        return "Workout stats for user " + userId + " period " + period;
    }

    public void deleteWorkout(Long workoutId, Long userId) {
        WorkoutLog workout = workoutLogRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        
        if (!workout.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        workoutLogRepository.delete(workout);
    }
}