package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mydiet.model.EmotionLog;

import java.time.LocalDate;
import java.util.List;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    List<EmotionLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<EmotionLog> findTop10ByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT COUNT(e) FROM EmotionLog e WHERE e.date = :date")
    long countByDate(LocalDate date);
}
