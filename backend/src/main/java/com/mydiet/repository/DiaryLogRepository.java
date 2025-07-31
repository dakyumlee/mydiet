package com.mydiet.repository;

import com.mydiet.model.DiaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryLogRepository extends JpaRepository<DiaryLog, Long> {
    List<DiaryLog> findByUserIdAndDateOrderByCreatedAtDesc(Long userId, LocalDate date);
    Optional<DiaryLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<DiaryLog> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);
}