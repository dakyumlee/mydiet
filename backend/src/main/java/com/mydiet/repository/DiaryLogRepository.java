package com.mydiet.repository;

import com.mydiet.model.DiaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryLogRepository extends JpaRepository<DiaryLog, Long> {
    List<DiaryLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<DiaryLog> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
}