package com.mydiet.repository;

import com.mydiet.entity.EmotionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    List<EmotionLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<EmotionLog> findByUserIdOrderByDateDesc(Long userId);
    long countByUserId(Long userId);
    Optional<EmotionLog> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}