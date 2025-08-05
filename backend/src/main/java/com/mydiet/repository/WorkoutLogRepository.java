package com.mydiet.repository;

import com.mydiet.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUserIdAndDate(Long userId, LocalDate date);
    long countByUserId(Long userId);
    
    @Modifying
    @Query("DELETE FROM WorkoutLog w WHERE w.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}