package com.mydiet.repository;

import com.mydiet.model.EmotionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    List<EmotionLog> findByUserIdAndDate(Long userId, LocalDate date);
    
    @Query("SELECT COUNT(e) FROM EmotionLog e WHERE e.date = :date")
    long countByDate(@Param("date") LocalDate date);
    
    List<EmotionLog> findByUserId(Long userId);
}