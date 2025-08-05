package com.mydiet.repository;

import com.mydiet.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    
    List<WorkoutLog> findByUserIdAndDate(Long userId, LocalDate date);
    
    List<WorkoutLog> findByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT COUNT(w) FROM WorkoutLog w WHERE w.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}