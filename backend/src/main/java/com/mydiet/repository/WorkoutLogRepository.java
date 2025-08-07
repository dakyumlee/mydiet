package com.mydiet.repository;

import com.mydiet.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdAndDate(Long userId, LocalDate date);
    
    @Query("SELECT COUNT(w) FROM WorkoutLog w WHERE w.date = :date")
    long countByDate(@Param("date") LocalDate date);
    
    List<WorkoutLog> findByUserId(Long userId);
}