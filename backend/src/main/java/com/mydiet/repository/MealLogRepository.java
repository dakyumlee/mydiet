package com.mydiet.repository;

import com.mydiet.model.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserIdAndDate(Long userId, LocalDate date);
    long countByDate(LocalDate date);
    long countByUserId(Long userId);
}