package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mydiet.model.WorkoutLog;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<WorkoutLog> findTop10ByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT COUNT(w) FROM WorkoutLog w WHERE w.date = :date")
    long countByDate(LocalDate date);
}