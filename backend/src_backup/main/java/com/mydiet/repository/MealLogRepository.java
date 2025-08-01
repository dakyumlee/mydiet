package com.mydiet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mydiet.model.MealLog;

import java.time.LocalDate;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<MealLog> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<MealLog> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);
    List<MealLog> findAllByOrderByDateDescCreatedAtDesc(Pageable pageable);
    List<MealLog> findByDateAfter(LocalDate date);
}