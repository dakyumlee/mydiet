package com.mydiet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mydiet.model.EmotionLog;

import java.time.LocalDate;
import java.util.List;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    List<EmotionLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<EmotionLog> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<EmotionLog> findByUserIdOrderByDateDesc(Long userId);
    List<EmotionLog> findByDateAfter(LocalDate date);
}
