package com.mydiet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mydiet.model.WorkoutLog;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<WorkoutLog> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<WorkoutLog> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);
    List<WorkoutLog> findByDateAfter(LocalDate date);
}