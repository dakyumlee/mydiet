package com.mydiet.repository;

import com.mydiet.model.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
    List<WeightLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<WeightLog> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    WeightLog findTopByUserIdOrderByDateDescCreatedAtDesc(Long userId);
}